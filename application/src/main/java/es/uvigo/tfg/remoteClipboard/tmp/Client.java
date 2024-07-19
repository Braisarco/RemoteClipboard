package es.uvigo.tfg.remoteClipboard.tmp;


import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Client implements ClipboardOwner {
    private List<RemoteClipboardSEI> remoteClipboards;
    private Executor executor;

    public Client() {
        remoteClipboards = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(50);
    }



    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        try {
            CustomTransferable transferable = new CustomTransferable(contents);

            for (RemoteClipboardSEI remote : this.remoteClipboards) {
                this.executor.execute(() -> remote.addContent("EU", transferable));
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.executor.execute(() -> {
            clipboard.setContents(contents, this);
        });
    }
}
