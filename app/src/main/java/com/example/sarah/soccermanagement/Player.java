package com.example.sarah.soccermanagement;

/**
 * Created by Sarah on 3/7/2016.
 */
public class Player {
    private String firstName;
    private String year;
    private String position;
    private int heightFeet;
    private int heightInches;
    private float weight;
    private String hometown;
    private String highSchool;
    private String club;
    private int picResourceId;

    public Player(){}

    public Player(String firstName, String year, int heightFeet, int heightInches, float weight, String hometown, String highSchool, String club) {
        this.firstName = firstName;
        this.year = year;
        //this.position = position;
        this.heightFeet = heightFeet;
        this.heightInches = heightInches;
        this.weight = weight;
        this.hometown = hometown;
        this.highSchool = highSchool;
        this.club = club;
    }

    public int getPicResourceId()
    {
        return picResourceId;
    }

    public void setPicResourceId(int id)
    {
        picResourceId = id;
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
