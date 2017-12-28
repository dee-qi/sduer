package com.example.sduhelper.Items;

/**
 * This is sduer
 * Created by qidi on 2017/7/22.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class BookQueryItem {
    private String name;
    private String code;//图书索引号
    private String author;
    private String press;//出版社
    private String all;//总数量
    private String canBor;//可借数量

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getCanBor() {
        return canBor;
    }

    public void setCanBor(String canBor) {
        this.canBor = canBor;
    }
}
