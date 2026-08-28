package com.pdftoolboxpro.core;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfCompressor {
    public void compress(File source, File dest) throws Exception {
        try (PDDocument doc = PDDocument.load(source)) {
            doc.setAllSecurityToBeRemoved(true);
            // Re-salva com compressão - remove histórico incremental
            doc.save(dest);
        }
    }
}