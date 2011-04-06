// ============================================================================
//
// Copyright (c) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.load.context;

import com.amalto.core.load.LoadParserCallback;
import com.amalto.core.load.Metadata;
import com.amalto.core.load.State;
import com.amalto.core.load.exception.ParserCallbackException;
import com.amalto.core.load.payload.EndPayload;
import com.amalto.core.load.payload.StartPayload;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.*;

/**
 *
 */
public class StateContextImpl implements StateContext {
    private final LoadParserCallback callback;
    private final String payLoadElementName;
    private final BufferStateContextWriter bufferStateContextWriter = new BufferStateContextWriter();
    private final Stack<String> currentLocation = new Stack<String>();
    private final String[] idPaths;
    private final Set<String> idLeftForMatch = new HashSet<String>();
    private final Metadata metadata;

    private State currentState = StartPayload.INSTANCE;
    private StateContextWriter contextWriter;
    private int payloadLimit = -1;
    private int payloadCount = 0;
    private boolean isFlushDone;
    private boolean isMetadataReady;

    public StateContextImpl(String payLoadElementName, String[] idPaths, LoadParserCallback callback, int payloadLimit) {
        this(payLoadElementName, idPaths, callback);
        this.payloadLimit = payloadLimit;
    }

    private StateContextImpl(String payLoadElementName, String[] idPaths, LoadParserCallback callback) {
        this.idPaths = idPaths;
        if (payLoadElementName == null) {
            throw new IllegalArgumentException("Payload element name cannot be null.");
        }

        contextWriter = bufferStateContextWriter;
        this.callback = callback;
        this.payLoadElementName = payLoadElementName;

        metadata = new Metadata();
        metadata.setContainer(payLoadElementName);
        metadata.setName(payLoadElementName);
        metadata.setDmn(payLoadElementName);
    }

    public String getPayLoadElementName() {
        return payLoadElementName;
    }

    public void setWriter(StateContextWriter contextWriter) {
        this.contextWriter = contextWriter;
    }

    public boolean isFlushDone() {
        return isFlushDone;
    }

    public boolean isMetadataReady() {
        return isMetadataReady;
    }

    public void setFlushDone() {
        this.isFlushDone = true;
    }

    public void reset() {
        isFlushDone = false;
        isMetadataReady = false;
        contextWriter = bufferStateContextWriter.reset();
        idLeftForMatch.addAll(Arrays.asList(idPaths));
        metadata.reset();
    }

    public StateContextWriter getWriter() {
        return contextWriter;
    }

    public void setCurrent(State state) {
        currentState = state;
    }

    public void parse(XMLStreamReader reader) throws XMLStreamException {
        try {
            currentState.parse(this, reader);
        } catch (ParserCallbackException e) {
            throw new RuntimeException(e);
        } catch (XMLStreamException e) {
            // Parsing exceptions should not happen, interrupt parsing
            throw new RuntimeException(e);
        }
    }

    public LoadParserCallback getCallback() {
        return callback;
    }

    public boolean hasFinished() {
        return payloadCount == payloadLimit || currentState.isFinal();
    }

    public boolean hasFinishedPayload() {
        boolean hasFinishedPayload;
        if (currentState == EndPayload.INSTANCE) {
            hasFinishedPayload = true;
            payloadCount++;
        } else {
            hasFinishedPayload = false;
        }
        return hasFinishedPayload;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void leaveElement() {
        if (!currentLocation.isEmpty()) {
            currentLocation.pop();
        }
    }

    public boolean enterElement(String elementLocalName) {
        currentLocation.push(elementLocalName);
        boolean hasMatchId = false;

        // Check path
        if (!isFlushDone()) {
            String currentPath = "";
            String separator = "";
            for (String currentPathElement : currentLocation) {
                currentPath += separator + currentPathElement;
                separator = "/";
            }

            Iterator<String> iterator = idLeftForMatch.iterator();
            while (iterator.hasNext()) {
                String match = iterator.next();
                if (match.equals(currentPath)) {
                    hasMatchId = true;
                    iterator.remove();
                }
            }

            if (idLeftForMatch.isEmpty()) {
                isMetadataReady = true;
            }
        }

        return hasMatchId;
    }

    public int getDepth() {
        return currentLocation.size();
    }

}
