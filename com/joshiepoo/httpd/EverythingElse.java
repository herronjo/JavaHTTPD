package com.joshiepoo.httpd;

import java.util.Map;
import java.util.Map.Entry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class EverythingElse {
	private static void print(OutputStream outstream, String str) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			outstream.write(str.charAt(i));
		}
	}

	public static void doRequest(Request request, Map<String, Map<String, String>> options) throws IOException {
		OutputStream outstream = request.outstream;
		String method = request.method;
		String path = request.path;
		Map<String, String> queries = request.queries;
		Map<String, String> headers = request.headers;
		ByteBuffer body = request.body;
		String host = "localhost";
		if (headers.containsKey("host")) {
			host = headers.get("host");
		}
		if (!options.containsKey(host)) {
			host = "localhost";
		}
		boolean doCGI = false;
		if (options.get(host).containsKey("enablecgi")) {
			doCGI = options.get(host).get("enablecgi").equals("true");
		}
		if (doCGI && (path.startsWith("/cgi-bin/") || path.contains(".cgi"))) {
			String[] filepath = path.split("/");
			String filename = options.get(host).get("webroot");
			String scriptname = "";
			String extrapath = "";
			boolean exists = false;
			for (int i = 0; i < filepath.length; i++) {
				if (!exists) {
					filename += filepath[i];
					scriptname += filepath[i];
					File cgifile = new File(filename);
					if (cgifile.isDirectory()) {
						filename += "/";
						scriptname += "/";
					} else if (cgifile.isFile()) {
						exists = true;
					} else if (!cgifile.exists()) {
						i = filepath.length;
						exists = false;
					}
				} else {
					extrapath += "/" + filepath[i];
				}
			}
			if (exists) {
				File cgifile = new File(filename);
				if (cgifile.canExecute()) {
					print(outstream, "HTTP/1.1 200 OK\r\n");
					ProcessBuilder pb = new ProcessBuilder(cgifile.getAbsolutePath());
					Map<String, String> env = pb.environment();
					env.put("SERVER_SOFTWARE", "JavaHTTPD/1.0");
					env.put("SERVER_NAME", host);
					env.put("GATEWAY_INTERFACE", "CGI/1.1");
					env.put("SERVER_PROTOCOL", "HTTP/1.1");
					env.put("SERVER_PORT", Integer.toString(request.socket.getLocalPort()));
					env.put("REQUEST_METHOD", method.toUpperCase());
					if (extrapath.length() > 0) {
						env.put("PATH_INFO", extrapath);
						env.put("FULL_PATH", (new File(options.get(host).get("webroot")).getAbsolutePath()) + extrapath);
					}
					env.put("SCRIPT_NAME", scriptname);
					StringBuilder qstring = new StringBuilder();
					for (Entry<String, String> entry : queries.entrySet()) {
						qstring.append(entry.getKey() + "=" + entry.getValue() + "&");
					}
					if (qstring.length() > 0) qstring.deleteCharAt(qstring.length() - 1);
					env.put("QUERY_STRING", qstring.toString());
					String remoteAddr = "";
					SocketAddress socketAddress = request.socket.getRemoteSocketAddress();
					remoteAddr = (((InetSocketAddress)socketAddress).getAddress()).toString();
					env.put("REMOTE_ADDR", remoteAddr);
					if (headers.containsKey("content-type")) {
						env.put("CONTENT_TYPE", headers.get("content-type"));
					}
					if (headers.containsKey("content-length")) {
						env.put("CONTENT_LENGTH", headers.get("content-length"));
					}
					for (Entry<String, String> entry : headers.entrySet()) {
						env.put("HTTP_"+entry.getKey().toUpperCase().replace("-", "_"), entry.getValue());
					}
					byte[] inarr = new byte[body.remaining()];
					body.get(inarr);
					Process p = pb.start();
					OutputStream execInStream = p.getOutputStream();
					execInStream.write(inarr);
					execInStream.close();
					InputStream execStream = p.getInputStream();
					while (p.isAlive()) {
						if (execStream.available() > 0) {
							while (execStream.available() > 0) {
								int bsize = 536;
								if (bsize > execStream.available()) {
									bsize = execStream.available();
								}
								byte[] buf = new byte[bsize];
								execStream.read(buf);
								outstream.write(buf);
							}
						}
					}
					if (execStream.available() > 0) {
						while (execStream.available() > 0) {
							int bsize = 536;
							if (bsize > execStream.available()) {
								bsize = execStream.available();
							}
							byte[] buf = new byte[bsize];
							execStream.read(buf);
							outstream.write(buf);
						}
					}
					outstream.close();
				} else {
					errors.do500(outstream);
				}
			} else {
				errors.do405(outstream);
			}
		} else {
			errors.do405(outstream);
		}
	}
}
