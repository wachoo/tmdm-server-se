package com.amalto.core.util;

/**
 * Thanks you SUN! The jwsdp generates a Byte[] instead of a byte[] from a list of xsd:byte
 */

import java.io.IOException;
import java.io.InputStream;

public class ByteObjectArrayInputStream extends InputStream {

	private Byte[] bytes;
	private int mark=0;
	
	public ByteObjectArrayInputStream(Byte[] bytes) {
		super();
		this.bytes = bytes;
	}


	@Override
	public int read() throws IOException {
		if (mark==bytes.length-1) return -1;
		return bytes[mark++].intValue();
	}

}
