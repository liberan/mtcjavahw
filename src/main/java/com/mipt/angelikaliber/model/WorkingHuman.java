package com.mipt.angelikaliber.model;

public abstract class WorkingHuman extends Human {
    public abstract void work( int a);
    public boolean goHome( String a, String b) {
        return  a.equals(b);
    }
}
