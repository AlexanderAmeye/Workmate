package me.example.paul.Activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
import me.example.paul.Utils.NFCHelper;

public class RewardsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    SessionManager sessionManager;
    NfcAdapter nfcAdapter;

    private boolean nfcConnected = false;

    private ImageView cardActive, cardInactive;

    private Store store;
    private ViewPager pager;
    ArrayList<Card> fragments;

    private int currentPage;

    private Button purchaseButton;
    CardFragmentPagerAdapter pagerAdapter;
    private TextView balance;
    String balanceString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //System
        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAvailable())
            Toast.makeText(RewardsActivity.this, "NFC disabled", Toast.LENGTH_SHORT).show();

        //UI
        cardActive = findViewById(R.id.card_active);
        cardInactive = findViewById(R.id.card_inactive);
        cardActive.setVisibility(View.INVISIBLE);
        purchaseButton = findViewById(R.id.purchase_button);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            store = new Gson().fromJson(bundle.getString("json_rewards"), Store.class); //this maps the json onto the store class
        }

        fragments = new ArrayList<>();

        balance = findViewById(R.id.total_balance);
        getUserBalance();

        for (Reward r : store.getRewards()) {

            if (r.getCategory().equals("Food")) {
                r.setIcon(R.drawable.food);
            }

            if (r.getCategory().equals("Voucher")) {
                r.setIcon(R.drawable.voucher);
            }

            if (r.getCategory().equals("Promotion")) {
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
        pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(pager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(false);

        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(false, fragmentCardShadowTransformer);
        pager.setOffscreenPageLimit(3);

        //Listeners
        pager.addOnPageChangeListener(viewListener);
        purchaseButton.setOnClickListener(purchaseButtonListener);
    }

    View.OnClickListener purchaseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (nfcConnected) {
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

            else Toast.makeText(RewardsActivity.this, "Card not found", Toast.LENGTH_SHORT).show();
        }
    };

    public boolean nfcAvailable() {
        return (nfcAdapter != null && nfcAdapter.isEnabled());
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
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, "https://studev.groept.be/api/a18_sd308/UpdateBalance/" + balance + "/" + email, null, response -> {
        }, error -> {

        });
        requestQueue.add(request);
    }

    private void getUserBalance() {
        String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/GetBalance/" + email, null, response -> {
            try {
                JSONObject obj = response.getJSONObject(0);
                String total_balance = obj.getString("balance");
                balance.setText(total_balance);
                balanceString = total_balance;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        requestQueue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            nfcConnected = true;
            cardActive.setVisibility(View.VISIBLE);
            cardInactive.setVisibility(View.INVISIBLE);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcAdapter.ignore(tag, 1000, () -> {
                cardActive.setVisibility(View.INVISIBLE);
                cardInactive.setVisibility(View.VISIBLE);
                nfcConnected = false;
            }, new Handler(Looper.getMainLooper()));
        }
    }

    @Override
    protected void onResume() {
        if (nfcAvailable()) NFCHelper.enableForegroundDispatchSystem(this, nfcAdapter, RewardsActivity.class);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (nfcAvailable()) NFCHelper.disableForegroundDispatchSystem(this, nfcAdapter);
        super.onPause();
    }
}
