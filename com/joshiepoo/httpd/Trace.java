package com.joshiepoo.httpd;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class Trace {
	private static void print(OutputStream outstream, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            outstream.write(str.charAt(i));
        }
    }

    public static void doRequest(Request request, Map<String, Map<String, String>> options) throws IOException {
        OutputStream outstream = request.outstream;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateString = dateFormat.format(cal.getTime());
		String error = "<!DOCTYPE html><html><head><title>HTTP 405: Method Not Allowed</title></head><body><h1>HTTP 405: Method Not Allowed</h1>Your client tried to use a disallowed or nonexistant method.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 405 Method Not Allowed\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + dateString + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		print(outstream, error);
		outstream.close();
    }
}
