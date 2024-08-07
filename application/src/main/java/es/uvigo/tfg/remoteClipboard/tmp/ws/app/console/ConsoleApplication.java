package es.uvigo.tfg.remoteClipboard.tmp.ws.app.console;

import es.uvigo.tfg.remoteClipboard.tmp.ws.client.RemoteClipboardClient;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.server.RemoteClipboardServer;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSIB;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {
    private boolean shutOff;
    private RemoteClipboardSIB clipboard;
    private RemoteClipboardServer server;
    private RemoteClipboardClient client;

    public ConsoleApplication(RemoteClipboardSIB clipboard){
        try{
            shutOff = false;
            this.client = new RemoteClipboardClient(InetAddress.getLocalHost().getHostName());
            this.clipboard = clipboard;
            this.server = new RemoteClipboardServer(this.clipboard);
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
    }

    public void init(){
        server.publishService();
        Scanner in = new Scanner(System.in);
        while (!this.shutOff) {
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
                        this.clipboard.createNet(in.nextLine());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    System.out.println("Write down the ip of a computer in the network:");
                    String netIP = in.nextLine();
                    System.out.println("write down the name of the network you want to join:");
                    String netName = in.nextLine();
                    List<String> nets = Arrays.stream(netName.split("/")).toList();

                    try{
                        this.clipboard.addSeveralNets(nets);
                        List<User> remoteUsers = client.connect("http://"+ netIP + ":1010/remoteClipboard?wsdl", nets);
                        if (!remoteUsers.isEmpty()){
                            for(User user : remoteUsers){
                                this.clipboard.register(user.getUsername(), user.getWsdl(), user.getNets());
                            }
                        }
                    }catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                    break;
                case "4":
                    System.out.println("Write the name of the net that you want to eliminate:");
                    String net = in.nextLine();
                    this.clipboard.removeNet(net);
                    break;
                case "5":
                    shutOff = false;
                    in.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("That is not an option honey <3");
                    break;
            }
        }
    }

    private void printAvaliableNetworks() {
        System.out.println("Avaliable Networks:");
        for (String net : this.clipboard.getAvaliableNets()) {
            System.out.println("\t-" + net);
        }
    }

    private void showMainMenu() {
        System.out.println("[1]CHOOSE NETWORK | [2]CREATE NETWORK | [3]JOIN NETWORK | [4]REMOVE NETWORK | [5]EXIT");
    }

    private void networkLookUp(String netName) {
        Scanner in = new Scanner(System.in);

        boolean active = true;
        if (this.clipboard.getAvaliableNets().contains(netName)) {
            while (active) {
                for (String user : this.clipboard.getUsersOfNet(netName)) {
                    System.out.println("\t- " + user);
                }
                System.out.println("[1] LOOK UP USER | [2] LEAVE");
                switch (in.nextLine()) {
                    case "1":
                        System.out.println("Which user do you want to look up?");
                        userLookUp(in.nextLine(), netName);
                        break;
                    case "2":
                        active = false;
                        break;
                }
            }
        } else {
            System.out.println("That network does not exist :(");
        }
    }

    private void userLookUp(String username, String netName) {
        if (this.clipboard.getUsersOfNet(netName).contains(username)) {
            System.out.println("Content:");
            for (String content : this.clipboard.getUserContent(username)) {
                System.out.println("\t-" + content);
            }
        } else {
            System.out.println("That user does not exist :v");
        }
    }
}
