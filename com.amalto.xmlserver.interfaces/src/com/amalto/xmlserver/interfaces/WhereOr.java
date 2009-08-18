package com.amalto.xmlserver.interfaces;

import java.util.ArrayList;

public class WhereOr extends WhereLogicOperator {

	public WhereOr() {
		super(WhereLogicOperator.OR);
	}
	
	public WhereOr(ArrayList<IWhereItem> whereItems) {
		super(WhereLogicOperator.OR, whereItems);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}

}
