package ru.track.prefork;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * - multithreaded +
 * - atomic counter +
 * - setName() +
 * - thread -> Worker +
 * - save threads
 * - broadcast (fail-safe)
 */
public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    public Server(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            byte[] buf = new byte[1024];
            int nRead = inputStream.read(buf);
            System.out.println(new String(buf, 0, nRead));
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}
