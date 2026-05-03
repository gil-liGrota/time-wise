package com.example.time_wise.notes;

import java.io.Serializable;

public class Folder implements Serializable {
    private String id;
    private String name;

    public Folder() {}

    public Folder(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "📁 " + name;
    }
}