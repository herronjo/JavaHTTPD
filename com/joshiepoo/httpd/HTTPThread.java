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
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class HTTPThread implements Runnable {
    private String threadId;
	private Socket socket;
    private Thread t;
    private int maxBodySize;
	private final int HEADER = 0;
	private final int BODY = 1;
	private final int FINISHED = 2;
    private final String[] METHODS = {"get", "head", "post", "put", "delete", "connect", "options", "trace", "patch"};
    private final String[] BODYMETHODS = {"post", "put", "patch"};
    private final String[] NOBODYMETHODS = {"trace"};

    public HTTPThread(String id, Socket sock, int maxBody) {
        threadId = id;
		socket = sock;
        maxBodySize = maxBody;
        System.out.println("Creating thread " + threadId + " to handle the HTTP request");
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
		System.out.println("Running " +  threadId );
		try {
            InputStream instream = socket.getInputStream();
            OutputStream outstream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outstream);
			String[] firstLine = readLine(instream).split(" ");
			String method = firstLine[0].toLowerCase();
            if (indexOfString(method, METHODS) > -1) {
                String path = URLDecoder.decode(firstLine[1], "UTF-8");
                int curMode = HEADER;
                Map<String, String> headers = new HashMap<String, String>();
                System.out.println(threadId + " - Begin header");
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
                        content.deleteCharAt(content.length() - 1);
                        content.deleteCharAt(content.length() - 1);
                        headers.put(name.toLowerCase(), content.toString());
                    }
                }
                System.out.println(threadId + " - Header finished");
                System.out.println(threadId + " - Begin body");
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
                    String error = "<!DOCTYPE html><html><head><title>HTTP 400: Bad Request</title></head><body><h1>HTTP 400: Bad Request</h1>Your client sent a bad request.<hr/><i>JavaHTTPD</i></body></html>";
                    out.print("HTTP/1.1 400 Bad Request\r\n");
                    out.print("Content-Type: text/html\r\n");
                    out.print("Content-Length: " + error.length() + "\r\n");
                    out.print("Server: JavaHTTPD\r\n\r\n");
                    out.print(error);
                    out.close();
                }
                System.out.println(threadId + " - Body finished");
                String error = "<!DOCTYPE html><html><head><title>HTTP 200: OK</title></head><body><h1>HTTP 200: OK</h1>Your client sent a valid request, this just isn't finished yet.<br/>Header info:<br/><br/>";
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    error += entry.getKey() + ": " + entry.getValue() + "<br/>";
                }
                error += "<br/>Body info:<br/><br/>";
                while (buffer.hasRemaining()) {
                    error = error.concat(Character.toString((char)buffer.get()));
                }
                error += "<hr/><i>JavaHTTPD</i></body></html>";
                out.print("HTTP/1.1 200 OK\r\n");
                out.print("Content-Type: text/html\r\n");
                out.print("Content-Length: " + error.length() + "\r\n");
                out.print("Server: JavaHTTPD\r\n\r\n");
                out.print(error);
                out.close();
            } else {
                String error = "<!DOCTYPE html><html><head><title>HTTP 405: Method Not Allowed</title></head><body><h1>HTTP 405: Method Not Allowed</h1>Your client tried to use a disallowed or nonexistant method.<hr/><i>JavaHTTPD</i></body></html>";
                    out.print("HTTP/1.1 405 Method Not Allowed\r\n");
                    out.print("Content-Type: text/html\r\n");
                    out.print("Content-Length: " + error.length() + "\r\n");
                    out.print("Server: JavaHTTPD\r\n\r\n");
                    out.print(error);
                    out.close();
            }
		} catch (Exception e) {
			System.out.println("Thread " + threadId + " crashed");
            e.printStackTrace();
		}
		System.out.println("Thread " + threadId + " exiting");
    }

    public void start() {
		System.out.println("Starting " + threadId);
		if (t == null) {
			t = new Thread(this, threadId);
			t.start();
		}
    }
}