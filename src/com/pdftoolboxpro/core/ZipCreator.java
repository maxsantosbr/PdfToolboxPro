package com.pdftoolboxpro.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCreator {
    public void createZip(List<File> sources, File dest) throws IOException {
        createZip(sources, dest, null);
    }

    public void createZip(List<File> sources, File dest, IntConsumer onProgress) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest))) {
            int total = sources.size();
            for (int i = 0; i < total; i++) {
                File f = sources.get(i);
                zos.putNextEntry(new ZipEntry(f.getName()));
                try (FileInputStream fis = new FileInputStream(f)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = fis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                }
                zos.closeEntry();
                if (onProgress != null && total > 0) {
                    int percent = (int) ((i + 1) * 100.0 / total);
                    onProgress.accept(percent);
                }
            }
        }
    }
}