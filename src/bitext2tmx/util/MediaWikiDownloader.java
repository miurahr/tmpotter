/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al
#                2015 Hiroshi Miura
#
#  Includes code: Copyright (C) 2002-2006 Keith Godfrey et al.
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#######################################################################
#
# 
# This file is stolen from OmegaT project.
# and distributed under the GPLv3, or later.
#
#######################################################################
*/

/** Original copyright notices **/
/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2007 Kim Bruning
               2010 Alex Buloichik, Didier Briel, Rashid Umarov
               2011 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package bitext2tmx.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Import pages from MediaWiki
 * 
 * @author Hiroshi Miura
 */
public class MediaWikiDownloader {
    protected static final String CHARSET_MARK = "charset=";

    /**
     * Gets mediawiki wiki-code data from remote server. The get strategy is
     * determined by the url format.
     * 
     * @param remote_url
     *            string representation of well-formed URL of wikipage to be
     *            retrieved
     * @param projectdir
     *            string representation of path to the project-dir where the
     *            file should be saved.
     */
    public static void download(String remote_url, String projectdir) {
        try {
            String joined = null; // contains edited url
            String name = null; // contains a useful page name which we can use
                                // as our filename
            if (remote_url.indexOf("index.php?title=") > 0) {
                // We're directly calling the mediawiki index.php script
                String[] splitted = remote_url.split("index.php\\?title=");
                String s = splitted[splitted.length - 1];
                name = s;
                s = s.replaceAll(" ", "_");
                // s=URLEncoder.encode(s, "UTF-8"); // breaks previously
                // correctly encoded page names
                splitted[splitted.length - 1] = s;
                joined = Utilities.joinString("index.php?title=", splitted);
                joined = joined + "&action=raw";
            } else {
                // assume script is behind some sort
                // of url-rewriting
                String[] splitted = remote_url.split("/");
                String s = splitted[splitted.length - 1];
                name = s;
                s = s.replaceAll(" ", "_");
                // s=URLEncoder.encode(s, "UTF-8");
                splitted[splitted.length - 1] = s;
                joined = Utilities.joinString("/", splitted);
                joined = joined + "?action=raw";
            }
            String page = getTextFromURL(joined);
            Utilities.saveUTF8(projectdir, name + ".UTF8", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Obtain UTF-8 format text from remote URL.
     * 
     * @param target
     *            String representation of well-formed URL.
     * @throws IOException 
     */
    public static String getTextFromURL(String target) throws IOException {
        StringBuilder page = new StringBuilder();
        URL url = new URL(target);
        InputStream in = url.openStream();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            page.append(new String(b, 0, n, "UTF-8"));
        }
        return page.toString();
    }

    /**
     * Method call without additional headers.
     */
    public static String post(String address, Map<String, String> params) throws IOException {
        return post(address, params, null);
    }

    /**
     * Get data from the remote URL.
     * 
     * @param address
     *            address to post
     * @param params
     *            parameters
     * @param additionalHeaders
     *            additional headers for request, can be null
     * @return sever output
     */
    public static String get(String address, Map<String, String> params,
            Map<String, String> additionalHeaders) throws IOException {
        String url;
        if (params == null || params.isEmpty()) {
            url = address;
        } else {
            StringBuilder s = new StringBuilder();
            s.append(address).append('?');
            boolean next=false;
            for (Map.Entry<String, String> p : params.entrySet()) {
                if (next) {
                    s.append('&');
                }else {
                    next=true;
                }
                s.append(p.getKey());
                s.append('=');
                s.append(URLEncoder.encode(p.getValue(), "UTF-8"));
            }
            url = s.toString();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod("GET");
            if (additionalHeaders != null) {
                for (Map.Entry<String, String> en : additionalHeaders.entrySet()) {
                    conn.setRequestProperty(en.getKey(), en.getValue());
                }
            }

            conn.setDoOutput(true);

            return getStringContent(conn);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Post data to the remote URL.
     * 
     * @param address
     *            address to post
     * @param params
     *            parameters
     * @param additionalHeaders
     *            additional headers for request, can be null
     * @return sever output
     */
    public static String post(String address, Map<String, String> params,
            Map<String, String> additionalHeaders) throws IOException {
        URL url = new URL(address);

        ByteArrayOutputStream pout = new ByteArrayOutputStream();
        for (Map.Entry<String, String> p : params.entrySet()) {
            if (pout.size() > 0) {
                pout.write('&');
            }
            pout.write(p.getKey().getBytes("UTF-8"));
            pout.write('=');
            pout.write(URLEncoder.encode(p.getValue(), "UTF-8").getBytes("UTF-8"));
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(pout.size()));
            if (additionalHeaders != null) {
                for (Map.Entry<String, String> en : additionalHeaders.entrySet()) {
                    conn.setRequestProperty(en.getKey(), en.getValue());
                }
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream cout = conn.getOutputStream();
            cout.write(pout.toByteArray());
            cout.flush();

            return getStringContent(conn);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Parse response as string.
     */
    private static String getStringContent(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new ResponseError(conn);
        }
        String contentType = conn.getHeaderField("Content-Type");
        int cp = contentType != null ? contentType.indexOf(CHARSET_MARK) : -1;
        String charset = cp >= 0 ? contentType.substring(cp + CHARSET_MARK.length()) : "ISO8859-1";
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        InputStream in = conn.getInputStream();
        try {
            streamcopy(in, res);
        } finally {
            in.close();
        }
        return new String(res.toByteArray(), charset);
    }

    /**
     * HTTP response error storage.
     */
    public static class ResponseError extends IOException {
        public final int code;
        public final String message;

        public ResponseError(HttpURLConnection conn) throws IOException {
            super(conn.getResponseCode() + ": " + conn.getResponseMessage());
            code = conn.getResponseCode();
            message = conn.getResponseMessage();
        }
    }
   
    
    public static void streamcopy(InputStream src, OutputStream dest) throws IOException {
        byte[] b = new byte[512];
        int readBytes;
        while ((readBytes = src.read(b)) > 0) {
            dest.write(b, 0, readBytes);
        }
    }

}
