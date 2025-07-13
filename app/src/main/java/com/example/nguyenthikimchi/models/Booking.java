package com.example.nguyenthikimchi.models;

public class Booking {
    private String id;
    private String fullName;
    private String phone;
    private String date;
    private String time;
    private String people;

    // Bắt buộc có constructor rỗng cho Firebase
    public Booking() {}

    public Booking(String fullName, String phone, String date, String time, String people) {
        this.fullName = fullName;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.people = people;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }
}
