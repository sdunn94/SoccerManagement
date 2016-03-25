package com.example.sarah.soccermanagement;

/**
 * Created by Sarah on 3/24/2016.
 */
public class Timer
{
    String name;
    Integer startTime;
    Integer endTime;
    int duration;

    public Timer(String n)
    {
        startTime = null;
        endTime = null;
        duration = 0;
        setName(n);
    }

    public Integer getStartTime()
    {
        return startTime;
    }
    public void setStartTime(int st)
    {
        startTime = st;
    }
    public Integer getEndTime()
    {
        return endTime;
    }
    public void setEndTime(int et)
    {
        endTime = et;
    }
    public int calulateDuration()
    {
        return getEndTime() - getStartTime();
    }
    public String getName()
    {
        return name;
    }
    public void setName(String n)
    {
        name = n;
    }
}

