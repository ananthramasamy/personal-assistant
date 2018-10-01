package com.personalassistant.enities;

public class WalkThrough {
    private String Tittle, Description;
    private int ImageResource;

    public WalkThrough(String tittle, String description, int imageResource) {
        this.Tittle = tittle;
        this.Description = description;
        this.ImageResource = imageResource;
    }

    public String getTittle() {
        return Tittle;
    }

    public void setTittle(String tittle) {
        Tittle = tittle;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getImageResource() {
        return ImageResource;
    }

    public void setImageResource(int imageResource) {
        ImageResource = imageResource;
    }


}
