// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.plugin.base.mimecontent.ejb;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.ejb.SessionBean;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2CtrlBean;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.plugin.base.mimecontent.CompiledParameters;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * 
 * @author Fliu
 * 
 * @ejb.bean name="MIMEContentPlugin" display-name="Name for MIMEContentPlugin"
 * description="Description for MIMEContentPlugin" local-jndi-name = "amalto/local/transformer/plugin/mime"
 * type="Stateless" view-type="local"
 * local-business-interface="com.amalto.core.objects.transformers.v2.util.TransformerPluginV2LocalInterface"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * 
 * 
 */

public class MimeContentPluginBean extends TransformerPluginV2CtrlBean implements SessionBean {

    /**
     * 
     */
    private static final long serialVersionUID = 6476837219022228630L;

    public static final String PARAMETERS = "com.amalto.core.plugin.base.mime.parameters";

    private static final String INPUT_TEXT = "raw_text";

    private static final String OUTPUT_TEXT = "mime_text";

    private static final String OUTPUT_TYPE = "mime_type";

    private static final String MIME_TYPE_HTML = "HTML";

    private static final String MIME_TYPE_PDF = "PDF";

    private static final String MIME_TYPE_RTF = "RTF";

    private static final String METHOD_TYPES = "(" + MIME_TYPE_HTML + "|" + MIME_TYPE_PDF + "|" + MIME_TYPE_RTF + ")";

    public MimeContentPluginBean() {
        super();
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getJNDIName() throws XtentisException {
        return "amalto/local/transformer/plugin/mime";
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getDescription(String twoLettersLanguageCode) throws XtentisException {
        String description = "";
        if (twoLettersLanguageCode.toLowerCase().equals("en")) {
            description = "This is a plugin used for generating MIME type content";
        } else {
            description = "Unsupported language! ";
        }
        return description;
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getDocumentation(String twoLettersLanguageCode) throws XtentisException {
        return "The mime plugin can generate mime content. \n" + "\n" + "\n" + "Parameters\n"
                + "   mimetype [mandatory]: specify the incoming mime content type" + "\n" + "Example" + "\n" + "   <parameters>"
                + "\n" + "       <mimetype>pdf</mimetype>" + "\n</parameters>\n" + "Notes for Plugin Developers: " + "\n"
                + "       empty";
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(String twoLettersLanguageCode)
            throws XtentisException {
        ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();

        TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
        descriptor.setVariableName(INPUT_TEXT);
        descriptor.setContentTypesRegex(new ArrayList<Pattern>(Arrays.asList(new Pattern[] { Pattern.compile("text/.*") })));
        HashMap<String, String> descriptions = new HashMap<String, String>();
        descriptions.put("en", "The text content to enter");
        descriptor.setDescriptions(descriptions);
        descriptor.setMandatory(true);
        descriptor.setPossibleValuesRegex(null);
        inputDescriptors.add(descriptor);
        return inputDescriptors;

    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(String twoLettersLanguageCode)
            throws XtentisException {
        ArrayList<TransformerPluginVariableDescriptor> outputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();

        // descriptor
        TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
        descriptor.setVariableName(OUTPUT_TEXT);
        descriptor.setContentTypesRegex(new ArrayList<Pattern>(Arrays.asList(new Pattern[] { Pattern.compile("text/.*") })));
        HashMap<String, String> descriptions = new HashMap<String, String>();
        descriptions.put("en", "The mime content");
        descriptor.setDescriptions(descriptions);
        descriptor.setMandatory(true);
        descriptor.setPossibleValuesRegex(null);
        outputDescriptors.add(descriptor);

        return outputDescriptors;
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getParametersSchema() throws XtentisException {
        // Is this feature in use now?
        return "<xsd:schema"
                + "       elementFormDefault='unqualified'"
                + "       xmlns:xsd='http://www.w3.org/2001/XMLSchema'"
                + ">"
                + "<xsd:element name='parameters'>"
                + "           <xsd:complexType >"
                + "               <xsd:sequence>"
                + "                   <xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='mimetype' type='xsd:string'/>"
                + "               </xsd:sequence>" + "           </xsd:complexType>" + "</xsd:element>" + "</xsd:schema>";

    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String compileParameters(String parameters) throws XtentisException {
        if (parameters == null || parameters.length() == 0)
            return "";
        CompiledParameters compiled = new CompiledParameters();

        try {
            Document params = Util.parse(parameters);

            String mimetype = Util.getFirstTextNode(params, "//mimetype");
            if (mimetype == null) {
                String err = "The mime type of the MIME Transformer Plugin cannot be empty";
                org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
                throw new XtentisException(err);
            } else if (!mimetype.trim().toUpperCase().matches(METHOD_TYPES)) {
                String err = "The format of the method parameter of the Codec Transformer Plugin is unavailable";
                org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
                throw new XtentisException(err);
            }

            compiled.setMimeType(mimetype);
            return compiled.serialize();

        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to serialize the configuration of the Codec Plugin" + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public void init(TransformerPluginContext context, String compiledParameters) throws XtentisException {

        try {

            // parse parameters
            CompiledParameters parameters = CompiledParameters.deserialize(compiledParameters);

            context.put(PARAMETERS, parameters);

        } catch (Exception e) {
            String err = "Could not init the Codec plugin:" + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            throw new XtentisException(e);
        }

    }

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public void execute(TransformerPluginContext context) throws XtentisException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("execute() mime");

        CompiledParameters parameters = (CompiledParameters) context.get(PARAMETERS);
        TypedContent textTC = (TypedContent) context.get(INPUT_TEXT);
        com.lowagie.text.Document document = null;
        FileInputStream in = null;

        try {
            String charset = Util.extractCharset(textTC.getContentType());
            String rawText = new String(textTC.getContentBytes(), charset);
            String tmpdir = System.getProperty("java.io.tmpdir");
            document = new com.lowagie.text.Document();
            String mimetype = parameters.getMimeType();
            String contextType = "text/html";
            File mimeFile = null;
            if (mimetype.equalsIgnoreCase(MIME_TYPE_PDF)) {
                mimeFile = new File(tmpdir, "mimeOutput.pdf");
                mimeFile.delete();
                mimeFile.createNewFile();
                PdfWriter pdf = PdfWriter.getInstance(document, new FileOutputStream(mimeFile));
                contextType = "application/pdf";
            } else if (mimetype.equalsIgnoreCase(MIME_TYPE_RTF)) {
                mimeFile = new File(tmpdir, "mimeOutput.rtf");
                mimeFile.delete();
                mimeFile.createNewFile();
                RtfWriter2 pdf = RtfWriter2.getInstance(document, new FileOutputStream(mimeFile));
                contextType = "application/rtf";
            } else {
                // we will have to create a html file as the solution if a mime type other than pdf and rtf comes in
                mimeFile = new File(tmpdir, "mimeOutput.html");
                mimeFile.delete();
                mimeFile.createNewFile();
                HtmlWriter html = HtmlWriter.getInstance(document, new FileOutputStream(mimeFile));
            }
            document.open();
            document.add(new Paragraph("The output report of " + context.getTransformerV2POJOPK(), FontFactory.getFont(
                    FontFactory.COURIER, 17, Font.BOLD, new Color(0, 0, 0))));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(rawText, FontFactory.getFont(FontFactory.COURIER, 14, Font.NORMAL, new Color(0, 0, 0))));
            document.close();
            in = new FileInputStream(mimeFile);
            byte[] bytes = IOUtils.toByteArray(in);
            context.put(OUTPUT_TEXT, new TypedContent(bytes, contextType + "; charset=utf-8"));
            // call the callback content is ready
            context.getPluginCallBack().contentIsReady(context);

        } catch (XtentisException xe) {
            throw (xe);
        } catch (Exception e) {
            String err = "Could not execute the codec plugin " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            throw new XtentisException(e);
        }
    }
}
