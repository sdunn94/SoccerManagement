package com.example.sarah.soccermanagement;

/**
 * Created by Sarah on 3/7/2016
 */
public class Player {
    private String firstName;
    private String lastName;
    private String year;
    private String position;
    private int heightFeet;
    private int heightInches;
    private float weight;
    private String hometown;
    private String highSchool;
    private String club;
    private String image;
    private Float xPos;
    private Float yPos;
    private boolean inPlay;
    private int timeOnField;
    private int groupNum;
    private boolean timerOn;

    public Player(){}

    public Player(String firstName, String lastName, String year, int heightFeet, int heightInches, float weight, String position, String hometown, String highSchool, String club, boolean inPlay) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.year = year;
        this.position = position;
        this.heightFeet = heightFeet;
        this.heightInches = heightInches;
        this.weight = weight;
        this.hometown = hometown;
        this.highSchool = highSchool;
        this.club = club;
        this.xPos = null;
        this.yPos = null;
        this.inPlay = inPlay;
        timeOnField = 0;
        groupNum = 0;
        timerOn = false;
    }

    public boolean isTimerOn() { return timerOn; }

    public void setTimerOn(boolean timer) { timerOn = timer; }

    public int getGroupNum() {return groupNum;}

    public void setGroupNum(int groupNum) {this.groupNum = groupNum;}

    public void setTimeOnField(int timeOnField) {
        this.timeOnField = timeOnField;
    }

    public int getTimeOnField() {
        return timeOnField;
    }

    public boolean isInPlay() {
        return inPlay;
    }

    public Float getxPos() {
        return xPos;
    }

    public void setxPos(Float xPos) {
        this.xPos = xPos;
    }

    public Float getyPos() {
        return yPos;
    }

    public void setyPos(Float yPos) {
        this.yPos = yPos;
    }

    public String getLastName() { return lastName; }

    public String getImage()
    {
        return image;
    }

    public void setImage(String i)
    {
        image = i;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getYear() {
        return year;
    }

    public String getPosition() {
        return position;
    }

    public int getHeightFeet() {
        return heightFeet;
    }

    public int getHeightInches() {
        return heightInches;
    }

    public float getWeight() {
        return weight;
    }

    public String getHometown() {
        return hometown;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public String getClub() {
        return club;
    }
}
