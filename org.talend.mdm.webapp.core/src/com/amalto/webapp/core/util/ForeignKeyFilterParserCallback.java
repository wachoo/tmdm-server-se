package com.amalto.webapp.core.util;

import com.amalto.webapp.util.webservices.WSWhereCondition;

public interface ForeignKeyFilterParserCallback {
	
	void parse(WSWhereCondition wc);

}
