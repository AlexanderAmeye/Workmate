package me.example.paul.Model;

import java.io.Serializable;

public class Reward implements Serializable {
    private String reward_id;
    private String title;
    private String price;
    private String description;
    private String category;

    public String getReward_id() {
        return reward_id;
    }

    public String getTitle() {
        return title;
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
