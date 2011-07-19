package com.amalto.webapp.core.gwt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class GwtWebContext {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final HttpServlet servlet;

    public GwtWebContext(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet) {
        this.request = request;
        this.response = response;
        this.servlet = servlet;
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public HttpServlet getServlet() {
        return servlet;
    }
}
