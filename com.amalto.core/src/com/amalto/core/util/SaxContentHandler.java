/*
 * Created on 11 sept. 2005
 */
package com.amalto.core.util;

import java.io.Writer;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author bgrieder
 *
 */
public class SaxContentHandler implements ContentHandler {
	
 	
 	SaxContentHandlerCaller caller;
 	Object[] parameters; //passed in the call back
 	Hashtable<String, String> namespaces = new Hashtable<String, String>(); 
 	
    /** Print writer. */
    protected Writer fOut;

    /** No XML declaration. */
    protected boolean omitXMLDeclaration;
    
    /** Canonical output. */
    protected boolean fCanonical;

    /** Element depth. */
    protected int fElementDepth;
    
    protected String topElement=null;
 	
    public void setWriter(Writer sw) {
    	this.fOut = sw;
    }
    
    protected boolean insideElement = false; 
    
    
 	public SaxContentHandler(
 			SaxContentHandlerCaller caller, 
			Writer writer,
			Object[] parameters
			) {
 		this.caller = caller;
 		this.fOut = writer;
 		this.parameters = parameters;
 		fElementDepth = 0;
 		fCanonical = false;
 		omitXMLDeclaration = true;
 	}
 	
 	
 	public void characters(char[] ch, int start, int length)
			throws SAXException  {

 		try {
 			//if (insideElement) {
		 		normalizeAndPrint(ch, start, length);
		        fOut.flush();
 			//}
 		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}
	}
	
 	
 	public void endDocument() throws SAXException {
 		//flush the las element
	 	try {
	 		caller.processSaxItem(this, this.fOut, parameters);
		} catch (Exception e) {
				throw new SAXException(e.getClass().getName()+": "+e.getMessage());
		}
	}
 	
 	
	public void endElement(String namespaceURI, String localName,
			String qName) throws SAXException {

		try {
			fElementDepth--;
	        fOut.write("</");
	        fOut.write(qName);
	        fOut.write('>');
	        fOut.flush();
	        insideElement = false;
		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}

	}
	
	public void endPrefixMapping(String prefix) throws SAXException {
	}
	
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		
		/*
		try {
			characters(ch, start, length);
	        fOut.flush();
		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}
 		*/

	}
	
	
	public void processingInstruction(String target, String data)
			throws SAXException {
		
		try {
			if (fElementDepth > 0) {
	            fOut.write("<?");
	            fOut.write(target);
	            if (data != null && data.length() > 0) {
	                fOut.write(' ');
	                fOut.write(data);
	            }
	            fOut.write("?>");
	            fOut.flush();
	        }
 		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}

	}
	
	
	public void setDocumentLocator(Locator locator) {
	}
	
	
	public void skippedEntity(String name) throws SAXException {
	}
	
	
	public void startDocument() throws SAXException {
        fElementDepth = 0;

        try {
	        if (!omitXMLDeclaration) {
	            fOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	            fOut.flush();
	        }
		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}

	}
	
	
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
        
		try {
			if (topElement == null) {
				topElement = localName;
			} else 	if (topElement.equals(localName)) {
				if (fElementDepth == 0)
					caller.processSaxItem(this, this.fOut, parameters);
			}
			
			insideElement = true;
			
			fElementDepth++;
	        fOut.write('<');
	        fOut.write(qName);
	        
	        //namespaces on top element
	        for (Iterator iter = namespaces.keySet().iterator(); iter.hasNext(); ) {
				String prefix = (String) iter.next();
				String uri = namespaces.get(prefix);
				fOut.write(" xmlns");
				if (!"XMLNS".equals(prefix)) {
					fOut.write(":"+prefix);
				}
				fOut.write("=\""+uri+"\"");
			}
			namespaces.clear();
			//write attributes
	        if (atts != null) {
	            //atts = sortAttributes(atts);
	            int len = atts.getLength();
	            for (int i = 0; i < len; i++) {
	                fOut.write(' ');
	                fOut.write(atts.getQName(i));
	                fOut.write("=\"");
	                normalizeAndPrint(atts.getValue(i));
	                fOut.write('"');
	            }
	        }
	        fOut.write('>');
	        fOut.flush();
 		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}

	}
	
	
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
//		System.out.println("START PREFIX MAPPING "+prefix+":"+uri);
		if ("".equals(prefix)) prefix = "XMLNS";
		namespaces.put(prefix, uri);
	}	
	
	
    /** Normalizes and prints the given string. */
    protected void normalizeAndPrint(String s) throws SAXException{

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            normalizeAndPrint(c);
        }

    } // normalizeAndPrint(String)

    /** Normalizes and prints the given array of characters. */
    protected void normalizeAndPrint(char[] ch, int offset, int length)  throws SAXException{
        for (int i = 0; i < length; i++) {
            normalizeAndPrint(ch[offset + i]);
        }
    } // normalizeAndPrint(char[],int,int)

    /** Normalizes and write the given character. */
    protected void normalizeAndPrint(char c) throws SAXException{

    	try {
	        switch (c) {
	            case '<': {
	                fOut.write("&lt;");
	                break;
	            }
	            case '>': {
	                fOut.write("&gt;");
	                break;
	            }
	            case '&': {
	                fOut.write("&amp;");
	                break;
	            }
	            case '"': {
	                fOut.write("&quot;");
	                break;
	            }
	            case '\r':
	            case '\t':
	            case '\n': {
	                if (fCanonical) {
	                    fOut.write("&#");
	                    fOut.write(Integer.toString(c));
	                    fOut.write(';');
	                    break;
	                } 
	                // else, default write char
	            }
	            default: {
	                fOut.write(c);
	            }
	        }
 		} catch (Exception e) {
 			throw new SAXException(e.getClass().getName()+": "+e.getMessage());
 		}

    } // normalizeAndPrint(char)
	
	
	
    
    /** Returns a sorted list of attributes. */
    protected Attributes sortAttributes(Attributes attrs) {

        AttributesImpl attributes = new AttributesImpl();

        int len = (attrs != null) ? attrs.getLength() : 0;
        for (int i = 0; i < len; i++) {
            String qName = attrs.getQName(i);
            int count = attributes.getLength();
            int j = 0;
            while (j < count) {
                if (qName.compareTo(attributes.getQName(j)) < 0) {
                    break;
                }
                j++;
            }
            attributes.setAttribute(
            		j, 
					attrs.getURI(i),
					attrs.getLocalName(i), 
					attrs.getQName(i),
					attrs.getType(i),
                    attrs.getValue(i));
        }

        return attributes;

    } // sortAttributes(AttributeList):AttributeList
	
	
	
 }


