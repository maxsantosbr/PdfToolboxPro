package com.pdftoolboxpro.core;

import java.io.File;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ImageToPdf {
    public void convert(List<File> images, File dest) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            for (File imgFile : images) {
                PDImageXObject img = PDImageXObject.createFromFileByContent(imgFile, doc);
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    float w = page.getMediaBox().getWidth();
                    float h = page.getMediaBox().getHeight();
                    cs.drawImage(img, 0, 0, w, h);
                }
            }
            doc.save(dest);
        }
    }
}