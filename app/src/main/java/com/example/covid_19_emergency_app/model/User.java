package com.example.covid_19_emergency_app.model;

public class User
{
    public String categories,address;

    public User(String categories, String address) {
        this.categories = categories;
        this.address = address;


    }
    /*
        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getMobile_no() {
            return mobile_no;
        }

        public void setMobile_no(String mobile_no) {
            this.mobile_no = mobile_no;
        }

        public String getEmail() {
            return Date_of_Birth;
        }

      //  public void setEmail(String email) {
        //    Date_of_Birth = dob;
        //}

        public String getCollege() {
            return Categories;
        }

       // public void setCollege(String college) {
        //    Categories = categories;
        //}
    */
    @Override
    public String toString() {
        return "User{" +
                "Categories='" + categories + '\'' +
                ", Address='" + address + '\'' +
                '}';
    }
}
