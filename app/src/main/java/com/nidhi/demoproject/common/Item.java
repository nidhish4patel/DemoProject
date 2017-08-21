package com.nidhi.demoproject.common;

import java.util.Hashtable;

public class Item extends Hashtable {

    public Item() {

    }

    public void setAttribute(String name, String value) {
        this.put(name, value);
    }

    public void setAttributeValue(String name, Object value) {
        this.put(name, value);
    }

    public Object getAttribValue(String key) {
        return this.get(key) != null ? this.get(key) : null;
    }

    public String getAttribute(String key) {
        return this.get(key) != null ? this.get(key).toString() : "";
    }

    public int getId() {
        return 0;
    }

    public Hashtable getAllAttributes() {
        return this;
    }

    public boolean containKey(String key) {
        return this.containsKey(key) ? true : false;
    }


}