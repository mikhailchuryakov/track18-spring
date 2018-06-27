
package ru.track.prefork;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

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
    private static Logger log = LoggerFactory.getLogger(Server.class);


    private AtomicLong id = new AtomicLong(0);
    private int port;
    private List<WorkingThread> workingThreadList = new LinkedList<>();

    private void changeId() {
        id.decrementAndGet();
    }

    private Server(int port) {
        this.port = port;
    }

    private void serve() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();

            AdminThread adminThread = new AdminThread();
            adminThread.start();

            WorkingThread wt = new WorkingThread(socket, id.get());
            workingThreadList.add(wt);
            wt.setName(String.format("Client[%d]@%s:%d", id.getAndIncrement(), socket.getLocalAddress().toString().replace("/", ""), socket.getPort()));
            log.info("{} is accepted", wt.getName());

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

                while (!isInterrupted()) {
                    try {
                        Message msg = (Message) in.readObject();
                        if (msg == null) {
                            System.out.println("Message is null");
                            break;
                        }
                        if (msg.getTs() == 0 || msg.getData().equals("EXIT")) {
                            for (WorkingThread wt : workingThreadList) {
                                if (wt.getId() > Thread.currentThread().getId()) {
                                    id.decrementAndGet();
                                }
                            }
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
                    } catch (SocketException | EOFException e) {
                        break;
                    }
                }

                for (WorkingThread wt : workingThreadList) {
                    if (wt.getId() > getId()) {
                        wt.setName(wt.getName().replace(String.format("[%s]", wt.getId()), String.format("[%s]", wt.id.decrementAndGet())));
                    }
                }
                changeId();
                workingThreadList.remove(Thread.currentThread());
                log.info(String.format("Client@%s%s was DISCONNECTED", socket.getLocalAddress(), socket.getPort()));
                sendAll(String.format("Client@%s%s was DISCONNECTED", socket.getLocalAddress(), socket.getPort()));

                out.close();
                socket.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException();
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

    public class AdminThread extends Thread {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);

            while (!isInterrupted()) {
                String line = scanner.nextLine();

                if (line.equals("list")) {
                    for (WorkingThread wt : workingThreadList) {
                        System.out.println(String.format(">>>%s", wt.getName()));
                    }
                }

                if (line.contains("drop")) {
                    String[] split = line.split(" ");
                    int dropId = -1;
                    if (split.length == 2) {
                        try {
                            dropId = Integer.parseInt(split[1]);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                    if (split[0].equals("drop") && dropId > -1 && dropId < workingThreadList.size()) {
                        try {
                            workingThreadList.get(dropId).socket.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}
