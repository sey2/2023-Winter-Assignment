package com.example.challenge22;

import java.util.ArrayList;

public interface OnDatabaseCallback {
    public void insert(String name, String author, String contents);
    public ArrayList<BookDTO> selectAll();
}