package net.ddns.buckeyeflash.models.datatable;

import java.io.Serializable;

public class Search implements Serializable {
    private String value;
    private boolean regex;
    private final static long serialVersionUID = -8617835832252967105L;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
