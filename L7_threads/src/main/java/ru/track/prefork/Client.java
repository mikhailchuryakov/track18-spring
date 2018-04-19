package ru.track.prefork;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    static Logger log = LoggerFactory.getLogger(Client.class);

    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void loop() throws Exception {
        Socket socket = new Socket(host, port);
        final OutputStream out = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();
            if ("q".equals(line)) {
                break;
            }
            out.write(line.getBytes());
            out.flush();
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
