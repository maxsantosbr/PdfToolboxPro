package com.pdftoolboxpro.core;

import java.io.File;
import java.util.List;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfSplitter {
    public void split(File source, File destDir) throws Exception {
        try (PDDocument doc = PDDocument.load(source)) {
            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(doc);
            String base = source.getName().replaceFirst("(?i)\\.pdf$", "");
            int i = 1;
            for (PDDocument page : pages) {
                File out = new File(destDir, base + "_page_" + i++ + ".pdf");
                page.save(out);
                page.close();
            }
        }
    }
}