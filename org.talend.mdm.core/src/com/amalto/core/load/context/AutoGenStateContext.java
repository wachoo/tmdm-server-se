/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.context;

import java.util.Collections;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.amalto.core.load.LoadParserCallback;
import com.amalto.core.load.Metadata;
import com.amalto.core.load.State;
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;

/**
 * Load parser context implementation that has 2 main features:
 * <ul>
 * <li>Metadata is always ready (no need to parse the document to get the ID)</li>
 * <li>Metadata stored in this context is ID read-only: any attempt to modify it throw exception</li>
 * </ul>
 */
public class AutoGenStateContext implements StateContext {

    private final StateContext delegate;
    private final AutoGenMetadata metadata;

    private AutoGenStateContext(StateContext delegate) {
        this.delegate = delegate;
        metadata = new AutoGenMetadata(this.delegate.getMetadata());
    }

    /**
     * @param context A context implementation. The <code>context</code> may not support auto generated PK.
     *                  But if it already does, this does not throw any exception.
     * @return A context that generate metadata automatically.
     */
    public static StateContext decorate(StateContext context) {
        return new AutoGenStateContext(context);
    }

    public Metadata getMetadata() {
        // Return *this* metadata as this is what matters here.
        return this.metadata;
    }

    public boolean isMetadataReady() {
        // Always return true since ID is auto-generated.
        return true;
    }

    public void parse(XMLStreamReader reader) throws XMLStreamException {
        delegate.parse(reader);
    }

    public String getPayLoadElementName() {
        return delegate.getPayLoadElementName();
    }

    public StateContextWriter getWriter() {
        return delegate.getWriter();
    }

    public void setCurrent(State state) {
        delegate.setCurrent(state);
    }

    public LoadParserCallback getCallback() {
        return delegate.getCallback();
    }

    public boolean hasFinished() {
        return delegate.hasFinished();
    }

    public boolean hasFinishedPayload() {
        return delegate.hasFinishedPayload();
    }

    public void setWriter(StateContextWriter contextWriter) {
        delegate.setWriter(contextWriter);
    }

    public boolean isFlushDone() {
        return delegate.isFlushDone();
    }

    public void setFlushDone() {
        delegate.setFlushDone();
    }

    public void reset() {
        delegate.reset();
        metadata.reset();
    }

    public void leaveElement() {
        delegate.leaveElement();
    }

    public boolean enterElement(String elementLocalName) {
        return delegate.enterElement(elementLocalName);
    }

    public int getDepth() {
        return delegate.getDepth();
    }

    private static class AutoGenMetadata extends Metadata {
        private final Metadata metadata;

        private AutoGenMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public String[] getId() {
            // TODO check if uuid key exist
            try {
                String universe = LocalUser.getLocalUser().getUniverse().getName();
                long id = AutoIncrementGenerator.generateNum(universe,
                        metadata.getDataClusterName(),
                        metadata.getName());
                return new String[]{String.valueOf(id)};
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setId(String id) {
            throw new UnsupportedOperationException("AutoGen id is read-only");
        }

        @Override
        public void setContainer(String container) {
            metadata.setContainer(container);
        }

        @Override
        public void setName(String name) {
            metadata.setName(name);
        }

        @Override
        public void setDmn(String dmn) {
            metadata.setDmn(dmn);
        }

        @Override
        public String getContainer() {
            return metadata.getContainer();
        }

        @Override
        public String getName() {
            return metadata.getName();
        }

        @Override
        public String getDMR() {
            return metadata.getDMR();
        }

        @Override
        public String getTaskId() {
            return metadata.getTaskId();
        }

        @Override
        public String getSP() {
            return metadata.getSP();
        }

        @Override
        public String getVersion() {
            return metadata.getVersion();
        }

        @Override
        public void reset() {
            metadata.reset();
        }

        @Override
        public String getDMN() {
            return metadata.getDMN();
        }
    }
}
