package me.example.paul.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import me.example.paul.R;
import me.example.paul.SessionManager;

public class MenuActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    SessionManager sessionManager;

    public static final int SURVEY_REQUEST = 8888;

    private CardView questions_card, rewards_card;
    private Button signoutButton;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //System
        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        //UI
        questions_card = findViewById(R.id.questions_card);
        rewards_card = findViewById(R.id.rewards_card);
        signoutButton = findViewById(R.id.signout_button);
        welcomeText = findViewById(R.id.welcome_text);

        //Listeners
        questions_card.setOnClickListener(questionsButtonListener);
        rewards_card.setOnClickListener(rewardsButtonListener);
        signoutButton.setOnClickListener(logoutButtonListener);


        welcomeText.setText(getString(R.string.welcome_message, sessionManager.getUserDetails().get("NAME")));
    }

    View.OnClickListener questionsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    "https://studev.groept.be/api/a18_sd308/GetUnansweredQuestions/" + sessionManager.getUserDetails().get("EMAIL"),
                    null,
                    response -> {
                        Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                        JSONObject questions = new JSONObject();
                        try {
                            questions.put("questions", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("json_survey", questions.toString());
                        startActivityForResult(intent, SURVEY_REQUEST);
                    },
                    error -> {
                    }
            );
            requestQueue.add(jsonArrayRequest);
        }
    };

    View.OnClickListener rewardsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    "https://studev.groept.be/api/a18_sd308/GetAllRewards",
                    null,
                    response -> {
                        Intent intent = new Intent(getApplicationContext(), RewardsActivity.class);
                        JSONObject rewards = new JSONObject();
                        try {
                            rewards.put("rewards", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("json_rewards", rewards.toString());
                        startActivity(intent);
                    },
                    error -> {
                    }
            );
            requestQueue.add(jsonArrayRequest);
        }
    };

    View.OnClickListener logoutButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sessionManager.logOut();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SURVEY_REQUEST) {
            if (resultCode == RESULT_OK) {
                StartActivity(MenuActivity.class);
            }
        }
    }

    void StartActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
    }
}