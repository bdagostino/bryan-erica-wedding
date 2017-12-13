package net.ddns.buckeyeflash.models.datatable;

import java.io.Serializable;

public class Order implements Serializable {
    private int column;
    private String dir;
    private final static long serialVersionUID = -6570706329689737596L;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
