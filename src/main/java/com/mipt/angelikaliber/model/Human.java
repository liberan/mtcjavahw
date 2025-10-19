package com.mipt.angelikaliber.model;

public class Human {
    private String firstName;
    private String lastName;
    private int age;
    private boolean isWorking;

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    public void setWorking( boolean isWorking ) {
        this.isWorking = isWorking;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName() {
        return  this.lastName;
    }

    public int getAge() {
        return this.age;
    }

    public boolean getIsWorking() {
        return isWorking;
    }
}
