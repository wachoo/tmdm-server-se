/*
 * Distributed as part of ssim v.0.6.0
 * 
 * Copyright (C) 2003 Machinery For Change, Inc.
 * 
 * Author: Steve Waldman <swaldman@mchange.com>
 * 
 * This package is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License version 2, as published by the Free Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this software; see the file LICENSE. If
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package com.mchange.v2.ssim;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import talend.ext.images.server.ImageLoadFrontFilter;

import com.mchange.v1.io.InputStreamUtils;
import com.mchange.v2.net.LocalHostManager;
import com.mchange.v2.util.PatternReplacementMap;

public class SsimServlet extends HttpServlet {

    private static final long serialVersionUID = 8159775007521681831L;

    public final static String PATTERN_REPLACEMENT_MAP_APPKEY = "com_mchange_v2_ssim_SsimServlet__patternReplacementMap"; //$NON-NLS-1$

    private final Logger LOG = Logger.getLogger(SsimServlet.class);

    final static int DFLT_MAX_WIDTH = 2000;

    final static int DFLT_MAX_HEIGHT = 2000;

    final static int DFLT_CACHE_SIZE = 50; // 50MB

    final static int DFLT_CULL_DELAY = 300; // try to cull every five minutes

    final static int DFLT_MAX_SIMULTANEOUS_SCALES = 3;

    static LocalHostManager lhm;

    ServletContext sc;

    File cacheDir;

    ImageFinder imf;

    int scale_counter = 0;

    PatternReplacementMap patternReplacementMap = null;

    // MT: unchanging after init()
    int max_width = DFLT_MAX_WIDTH;

    int max_height = DFLT_MAX_HEIGHT;

    int max_simultaneous_scales = DFLT_MAX_SIMULTANEOUS_SCALES;

    boolean open_relay = false;

    boolean never_relay = false;

    String myDomain = null;

    String[] allowDomains = null; // all lower case

    String baseUrl = null;

    String baseResourcePath = null;

    // MODIFIED BY STARKEY
    boolean useDBBackup = false;

    String dbDelegateClass = null;

    InetAddress localHostAddr; // only used for access checks, iff open_relay stays false

    // MT: end unchanging after init()

    @Override
    public void init() throws ServletException {
        this.sc = this.getServletContext();
        String cacheDirStr = this.getInitParameter("cacheDir"); //$NON-NLS-1$
        String maxWidthStr = this.getInitParameter("maxWidth"); //$NON-NLS-1$
        String maxHeightStr = this.getInitParameter("maxHeight"); //$NON-NLS-1$
        String allowDomainsStr = this.getInitParameter("allowDomains"); //$NON-NLS-1$
        String baseUrlStr = this.getInitParameter("baseUrl"); //$NON-NLS-1$
        String baseResourcePathStr = this.getInitParameter("baseResourcePath"); //$NON-NLS-1$
        String cacheSizeStr = this.getInitParameter("cacheSize"); //$NON-NLS-1$
        String cullDelayStr = this.getInitParameter("cullDelay"); //$NON-NLS-1$
        String maxConcurrencyStr = this.getInitParameter("maxConcurrency"); //$NON-NLS-1$

        useDBBackup = Boolean.valueOf(this.getInitParameter("use-db-backup")); //$NON-NLS-1$
        dbDelegateClass = this.getInitParameter("db-delegate-class"); //$NON-NLS-1$

        if (cacheDirStr != null) {
            String jbossServerDir = System.getProperty("jboss.server.home.dir"); //$NON-NLS-1$
            if (jbossServerDir != null) {
                cacheDir = new File(jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                        + "mdm_resources" + File.separator + "scale_tmp"); //$NON-NLS-1$ //$NON-NLS-2$
                cacheDir.mkdirs();
            }
        } else {
            cacheDir = (File) sc.getAttribute("javax.servlet.context.tempdir"); //$NON-NLS-1$
        }

        if (!cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new UnavailableException("Cache directory: " + cacheDir //$NON-NLS-1$
                    + " does not exist, is not a directory, or is not writable!"); //$NON-NLS-1$
        } else {
            LOG.info("Using cache directory: " + cacheDir); //$NON-NLS-1$
        }

        if (baseUrlStr != null && baseResourcePathStr != null) {
            throw new UnavailableException("It is illegal to specify both a baseUrl and a baseResourcePath Servlet init param."); //$NON-NLS-1$       
        } else if (baseUrlStr != null) {
            baseUrl = baseUrlStr.trim();
            if (baseUrl.endsWith("/")) { //$NON-NLS-1$
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        } else if (baseResourcePathStr != null) {
            baseResourcePath = baseResourcePathStr.trim();
        }

        try {
            if (maxWidthStr != null) {
                max_width = Integer.parseInt(maxWidthStr);
            }
        } catch (NumberFormatException e) {
            throw new UnavailableException("Could not parse maxWidth init param: " + maxWidthStr); //$NON-NLS-1$ 
        }

        if (max_width <= 0) {
            max_width = Integer.MAX_VALUE;
        }

        try {
            if (maxHeightStr != null) {
                max_height = Integer.parseInt(maxHeightStr);
            }
        } catch (NumberFormatException e) {
            throw new UnavailableException("Could not parse maxHeight init param: " + maxHeightStr); //$NON-NLS-1$ 
        }

        if (max_height <= 0) {
            max_height = Integer.MAX_VALUE;
        }

        if (allowDomainsStr != null) {
            allowDomains = allowDomainsStr.trim().toLowerCase().split("\\s*,\\s*"); //$NON-NLS-1$
        } else {
            allowDomains = new String[0];
        }

        for (int i = 0; i < allowDomains.length; ++i) {
            if ("all".equalsIgnoreCase(allowDomains[i])) { //$NON-NLS-1$
                open_relay = true;
                break;
            } else if ("none".equalsIgnoreCase(allowDomains[i])) { //$NON-NLS-1$
                never_relay = true;
                break;
            }
        }

        if (open_relay && never_relay) {
            throw new UnavailableException("allowDomains cannot include both 'all' and 'none'."); //$NON-NLS-1$
        }

        if (!open_relay) {
            try {
                localHostAddr = InetAddress.getLocalHost();
                String localHostName = localHostAddr.getCanonicalHostName();
                myDomain = subdomainFromHost(localHostName);
                if (myDomain == null) {
                    myDomain = "local"; //$NON-NLS-1$
                }
            } catch (UnknownHostException e) {
                LOG.error("Couldn't look up localhost to permit within-domain relaying!", e); //$NON-NLS-1$
                myDomain = null;
            }
        }

        try {
            if (maxConcurrencyStr != null) {
                max_simultaneous_scales = Integer.parseInt(maxConcurrencyStr);
            }
        } catch (NumberFormatException e) {
            throw new UnavailableException("Could not parse maxConcurrency init param: " + maxConcurrencyStr); //$NON-NLS-1$ 
        }

        int cache_size = DFLT_CACHE_SIZE;
        int cull_delay = DFLT_CULL_DELAY;
        try {
            if (cacheSizeStr != null) {
                cache_size = Integer.parseInt(cacheSizeStr);
            }
        } catch (NumberFormatException e) {
            throw new UnavailableException("Could not parse cacheSize init param: " + cacheSizeStr); //$NON-NLS-1$
        }

        try {
            if (cullDelayStr != null) {
                cull_delay = Integer.parseInt(cullDelayStr);
            }
        } catch (NumberFormatException e) {
            throw new UnavailableException("Could not parse cullDelay init param: " + cullDelayStr); //$NON-NLS-1$
        }

        imf = new MyImageFinder(cache_size, cull_delay);

        patternReplacementMap = (PatternReplacementMap) sc.getAttribute(SsimServlet.PATTERN_REPLACEMENT_MAP_APPKEY);
        if (patternReplacementMap == null) {
            patternReplacementMap = new PatternReplacementMap();
            sc.setAttribute(PATTERN_REPLACEMENT_MAP_APPKEY, patternReplacementMap);
        }

        if (patternReplacementMap != null) {
            LOG.info("Trusted PatternReplacementMap installed in application -- takes precedence over any baseURL " //$NON-NLS-1$
                    + " or beseResourcePath set, and no security checks will be performed on replaced URLs!"); //$NON-NLS-1$
        }

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        ImageLoadFrontFilter imageLoadFrontFilter = new ImageLoadFrontFilter();
        if (!imageLoadFrontFilter.doFilter(getServletContext(), req, res, useDBBackup, dbDelegateClass)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Image file has not been found! "); //$NON-NLS-1$
            return;
        }

        String widthStr = req.getParameter("width"); //$NON-NLS-1$
        String heightStr = req.getParameter("height"); //$NON-NLS-1$
        String mimeType = req.getParameter("mimeType"); //$NON-NLS-1$
        String imageUrlStr = req.getParameter("imageUrl"); //$NON-NLS-1$
        String preserveAspectRatioStr = req.getParameter("preserveAspectRatio"); //$NON-NLS-1$

        int width;
        int height;
        boolean preserve_aspect_ratio;

        try {
            width = (widthStr != null ? Integer.parseInt(widthStr) : -1);
        } catch (Exception e) {
            width = -1;
        }

        try {
            height = (heightStr != null ? Integer.parseInt(heightStr) : -1);
        } catch (Exception e) {
            height = -1;
        }

        if (width > max_width || height > max_height) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Dimensions exceed maximum [max_width: " + max_width + ", max_height: " + max_height + ']'); //$NON-NLS-1$//$NON-NLS-2$
            return;
        }

        String servletPath = imageLoadFrontFilter.getServletPath();
        String uid = null;
        boolean base_url_host = false;
        boolean explicitly_replaced_pattern = false;

        if (patternReplacementMap != null) {
            uid = patternReplacementMap.attemptReplace(servletPath);
        }

        if (uid != null) {
            explicitly_replaced_pattern = true;
        } else {
            if (imageUrlStr == null) {
                if (baseUrl != null) {
                    if (considerAbsoluteUrl(baseUrl)) {
                        uid = baseUrl + servletPath;
                        base_url_host = true;
                    } else {
                        StringBuffer sb = req.getRequestURL();
                        int doubleslash_index = sb.indexOf("//"); //$NON-NLS-1$
                        int path_slash_index = sb.indexOf("/", doubleslash_index + 2); //$NON-NLS-1$
                        uid = sb.substring(0, path_slash_index) + servletPath;
                    }
                } else if (baseResourcePath != null) {
                    uid = baseResourcePath + servletPath;
                } else {
                    uid = servletPath;
                }
            } else {
                if (never_relay) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "This Servlet is configured not to allow specification of 'imageURL' in the request."); //$NON-NLS-1$
                    return;
                } else {
                    uid = imageUrlStr.trim();
                }
            }
        }

        if (!open_relay && !explicitly_replaced_pattern && considerAbsoluteUrl(uid) && !base_url_host
                && !checkAccessibleHost(uid, req.getHeader("Host"))) { //$NON-NLS-1$
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forwarding items from the URL " + uid + " is not permitted."); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        if (mimeType != null) {
            mimeType = mimeType.trim();
        }

        if (preserveAspectRatioStr != null) {
            preserveAspectRatioStr = preserveAspectRatioStr.trim();
        }

        preserve_aspect_ratio = Boolean.valueOf(preserveAspectRatioStr).booleanValue();

        try {
            ImageData data = null;
            try {
                enterScale();
                data = imf.find(uid, mimeType, width, height, preserve_aspect_ratio);
            } finally {
                exitScale();
            }

            res.setContentType(data.getMimeType());
            int cl = data.getContentLength();
            if (cl >= 0) {
                res.setContentLength(cl);
            }

            InputStream is = null;
            try {
                is = data.getInputStream();
                OutputStream os = res.getOutputStream();
                for (int b = is.read(); b >= 0; b = is.read()) {
                    os.write(b);
                }
                os.flush();
            } finally {
                InputStreamUtils.attemptClose(is);
            }
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (SsimException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            imf.close();
        } catch (SsimException e) {
            // Walker
            e.printStackTrace();
        }
    }

    private static boolean considerAbsoluteUrl(String uid) {
        return (uid.indexOf(':') > 0);
    }

    private boolean checkAccessibleHost(String uid, String reqHost) throws MalformedURLException, SocketException,
            ServletException {
        URL absUrl = new URL(uid);
        String host = absUrl.getHost().toLowerCase();

        if (host.equals("localhost") || host.equals("127.0.0.1")) {  //$NON-NLS-1$//$NON-NLS-2$
            return true;
        }

        if (myDomain != null && host.endsWith(myDomain)) {
            return true;
        }

        if (reqHost != null) {
            if (!isValidToLocalHost(reqHost)) {
                throw new ServletException(
                        "Invalid request to virtual host '" + reqHost + "' which is not a valid name for server!");//$NON-NLS-1$ //$NON-NLS-2$
            }

            if (reqHost.equals(host)) {
                return true;
            }

            String reqsubdomain = subdomainFromHost(reqHost);
            if (host.endsWith(reqsubdomain)) {
                return true;
            }
        }

        for (int i = 0, len = allowDomains.length; i < len; ++i) {
            if (host.endsWith(allowDomains[i])) {
                return true;
            }
        }

        try {
            if (localHostAddr.equals(InetAddress.getByName(host))) {
                return true;
            }
        } catch (UnknownHostException e) {
            LOG.error("Denied serving image from unknown host: " + host, e); //$NON-NLS-1$
        }
        return false;
    }

    private synchronized void enterScale() throws InterruptedException {
        while (scale_counter == max_simultaneous_scales) {
            this.wait();
        }
        ++scale_counter;
    }

    private synchronized void exitScale() {
        --scale_counter;
        this.notifyAll();
    }

    class MyImageFinder extends AbstractImageFinder {

        MyImageFinder(int max_size, int cull_delay) {
            super(new DirectoryBasedPersistentStore(cacheDir, max_size, cull_delay));
        }

        @Override
        protected URL urlForUid(final String uid) throws Exception {
            if (considerAbsoluteUrl(uid)) {
                return new URL(uid);
            } else {
                return sc.getResource(uid);
            }
        }

        @Override
        protected boolean cacheUnmodified(String uid) throws Exception {
            return considerAbsoluteUrl(uid) && (!uid.startsWith("file:")); //$NON-NLS-1$
        }
    }

    static String subdomainFromHost(String fqhostname) {
        int first_dot = fqhostname.indexOf('.');
        if (first_dot > 0) {
            return fqhostname.substring(first_dot + 1);
        } else {
            return null;
        }
    }

    private static synchronized boolean isValidToLocalHost(String name) throws SocketException {
        if (lhm == null) {
            lhm = new LocalHostManager();
        }
        return lhm.isLocalHostName(name);
    }
}