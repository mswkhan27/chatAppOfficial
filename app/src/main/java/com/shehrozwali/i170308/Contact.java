package com.shehrozwali.i170308;

public class Contact {
    String id;
    String username;
    String imageURL;
    String FirstName;
    String LastName;
    String Phone;
    String Gender;
    String DateofBirth;
    String Bio;
    String status;

    public Contact(String id, String username, String imageURL, String firstName, String lastName, String phone, String gender, String dateofBirth, String bio,String userStatus) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Phone = phone;
        this.Gender = gender;
        this.DateofBirth = dateofBirth;
        this.Bio = bio;
        this.status=userStatus;
    }

    public Contact(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDateofBirth() {
        return DateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        DateofBirth = dateofBirth;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String userStatus) {
        this.status = userStatus;
    }
}