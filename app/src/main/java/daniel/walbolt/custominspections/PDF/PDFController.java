package daniel.walbolt.custominspections.PDF;

/*

The PDFController dictates where modules go by using their measurements and the available space in pages.

 */

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Defects;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Restrictions;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Settings;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Sub_System;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Front;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.PDF.Modules.CategoryHeaderModule;
import daniel.walbolt.custominspections.PDF.Modules.ChapterModule;
import daniel.walbolt.custominspections.PDF.Modules.ChecklistModule;
import daniel.walbolt.custominspections.PDF.Modules.DefectModule;
import daniel.walbolt.custominspections.PDF.Modules.FrontPageModule;
import daniel.walbolt.custominspections.PDF.Modules.GridModule;
import daniel.walbolt.custominspections.PDF.Modules.ImageModule;
import daniel.walbolt.custominspections.PDF.Modules.ObservationModule;
import daniel.walbolt.custominspections.PDF.Modules.ReadReportModule;
import daniel.walbolt.custominspections.PDF.Modules.RestrictionModule;
import daniel.walbolt.custominspections.PDF.Modules.SpacerModule;
import daniel.walbolt.custominspections.PDF.Objects.Chapter;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.PDF.Objects.Page;

public class PDFController
{

    private ArrayList<Chapter> chapters;

    /*
    TODO Modules:
    TitlePage
    Table Of Contents
    Legal Agreement
    How to Read this Report
     */

    private int currentPageNumber;
    private Page currentPage; // The page the controller is currently adding modules to
    private Chapter currentChapter; // The chapter the controller is currently adding pages to.

    private int excludedSystems = 0;

    public PDFController()
    {

        this.currentPageNumber = 1;
        chapters = new ArrayList<>();

    }

    //The method that starts the creation of the modules. This method needs to create the introduction modules BEFORE the system/chapter modules.
    public ArrayList<Page> generatePages(Context context)
    {

        ArrayList<Page> allPages = new ArrayList<>();
        currentPage = new Page(Main.inspectionSchedule.address, currentPageNumber, false);
        currentPageNumber++;

        //TODO: Create introduction modules
        //Get the front page system
        Front frontSystem = (Front) Main.inspectionSchedule.inspection.getCustomSystems().get(0);

        //Create the front system chapter. Pages are grouped by the chapters they're apart of.
        //This chapter is unique, though. This chapter does not include a ChapterModule.
        //The reason for creating a chapter here is because addingPages is done on the current chapter variable, which can't be null.
        currentChapter = new Chapter(frontSystem.getDisplayName(), frontSystem.getStatus());

        FrontPageModule frontPage = new FrontPageModule(frontSystem);
        addModuleToPage(frontPage);
        addModuleToPage(new ReadReportModule());

        chapters.add(currentChapter);

        createModules(context); // Create the modules

        int defects = 0;
        int hpDefects = 0;
        int restrictions = 0;

        ArrayList<String> restrictedSystems = new ArrayList<>();
        ArrayList<String> hpDefectSystems = new ArrayList<>();

        //Chapters variable is no longer empty
        for (Chapter chapter : chapters)
        {

            defects += chapter.allDefectCount;
            hpDefects += chapter.highPriorityDefectCount;
            restrictions += chapter.restrictionCount;

            if (chapter.restrictionCount > 0)
                restrictedSystems.add(chapter.title);

            if  (chapter.highPriorityDefectCount > 0)
                hpDefectSystems.add(hpDefects + " in " + chapter.title);

            allPages.addAll(chapter.getPages());

        }

        frontPage.restrictionSystems = restrictedSystems;
        frontPage.hpDefectSystems = hpDefectSystems;
        frontPage.totalDefects = defects;
        frontPage.hpDefects = hpDefects;
        frontPage.restrictionCount = restrictions;

        return allPages;

    }


    //Create the modules, and pages to display the inspection systems. This method is computationally intensive.
    private void createModules(Context context)
    {

        //Iterate through every system
        for(System system : Main.inspectionSchedule.inspection.getSystemList())
        {

            /*
            TODO: Handle excluded systems, display the system or not?
             */

            //The start of a system means creating a new chapter. Every chapter title page is a full page.
            currentPage = new Page(system.getDisplayName(), currentPageNumber, false);

            //Every system is a new chapter.
            currentChapter = new Chapter(system.getDisplayName(), system.getStatus());
            ChapterModule chapterModule = new ChapterModule(currentChapter);
            addModuleToPage(chapterModule);
            currentChapter.addPage(currentPage);
            currentPageNumber++;


            //Organize the categories in the system so they are displayed in the correct order
            Category[] organizedCategories = organizeCategories(system);

            //Loop through every system in the inspection.
            for(Category category : organizedCategories)
            {

                //Every category starts on a blank page
                currentPage = new Page(getCategoryHeader(category), currentPageNumber, false);

                CategoryHeaderModule categoryHeader = new CategoryHeaderModule(category); // Create the category header
                addModuleToPage(categoryHeader); // Add the category header to the page, this is the beginning of the page.

                if (category instanceof Media)
                {

                    addImageGrid(((Media)category).getMedia());

                }
                else if (category instanceof Information)
                {
                    handleInformationCategory((Information)category);
                }
                else if(category instanceof Observations)
                {
                    handleObservationsCategory((Observations)category, context);
                }
                else if(category instanceof Restrictions)
                {
                    handleRestrictionsCategory((Restrictions)category, context);
                }
                else if(category instanceof Defects)
                {
                    handleDefectsCategory((Defects)category, context);
                }

                //Add the final page of the category to the chapter
                currentChapter.addPage(currentPage);
                currentPageNumber++;

            }

            chapters.add(currentChapter);

        }

    }

    //Add all the infoItems of an Information category to the PDF
    private void handleInformationCategory(Information informationCategory)
    {

        ArrayList<CategoryItem> ungroupedItems = new ArrayList<>();
        ArrayList<ChecklistModule> checkListModules = new ArrayList<>(); // List to store all checklist modules created for the information category

        //Iterate through every item in the information category
        for (CategoryItem infoItem : informationCategory.getCategoryItems())
        {

            //Check if the item in the category is a Group
            if (infoItem instanceof CategoryGroup)
            {

                CategoryGroup infoGroup =  (CategoryGroup) infoItem;

                //Store the items in the group that are applicable.
                ArrayList<CategoryItem> applicableItems = new ArrayList<>();

                //Iterate through the items in the group
                for (CategoryItem groupItem : infoGroup.getItems())
                {

                    //Find all the items that are applicable
                    if (groupItem.isApplicable())
                        applicableItems.add(groupItem);

                }

                //Check if we just found applicable items in the group
                if (!applicableItems.isEmpty())
                {

                    //Create a check list module to display the applicable items
                    ChecklistModule groupCheckListModule = new ChecklistModule(infoGroup.getName(), applicableItems);

                    //Add the module to the list of checklist modules
                    checkListModules.add(groupCheckListModule);

                }


            }
            else if(infoItem.getGroup() == null)
            {

                //The item in the category is not part of a group
                //Add the item to the ungrouped item list if it is marked by the inspector
                if (infoItem.isApplicable())
                    ungroupedItems.add(infoItem);

            }

        }

        //After looping through the information category
        //Check if the ungrouped item list is empty
        if (!ungroupedItems.isEmpty())
        {

            //Create a checklist module to display the ungrouped items.
            ChecklistModule ungroupedChecklist = new ChecklistModule("General", ungroupedItems);
            checkListModules.add(ungroupedChecklist);

        }

        //Check if there are any other checklist modules to be displayed
        if (!checkListModules.isEmpty())
        {

            addModuleToPage(new SpacerModule(100));
            addGridModule(checkListModules);

        }


    }

    //Add all the observations of a system onto a page
    private void handleObservationsCategory(Observations observations, Context context)
    {

        //Loop through the category, all items should be groups or ObservationItems.
        for (CategoryItem categoryItem : observations.getCategoryItems())
        {

            if (categoryItem instanceof ObservationItem)
            {

                //Only add the observations that were added by the inspector
                if (categoryItem.isApplicable())
                    addObservation((ObservationItem)categoryItem, context);

            }
            else if(categoryItem instanceof CategoryGroup)
            {

                //If the item is a group, loop through its items and find observations added by the inspector
                for (CategoryItem groupItem : ((CategoryGroup)categoryItem).getItems())
                {

                    if (groupItem instanceof ObservationItem)
                    {

                        //Only add the observations that were added by the inspector
                        if(groupItem.isApplicable())
                            addObservation((ObservationItem)groupItem, context);

                    }

                }

            }

        }

    }

    //Add all the observations of a system onto a page
    private void handleRestrictionsCategory(Restrictions restrictions, Context context)
    {

        //Loop through the category, all items should be groups or ObservationItems.
        for (CategoryItem categoryItem : restrictions.getCategoryItems())
        {

            if (categoryItem instanceof RestrictionItem)
            {

                //Only add the restrictions that were added by the inspector
                if (categoryItem.isApplicable())
                    addRestriction((RestrictionItem)categoryItem, context);

            }
            else if(categoryItem instanceof CategoryGroup)
            {

                //If the item is a group, loop through its items and find observations added by the inspector
                for (CategoryItem groupItem : ((CategoryGroup)categoryItem).getItems())
                {

                    if (groupItem instanceof RestrictionItem)
                    {

                        //Only add the restrictions that were added by the inspector
                        if(groupItem.isApplicable())
                            addRestriction((RestrictionItem)groupItem, context);

                    }

                }

            }

        }

    }

    private void handleDefectsCategory(Defects defects, Context context)
    {

        //Loop through the category, all items should be groups or ObservationItems.
        for (CategoryItem categoryItem : defects.getCategoryItems())
        {

            if (categoryItem instanceof DefectItem)
            {

                //Only add the restrictions that were added by the inspector
                if (categoryItem.isApplicable())
                    addDefect((DefectItem)categoryItem, context);

            }
            else if(categoryItem instanceof CategoryGroup)
            {

                //If the item is a group, loop through its items and find observations added by the inspector
                for (CategoryItem groupItem : ((CategoryGroup)categoryItem).getItems())
                {

                    if (groupItem instanceof DefectItem)
                    {

                        //Only add the restrictions that were added by the inspector
                        if(groupItem.isApplicable())
                            addDefect((DefectItem)groupItem, context);

                    }

                }

            }

        }

    }

    private void addObservation(ObservationItem observation, Context context)
    {

        //After every observation module, create a grid to display its images
        ObservationModule observationModule = new ObservationModule(context, observation);

        addModuleToPage(observationModule);

        boolean hasImages = addImageGrid(observation.getPictures());

        //Set the observation module to display "Images" above the Observation image if the image grid was added or hide the text if it wasn't
        observationModule.setHasImages(hasImages);

    }

    private void addRestriction(RestrictionItem restriction, Context context)
    {

        RestrictionModule restrictionModule = new RestrictionModule(context, restriction);

        addModuleToPage(restrictionModule);
        currentChapter.restrictionCount++; // Increase restriction count in the target chapter

        boolean hasImages = addImageGrid(restriction.getPictures());

        restrictionModule.setHasImages(hasImages);

    }

    private void addDefect(DefectItem defect, Context context)
    {

        DefectModule defectModule = new DefectModule(context, defect);

        addModuleToPage(defectModule);
        currentChapter.allDefectCount++; //Increase defect count in the current chapter

        //If this defect is of high priority, increase count of high priority defect in the chapter
        if (defect.getSeverity() == DefectItem.SEVERITY.HIGH)
        {

            currentChapter.highPriorityDefectCount++;
            currentChapter.createDefectMark(currentPage, defect);

        }

        boolean hasImages = addImageGrid(defect.getPictures());

        defectModule.setHasImages(hasImages);

    }

    private void addModuleToPage(Module module)
    {

        module.establishHeight(); //Verify the height of the module

        if (currentPage.getRemainingHeight() >= module.height)
        {

            currentPage.addModule(module);

        }
        else // Otherwise, if the current page does not have enough space to fit the module
        {

            //Create another page with an identical header, and increased page number
            getNextPage(true, true);

            //Add the module to the next page.
            currentPage.addModule(module);

        }

    }

    private Category[] organizeCategories(System system)
    {

        Category[] organizedCategories = new Category[5];

        for(Category category :system.getCategories())
        {

            if (category instanceof Media)
                organizedCategories[0] = category;
            else if(category instanceof Information)
                organizedCategories[1] = category;
            else if(category instanceof Observations)
                organizedCategories[2] = category;
            else if(category instanceof Restrictions)
                organizedCategories[3] = category;
            else if(category instanceof Defects)
                organizedCategories[4] = category;

        }

        return organizedCategories;

    }

    //Using a list of InspectionMedia, create a grid if the list is not empty. Return the new page.
    private boolean addImageGrid(ArrayList<InspectionMedia> media)
    {

        ArrayList<ImageModule> images = new ArrayList<>();

        for (InspectionMedia image : media)
        {

            images.add(new ImageModule(image));

        }

        if (images.isEmpty())
            return false;
        else
        {

            addGridModule(images);
            return true;

        }

    }

    //This method adds a list of modules to the PDF by creating as many grid modules as necessary to display them all.
    //IF there are a sufficient amount of modules to display, another grid module must be created on the next page.
    private void addGridModule(ArrayList<? extends Module> modulesToAdd)
    {

        //Create the first grid module
        GridModule gridModule = new GridModule(modulesToAdd, currentPage.getRemainingHeight());

        //Get the modules that won't fit in the grid
        ArrayList<Module> overflowModules = gridModule.getOverflowModules();

        //The following code continuously creates Grid Modules if there are too many modules to fit on the current page.
        while(!overflowModules.isEmpty())
        {

            //If overflow modules ArrayList is not empty, that means the GridModule has determined the module does not fit on the page
            //However, when creating a new GridModule, it will attempt to add it to the current page, which already can't fit the overflowed module.
            // The GridModule removes the module from its expected height, meaning it has 0 height. Attempting to add 0 height module to a page will never advance the page.
            // This logic creates an infinite loop.

            //Attempt to add the current grid module to the page, this will always work because the grid module will only accept what WILL fit on the page
            addModuleToPage(gridModule);

            //Advance to the next page
            getNextPage(true, true);

            //Create the new grid module
            gridModule = new GridModule(overflowModules, currentPage.getRemainingHeight());
            overflowModules = gridModule.getOverflowModules();

        }

        addModuleToPage(gridModule);

    }

    //Get the string for the page header when in a category
    private String getCategoryHeader(Category category)
    {

        // "Category name" - "Sub System Name" - "System Name" when applicable
        if(category.getSystem().isSubSystem())
            return category.getName() + " - " + category.getSystem().getDisplayName() + " - " + category.getSystem().getParentSystem().getDisplayName();
        else
            return category.getName() + " - " + category.getSystem().getDisplayName();

    }

    //Gets the next page with identical characteristics to the previous
    //Adds the previous page to the chapter as well
    private void getNextPage(boolean headerVisibility, boolean addSpacing)
    {

        currentChapter.addPage(currentPage);

        currentPage = new Page(currentPage.getHeader(), ++currentPageNumber, headerVisibility);

        if (addSpacing)
            currentPage.addModule(new SpacerModule(40)); // Add spacing from the top header of the page

    }

}
