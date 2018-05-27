package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    private int port;
    private String host;

    private Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    private void loop() throws Exception {
        Socket socket = new Socket(host, port);
        log.info("Success connection to {}", host);

        WriteThread wt = new WriteThread(socket);
        ListenThread lt = new ListenThread(socket);
        wt.start();
        lt.start();
    }


    public class WriteThread extends Thread {
        private ObjectOutputStream out;
        private Socket socket;

        WriteThread(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Scanner scanner = new Scanner(System.in);
                while (!isInterrupted()) {
                    String line = scanner.nextLine();

                    Message msg = new Message(1, line);
                    out.writeObject(msg);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ListenThread extends Thread {
        private ObjectInputStream in;
        private Socket socket;

        ListenThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                in = new ObjectInputStream(socket.getInputStream());
                while (!isInterrupted()) {
                    Message msg = (Message) in.readObject();
                    if (msg == null) {
                        break;
                    }
                    System.out.println(msg.getData());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
