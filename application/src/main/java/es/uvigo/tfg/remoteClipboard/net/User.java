package es.uvigo.tfg.remoteClipboard.net;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "user")
public class User {
    @XmlAttribute
    private String ip;
    @XmlAttribute(name = "username")
    private String username;
    @XmlElement(name = "transferable")
    private List<String> clipboardContent;

    public User() {

    }

    public User(String newIP, String name) {
        this.ip = newIP;
        this.username = name;
        this.clipboardContent = new ArrayList<>();

    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getClipboardContent() {
        return clipboardContent;
    }

    public void addContent(String content) {
        clipboardContent.add(content);
    }

    public void setClipboardContent(List<String> newContent) {
        this.clipboardContent = newContent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User && ((User) obj).ip == this.ip && ((User) obj).username == this.username) {
            return true;
        }
        return false;
    }
}
