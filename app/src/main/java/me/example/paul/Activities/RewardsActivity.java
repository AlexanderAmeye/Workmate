package me.example.paul.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import me.example.paul.Model.Reward;
import me.example.paul.R;
import me.example.paul.RewardsAdapter;

public class RewardsActivity extends AppCompatActivity {


    private Reward[] rewards = {
            new Reward("Reward1", "Description 1", R.drawable.areyoumymother),

            new Reward("Reward2", "Description 2", R.drawable.theveryhungrycaterpillar)

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_store);

        GridView gridView = (GridView)findViewById(R.id.gridview);
        RewardsAdapter rewardsAdapter = new RewardsAdapter(this, rewards);
        gridView.setAdapter(rewardsAdapter);

    }
}
