package daniel.walbolt.custominspections.Adapters.PDF;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.PDF.Objects.Page;
import daniel.walbolt.custominspections.R;

/*
This Recycler controls the displaying of each page.
Each page will be assigned content prior to drawing on the screen, so this recycler takes pages with allocated modules.

This adapter will create the page views and allow the Page object to render its modules onto it.
 */

public class PDFPageRecyclerAdapter extends RecyclerView.Adapter<PDFPageRecyclerAdapter.PageHolder>
{

    ArrayList<Page> pages;

    public PDFPageRecyclerAdapter(ArrayList<Page> pages)
    {

        //Pages are the foundational object that display content.
        //The recycler view, and the PDFDocument object use the base page view, however the Page class stores its own content.
        this.pages = pages;

    }

    @NonNull
    @Override
    public PDFPageRecyclerAdapter.PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page, parent, false);

        return new PageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position)
    {


        Page page = pages.get(position);
        page.attachPageView(holder.itemView); // Handles the title and number
        page.initModules(); // Initialize the page's modules onto the page view.

    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    class PageHolder extends RecyclerView.ViewHolder
    {

        //Every page consists of a vertical linear layout
        LinearLayout content;

        // Header of the page. Each page automatically has it ENABLED. Certain pages will disable it, e.g. front page.
        // Note: The header layout contains the pageTitle TextView.
        RelativeLayout header;

        TextView pageNumber;
        TextView pageTitle;

        public PageHolder(@NonNull View itemView)
        {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView)
        {

            pageTitle = itemView.findViewById(R.id.pdf_page_title);
            pageNumber = itemView.findViewById(R.id.pdf_page_number);
            content = itemView.findViewById(R.id.pdf_page_content);
            header = itemView.findViewById(R.id.pdf_page_header);

        }

    }

}
