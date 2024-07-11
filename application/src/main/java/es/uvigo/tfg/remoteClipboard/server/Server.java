package es.uvigo.tfg.remoteClipboard.server;

import java.awt.datatransfer.Transferable;

public interface Server {
  public void start();

  public void run();

  public void shutDown();

  public void broadCast(Transferable content);
}
