package com.amalto.xmldb.opt;

public class NewReturn implements GenAble{
    
	private boolean withTotalCountOnFirstRow;
	public NewReturn(boolean withTotalCountOnFirstRow) {
		this.withTotalCountOnFirstRow=withTotalCountOnFirstRow;
	}
	
	@Override
	public String gen() {
		String query=" return";
		if (withTotalCountOnFirstRow) {
    		query +=
    			" insert-before($_page_,0,<totalCount>{count($_leres_)}</totalCount>) ";
		} else {
    		query +=
    			" $_page_ ";
		}
		return query;
	}

}
