package com.joshiepoo.httpd;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class errors {
	private static void print(OutputStream outstream, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            outstream.write(str.charAt(i));
        }
    }

	private static String getDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(cal.getTime());
	}

	public static void do400(OutputStream outstream) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 400: Bad Request</title></head><body><h1>HTTP 400: Bad Request</h1>Your client sent a bad request.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 400 Bad Request\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		print(outstream, error);
		outstream.close();
	}

	public static void do400(OutputStream outstream, boolean headless) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 400: Bad Request</title></head><body><h1>HTTP 400: Bad Request</h1>Your client sent a bad request.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 400 Bad Request\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		if (!headless) {
			print(outstream, error);
		}
		outstream.close();
	}

	public static void do404(OutputStream outstream) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 404: Not Found</title></head><body><h1>HTTP 404: Not Found</h1>The server could not find the file you requested.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 404 Not Found\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		print(outstream, error);
		outstream.close();
	}

	public static void do404(OutputStream outstream, boolean headless) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 404: Not Found</title></head><body><h1>HTTP 404: Not Found</h1>The server could not find the file you requested.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 404 Not Found\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		if (!headless) {
			print(outstream, error);
		}
		outstream.close();
	}

	public static void do405(OutputStream outstream) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 405: Method Not Allowed</title></head><body><h1>HTTP 405: Method Not Allowed</h1>Your client tried to use a disallowed or nonexistant method.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 405 Method Not Allowed\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		print(outstream, error);
		outstream.close();
	}

	public static void do405(OutputStream outstream, boolean headless) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 405: Method Not Allowed</title></head><body><h1>HTTP 405: Method Not Allowed</h1>Your client tried to use a disallowed or nonexistant method.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 405 Method Not Allowed\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		if (!headless) {
			print(outstream, error);
		}
		outstream.close();
	}

	public static void do500(OutputStream outstream) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 500: Server Error</title></head><body><h1>HTTP 500: Server Error</h1>The server encountered an error while processing your request.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 500 Server Error\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		print(outstream, error);
		outstream.close();
	}

	public static void do500(OutputStream outstream, boolean headless) throws IOException {
		String error = "<!DOCTYPE html><html><head><title>HTTP 500: Server Error</title></head><body><h1>HTTP 500: Server Error</h1>The server encountered an error while processing your request.<hr/><i>JavaHTTPD</i></body></html>";
		print(outstream, "HTTP/1.1 500 Server Error\r\n");
		print(outstream, "Content-Type: text/html\r\n");
		print(outstream, "Content-Length: " + error.length() + "\r\n");
		print(outstream, "Date: " + getDate() + "\r\n");
		print(outstream, "Server: JavaHTTPD\r\n\r\n");
		if (!headless) {
			print(outstream, error);
		}
		outstream.close();
	}
}
