package com.pdftoolboxpro.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.IntConsumer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PdfMerger {
    public void merge(List<File> sources, File destination) throws IOException {
        merge(sources, destination, null);
    }

    public void merge(List<File> sources, File destination, IntConsumer onProgress) throws IOException {
        PDFMergerUtility ut = new PDFMergerUtility();
        int total = sources.size();
        for (int i = 0; i < total; i++) {
            ut.addSource(sources.get(i));
            if (onProgress != null && total > 0) {
                int percent = (int) ((i + 1) * 45.0 / total);
                onProgress.accept(percent);
            }
        }
        ut.setDestinationFileName(destination.getAbsolutePath());
        if (onProgress != null) onProgress.accept(90);
        ut.mergeDocuments(null);
        if (onProgress != null) onProgress.accept(100);
    }
}