package com.amalto.xmlserver.interfaces;

import java.util.ArrayList;

public class WhereAnd extends WhereLogicOperator {

	public WhereAnd() {
		super(WhereLogicOperator.AND);
	}
	
	public WhereAnd(ArrayList<IWhereItem> whereItems) {
		super(WhereLogicOperator.AND, whereItems);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}

}
