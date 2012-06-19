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
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.Map;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class CommonUtilTest extends TestCase {
    
    public void testHandleProcessMessage() {
    	String outputMessage = "<report><message/></report>";
    	String language = "en";
    	Map<String, String> map = null;
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(map);
		assertNotNull(map.get("typeCode"));
		assertTrue(map.get("typeCode").equalsIgnoreCase(""));
		assertNull(map.get("message"));
		
		map.clear();
		outputMessage = "<report><message type=\"error\" /></report>";
    	language = "en";
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(map);
		assertNotNull(map.get("typeCode"));
		assertEquals("error", map.get("typeCode"));
		assertNull(map.get("message"));
		
		map.clear();
		outputMessage = "<report><message type=\"info\" >[en:message_en][fr:message_fr]</message></report>";
    	language = "en";
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(map);
		assertNotNull(map.get("typeCode"));
		assertEquals("info", map.get("typeCode"));
		assertNotNull(map.get("message"));
		assertEquals("message_en", map.get("message"));
		
		map.clear();
		outputMessage = "<report><message type=\"info\" >Test_Message</message></report>";
    	language = "en";
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(map);
		assertNotNull(map.get("typeCode"));
		assertEquals("info", map.get("typeCode"));
		assertNotNull(map.get("message"));
		assertEquals("Test_Message", map.get("message"));
		
		map.clear();
		outputMessage = "<report><message type=\"info\" ><subMessage>test_subMessage</subMessage></message></report>";
    	language = "en";
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(map);
		assertNotNull(map.get("typeCode"));
		assertEquals("info", map.get("typeCode"));
		assertNotNull(map.get("message"));
		assertEquals("test_subMessage", map.get("message"));
		
		map.clear();
		outputMessage = "<report><message type=\"info\" ><subMessage><subsub>subsub<subsub></subMessage></message></report>";
    	language = "en";
    	try {
			map = CommonUtil.handleProcessMessage(outputMessage, language);
		} catch (Exception e) {
			assertNotNull(e);
		}
    }
}