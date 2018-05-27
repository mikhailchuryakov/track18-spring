package ru.track.prefork;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//todo change exceptions
//todo use ID
//todo can't disconnect correctly

/**
 * - multithreaded +
 * - atomic counter +
 * - setName() +
 * - thread -> Worker +
 * - save threads
 * - broadcast (fail-safe)
 */
public class Server {
    private static Logger log = LoggerFactory.getLogger(Server.class);


    private AtomicInteger id = new AtomicInteger(0);
    private int port;
    private List<WorkingThread> workingThreadList = new LinkedList<>();

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();
            WorkingThread wt = new WorkingThread(socket, id.get());
            workingThreadList.add(wt);
            wt.setName(String.format("Client[%d]@%s:%d", id.get(), socket.getLocalAddress().toString().replace("/", ""), socket.getPort()));
            log.info("{} is accepted", wt.getName());
            id.incrementAndGet();
            wt.start();
        }
    }

    public class WorkingThread extends Thread {
        private Socket socket;
        private AtomicLong id;

        @Override
        public long getId() {
            return id.get();
        }

        ObjectInputStream in;
        ObjectOutputStream out;
        private String outputNameFormat = "Client@%s:%d>%s";

        WorkingThread(Socket socket, long id) throws IOException {
            this.socket = socket;
            this.id = new AtomicLong(id);
        }


        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (!socket.isClosed() || !isInterrupted()) {
                    Message msg = (Message) in.readObject();
                    if (msg == null) {
                        break;
                    }
                    if (msg.getTs() == 0 || msg.getData().equals("EXIT")) {
                        for (WorkingThread wt : workingThreadList) {
                            if (wt.getId() > Thread.currentThread().getId()) {
                                id.decrementAndGet();
                            }
                        }
                        socket.close();
                        System.out.println(Thread.currentThread().getName());
                        Thread.currentThread().join();
                        break;
                    }
                    String clientData = String.format(outputNameFormat, socket.getLocalAddress().toString().replace("/", ""), socket.getPort(), msg.getData());
                    System.out.println(clientData);

                    if (msg.getTs() == 1) {
                        msg.setData(clientData);
                    }
                    sendAll(msg.getData());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        private void sendAll(String buf) throws IOException {
            for (WorkingThread wt : workingThreadList) {
                if (!wt.equals(Thread.currentThread())) {
                    ObjectOutputStream os = wt.out;
                    Message msg = new Message(1, buf);

                    os.writeObject(msg);
                    os.flush();
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}
