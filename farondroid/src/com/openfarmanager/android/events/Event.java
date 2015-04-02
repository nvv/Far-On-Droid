package com.openfarmanager.android.events;

/**
 * @author Vlad Namashko.
 */
public class Event {

    // some values from #android.os.Message

    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    public Event what(int what) {
        this.what = what;
        return this;
    }

    public Event arg1(int arg1) {
        this.arg1 = arg1;
        return this;
    }

    public Event arg2(int arg2) {
        this.arg2 = arg2;
        return this;
    }

    public Event obj(Object obj) {
        this.obj = obj;
        return this;
    }

    @Override
    public String toString() {
        return String.format("[what: %s, arg1: %s, arg2: %s]", what, arg1, arg2);
    }
}
