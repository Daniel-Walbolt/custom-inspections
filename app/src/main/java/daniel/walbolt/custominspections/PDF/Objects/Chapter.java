package daniel.walbolt.custominspections.PDF.Objects;

/*
The Chapter class is heavily used to distinguish the different systems of an inspection report. A chapter contains data about its contents
    such as the page range, number of defects, status (REMOVED, INCOMPLETE, QUALITY, etc.).

The chapter class is basically the title page of any System / subsystem. There are different levels of chapters, similar to how a .docx file has layers of headings.

 */

import java.util.ArrayList;
import java.util.HashMap;

import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;

public class Chapter
{

    /*
    The Chapter class is the PDF equivalent of a "System," and contains all of its information.
    The PDF Controller will create Chapters, and populate the chapters with pages that display the information in the correct formats.
    When the Page Recycler Adapter loads pages, it will take each page's information and display it, and assign the page its page number.
    It was done this way in order to guarantee that certain information could be updated.
     */

    public String title;
    private ArrayList<Page> pages; // This list stores the pages of this chapter.
    private ArrayList<SystemTags> tags; //Stores the tags of the chapter

    //Chapter will provide a count of the defects
    public int highPriorityDefectCount = 0;
    public int allDefectCount = 0;
    public int restrictionCount = 0;

    public ArrayList<TableMark> marks;

    public Chapter(String chapterName, ArrayList<SystemTags> tags)
    {

        this.title = chapterName;
        this.tags = new ArrayList<>(tags);
        this.pages = new ArrayList<>();
        this.marks = new ArrayList<>();

    }

    public int getPageStart()
    {
        if(!pages.isEmpty())
            return pages.get(0).getPageNumber();
        return -1;
    }

    public int getPageEnd() {
        if(!pages.isEmpty())
            return pages.get(pages.size() - 1).getPageNumber();
        else
            return -1;
    }

    public void addPage(Page page)
    {

        //Pages added to this chapter are *in order*, and the first page added is the start of the chapter.
        pages.add(page);

    }

    public ArrayList<Page> getPages()
    {

        return pages;

    }

    public String getChapterName()
    {

        return title;

    }

    public ArrayList<SystemTags> getTags()
    {

        return tags;

    }

    public void createDefectMark(Page page, DefectItem defect)
    {

        TableMark defectMark = new TableMark(defect.getName(),  page.getPageNumber());

        marks.add(defectMark);

    }

}
