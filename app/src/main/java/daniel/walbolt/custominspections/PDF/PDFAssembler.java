package daniel.walbolt.custominspections.PDF;

/*
PDFAssembler creates a printable PDF document from the information in the inspection

This class also displays the predicted PDF to the inspector before uploading it.

 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;
import daniel.walbolt.custominspections.PDF.Objects.Page;

public class PDFAssembler extends PrintDocumentAdapter
{

    private File pdfFile;
    private PrintedPdfDocument pdfDocument;
    private Context context;
    private PDFPreview preview;
    private ArrayList<Page> pages;

    public PDFAssembler(Context context, PDFPreview preview, ArrayList<Page> pages)
    {

        this.context = context;
        this.preview = preview;
        this.pages = pages;

    }

    // Call THIS method to start the printing process!
    public void printPDF()
    {

        //PrintAttributes help determine margins, resolution, and the desired size of paper to print on.
        PrintAttributes attributes = new PrintAttributes.Builder().setDuplexMode(PrintAttributes.DUPLEX_MODE_SHORT_EDGE)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(new PrintAttributes.Margins(15, 15, 15, 15))
                .setResolution(new PrintAttributes.Resolution("RSReport", "Inspection PDF", 300, 300))
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();

        PrintedPdfDocument pdf = new PrintedPdfDocument(context, attributes);

        for (Page androidPage : pages)
        {

            PdfDocument.Page pdfPage = pdf.startPage(androidPage.getPageNumber());

            View pageLayout = androidPage.getView();

            //Create the bitmap that will store the content on the page
            Bitmap bitmap = Bitmap.createBitmap(pageLayout.getWidth(), pageLayout.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            pageLayout.draw(canvas); // Draw the page onto the canvas exactly the same size as it.

            Rect src = new Rect(0, 0, pageLayout.getWidth(), pageLayout.getHeight());

            //Now get the PdfDocument Page's Canvas and measure it
            Canvas pageCanvas = pdfPage.getCanvas();
            float pageWidth = pageCanvas.getWidth();
            float pageHeight = pageCanvas.getHeight();

            //Find the scale factor to fit the rectangle onto the pageCanvas while preserving aspect ratio
            float scale = Math.min(pageWidth/src.width(), pageHeight/src.height());
            float left = pageWidth / 2 - src.width() * scale / 2;
            float top = pageHeight / 2 - src.height() * scale / 2;
            float right = pageWidth / 2 + src.width() * scale / 2;
            float bottom = pageHeight / 2 + src.height() * scale / 2;
            RectF dst = new RectF(left, top, right, bottom);

            //Draw the view's bitmap onto the page's canvas using the scalar rectangle
            pageCanvas.drawBitmap(bitmap, src, dst, null);

            pdf.finishPage(pdfPage);

        }

        try {
            pdf.writeTo(getFileOutputStream(context));
            Toast.makeText(context, "PDF Created.", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {

            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF.", Toast.LENGTH_SHORT).show();

        }
        finally {
            pdf.close();
        }

        if(pdfFile != null)
        {

            System.out.println("Uploading PDF");
            
            FirebaseBusiness database = new FirebaseBusiness();
                database.uploadPDF(Uri.fromFile(pdfFile), "Inspection_Report_" + Main.inspectionSchedule.getScheduleID());
            Toast.makeText(context, "PDF Uploaded", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

        pdfDocument = new PrintedPdfDocument(context, newAttributes);

        if (cancellationSignal.isCanceled())
        {
            layoutResultCallback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("WR_Inspection_" + Main.inspectionSchedule.getScheduleID())
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(preview.getPageCount());

        PrintDocumentInfo info = builder.build();
        layoutResultCallback.onLayoutFinished(info, true);

    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback)
    {

        // Code taken from stack overflow: https://stackoverflow.com/questions/32975339/generate-pdf-from-android-using-printedpdfdocument-and-view-draw
        // Here we take an input view, and draw it entirely on a canvas. We take this canvas and put it on a rectangle.
        // We then scale the rectangle to fit the page's canvas while preserving aspect ratio

        for (Page page : pages)
        {

            PdfDocument.Page pdfPage = pdfDocument.startPage(page.getPageNumber());

            View pageLayout = page.getView();

            //Create the bitmap that will store the content on the page
            Bitmap bitmap = Bitmap.createBitmap(pageLayout.getWidth(), pageLayout.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            pageLayout.draw(canvas); // Draw the page onto the canvas exactly the same size as it.

            Rect src = new Rect(0, 0, pageLayout.getWidth(), pageLayout.getHeight());

            //Now get the PdfDocument Page's Canvas and measure it
            Canvas pageCanvas = pdfPage.getCanvas();
            float pageWidth = pageCanvas.getWidth();
            float pageHeight = pageCanvas.getHeight();

            //Find the scale factor to fit the rectangle onto the pageCanvas while preserving aspect ratio
            float scale = Math.min(pageWidth/src.width(), pageHeight/src.height());
            float left = pageWidth / 2 - src.width() * scale / 2;
            float top = pageHeight / 2 - src.height() * scale / 2;
            float right = pageWidth / 2 + src.width() * scale / 2;
            float bottom = pageHeight / 2 + src.height() * scale / 2;
            RectF dst = new RectF(left, top, right, bottom);

            //Draw the view's bitmap onto the page's canvas using the scalar rectangle
            pageCanvas.drawBitmap(bitmap, src, dst, null);
            pdfDocument.finishPage(pdfPage);

        }

        System.out.println("------------------------------------");
        System.out.println(pdfDocument.getPages());

        try {
            pdfDocument.writeTo(getFileOutputStream(context));
        }catch(Exception e)
        {

            writeResultCallback.onWriteFailed(e.toString());
            return;

        }
        finally {
            pdfDocument.close();
            pdfDocument = null;
        }

        writeResultCallback.onWriteFinished(new PageRange[] {new PageRange(0, preview.getPageCount()-1)});

    }

    public FileOutputStream getFileOutputStream(Context context)
    {

        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        directory.mkdirs();
        File pdfOutput = new File(directory, "pdfTest.pdf");
        if(directory.exists())
        {

            try
            {

                pdfOutput.createNewFile();
                pdfFile = pdfOutput;

                FileOutputStream fos = new FileOutputStream(pdfOutput);

                return fos;

            }
            catch(IOException e)
            {

                e.printStackTrace();

            }

        }

        return null;

    }
}
