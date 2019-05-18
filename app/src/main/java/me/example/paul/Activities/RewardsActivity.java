package me.example.paul.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import me.example.paul.CardFragmentPagerAdapter;
import me.example.paul.Fragments.Card;
import me.example.paul.Model.Reward;
import me.example.paul.Model.Store;
import me.example.paul.R;
import me.example.paul.SessionManager;
import me.example.paul.ShadowTransformer;

public class RewardsActivity extends AppCompatActivity {

    private ImageView activeCard;
    private ImageView inactiveCard;
    private Switch nfc_connected;


    SessionManager sessionManager;

    private TextView employeeName;

    private Store store;
    private ViewPager pager;
    ArrayList<Card> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        sessionManager = new SessionManager(this);

        nfc_connected = findViewById(R.id.nfc_switch);
        inactiveCard = findViewById(R.id.card_inactive);
        activeCard = findViewById(R.id.card_active);
        inactiveCard.setVisibility(View.VISIBLE);
        activeCard.setVisibility(View.INVISIBLE);

        employeeName = findViewById(R.id.employee_name);
        employeeName.setText(sessionManager.getUserDetails().get("NAME"));

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            store = new Gson().fromJson(bundle.getString("json_rewards"), Store.class); //this maps the json onto the store class
        }

        fragments = new ArrayList<>();

        for (Reward r : store.getRewards()) {
            Card card = new Card();
            Bundle xBundle = new Bundle();
            xBundle.putSerializable("data", r);
            card.setArguments(xBundle);
            fragments.add(card);
        }

        pager = findViewById(R.id.pager);
        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(pager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(false, fragmentCardShadowTransformer);
        pager.setOffscreenPageLimit(3);

        nfc_connected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nfc_connected.isChecked()) {
                    inactiveCard.setVisibility(View.INVISIBLE);
                    activeCard.setVisibility(View.VISIBLE);
                } else {
                    inactiveCard.setVisibility(View.VISIBLE);
                    activeCard.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


}
