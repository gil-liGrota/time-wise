package com.example.time_wise;

import androidx.annotation.NonNull;

public class Note {
    public String id;
    public String text;
    public String imageUrl;

    public Note() {}

    public Note(String id, String text, String imageUrl) {
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public String getImageUrl() { return imageUrl; }
    public void setId(String id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return this.text;
    }
}
