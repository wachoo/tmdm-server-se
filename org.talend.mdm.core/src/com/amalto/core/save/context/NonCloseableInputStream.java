/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.io.IOException;
import java.io.InputStream;

public class NonCloseableInputStream extends InputStream {

    private final InputStream delegate;

    public NonCloseableInputStream(InputStream delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        if (!delegate.markSupported()) {
            throw new IllegalArgumentException("Document stream must support marks.");
        }
        this.delegate = delegate;
    }

    public void forceClose() throws IOException {
        delegate.close();
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public void close() throws IOException {
        // No op
    }

    @Override
    public void mark(int readLimit) {
        delegate.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }
}
