package com.dreiri.stolpersteine.models;

public class Person {

    private String firstName;
    private String lastName;
    private String biography;
    
    public Person(String firstName, String lastName) {
        new Person(firstName, lastName, "");
    }
    
    public Person(String firstName, String lastName, String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String name() {
        return firstName + " " + lastName;
    }

}
