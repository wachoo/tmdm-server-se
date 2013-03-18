// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FileChunkLoader {

    public static final int CR = '\r';

    public static final int LF = '\n';

    private final File file;

    private static class FileChunkCopier extends RandomAccessFile {

        public FileChunkCopier(File file, String mode) throws FileNotFoundException {
            super(file, mode);
        }

        public long copyChunkTo(OutputStream os, int maxLines) throws IOException {
            int readLines = 0;
            boolean eof = false;
            while (!eof && readLines < maxLines) {
                eof = copyLineTo(os);
                if (!eof) {
                    os.write(CR);
                    os.write(LF);
                }
                readLines++;
            }
            return getFilePointer();
        }

        private boolean copyLineTo(OutputStream os) throws IOException {
            int c = -1;
            boolean eol = false;

            while (!eol) {
                switch (c = read()) {
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = getFilePointer();
                    if ((read()) != '\n') {
                        seek(cur);
                    }
                    break;
                default:
                    os.write(c);
                    break;
                }
            }
            return (c == -1);
        }
    }

    public FileChunkLoader(File file) {
        this.file = file;
    }

    public long loadChunkTo(OutputStream os, long position, int maxLines) throws IOException {
        if (!file.exists()) {
            // file doesn't exist yet ?
            return 0;
        }
        if (file.length() < position) {
            position = 0; // content rolled over
        }
        FileChunkCopier copier = null;
        try {
            copier = new FileChunkCopier(file, "r"); //$NON-NLS-1$
            copier.seek(position);
            return copier.copyChunkTo(os, maxLines);
        } finally {
            if (copier != null) {
                try {
                    copier.close();
                } catch (Exception e) {
                    // ignore it
                }
            }
        }
    }
}
