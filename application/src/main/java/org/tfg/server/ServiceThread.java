package org.tfg.server;

import org.tfg.CustomTransferable;
import org.tfg.services.ClipboardManager;

import java.awt.datatransfer.Transferable;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServiceThread extends Thread{
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ClipboardManager clipboardManager;

    public ServiceThread(ClipboardManager manager){
        this.clipboardManager = manager;
    }

    public ServiceThread(Socket newClientSocket){
        System.out.println("SERVICE THREAD: Creating new Service Thread");
        this.clientSocket = newClientSocket;
    }

    @Override
    public void run() {
        try (Socket socket = this.clientSocket) {
            output = new ObjectOutputStream(socket.getOutputStream());
            while (true) {}
        } catch (Exception e) {
            System.err.println("Error getting the client socket");
            e.printStackTrace();
        }
    }

    public void setSocket(Socket newClientSocket){
        System.out.println("SERVICE THREAD: Assigning socket to thread");
        this.clientSocket = newClientSocket;
    }

    public void sendContent(Transferable transferedObject){
        try{
            System.out.println("SERVICE THREAD: Sending content");
            this.clipboardManager.addLocalContent(new CustomTransferable(transferedObject));
            this.output.writeObject(new CustomTransferable(transferedObject));

        } catch (Exception e) {
            System.err.println("SERVER THREAD: Error while sending content");
            e.printStackTrace();
        }
    }
}