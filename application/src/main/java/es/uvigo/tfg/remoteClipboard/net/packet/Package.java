package es.uvigo.tfg.remoteClipboard.net.packet;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/*
 *
 *   PACKAGE DEFINITION AND TYPES
 *
 * 1-All packages must contain the origin ip.
 * 2-All packages must contain the type of request/post.
 * 3-Some packages may have the info field set with information.
 *      ·The info field consist in a series of Strings separated by | character.
 *      ·You will find information about userName or netName in this element.
 * 4-Packages that transfer clipboardContent will store it in the clipboardContent field.
 *
 * Here are some schemes to how the packages will look depending on its type:
 *
 *      ·Entrance Request:
 *          The entrance request package is the responsible for being able to
 *          join a new network:
 *
 *              {ip: origin ip,
 *               type: ENTRANCE_REQUEST,
 *               info: username | netName
 *               }
 *
 *
 *        ·
 *
 * */

@XmlRootElement(name = "package")
public class Package {

  private String ip;

  private PackageType type;

  private byte[] info;

  private byte[] clipboardContent;

  public Package() {

  }

  public String serialize() {
    StringWriter swriter = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(this.getClass());
      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(this, swriter);
    } catch (JAXBException e) {
      System.err.println("PACKAGE_SERIALIZER: Error while serializing package");
      e.printStackTrace();
    }
    return swriter.getBuffer().toString();
  }

  @XmlAttribute(name = "ip_origin")
  public String getIp() {
    return ip;
  }

  @XmlElement(name = "content")
  public byte[] getInfo() {
    return info;
  }

  @XmlAttribute(name = "package_type")
  public PackageType getType() {
    return type;
  }

  @XmlElement(name = "transferable")
  public byte[] getClipboardContent() {
    return clipboardContent;
  }

  public void setClipboardContent(byte[] newClipboardContent) {
    clipboardContent = newClipboardContent;
  }

  public void setCustomClipboardContent(CustomTransferable cbcontent) {
    this.clipboardContent = cbcontent.serialize();
  }

  public void setType(PackageType newType) {
    this.type = newType;
  }

  public void setInfo(byte[] newContent) {
    this.info = newContent;
  }

  public void setIp(String newIP) {
    this.ip = newIP;
  }

}
