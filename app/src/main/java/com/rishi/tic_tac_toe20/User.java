package com.rishi.tic_tac_toe20;

public class User {
    static String name,image;
    User(){

    }

    public User(String name, String image) {
        User.name = name;
        User.image = image;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        User.image = image;
    }
}
