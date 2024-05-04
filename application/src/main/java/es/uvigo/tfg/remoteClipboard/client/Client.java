package es.uvigo.tfg.remoteClipboard.client;

import es.uvigo.tfg.remoteClipboard.net.Network;
import es.uvigo.tfg.remoteClipboard.net.NetworksManager;
import es.uvigo.tfg.remoteClipboard.net.User;
import es.uvigo.tfg.remoteClipboard.net.packet.Package;
import es.uvigo.tfg.remoteClipboard.net.packet.PackageType;
import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;
import es.uvigo.tfg.remoteClipboard.CustomTransferable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


public class Client extends Thread {
    private String ip;
    private String userName;
    private String netName;
    private ClipboardManager clipboardManager;
    private NetworksManager networksManager;

    public Client(ClipboardManager manager,NetworksManager netManager, String ip, String userName, String netName) {
        this.networksManager = netManager;
        this.clipboardManager = manager;
        this.ip = ip;
        this.userName = userName;
        this.netName = netName;
    }

    @Override
    public void run() {
        System.out.println("CLIENT: Client running");
        try (Socket clientSocket = new Socket(ip, 10101)) {
            try (DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
                sendEntranceRequest(userName, netName,output);
                String line;
                while (!(line = input.readUTF()).equals("</package>")) {
                    procesPackage(line);
                }
            } catch (EOFException e) {
                System.out.print("CLIENTE: ");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("CLIENT: Error while creating the ObjectInputStream");
                e.printStackTrace();

            }
        } catch (Exception e) {
            System.err.println("CLIENTE: Error while creating Socket in client");
            e.printStackTrace();
        }
    }

    private void procesPackage(String serializedPkg) throws IOException, UnsupportedFlavorException {
        Package pkg = deSerializePackage(serializedPkg);
        switch (pkg.getType()) {
            //De momento para salir do paso faremos que se acepten todas as peticions
            //Pero no futuro esta opción ten que generar unha interrupción ou algo polo
            //estilo.
            case ENTRANCE_ACCEPT:
                processEntranceAccept(pkg);
                break;
            case TRANSFERABLE_CONTENT:
                clipboardManager.addRemoteContent(getTransferable(pkg.getClipboardContent()), pkg.getIp());
                break;
        }
    }

    private void processEntranceAccept(Package pkg){
        System.out.println("Aceptaronme");
        String[] netUsers = new String(pkg.getInfo()).split("\\|");
        for(String user : netUsers){
            System.out.println(user);
        }
    }

    private CustomTransferable getTransferable(byte[] encodedTransferable) {
        ByteArrayInputStream input = new ByteArrayInputStream(encodedTransferable);
        try (ObjectInputStream objInput = new ObjectInputStream(input)) {
            return (CustomTransferable) objInput.readObject();
        } catch (Exception e) {
            System.err.println("CLIENT: Error while getting transferable");
            return null;
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

    private void saveTransferable(Transferable content, String username) throws IOException, UnsupportedFlavorException {
        clipboardManager.addRemoteContent(content, username);
    }

    private void sendEntranceRequest(String userName, String netName, DataOutputStream output){
        Package pkg = new Package();
        try{
            pkg.setIp(InetAddress.getLocalHost().getHostAddress());
            pkg.setType(PackageType.ENTRANCE_REQUEST);
            pkg.setInfo((userName + "|" + netName).getBytes(StandardCharsets.UTF_8));

            System.out.println("CLIENT: Sending content");
            output.writeUTF(pkg.serialize());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

