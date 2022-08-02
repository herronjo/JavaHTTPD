import java.util.Map;
import java.util.Random;
import java.lang.StringBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import com.joshiepoo.httpd.HTTPThread;
import com.joshiepoo.infparser.InfParser;

public class JavaHTTPD {
	private static String genID(int length) {
		Random rand = new Random();
		StringBuilder randStr = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char randChar = (char)(rand.nextInt(26) + 'a');
			randStr.append(randChar);
		}
		return randStr.toString();
	}
    public static void main(String[] args) {
		try {
			Map<String, Map<String, String>> options = InfParser.parse("options.inf");
			Map<String, Map<String, String>> mimes = InfParser.parse("mimes.inf");
			options.put("ExtToMimes", mimes.get("ExtToMimes"));
			options.put("MimesToExt", mimes.get("MimesToExt"));
			ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(80);
				System.out.println("Listening on " + serverSocket.getInetAddress().toString().split("/")[1] + ":" + serverSocket.getLocalPort());
				while (true) {
					Socket socket = serverSocket.accept();
					HTTPThread thread = new HTTPThread(genID(25), socket, options);
					thread.start();
				}
			} catch (IOException e) {
				System.out.println("Failed to bind to port 80");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Failed to open options.inf");
			e.printStackTrace();
		}
    }
}