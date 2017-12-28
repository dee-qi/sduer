package com.example.sduhelper.Items;

/**
 * This is sduer
 * Created by qidi on 2017/7/19.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class ScoreItem {
    private String courseName;
    private String courseId;
    private String examRoom;//考试地点
    private String examTime;
    private String examMethod;//考试方法（开卷、闭卷等）
    private String resultComposition;//成绩组成

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getExamMethod() {
        return examMethod;
    }

    public void setExamMethod(String examMethod) {
        this.examMethod = examMethod;
    }

    public String getResultComposition() {
        return resultComposition;
    }

    public void setResultComposition(String resultComposition) {
        this.resultComposition = resultComposition;
    }
}
