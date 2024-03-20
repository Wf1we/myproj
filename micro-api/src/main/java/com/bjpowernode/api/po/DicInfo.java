package com.bjpowernode.api.po;

import java.io.Serializable;

public class DicInfo implements Serializable {

    private Integer id;
    private String name;
    private String val;
    private String category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "DicInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", val='" + val + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
