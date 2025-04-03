package com.example.healthcheckapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.view.View;
import android.widget.ImageView;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintAdapter {

    // Custom PrintDocumentAdapter to handle the printing process
    public static class MyPrintAdapter extends PrintDocumentAdapter {
        private final Context context;
        private final View view;
        private PdfDocument pdfDocument;
        private static final int PAGE_WIDTH = 2480; // PDF page width (in pixels)
        private static final int PAGE_HEIGHT = 3508; // PDF page height (in pixels)

        // Constructor to initialize context and view
        public MyPrintAdapter(Context context, View view) {
            this.context = context;
            this.view = view;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal, LayoutResultCallback callback,
                             Bundle extras) {

            // Create a new PdfDocument instance when layout is requested
            pdfDocument = new PdfDocument();

            // Set document info, specifying that the document is a single-page document
            PrintDocumentInfo info = new PrintDocumentInfo.Builder("full_screen_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build();

            // Notify the printing system that the layout is finished
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal, WriteResultCallback callback) {

            // Get the bitmap representation of the view to be printed
            Bitmap bitmap = getBitmapFromView(view);
            if (bitmap == null) {
                // If bitmap capture failed, report the failure
                callback.onWriteFailed("Error capturing bitmap");
                return;
            }

            // Scale the bitmap to fit the PDF page size
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, PAGE_WIDTH, PAGE_HEIGHT, true);

            // Set up page info for the PDF document
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // Get the canvas to draw the content on the page
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);

            // Calculate the scaling factor to center the bitmap on the page
            float scaleX = (float) PAGE_WIDTH / bitmap.getWidth();
            float scaleY = (float) PAGE_HEIGHT / bitmap.getHeight();
            float scale = Math.min(scaleX, scaleY);
            float offsetX = (PAGE_WIDTH - bitmap.getWidth() * scale) / 2;
            float offsetY = (PAGE_HEIGHT - bitmap.getHeight() * scale) / 2;

            // Apply scaling and draw the bitmap on the canvas
            canvas.scale(scale, scale);
            canvas.drawBitmap(bitmap, offsetX / scale, offsetY / scale, paint);

            // Finish the page creation
            pdfDocument.finishPage(page);

            try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
                // Write the document to the destination file
                pdfDocument.writeTo(out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the PdfDocument to release resources
                pdfDocument.close();
            }

            // Notify that the write operation is complete
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }
    }

    // Method to initiate the printing process
    public static void printDocument(Context context, View view) {
        // Get the PrintManager system service
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        // Define the name of the print job
        String jobName = "FullScreen_Print";

        // Start the print job by passing the PrintDocumentAdapter
        printManager.print(jobName, new MyPrintAdapter(context, view), new PrintAttributes.Builder().build());
    }

    // Helper method to capture a bitmap from the view to be printed
    private static Bitmap getBitmapFromView(View view) {
        // Enable drawing cache to capture the view's content
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false); // Disable drawing cache after capturing
        return bitmap;
    }
}
