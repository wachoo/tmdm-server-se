/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package com.amalto.core.schematron.xml.dom;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import com.amalto.core.schematron.xml.AbstractXMLProducer;
import com.amalto.core.schematron.xml.EmbeddedXMLPipe;
import com.amalto.core.schematron.xml.XMLConsumer;
 
/**
 * The <code>DOMStreamer</code> is a utility class that will generate SAX
 * events from a W3C DOM Document.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Id: DOMStreamer.java,v 1.1 2003/03/28 14:11:15 ivelin Exp $
 */
public class DOMStreamer extends AbstractXMLProducer {
 
    /** The transformer factory shared by all instances */
    protected static TransformerFactory factory = TransformerFactory.newInstance();

    /** The private transformer for this instance */
    protected Transformer transformer;

    /**
     * Create a new <code>DOMStreamer</code> instance.
     */
    public DOMStreamer() {
        super();
    }

    /**
     * Create a new <code>DOMStreamer</code> instance.
     */
    public DOMStreamer(XMLConsumer consumer) {
        this(consumer, consumer);
    }

    /**
     * Create a new <code>DOMStreamer</code> instance.
     */
    public DOMStreamer(ContentHandler content) {
        this(content,null);
        if (content instanceof LexicalHandler) {
            this.setLexicalHandler((LexicalHandler)content);
        }
    }

    /**
     * Create a new <code>DOMStreamer</code> instance.
     */
    public DOMStreamer(ContentHandler content, LexicalHandler lexical) {
        this();
        super.setContentHandler(content);
        super.setLexicalHandler(lexical);
    }

    /**
     * Start the production of SAX events.
     */
    public void stream(Node node)
    throws SAXException {
        if (this.transformer == null) {
            try {
                this.transformer = factory.newTransformer();
            } catch (TransformerConfigurationException e) {
                getLogger().log(Level.SEVERE, "DOMStreamer", e);
                throw new SAXException(e);
            }
        }
        DOMSource source = new DOMSource(node);

        ContentHandler handler;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            // Pass all SAX events
            handler = super.contentHandler;
        } else {
            // Strip start/endDocument
            handler = new EmbeddedXMLPipe(super.contentHandler);
        }

        SAXResult result = new SAXResult(handler);
        result.setLexicalHandler(super.lexicalHandler);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            getLogger().log(Level.SEVERE, "DOMStreamer", e);
            throw new SAXException(e);
        }
    }
    
    protected Logger getLogger()
    {
    	return Logger.getLogger("XMLForm");
    }
}
