package es.uvigo.tfg.remoteClipboard.net.packet;

public enum PackageType {
    ENTRANCE_REQUEST(100),
    ENTRANCE_ACCEPT(101),
    ENTRANCE_DENNIED(102),
    TRANSFERABLE_CONTENT(200),
    REMOVE(300);

    int type;

    PackageType(int n){
        type = n;
    }

    public int getType(){
        return this.type;
    }
}
