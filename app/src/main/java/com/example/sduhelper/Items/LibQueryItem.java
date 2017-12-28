package com.example.sduhelper.Items;

import android.widget.ArrayAdapter;

import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/8/24.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class LibQueryItem {

    private String name;
    private String author;
    private String press;
    private String code;//图书索书号
    private List<String> locations;//馆藏地

    public LibQueryItem(String name, String author, String press, String code, List<String> locations) {
        this.name = name;
        this.author = author;
        this.press = press;
        this.code = code;
        this.locations = locations;
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

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
