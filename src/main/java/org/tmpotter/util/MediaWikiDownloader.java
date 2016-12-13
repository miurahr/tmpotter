/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT project
 * 
 *  Copyright (C) 2007 - Zoltan Bartko
 *                2011 Alex Buloichik
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Import pages from MediaWiki.
 * 
 * @author Hiroshi Miura
 */
public class MediaWikiDownloader {

  protected static final String CHARSET_MARK = "charset=";

  /**
   * Gets mediawiki wiki-code data from remote server. The get strategy is
   * determined by the url format.
   * 
   * @param remoteUrl
   *            string representation of well-formed URL of wikipage to be
   *            retrieved
   * @param projectdir
   *            string representation of path to the project-dir where the
   *            file should be saved.
   */
  public static void download(String remoteUrl, String projectdir) {
    try {
      String joined = null; // contains edited url
      // contains a useful page name which we can use  as our filename
      String name = null;

      if (remoteUrl.indexOf("index.php?title=") > 0) {
        // We're directly calling the mediawiki index.php script
        String[] splitted = remoteUrl.split("index.php\\?title=");
        String splitTerm = splitted[splitted.length - 1];
        name = splitTerm;
        splitTerm = splitTerm.replaceAll(" ", "_");
        // s=URLEncoder.encode(s, "UTF-8"); // breaks previously
        // correctly encoded page names
        splitted[splitted.length - 1] = splitTerm;
        joined = StringUtil.joinString("index.php?title=", splitted);
        joined = joined + "&action=raw";
      } else {
        // assume script is behind some sort
        // of url-rewriting
        String[] splitted = remoteUrl.split("/");
        String splitTerm = splitted[splitted.length - 1];
        name = splitTerm;
        splitTerm = splitTerm.replaceAll(" ", "_");
        // s=URLEncoder.encode(s, "UTF-8");
        splitted[splitted.length - 1] = splitTerm;
        joined = StringUtil.joinString("/", splitted);
        joined = joined + "?action=raw";
      }
      String page = getTextFromUrl(joined);
      Utilities.saveUtf8(projectdir, name + ".UTF8", page);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Obtain UTF-8 format text from remote URL.
   * 
   * @param target
   *            String representation of well-formed URL.
   * @throws IOException  when connection error.
   */
  public static String getTextFromUrl(String target) throws IOException {
    StringBuilder page = new StringBuilder();
    URL url = new URL(target);
    InputStream in = url.openStream();
    byte[] readByte = new byte[4096];
    for (int n; (n = in.read(readByte)) != -1;) {
      page.append(new String(readByte, 0, n, "UTF-8"));
    }
    return page.toString();
  }

  /**
   * Method call without additional headers.
   * 
   * @param address to access
   * @param params for access
   * @return  result
   * @throws java.io.IOException when connection error
   */
  public static String post(String address, Map<String, String> params) throws IOException {
    return post(address, params, null);
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
      StringBuilder sb = new StringBuilder();
      sb.append(address).append('?');
      boolean next = false;
      for (Map.Entry<String, String> p : params.entrySet()) {
        if (next) {
          sb.append('&');
        } else {
          next = true;
        }
        sb.append(p.getKey());
        sb.append('=');
        sb.append(URLEncoder.encode(p.getValue(), "UTF-8"));
      }
      url = sb.toString();
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
  @SuppressWarnings("serial")
  public static class ResponseError extends IOException {
    public final int code;
    public final String message;

    /**
     * Response error representing class.
     * 
     * @param conn connection
     * @throws IOException when connection error
     */
    public ResponseError(HttpURLConnection conn) throws IOException {
      super(conn.getResponseCode() + ": " + conn.getResponseMessage());
      code = conn.getResponseCode();
      message = conn.getResponseMessage();
    }
  }

  /**
   * Copy between stream.
   * 
   * @param src copy from 
   * @param dest copy to 
   * @throws IOException when stream has a problem
   */
  public static void streamcopy(InputStream src, OutputStream dest) throws IOException {
    byte[] rb = new byte[512];
    int readBytes;
    while ((readBytes = src.read(rb)) > 0) {
      dest.write(rb, 0, readBytes);
    }
  }

}
