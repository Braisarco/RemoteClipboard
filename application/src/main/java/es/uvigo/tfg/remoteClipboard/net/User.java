package es.uvigo.tfg.remoteClipboard.net;

import java.awt.datatransfer.Transferable;
import java.util.List;

import es.uvigo.tfg.remoteClipboard.CustomClipboard;

public class User {
  private String ip;
  private String username;
  private CustomClipboard clipboard;

  public User() {

  }

  public User(String newIP, String name) {
    this.ip = newIP;
    this.username = name;
    this.clipboard = new CustomClipboard();

  }

  public String getIp() {
    return ip;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String name) {
    this.username = name;
  }

  public List<String> getClipboardStringContent() {
    return clipboard.getClipboardContentString();
  }

//  public void addContent(Transferable content) {
//    clipboard.addContent(content);
//  }
//
//  public void setClipboardContent(List<Transferable> newContent) {
//    clipboard.setClipboardContent(newContent);
//  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof User && ((User) obj).ip == this.ip && ((User) obj).username == this.username) {
      return true;
    }
    return false;
  }
}
