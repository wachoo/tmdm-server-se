package org.talend.mdm.webapp.browserecords.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CSVBufferedReader extends BufferedReader {

    char quotes;
    public CSVBufferedReader(Reader in, char quotes) {
        super(in);
        this.quotes = quotes;
    }

    public String readLine() throws IOException {
        return readln(false);
    }

    private String readln(boolean inStr) throws IOException {
        String line = super.readLine();
        if (line == null)
            return null;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == quotes) {
                inStr = !inStr;
            }
        }
        if (!inStr) {
            return line;
        }
        return line + "\n" + readln(inStr); //$NON-NLS-1$
    }
}
