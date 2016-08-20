package com.hegesippe.swifttimer;

final class Alarm{
    private long time;
    private long startTime;

    Alarm(long time, long startTime){
        this.startTime = startTime;
        this.time = time;
    }

    long getTime(){
        return this.time;
    }

    long getStartTime() {
        return startTime;
    }
}
