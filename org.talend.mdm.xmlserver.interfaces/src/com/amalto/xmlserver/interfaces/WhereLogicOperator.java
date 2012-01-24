// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.xmlserver.interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WhereLogicOperator implements IWhereItem {

	public final static int AND = 1;

	public final static int OR = 2;

    private final List<IWhereItem> whereItems = new ArrayList<IWhereItem>();

    int type = AND;
	

	public WhereLogicOperator(int type) {
		super();
		this.type = type;
	}
	
	public WhereLogicOperator(int type, List<IWhereItem> whereElements) {
		super();
		this.type = type;
		whereItems.addAll(whereElements);
	}

	public void add(IWhereItem whereItem) {
		whereItems.add(whereItem);
	}
	
	public void addAll(ArrayList<IWhereItem> whereElements) {
		whereItems.addAll(whereElements);
	}
	
	public void remove(IWhereItem whereItem) {
		whereItems.remove(whereItem);
	}
	
	public int getSize() {
		return whereItems.size();
	}
	
	public List<IWhereItem> getItems() {
		return whereItems;
	}
	
	public IWhereItem getItem(int index) {
		return whereItems.get(index);
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s="";
		if (AND == getType()) {
			for (Iterator<IWhereItem> iter = getItems().iterator(); iter.hasNext(); ) {
				s+="".equals(s)?"(":" and ";
				s+= (iter.next());
			}
		} else if (OR == getType()) {
			for (Iterator<IWhereItem> iter = getItems().iterator(); iter.hasNext(); ) {
				s+="".equals(s)?"(":" and ";
				s+= (iter.next());
			}
		}
		s+=")";
		return s;
	}
	
	
}
