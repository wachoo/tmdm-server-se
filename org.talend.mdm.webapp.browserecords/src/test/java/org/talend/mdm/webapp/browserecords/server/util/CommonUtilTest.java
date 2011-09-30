package org.talend.mdm.webapp.browserecords.server.util;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.client.model.Criteria;
import org.talend.mdm.webapp.browserecords.client.model.SimpleCriterion;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;

import com.amalto.webapp.util.webservices.WSWhereItem;

public class CommonUtilTest extends TestCase {

	public void testJoinStrings() {
		ArrayList testee=new ArrayList();
		testee.add("NJ01");
		testee.add("#@$.");
		testee.add("\\/.");
		assertEquals(CommonUtil.joinStrings(testee, "."),"NJ01.#@$..\\/.");
	}

	public void testBuildWhereItems() throws Exception {
		String inputString="Agency/Id EQUALS *";
		WSWhereItem wi=CommonUtil.buildWhereItem(inputString);
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getLeftPath(),"Agency/Id");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getRightValueOrPath(),"*");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getOperator().toString(),"EQUALS");
		
	}
	
	public void testBuildWhereItemsByCriteria() throws Exception{
		Criteria input=new SimpleCriterion("Agent/Id","EQUALS","*");
		WSWhereItem wi=CommonUtil.buildWhereItemsByCriteria(input);
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getLeftPath(),"Agent/Id");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getRightValueOrPath(),"*");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getOperator().toString(),"EQUALS");
		Criteria input2=new SimpleCriterion("Agentsss../Id","EQUALS","*");
		//try to search one not existing
		WSWhereItem wi2=CommonUtil.buildWhereItemsByCriteria(input2);
		assertNotNull(wi2);
		assertEquals(wi2.getWhereAnd().getWhereItems()[0].getWhereCondition().getLeftPath(),"Agentsss../Id");
		assertEquals(wi2.getWhereAnd().getWhereItems()[0].getWhereCondition().getRightValueOrPath(),"*");
		assertEquals(wi2.getWhereAnd().getWhereItems()[0].getWhereCondition().getOperator().toString(),"EQUALS");
		
	}
	public void testGetDefaultTreeModel()
	{
		try
		{
		assertNull(CommonUtil.getDefaultTreeModel(new SimpleTypeModel(),"en"));
		}catch(NullPointerException e)
		{
			
		}catch(Exception e)
		{
			fail("the model should not exist when provided as null value");
		}
	}

}
