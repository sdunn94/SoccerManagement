package com.example.sarah.soccermanagement;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Sarah on 3/7/2016.
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

    public Player(){}

    public Player(String firstName, String lastName, String year, int heightFeet, int heightInches, float weight, String position, String hometown, String highSchool, String club) {
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
