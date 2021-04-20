import java.util.Random;
import java.lang.StringBuilder;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import com.joshiepoo.httpd.HTTPThread;

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
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(80);
			while (true) {
				Socket socket = serverSocket.accept();
				HTTPThread thread = new HTTPThread(genID(25), socket, 2000000000);
				thread.start();
			}
		} catch (IOException e) {
			System.err.println("Failed to bind to port 80");
			e.printStackTrace();
		}
    }
}