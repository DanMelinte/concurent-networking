package com.example;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
//    private int port;
//    private Socket socket = null;
//    private ServerSocket server = null;
//    private DataInputStream input = null;

//    public Server(int port) {
//        this.port = port;
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        InetAddress address = InetAddress.getLocalHost();
//        System.out.println("Inet addr: " + address);
//
//        InetAddress webAddress = InetAddress.getByName("google.com");
//        System.out.println("Web address: " + webAddress);
//
//        InetAddress[] webAddresses = InetAddress.getAllByName("google.com");
//        for (int i = 0; i < webAddresses.length; i++) {
//            System.out.println("Web address: " + webAddresses[i]);
//        }
//
//        URL url = new URL("https://write.geeksforgeeks.org/post/3038131");
//        url.getProtocol();
//        url.getHost();
//        url.getFile();
//        System.out.println(url.getDefaultPort());
//        url.getPath();
//        String hostName = address.getHostName();
//        System.out.println("Host Name: " + hostName);

/*        Server server1 = new Server(6000);
        Server server2 = new Server(6001);

        Thread thread1 = new Thread(server1);
        Thread thread2 = new Thread(server2);

        thread1.start();
        thread2.start();*/

//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        ServerImpl server = new ServerImpl(6001);
//        executorService.execute(server);

        if (args.length != 2) {
            System.out.println("No arguments provided. [-nThread -port]");
            System.exit(1);
        }

        int nThreads = 0;
        int port = 0;

        try {
            nThreads = Integer.parseInt(args[0]);
            port = Integer.parseInt(args[1]);
            if(nThreads == 0){
                System.out.println("server needs >= 1 threads");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Incorrect format (insert server thread capacity)");
        }



        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + " with " + nThreads + " thread capacity");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                executorService.execute(new ServerImpl(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}

class ServerImpl implements Runnable {

    private final Socket clientSocket;
    public ServerImpl(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
//        try (ServerSocket server = new ServerSocket(port)) {
//            System.out.println("Server Started on port " + server.getLocalPort());
//            System.out.println("Waiting for clients ...");
//
//
//            while (true) {
//                System.out.println("Thread: " + Thread.currentThread().getName());
//                Socket socket = server.accept(); //waits there
//                System.out.println("Client accepted");
////                new Thread(() -> handleClient(socket)).start();
//                handleClient(socket);
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println("Handling client: " + clientSocket.getInetAddress());
        handleClient(clientSocket);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {

        try (DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
            String line = "";

            while (!line.equals("End")) {
                try {
                    line = input.readUTF();
                    System.out.println("ThreadHandle: " + Thread.currentThread().getName() + " Revieved from client : " + line);
                } catch (IOException e) {
                    System.out.println("client interrupt connection");
                }
            }
            System.out.println("Client disconnected " + Thread.currentThread().getName() + " is free");

            socket.close();
        } catch (IOException e) {
            System.out.println("Internal server error");
        }
    }
}
