package com.amalto.xmldb;

import org.apache.commons.lang.StringEscapeUtils;

public class ExistQueryBuilder extends QueryBuilder{

	@Override
	public String getFullTextQueryString(String queryStr) {
		return "ft:query(.,\""
		+ StringEscapeUtils.escapeXml(queryStr
				.trim()) + "\")";
	}
}
