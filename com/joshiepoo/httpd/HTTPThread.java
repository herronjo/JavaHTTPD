package com.joshiepoo.httpd;

import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.lang.Thread;
import java.lang.Exception;
import java.net.Socket;
import java.net.URLDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class HTTPThread implements Runnable {
    private String threadId;
	private Socket socket;
    private Thread t;
    private Map<String, Map<String, String>> options;
    private int maxBodySize;
	private final int HEADER = 0;
	private final int BODY = 1;
	private final int FINISHED = 2;
    //private final String[] METHODS = {"get", "head", "post", "put", "delete", "connect", "options", "trace", "patch"};
    private final String[] BODYMETHODS = {"post", "put", "patch"};

    public HTTPThread(String id, Socket sock, Map<String, Map<String, String>> poptions) {
        threadId = id;
		socket = sock;
        options = poptions;
        maxBodySize = Integer.parseInt(options.get("Global").get("maxbody"));
    }

    private static int indexOfString(String str, String[] arr) {
        int index = -1;
        for (int i = 0; i < arr.length && index == -1; i++) {
            if (arr[i].equals(str)) {
                index = i;
            }
        }
        return index;
    }

    private String readLine(InputStream instream) throws IOException, OutOfMemoryError {
        StringBuilder retStr = new StringBuilder();
        char in = 0;
        while (in != '\n' && instream.available() > 0) {
            in = (char) instream.read();
            retStr.append(in);
        }
        return retStr.toString().trim();
    }

    public void run() {
		try {
            InputStream instream = socket.getInputStream();
            OutputStream outstream = socket.getOutputStream();
			String[] firstLine = readLine(instream).split(" ");
			String method = firstLine[0].toLowerCase();
            //if (indexOfString(method, METHODS) > -1) {
            String path = URLDecoder.decode(firstLine[1], "UTF-8").split("\\?")[0];
            Map<String, String> queries = new HashMap<String, String>();
            if (firstLine[1].split("\\?").length > 1) {
                String[] queriestmp = firstLine[1].split("\\?")[1].split("&");
                for (int i = 0; i < queriestmp.length; i++) {
                    String[] qtmp = queriestmp[i].split("=");
                    String name = URLDecoder.decode(queriestmp[i].split("=")[0], "UTF-8");
                    String value = "";
                    if (qtmp.length > 1) {
                        value = URLDecoder.decode(queriestmp[i].split("=")[1], "UTF-8");
                    }
                    queries.put(name, value);
                }
            }
            int curMode = HEADER;
            Map<String, String> headers = new HashMap<String, String>();
            while (curMode == HEADER) {
                String line = readLine(instream);
                if (line.trim().length() == 0) {
                    curMode = BODY;
                } else {
                    String[] parts = line.split(": ");
                    String name = parts[0];
                    StringBuilder content = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        content.append(parts[i]);
                        content.append(": ");
                    }
                    if (content.length() > 0) content.deleteCharAt(content.length() - 1);
                    if (content.length() > 0) content.deleteCharAt(content.length() - 1);
                    headers.put(name.toLowerCase(), content.toString());
                }
            }
            ByteBuffer buffer = ByteBuffer.allocate(0);
            int contentLength;
            if (headers.containsKey("content-length")) {
                contentLength = Integer.parseInt(headers.get("content-length"));
                buffer = ByteBuffer.allocate(contentLength);
                byte[] byteArr = new byte[contentLength];
                instream.readNBytes(byteArr, 0, contentLength);
                buffer.put(byteArr);
                buffer.flip();
                curMode = FINISHED;
            } else if (headers.containsKey("transfer-encoding") && headers.get("transfer-encoding").equals("chunked")) {
                buffer = ByteBuffer.allocate(maxBodySize);
                while (curMode == BODY) {
                    int length = Integer.parseInt(readLine(instream));
                    if (length > 0) {
                        for (int i = 0; i < length; i++) {
                            buffer.put((byte) instream.read());
                        }
                        instream.read();
                        instream.read();
                    } else {
                        buffer.flip();
                        curMode = FINISHED;
                        readLine(instream);
                    }
                }
            } else if (indexOfString(method, BODYMETHODS) > -1) {
                errors.do400(outstream);
            }
            Request request = new Request(instream, outstream, method, path, queries, headers, buffer, socket);
            switch (method) {
                case "get":
                    Get.doRequest(request, options);
                    break;
                case "post":
                    Post.doRequest(request, options);
                    break;
                case "head":
                    Head.doRequest(request, options);
                    break;
                case "options":
                    Options.doRequest(request, options);
                    break;
                case "trace":
                    Trace.doRequest(request, options);
                    break;
                default:
                    EverythingElse.doRequest(request, options);
                    break;
            }
            /*} else {
                errors.do405(outstream);
            }*/
		} catch (Exception e) {
            e.printStackTrace();
		}
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
		if (t == null) {
			t = new Thread(this, threadId);
			t.start();
		}
    }
}