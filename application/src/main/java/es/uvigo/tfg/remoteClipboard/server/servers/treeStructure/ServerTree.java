package es.uvigo.tfg.remoteClipboard.server.servers.treeStructure;

import es.uvigo.tfg.remoteClipboard.server.ServiceThread;
import org.apache.commons.lang3.StringUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerTree implements Runnable {
    private String leftLeafIP;
    private String rightLeafIP;
    private List<ServiceThread> connections;

    public ServerTree(){
        this.connections = new ArrayList<>(3);
    }

    @Override
    public void run(){
        System.out.println("SERVER: Server running");
        try (ServerSocket serverSocket = new ServerSocket(10101)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER: Conexion detected");
                for(ServiceThread thread : this.connections){

                }
            }
        } catch (Exception e) {
            System.err.println("Error creating server socket");
            e.printStackTrace();
        }
    }

    private void addNode(Socket clientSocket){
        if(StringUtils.isNotBlank(this.rightLeafIP)){
            this.rightLeafIP = clientSocket.getInetAddress().getHostAddress();
        } else if(StringUtils.isNotBlank((leftLeafIP))){
            this.leftLeafIP = clientSocket.getInetAddress().getHostAddress();
        }else{
            sendNode(clientSocket);
        }
    }

    private void sendNode(Socket clientSocket){

    }
}
