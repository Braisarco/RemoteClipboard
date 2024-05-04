package es.uvigo.tfg.remoteClipboard.server;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.net.Network;
import es.uvigo.tfg.remoteClipboard.net.NetworksManager;
import es.uvigo.tfg.remoteClipboard.net.User;
import es.uvigo.tfg.remoteClipboard.net.packet.Package;
import es.uvigo.tfg.remoteClipboard.net.packet.PackageType;
import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServiceThread extends Thread{
    private Socket clientSocket;
    private DataOutputStream output;
    private ClipboardManager clipboardManager;

    private NetworksManager networksManager;

    public ServiceThread(ClipboardManager manager, NetworksManager netManager) {
        this.clipboardManager = manager;
        this.networksManager = netManager;
    }

    public ServiceThread(Socket newClientSocket){
        System.out.println("SERVICE THREAD: Creating new Service Thread");
        this.clientSocket = newClientSocket;
    }

    @Override
    public void run() {
        try (Socket socket = this.clientSocket) {
            output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            String line;
            while (!(line = input.readUTF()).equals("</package>")) {
                procesPackage(line);
            }
        } catch (Exception e) {
            System.err.println("Error getting the client socket");
            e.printStackTrace();
        }
    }

    public void setSocket(Socket newClientSocket){
        System.out.println("SERVICETHREAD: Assigning socket to thread");
        this.clientSocket = newClientSocket;
    }


    public void sendContent(Transferable transferedObject){
        Package pkg = new Package();
        try{
            CustomTransferable serializableTransferable = new CustomTransferable(transferedObject);

            pkg.setIp(InetAddress.getLocalHost().getHostAddress());
            pkg.setType(PackageType.TRANSFERABLE_CONTENT);
            //pkg.setInfo();
            pkg.setCustomClipboardContent(new CustomTransferable(transferedObject));

            System.out.println("SERVICETHREAD: Sending content");
            this.clipboardManager.addLocalContent(new CustomTransferable(transferedObject));
            output.writeUTF(pkg.serialize());

        } catch (Exception e) {
            System.err.println("SERVERTHREAD: Error while sending content");
            e.printStackTrace();
        }
    }

    private void procesPackage(String serializedPkg) throws IOException, UnsupportedFlavorException {
        Package pkg = deSerializePackage(serializedPkg);
        switch (pkg.getType()) {
            //De momento para salir do paso faremos que se acepten todas as peticions
            //Pero no futuro esta opción ten que generar unha interrupción ou algo polo
            //estilo.
            case ENTRANCE_REQUEST:
                processEntranceRequest(pkg);
                break;
        }
    }

    private void processEntranceRequest(Package pkg) {
        String[] info = new String(pkg.getInfo()).split("\\|");
        boolean accepted = false;
        List<String> users = new ArrayList<>();
        String originIP = pkg.getIp();
        String userName = info[0];
        String netName = info[1];

        for (Network net : networksManager.getNetworks()){
            if(net.getName().equals(netName)){
                net.addUser(new User(originIP, userName));
                clipboardManager.createClipboard(originIP,userName);
                users = net.getUsersNames();
                accepted = true;
                break;
            }
        }
        if(accepted){
            try{
                Package acceptanceResponse = new Package();
                String acceptanceResponseInfo = "";
                acceptanceResponse.setIp(InetAddress.getLocalHost().getHostAddress());
                acceptanceResponse.setType(PackageType.ENTRANCE_ACCEPT);

                for (String user : users){
                    acceptanceResponseInfo += user + "|";
                }
                acceptanceResponse.setInfo(acceptanceResponseInfo.getBytes(StandardCharsets.UTF_8));

                output.writeUTF(acceptanceResponse.serialize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try{
                Package acceptanceResponse = new Package();
                acceptanceResponse.setIp(InetAddress.getLocalHost().getHostAddress());
                acceptanceResponse.setType(PackageType.ENTRANCE_DENNIED);
                output.writeUTF(acceptanceResponse.serialize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Package deSerializePackage(String serializedPkg) {
        Package pkg = new Package();
        try {
            JAXBContext context = JAXBContext.newInstance(Package.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            StringReader reader = new StringReader(serializedPkg);
            pkg = (Package) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            System.err.println("PACKAGE: Error while creating package from serialization");
        }
        return pkg;
    }

}