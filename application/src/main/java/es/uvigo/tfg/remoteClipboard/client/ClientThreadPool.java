package es.uvigo.tfg.remoteClipboard.client;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.tfg.remoteClipboard.services.AppManager;

public class ClientThreadPool extends Thread {
  private List<Client> clientPool = new ArrayList<>();

  public ClientThreadPool(String userName) {
    for (int i = 0; i < 50; i++) {
      clientPool.add(new Client(userName));
    }
  }

  public void executeClient(String ip, String netName, AppManager manager) {
    for (Client client : clientPool) {
      if (!client.isAlive()) {
        client.setIp(ip);
        client.setManager(manager);
        client.setNetName(netName);
        client.start();
        break;
      }
    }
  }

  public void shutdown() {
    for (Client client : clientPool) {
      try {
        client.join();
      } catch (InterruptedException e) {
        System.err.println("CLIENTPOOL: Error while shutting down");
        e.printStackTrace();
      }
    }
  }
}
