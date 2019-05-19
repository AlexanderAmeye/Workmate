package me.example.paul.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

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

    private Toolbar topToolBar;

    private Store store;
    private ViewPager pager;
    ArrayList<Card> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        sessionManager = new SessionManager(this);

       // nfc_connected = findViewById(R.id.nfc_switch);
       // inactiveCard = findViewById(R.id.card_inactive);
       // activeCard = findViewById(R.id.card_active);
     //   inactiveCard.setVisibility(View.VISIBLE);
      //  activeCard.setVisibility(View.INVISIBLE);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            store = new Gson().fromJson(bundle.getString("json_rewards"), Store.class); //this maps the json onto the store class
        }

     // topToolBar = findViewById(R.id.my_toolbar);
       // setSupportActionBar(topToolBar);

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
        fragmentCardShadowTransformer.enableScaling(false);

        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(false, fragmentCardShadowTransformer);
        pager.setOffscreenPageLimit(3);

       /* nfc_connected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               /* if (nfc_connected.isChecked()) {
                   // inactiveCard.setVisibility(View.INVISIBLE);
                   // activeCard.setVisibility(View.VISIBLE);
                } else {
                   // inactiveCard.setVisibility(View.VISIBLE);
                   // activeCard.setVisibility(View.INVISIBLE);
                }
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(RewardsActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
