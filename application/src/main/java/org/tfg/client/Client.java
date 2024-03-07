package org.tfg.client;

import org.tfg.services.ClipboardManager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread {
    private ClipboardManager clipboardManager;
    public Client(ClipboardManager manager){
        this.clipboardManager = manager;
    }

    @Override
    public void run(){
        System.out.println("CLIENT: Client running");
        try (Socket clientSocket = new Socket(InetAddress.getLocalHost(), 10101);) {
            try ( ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())){
                Transferable newClipboardContent = (Transferable) input.readObject();
                this.clipboardManager.addRemoteContent(newClipboardContent);
                System.out.println("CLIENT: Message recived");

            } catch(EOFException e){
                    System.out.print("CLIENTE: ");
                    e.printStackTrace();
                } catch(Exception e){
                    System.err.println("CLIENT: Error while creating the ObjectInputStream");
                    e.printStackTrace();

            }
        } catch (Exception e) {
            System.err.println("CLIENTE: Error while creating Socket in client");
            e.printStackTrace();
        }
    }
}

