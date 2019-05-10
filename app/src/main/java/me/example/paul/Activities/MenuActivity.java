package me.example.paul.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.example.paul.R;
import me.example.paul.SessionManager;

public class MenuActivity extends AppCompatActivity {

    public static final int SURVEY_REQUEST = 1337;

    SessionManager sessionManager;

    private String getUnansweredQuestionsURL = "https://studev.groept.be/api/a18_sd308/GetUnansweredQuestions/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sessionManager = new SessionManager(this);

        Button rewardsActivityButton = findViewById(R.id.reward_button);
        rewardsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(RewardsActivity.class);
            }
        });

        Button questionActivityButton = findViewById(R.id.questions_button);
        questionActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        getUnansweredQuestionsURL + sessionManager.getUserDetails().get("EMAIL"),
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                                JSONArray array = response;
                                JSONObject questions = new JSONObject();
                                try {
                                    questions.put("questions", array);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("json_survey", questions.toString());
                                startActivityForResult(intent, SURVEY_REQUEST);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                );
                requestQueue.add(jsonArrayRequest);
            }
        });
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logOut();
            }
        });
    }

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
