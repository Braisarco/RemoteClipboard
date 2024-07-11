package es.uvigo.tfg.remoteClipboard.client;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.net.packet.Package;
import es.uvigo.tfg.remoteClipboard.net.packet.PackageType;
import es.uvigo.tfg.remoteClipboard.services.AppManager;

public class Client extends Thread {
  private String ip;
  private String userName;
  private String netName;
  private AppManager manager;

  public Client(AppManager manager, String ip, String userName, String netName) {
    this.manager = manager;
    this.ip = ip;
    this.userName = userName;
    this.netName = netName;
  }

  public Client(String userName) {
    this.userName = userName;
  }

  public void setManager(AppManager appManager) {
    this.manager = appManager;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public void setNetName(String name) {
    this.netName = name;
  }

  @Override
  public void run() {
    System.out.println("CLIENT: Client running");
    try (Socket clientSocket = new Socket(ip, 10101)) {
      try (
        DataInputStream input = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())
      ) {
        if (!this.netName.isEmpty()) {
          sendEntranceRequest(userName, netName, output);
        }
        String line;
        while (!(line = input.readUTF()).equals("</package>")) {
          processPackage(line);
        }
      } catch (EOFException e) {
        manager.deleteRemoteUser(ip);
        System.err.println("CLIENT: Client gone");
      } catch (Exception e) {
        manager.deleteRemoteUser(ip);
        System.err.println("CLIENT: Client gone");
      }
    } catch (Exception e) {
      manager.deleteRemoteUser(ip);
      System.err.println("CLIENT: Client gone");
    }
  }

  private void processPackage(String serializedPkg) throws IOException, UnsupportedFlavorException {
    Package pkg = deSerializePackage(serializedPkg);
    switch (pkg.getType()) {
    // De momento para salir do paso faremos que se acepten todas as peticions
    // Pero no futuro esta opción ten que generar unha interrupción ou algo polo
    // estilo.
    case ENTRANCE_ACCEPT:
      processEntranceAccept(pkg);
      break;
    case TRANSFERABLE_CONTENT:
      manager.addContent(pkg.getIp(), getTransferable(pkg.getClipboardContent()));
      break;
    case DISCONNECT:
      processDisconnection(pkg);
      break;
    default:
      System.out.println("That package has not yet been prepared");
    }
  }

  private void processEntranceAccept(Package pkg) {
    // Esto tería que crear un servidor que se conectase co cliente do outro
    // directamente
    manager.createNetwork(netName);
    Map<String, String> users = getUsersMap(pkg.getInfo());
    users.forEach((name, userIp) -> {
      manager.addRemoteUser(userIp, name, netName);
    });
  }

  private void processDisconnection(Package pkg) {
    manager.deleteRemoteUser(pkg.getIp());
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> getUsersMap(byte[] encodedUsers) {
    ByteArrayInputStream input = new ByteArrayInputStream(encodedUsers);
    try (ObjectInputStream objectInput = new ObjectInputStream(input)) {
      return (Map<String, String>) objectInput.readObject();
    } catch (Exception e) {
      System.err.println("Error while getting users");
      return null;
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

  private void sendEntranceRequest(String userName, String netName, DataOutputStream output) {
    Package pkg = new Package();
    try {
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