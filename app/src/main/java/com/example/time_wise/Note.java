package com.example.time_wise;
import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private String folderId;

    public Note() {}

    public Note(String id, String title, String content, String folderId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.folderId = folderId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getFolderId() { return folderId; }

    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "📝 " + title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id.equals(note.id);
    }
}