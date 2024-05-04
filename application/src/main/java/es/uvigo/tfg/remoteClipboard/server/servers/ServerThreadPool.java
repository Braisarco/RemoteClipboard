package es.uvigo.tfg.remoteClipboard.server.servers;

import java.awt.datatransfer.Transferable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.tfg.remoteClipboard.clipboardUtiles.ClipboardListener;
import es.uvigo.tfg.remoteClipboard.clipboardUtiles.Subscriber;
import es.uvigo.tfg.remoteClipboard.net.NetworksManager;
import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.ServiceThread;

public class ServerThreadPool extends Thread implements Server, Subscriber {
    private boolean active;
    private ClipboardManager clipboardManager;
    private NetworksManager networksManager;
    private List<ServiceThread> threadPool = new ArrayList<>();
    private ClipboardListener cbListener = new ClipboardListener();

    public ServerThreadPool(ClipboardManager manager, NetworksManager netManager){
        this.networksManager = netManager;
        this.clipboardManager = manager;
        this.cbListener.subsribe(this);
        this.cbListener.start();
        this.active = true;
        for(int i = 0; i<50; i++){
            this.threadPool.add(new ServiceThread(clipboardManager, networksManager));
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
                        this.clipboardManager.createClipboard(InetAddress.getLocalHost().getHostName(), "name");
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

    /*
    public void sendConnectionRequest(String ip){
        try(Socket serverSocket = new Socket(ip, 10101)){
            try(){

            }catch(Exception e){

            }
        }catch(Exception e){
            System.err.println("CLIENT: Error while creating socket in connection request");
        }
    }
    */
}