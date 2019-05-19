package me.example.paul.Model;

import java.io.Serializable;

public class Reward implements Serializable {
    private String reward_id;
    private String title;
    private String price;
    private String description;
    private String category;
    private int icon;

    public String getReward_id() {
        return reward_id;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
