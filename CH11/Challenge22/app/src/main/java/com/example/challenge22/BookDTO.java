package com.example.challenge22;

public class BookDTO {

    String name;
    String author;
    String contents;

    public BookDTO(String name, String author, String contents) {
        this.name = name;
        this.author = author;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

}