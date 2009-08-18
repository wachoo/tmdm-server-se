package com.amalto.xmlserver.interfaces;

import java.util.ArrayList;
import java.util.Iterator;

public class WhereLogicOperator implements IWhereItem {

	public final static int AND = 1;
	public final static int OR = 2;
	
	ArrayList<IWhereItem> whereItems = new ArrayList<IWhereItem>();
	int type = AND;
	

	public WhereLogicOperator(int type) {
		super();
		this.type = type;
	}
	
	public WhereLogicOperator(int type, ArrayList<IWhereItem> whereElements) {
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
	
	public ArrayList<IWhereItem> getItems() {
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
