package es.uvigo.tfg.remoteClipboard.server.servers;

import java.awt.datatransfer.Transferable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.tfg.remoteClipboard.clipboardUtiles.ClipboardListener;
import es.uvigo.tfg.remoteClipboard.clipboardUtiles.Subscriber;
import es.uvigo.tfg.remoteClipboard.services.AppManager;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.ServiceThread;

public class ServerThreadPool extends Thread implements Server, Subscriber {
    private boolean active;
    private AppManager manager;
    private List<ServiceThread> threadPool = new ArrayList<>();
    private ClipboardListener cbListener = new ClipboardListener();

    public ServerThreadPool(AppManager manager){
        this.manager = manager;
        this.cbListener.subsribe(this);
        this.cbListener.start();
        for(int i = 0; i<50; i++){
            this.threadPool.add(new ServiceThread(this.manager));
        }
    }

    /*

     * * Creates a thread pool and assignates 1 thread to each connection
     */
    @Override
    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(10101)) {
                Socket clientSocket = serverSocket.accept();
                for (ServiceThread thread: this.threadPool){
                    if (!thread.isAlive()){
                        thread.setSocket(clientSocket);
                        thread.start();
                        break;
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
    public void shutDown(){
        for(ServiceThread thread : this.threadPool){
            try{
                thread.join();
            }catch (InterruptedException e){
                System.err.println("SERVER: Error while shutting down");
                e.printStackTrace();
            }
        }
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