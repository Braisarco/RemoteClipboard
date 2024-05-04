package es.uvigo.tfg.remoteClipboard.clipboardUtiles;

import java.awt.datatransfer.Transferable;

public interface Subscriber {
    public void execute(Transferable transferable);
}
