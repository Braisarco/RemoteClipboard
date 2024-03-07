package org.tfg.server.servers;

import java.awt.datatransfer.Transferable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.tfg.server.Server;
import org.tfg.server.ServiceThread;
import org.tfg.clipboardUtiles.ClipboardListener;
import org.tfg.clipboardUtiles.Subscriber;
import org.tfg.services.ClipboardManager;

public class ServerThreadPool extends Thread implements Server, Subscriber {
    private boolean active;
    private ClipboardManager clipboardManager;
    private List<ServiceThread> threadPool = new ArrayList<>();
    private ClipboardListener cbListener = new ClipboardListener();

    public ServerThreadPool(ClipboardManager manager){
        this.clipboardManager = manager;
        this.cbListener.subsribe(this);
        this.cbListener.start();
        this.active = true;
        for(int i = 0; i<50; i++){
            this.threadPool.add(new ServiceThread(this.clipboardManager));
        }
    }

    /*
     * Creates a thread pool and assignates 1 thread to each connection
     */
    @Override
    public void run(){
        System.out.println("SERVER: Server running");
        try (ServerSocket serverSocket = new ServerSocket(10101)) {
            while (active) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER: Conexion detected");
                for (ServiceThread thread: this.threadPool){
                    if (!thread.isAlive()){
                        System.out.println("SERVER: Thread avaliable");
                        this.clipboardManager.createClipboard(thread.getId());
                        thread.setSocket(clientSocket);
                        thread.start();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating server socket");
            e.printStackTrace();
        }
    }

    /*
     * Turns the server off
     */
    public void turnOff(){
        this.active = false;
    }

    public void broadCast(Transferable content){

        System.out.println("SERVER: Sending content to all servers");
        for (ServiceThread thread: this.threadPool){
            if (thread.isAlive()){
                thread.sendContent(content);
            }
        }
    }

    @Override
    public void execute(Transferable clipboardContent){
        System.out.println("SERVER: New content recived, broadcasting...");
        broadCast(clipboardContent);
    }
}