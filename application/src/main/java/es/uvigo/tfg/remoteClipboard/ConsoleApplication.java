package es.uvigo.tfg.remoteClipboard;

import es.uvigo.tfg.remoteClipboard.client.Client;
import es.uvigo.tfg.remoteClipboard.client.ClientThreadPool;
import es.uvigo.tfg.remoteClipboard.net.User;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.servers.ServerThreadPool;
import es.uvigo.tfg.remoteClipboard.services.AppManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ConsoleApplication {
    private AppManager manager;
    private Server server;
    private ClientThreadPool clientPool;
    private boolean applicationOn;

    public ConsoleApplication(String username){
        this.manager = new AppManager(username);
        this.server = new ServerThreadPool(manager);
        this.clientPool = new ClientThreadPool(manager);
        this.applicationOn = true;
    }

    public void inicializate() {
        server.start();
        clientPool.start();
        Scanner in = new Scanner(System.in);
        String option = "";
        while (applicationOn) {
            printAvaliableNetworks();
            showMainMenu();
            option = in.nextLine();
            switch (option) {
                case "1":
                    System.out.println("Which network do you want to look up?");
                    networkLookUp(in.nextLine());
                    break;
                case "2":
                    try {
                        System.out.println("How do you want to call the network?");
                        manager.createNetwork(in.nextLine());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    System.out.println("Write down the ip of a computer in the network:");
                    String netIP = in.nextLine();
                    System.out.println("write down the name of the network you want to join:");
                    //Client client = new Client(manager, netIP, manager.getLocalUserName(), in.nextLine());
                    clientPool.executeClient(netIP, in.nextLine());
                    break;
                case "4":
                    applicationOn = false;
                    break;
                default:
                    System.out.println("That is not an option honey <3");
            }
        }
        in.close();

    }

    //Esta funcion vouna ter que sacar de aquí e darlle a responsabilida ao clipboardManager
    private void printAvaliableNetworks() {
        System.out.println("Avaliable Networks:");
        for (String net : manager.getAvaliableNets()){
            System.out.println("\t-" + net);
        }
    }

    private void showMainMenu() {
        System.out.println("[1]CHOOSE NETWORK | [2]CREATE NETWORK | [3]JOIN NETWORK | [4]EXIT");
    }

    //Esta funcion vouna ter que sacar de aquí e darlle a responsabilida ao clipboardManager
    private void networkLookUp(String netName) {
        Scanner in = new Scanner(System.in);
        boolean active = true;
        if(manager.existsNet(netName)){
            while (active) {
                for (String user : manager.getNetUsersNames(netName)) {
                    System.out.println("\t- " + user);
                }
                System.out.println("[1] LOOK UP USER | [2] LEAVE");
                switch (in.nextLine()) {
                    case "1":
                        System.out.println("Which user do you want to look up?");
                        userLookUp(in.nextLine());
                        break;
                    case "2":
                        active = false;
                        break;
                }
            }
        }else{
            System.out.println("That network does not exist :(");
        }
    }


    //Esta funcion vouna ter que sacar de aquí e darlle a responsabilida ao clipboardManager
    private void userLookUp(String userName) {
        if(manager.existUser(userName)){
            System.out.println("Content:");
            for(String content : manager.getUserContent(userName)){
                System.out.println("\t-" + content);
            }
        }
        else{
            System.out.println("That user does not exist :v");
        }
    }
}
