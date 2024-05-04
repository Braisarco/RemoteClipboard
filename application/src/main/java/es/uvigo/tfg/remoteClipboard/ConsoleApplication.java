package es.uvigo.tfg.remoteClipboard;

import es.uvigo.tfg.remoteClipboard.client.Client;
import es.uvigo.tfg.remoteClipboard.net.Network;
import es.uvigo.tfg.remoteClipboard.net.NetworksManager;
import es.uvigo.tfg.remoteClipboard.net.User;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.servers.ServerThreadPool;
import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ConsoleApplication {
    private ClipboardManager clipboardManager = new ClipboardManager();
    private NetworksManager networksManager = new NetworksManager(clipboardManager);
    private Server server = new ServerThreadPool(clipboardManager,networksManager);
    private String username = "";
    private boolean applicationOn = true;

    public void inicializate() {
        server.start();
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your username:");
        username = in.nextLine();
        while (applicationOn) {
            printAvaliableNetworks();
            showMainMenu();
            switch (in.nextLine()) {
                case "1":
                    System.out.println("Which network do you want to look up?");
                    networkLookUp(in.nextLine());
                    break;
                case "2":
                    try {
                        System.out.println("How do you want to call the network?");
                        Network net = new Network(in.nextLine(), clipboardManager);
                        User localUser = new User(InetAddress.getLocalHost().getHostAddress(), username);
                        net.addUser(localUser);
                        networksManager.addNetwork(net);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //net.addUser();
                    break;
                case "3":
                    System.out.println("Write down the ip of a computer in the network:");
                    String netIP = in.nextLine();
                    System.out.println("write down the name of the network you want to join:");
                    Client client = new Client(clipboardManager, networksManager, netIP, username, in.nextLine());
                    client.run();
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
        if (networksManager.getNetworks().size() != 0) {
            for (Network net : networksManager.getNetworks()) {
                System.out.println("\t-" + net.getName());
            }
        } else {
            System.out.println("No networks avaliable");
        }
    }

    private void showMainMenu() {
        System.out.println("[1]CHOOSE NETWORK | [2]CREATE NETWORK | [3]JOIN NETWORK | [4]EXIT");
    }

    //Esta funcion vouna ter que sacar de aquí e darlle a responsabilida ao clipboardManager
    private void networkLookUp(String netName) {
        Scanner in = new Scanner(System.in);
        boolean userMenuController = true;
        for (Network net : networksManager.getNetworks()) {
            if (net.getName().equals(netName)) {
                while (userMenuController) {
                    for (User user : net.getUsers()) {
                        System.out.println("\t- " + user.getUsername());
                    }
                    System.out.println("[1] LOOK UP USER | [2] LEAVE");
                    switch (in.nextLine()) {
                        case "1":
                            System.out.println("Which user do you want to look up?");
                            userLookUp(netName, in.nextLine());
                            break;
                        case "2":
                            userMenuController = false;
                            break;
                    }
                }
                break;
            }
        }
        System.out.println("That network does not exist :(");
    }


    //Esta funcion vouna ter que sacar de aquí e darlle a responsabilida ao clipboardManager
    private void userLookUp(String netName, String userName) {
        System.out.println("Content:");
        for (Network net : networksManager.getNetworks()) {
            if (net.getName().equals(netName)){
                net.updateNetwork();
                for (User usr : net.getUsers()){
                    if (usr.getUsername().equals(userName)){
                        for(String content : usr.getClipboardContent()){
                            System.out.println("\t- " + content);
                        }
                    }
                }
            }
        }
    }
}
