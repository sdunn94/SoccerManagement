package com.example.sarah.soccermanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Sarah on 3/24/2016
 */
public class Profiler
{
    private static Profiler instance = null;

    HashMap< String, ArrayList< Timer >> timers = new HashMap <>();

    Boolean isEnabled;

    private Profiler()
    {
        setEnabled(true);
    }

    public static Profiler getInstance()
    {
        if(instance == null)
        {
            instance = new Profiler();
        }

        return instance;
    }

    public void start(String timerName) throws ProfilerStartException
    {
        if(timers.containsKey(timerName) && timers.get(timerName).get(timers.get(timerName).size() - 1).getEndTime() == null)
        {
            throw new ProfilerStartException("called start twice for " + timerName);
        }
        else
        {
            if(isEnabled())
            {
                Date d = new Date();
                Timer t = new Timer(timerName);
                t.setStartTime((int)d.getTime());

                if(timers.containsKey(timerName))
                {
                    timers.get(timerName).add(t);
                }
                else
                {
                    ArrayList < Timer > temp = new ArrayList <>();
                    temp.add(t);
                    timers.put(timerName, temp);
                }
            }
            else
            {
                System.out.println("Error: Cannot Start Timer, Profiler Not Enabled");
            }
        }
    }

    public void stop(String timerName) throws ProfilerEndException
    {
        if(timers.containsKey(timerName) && timers.get(timerName).get(timers.get(timerName).size() - 1).getEndTime() != null)
        {
            throw new ProfilerEndException("did not call start for " + timerName);
        }
        else
        {
            if(isEnabled())
            {
                Date d = new Date();
                if(timers.containsKey(timerName))
                {
                    timers.get(timerName).get(timers.get(timerName).size() - 1).setEndTime((int)d.getTime());
                }
                else
                {
                    throw new ProfilerEndException("did not call start for " + timerName);
                }
            }
            else
            {
                System.out.println("Error: Cannot Stop Timer, Profiler Not Enabled");
            }
        }
    }

    public int getDuration(String timerName) {

        int totalDuration = 0;
        if(timers.containsKey(timerName)) {
            for(Timer t : timers.get(timerName)) {
                totalDuration += t.calulateDuration();
            }
        }
        return totalDuration;
    }

    public void clear(String timerName) {

        if(timers.containsKey(timerName)) {
            timers.get(timerName).clear();
            timers.remove(timerName);
        }
    }

    public void setEnabled(Boolean e)
    {
        isEnabled = e;
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }
}
