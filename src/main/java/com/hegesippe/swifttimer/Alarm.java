package com.hegesippe.swifttimer;

/**
 * Created by Администратор on 09.09.2015.
 */
class Alarm{
    private long time;
    private long startTime;

    public Alarm(long time, long startTime){
        this.startTime = startTime;
        this.time = time;
    }

    public long getTime(){
        return this.time;
    }

    public long getStartTime() {
        return startTime;
    }
}
