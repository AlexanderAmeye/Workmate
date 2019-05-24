package me.example.paul.Model;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private List<Reward> rewards = new ArrayList<>();

    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getReward(int index) {
        return rewards.get(index);
    }
}
