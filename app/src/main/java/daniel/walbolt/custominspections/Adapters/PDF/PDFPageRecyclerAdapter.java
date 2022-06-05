/*
package daniel.walbolt.custominspections.Adapters.PDF;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.PDF.Chapter;
import daniel.walbolt.custominspections.PDF.Module;
import daniel.walbolt.custominspections.PDF.PDFController;
import daniel.walbolt.custominspections.PDF.Page;
import daniel.walbolt.custominspections.R;

public class PDFPageRecyclerAdapter extends RecyclerView.Adapter<PDFPageRecyclerAdapter.PageHolder>
{

    ArrayList<Page> pages;

    public PDFPageRecyclerAdapter(final RecyclerView attachedTo)
    {

        //Pages are the foundational object that display content.
        //The recycler view, and the PDFDocument object use the base page view, however the Page class stores its own content.
        pages = new ArrayList<>();

        //The controller for the all the content of the PDF pages.
        PDFController contentController = new PDFController();

        for(System system : Main.inspectionSchedule.inspection.getSystemList())
        {

            //Create the chapter for every system. Adds all modules and sub-chapters.
            Chapter systemChapter = new Chapter(system.getDisplayName());

            //Check if the system returns modules
            if(system.getPDFModules() != null)
                systemChapter.addModules(system.getPDFModules());
            else // otherwise, create a placeholder (show the absence of data)
            {



            }


        }

        requiredPages = systemChapter.getRequiredPages();
        systemChapter.createPages(pages, requiredPages);
        systemChapter.transferModulesToPages(pages);

    }

    @NonNull
    @Override
    public PDFPageRecyclerAdapter.PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pdf_page, parent, false);

        return new PageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position)
    {

        Page page = pages.get(position);
        page.attachPageView(holder.content);
        page.initModules();

    }

    @Override
    public int getItemCount() {
        return requiredPages;
    }

    public ArrayList<Page> getPageViews()
    {

        return pages;

    }

    class PageHolder extends RecyclerView.ViewHolder
    {

        //Every page consists of a vertical linear layout
        LinearLayout content;

        public PageHolder(@NonNull View itemView)
        {
            super(itemView);
        }


    }

}
*/
