package com.example.sduhelper.Items;

import com.example.sduhelper.utils.Information;

import java.io.Serializable;

/**
 * This is sduer
 * Created by qidi on 2017/7/25.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class ItemCurriculum implements Serializable{
    boolean isClass;
    private String name;
    private String teacher;
    private String location;
    private String weekSequence;//在那几周上课的序列号，是一个长度为24的String，如”111111111111111111000000“表示1-18周上课

    private int weekday;
    private int order;

    public ItemCurriculum(boolean isClass, String name, String teacher, String location, String weekSequence) {
        this.isClass = isClass;
        this.name = name;
        this.teacher = teacher;
        this.location = location;
        this.weekSequence = weekSequence;
    }
    //设置这节课是星期几（weekday）的第几节（order）
    public void setWeekdayAndOrder(int weekday, int order){
        this.weekday = weekday;
        this.order = order;
    }
    //判断该结课是不是在这一天，weekCount->第几周，weekday->星期几
    public boolean isOnThisDay(int weekCount,int weekday){
        boolean isThisWeek = false;
        if(weekSequence.length() != 0 && weekSequence.charAt(weekCount-1) == '1'){
            isThisWeek = true;
        }
        if(weekday == this.weekday&&isThisWeek){
            return true;
        } else return false;
    }

    public String getOrder(){
        switch (order){
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            default:
                return "无order";
        }
    }

    public boolean isClass(){
        return isClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeekSequence() {
        return weekSequence;
    }

    public void setWeekSequence(String weekSequence) {
        this.weekSequence = weekSequence;
    }
}
