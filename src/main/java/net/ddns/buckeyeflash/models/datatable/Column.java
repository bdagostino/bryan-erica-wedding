package net.ddns.buckeyeflash.models.datatable;

import java.io.Serializable;

public class Column implements Serializable {
    private String data;
    private String name;
    private boolean searchable;
    private boolean orderable;
    private Search search;
    private final static long serialVersionUID = -8199389606567493567L;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
