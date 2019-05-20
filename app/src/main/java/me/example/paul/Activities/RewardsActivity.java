package me.example.paul.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.example.paul.CardFragmentPagerAdapter;
import me.example.paul.Fragments.Card;
import me.example.paul.Model.Reward;
import me.example.paul.Model.Store;
import me.example.paul.R;
import me.example.paul.SessionManager;
import me.example.paul.ShadowTransformer;

public class RewardsActivity extends AppCompatActivity {

    SessionManager sessionManager;
    private String getBalanceUrl = "https://studev.groept.be/api/a18_sd308/GetBalance/";
    private String updateBalanceUrl = "https://studev.groept.be/api/a18_sd308/UpdateBalance/";
    private RequestQueue serverQueue;
    private Store store;
    private ViewPager pager;
    ArrayList<Card> fragments;

    private int currentPage;

    private Button purchaseButton;

    private TextView balance;
    String balanceString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        serverQueue = Volley.newRequestQueue(this);

        purchaseButton = findViewById(R.id.purchase_button);


        sessionManager = new SessionManager(this);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            store = new Gson().fromJson(bundle.getString("json_rewards"), Store.class); //this maps the json onto the store class
        }

        fragments = new ArrayList<>();

        balance = findViewById(R.id.total_balance);
        getUserBalance();

        for (Reward r : store.getRewards()) {

            if (r.getCategory().equals("food")) {
                r.setIcon(R.drawable.food);
            }

            if (r.getCategory().equals("voucher")) {
                r.setIcon(R.drawable.voucher);
            }

            if (r.getCategory().equals("promotion")) {
                r.setIcon(R.drawable.promotion);
            }

            if (r.getCategory().equals("")) {
                r.setIcon(R.drawable.promotion);
            }

            Card card = new Card();
            Bundle xBundle = new Bundle();
            xBundle.putSerializable("data", r);
            card.setArguments(xBundle);
            fragments.add(card);
        }

        pager = findViewById(R.id.pager);
        final CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(pager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(false);

        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(false, fragmentCardShadowTransformer);
        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(viewListener);

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int price = Integer.parseInt(pagerAdapter.getPrice(currentPage));
                int userBalance = Integer.parseInt(balanceString);

                if (userBalance >= price) {
                    int newBalance = userBalance - price;
                    balance.setText(Integer.toString(newBalance));
                    balanceString = Integer.toString(newBalance);
                    setUserBalance(newBalance);
                    Toast.makeText(RewardsActivity.this, "Reward Purchased!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RewardsActivity.this, "Insufficient Funds!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            currentPage = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    private void setUserBalance(int balance) {
        String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, updateBalanceUrl + balance + "/" + email, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        serverQueue.add(request);
    }

    private void getUserBalance() {
        String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getBalanceUrl + email, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    String total_balance = obj.getString("balance");
                    balance.setText(total_balance);
                    balanceString = total_balance;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        serverQueue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Toast.makeText(RewardsActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
