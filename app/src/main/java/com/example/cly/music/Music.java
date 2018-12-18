package com.example.cly.music;

import org.litepal.crud.DataSupport;

public class Music extends DataSupport {
    private String title;
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
}
