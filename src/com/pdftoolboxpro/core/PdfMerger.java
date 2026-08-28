package com.pdftoolboxpro.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PdfMerger {
    public void merge(List<File> sources, File destination) throws IOException {
        PDFMergerUtility ut = new PDFMergerUtility();
        for (File f : sources) {
            ut.addSource(f);
        }
        ut.setDestinationFileName(destination.getAbsolutePath());
        ut.mergeDocuments(null);
    }
}