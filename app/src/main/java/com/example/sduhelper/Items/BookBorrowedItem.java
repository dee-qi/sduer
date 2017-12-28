package com.example.sduhelper.Items;

/**
 * This is sduer
 * Created by qidi on 2017/7/21.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class BookBorrowedItem {
    String bookName;
    String borrowedTime;
    String returnTime;
    String id;//图书id
    String checkCode;//续借验证码

    public BookBorrowedItem(String bookName,
                            String startTime,
                            String endTime,
                            String id,
                            String checkCode){
        this.bookName = bookName;
        this.borrowedTime = startTime;
        this.returnTime = endTime;
        this.id = id;
        this.checkCode = checkCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBorrowedTime() {
        return borrowedTime;
    }

    public void setBorrowedTime(String borrowedTime) {
        this.borrowedTime = borrowedTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }
}
