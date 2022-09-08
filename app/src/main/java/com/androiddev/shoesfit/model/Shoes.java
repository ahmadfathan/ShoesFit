package com.androiddev.shoesfit.model;

public class Shoes {
    int id;
    String type, category, name, color;
    int price;
    String image1, image2, image3, description, suits_with;

    public Shoes() {
    }

    public Shoes(int id, String type, String category, String name, String color, int price, String image1, String image2, String image3, String description, String suits_with) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.name = name;
        this.color = color;
        this.price = price;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.description = description;
        this.suits_with = suits_with;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuits_with() {
        return suits_with;
    }

    public void setSuits_with(String suits_with) {
        this.suits_with = suits_with;
    }
}
