package net.ddns.buckeyeflash.models.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatatableRequest implements Serializable {

    private int draw;
    private List<Column> columns = new ArrayList<Column>();
    private List<Order> order = new ArrayList<Order>();
    private int start;
    private int length;
    private Search search;
    private final static long serialVersionUID = -1677715404926581308L;


    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
