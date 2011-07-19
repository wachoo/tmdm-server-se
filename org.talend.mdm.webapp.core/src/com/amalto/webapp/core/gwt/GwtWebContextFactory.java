package com.amalto.webapp.core.gwt;


public class GwtWebContextFactory {

    private static ThreadLocal<GwtWebContext> threadGwtWebContext = new ThreadLocal<GwtWebContext>();

    public static GwtWebContext get() {
        return threadGwtWebContext.get();
    }

    static void set(GwtWebContext webContext) {
        threadGwtWebContext.set(webContext);
    }
}
