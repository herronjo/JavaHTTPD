package com.joshiepoo.httpd;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

public class Request{
    public InputStream instream;
    public OutputStream outstream;
    public String method;
    public String path;
    public Map<String, String> queries;
    public Map<String, String> headers;
    public ByteBuffer body;
    public Socket socket;

    public Request(InputStream iinstream, OutputStream ioutstream, String imethod, String ipath, Map<String, String> iqueries, Map<String, String> iheaders, ByteBuffer buffer, Socket sock) {
        instream = iinstream;
        outstream = ioutstream;
        method = imethod;
        path = ipath;
        queries = iqueries;
        headers = iheaders;
        body = buffer;
        socket = sock;
    }

    public InputStream getInputStream() {
        return instream;
    }

    public void setInputStream(InputStream iinstream) {
        instream = iinstream;
    }

    public OutputStream getOutputStream() {
        return outstream;
    }

    public void setOutputStream(OutputStream ioutstream) {
        outstream = ioutstream;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String imethod) {
        method = imethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String ipath) {
        path = ipath;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> iqueries) {
        queries = iqueries;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> iheaders) {
        headers = iheaders;
    }

    public ByteBuffer getBody() {
        return body;
    }

    public void setBody(ByteBuffer buffer) {
        body = buffer;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket sock) {
        socket = sock;
    }
}
