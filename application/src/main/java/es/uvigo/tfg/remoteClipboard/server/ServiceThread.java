package es.uvigo.tfg.remoteClipboard.server;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.net.User;
import es.uvigo.tfg.remoteClipboard.net.packet.Package;
import es.uvigo.tfg.remoteClipboard.net.packet.PackageType;
import es.uvigo.tfg.remoteClipboard.services.AppManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceThread extends Thread {
    private Socket clientSocket;
    private DataOutputStream output;
    private AppManager manager;

    public ServiceThread(AppManager manager) {
        this.manager = manager;
    }

    public ServiceThread(Socket newClientSocket) {
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

    public void setSocket(Socket newClientSocket) {
        System.out.println("SERVICETHREAD: Assigning socket to thread");
        this.clientSocket = newClientSocket;
    }


    public void sendContent(Transferable transferedObject) {
        Package pkg = new Package();
        try {
            pkg.setIp(InetAddress.getLocalHost().getHostAddress());
            pkg.setType(PackageType.TRANSFERABLE_CONTENT);
            //pkg.setInfo();
            pkg.setCustomClipboardContent(new CustomTransferable(transferedObject));

            System.out.println("SERVICETHREAD: Sending content");
            this.manager.addLocalContent(new CustomTransferable(transferedObject));
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
        String originIP = pkg.getIp();
        String userName = info[0];
        String netName = info[1];

        if (manager.addRemoteUser(originIP, userName, netName)) {
            //Here we could make the user decide if he/she accepts the new user or not
            accepted = true;
        }
        if (accepted) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Map<String, String> acceptanceInfo = new HashMap<>();
                Package acceptanceResponse = new Package();
                acceptanceResponse.setIp(InetAddress.getLocalHost().getHostAddress());
                acceptanceResponse.setType(PackageType.ENTRANCE_ACCEPT);

                for (User user : manager.getNetUsers(netName)) {
                    acceptanceInfo.put(user.getUsername(), user.getIp());
                }

                try(ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream)){
                    objectOutput.writeObject(acceptanceInfo);
                    objectOutput.close();
                }catch (Exception e){
                    System.err.println("CUSTOMTRANSFERABLE: Error while serializing transferable");
                    e.printStackTrace();
                }

                acceptanceResponse.setInfo(outputStream.toByteArray());

                output.writeUTF(acceptanceResponse.serialize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
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