package com.example.resep_sehat.model;

public class recipe {

    private int id;
    private String title;
    private String description;
    private String imagePath;
    private float rating;
    private int calories;
    private int duration; // Add this if not present

    public recipe(int id, String title, String description, String imagePath, float rating, int calories, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.rating = rating;
        this.calories = calories;
        this.duration = duration;
    }

    // Getters for all fields
//    public int getId() { return id; }
//    public String getTitle() { return title; }
//    public String getDescription() { return description; }
//    public String getImagePath() { return imagePath; }
//    public float getRating() { return rating; }
//    public int getCalories() { return calories; }
//    public int getDuration() { return duration; }

    // Getter and Setter methods
    public int getId() { return id; }

//    public void setId(int id) { this.id = id; }

    public String getTitle() {return title;}

//    public void setTitle(String title) {
//        this.title = title;
//    }

    public String getDescription() { return description; }
    // Getters and setters
    public String getImagePath() { return imagePath; }
    public float getRating() { return rating; }

    // Include getters
    public int getCalories() { return calories; }

    public int getDuration() { return duration; }

    public void setDescription(String description) {this.description = description;}


}
