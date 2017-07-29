package net.gusri.yongdolah.fragments;

import java.util.Date;

/**
 * Created by ghostonk on 15/12/16.
 */

public class BlogModel {
    private String title;
    private String desc;
    private String image;
    private String dateupload;
    private String category;
    private String imguser;
    private String username;
    private String firstname;
    private String lastname;
    private String imageuser;
    private String status;
    private String message;
    private String date;
    private String time;
    private long messagetime;

    public BlogModel() {

    }


    public BlogModel(String title, String desc, String image, String dateupload, String category, String imguser, String username, String firstname, String lastname, String imageuser, String status, String message, String date, String time) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.dateupload = dateupload;
        this.category = category;
        this.imguser = imguser;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.imageuser = imageuser;
        this.status = status;
        this.message = message;
        this.date = date;
        this.time = time;

    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateupload() {
        return dateupload;
    }

    public void setDateupload(String dateupload) {
        this.dateupload = dateupload;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImguser() {
        return imguser;
    }

    public void setImguser(String imguser) {
        this.imguser = imguser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageuser() {
        return imageuser;
    }

    public void setImageuser(String imageuser) {
        this.imageuser = imageuser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
