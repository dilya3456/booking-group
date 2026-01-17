package com.company.models;

public class Passenger {
    private int id;
    private String fullName;
    private String passportNumber;
    private int age;
    private String nationality;

    public Passenger(int id, String fullName, String passportNumber, int age, String nationality) {
        this.id = id;
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.age = age;
        this.nationality = nationality;
    }

    public Passenger(String fullName, String passportNumber, int age, String nationality) {
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.age = age;
        this.nationality = nationality;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPassportNumber() { return passportNumber; }
    public int getAge() { return age; }
    public String getNationality() { return nationality; }
}
