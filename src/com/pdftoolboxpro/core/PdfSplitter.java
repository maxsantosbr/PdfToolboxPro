package com.pdftoolboxpro.core;

import java.io.File;
import java.util.List;
import java.util.function.IntConsumer;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfSplitter {
    public void split(File source, File destDir) throws Exception {
        split(source, destDir, null);
    }

    public void split(File source, File destDir, IntConsumer onProgress) throws Exception {
        try (PDDocument doc = PDDocument.load(source)) {
            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(doc);
            String base = source.getName().replaceFirst("(?i)\\.pdf$", "");
            int total = pages.size();
            int digitos = String.valueOf(total).length();
            int i = 1;
            for (PDDocument page : pages) {
                File out = new File(destDir, String.format("%s_page_%0" + digitos + "d.pdf", base, i++));
                page.save(out);
                page.close();
                if (onProgress != null && total > 0) {
                    int percent = (int) ((i - 1) * 100.0 / total);
                    onProgress.accept(percent);
                }
            }
        }
    }
}