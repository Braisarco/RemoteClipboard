package es.uvigo.tfg.remoteClipboard.server.servers;

import java.awt.datatransfer.Transferable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.tfg.remoteClipboard.clipboardUtiles.ClipboardListener;
import es.uvigo.tfg.remoteClipboard.clipboardUtiles.Subscriber;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.ServiceThread;
import es.uvigo.tfg.remoteClipboard.services.AppManager;

public class ServerExecutorService extends Thread implements Server, Subscriber {
    private boolean active;
    private ExecutorService threadPool = Executors.newFixedThreadPool(50);
    private ClipboardListener cbListener = new ClipboardListener();
    private AppManager manager;

    public ServerExecutorService(AppManager manager){
        this.manager = manager;
        this.cbListener.subsribe(this);
        this.cbListener.start();
        this.active = true;
        this.run();
    }

    /*
     * Creates a thread pool and assignates 1 thread to each connection
     */
    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(10101)) {


            while (active) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ServiceThread(clientSocket));
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

    }

    @Override
    public void execute(Transferable clipboardContent){
        System.out.println("SERVER: New content recived, broadcasting...");
        broadCast(clipboardContent);
    }
}