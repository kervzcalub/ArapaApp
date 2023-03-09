package com.arapa.app.util;

import java.io.Serializable;
import java.util.ArrayList;

public class School implements Serializable {

    String school_id;
    String name, address, contact, description, email, type, web;
    ArrayList<String> courses_list;
    double latitude;
    double longitude;

    String facebook, instagram, other_site, other_contact;
    String requirements;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    double distance;

    public School(String school_id, String name, String address, String contact, String description, String email, String type, String web, ArrayList<String> courses_list, double latitude, double longitude) {
        this.school_id = school_id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.description = description;
        this.email = email;
        this.type = type;
        this.web = web;
        this.courses_list = courses_list;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public ArrayList<String> getCourses_list() {
        return courses_list;
    }

    public void setCourses_list(ArrayList<String> courses_list) {
        this.courses_list = courses_list;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getOther_site() {
        return other_site;
    }

    public void setOther_site(String other_site) {
        this.other_site = other_site;
    }

    public String getOther_contact() {
        return other_contact;
    }

    public void setOther_contact(String other_contact) {
        this.other_contact = other_contact;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }


}
