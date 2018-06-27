package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.List;
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
        ListenThread lt = new ListenThread(socket, wt);
        wt.setListen(lt);
        wt.start();
        lt.start();

        wt.join();
        lt.join();

        socket.close();
    }


    public class WriteThread extends Thread {
        private Socket socket;
        private ListenThread listen;

        WriteThread(Socket socket) {
            this.socket = socket;
        }

        public void setListen(ListenThread listen) {
            this.listen = listen;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                Scanner scanner = new Scanner(System.in);

                while (!isInterrupted()) {
                    String line = scanner.nextLine();

                    if (line.equals("EXIT")) {
                        break;
                    }
                    try {
                        Message msg = new Message(new Date().getTime(), line);
                        out.writeObject(msg);
                        out.flush();
                    } catch (SocketException e) {
                        break;
                    }
                }
                listen.interrupt();
            } catch (SocketException ignored) {
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public class ListenThread extends Thread {
        private Socket socket;
        private WriteThread writer;

        ListenThread(Socket socket, WriteThread writer) {
            this.socket = socket;
            this.writer = writer;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                while (!isInterrupted()) {
                    try {
                        Message msg = (Message) in.readObject();
                        if (msg == null) {
                            break;
                        }
                        System.out.println(msg.getData());
                    } catch (SocketException | EOFException e) {
                        break;
                    }
                }

                System.out.println("DISCONNECT. Press ENTER.");
                writer.interrupt();
            } catch (SocketException ignored) {
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
