package me.example.paul.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.example.paul.Fragments.Multiselect;
import me.example.paul.Fragments.NoQuestions;
import me.example.paul.Fragments.Select;
import me.example.paul.Fragments.SurveyEnd;
import me.example.paul.Fragments.Text;
import me.example.paul.Model.Answers;
import me.example.paul.Model.Question;
import me.example.paul.Model.Survey;
import me.example.paul.PagerAdapter;
import me.example.paul.R;

public class SurveyActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String userEmail;

    private Survey survey;
    private int currentPage;
    private int totalEarnedCredits;
    ArrayList<Fragment> fragments;

    private ViewPager pager;
    private LinearLayout dotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //System
        requestQueue = Volley.newRequestQueue(this);
        userEmail = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");

        //UI
        dotLayout = findViewById(R.id.dots);
        pager = findViewById(R.id.pager);

        //Survey
        Answers.getInstance().clear();
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            survey = new Gson().fromJson(bundle.getString("json_survey"), Survey.class); //this maps the json onto the survey class
        }

        fragments = new ArrayList<>();

        for (Question question : survey.getQuestions()) {
            if (question.getQuestionType().equals("String")) {
                Bundle bundle = new Bundle(); //bundle is used to pass data from activity to fragment
                bundle.putSerializable("data", question); //when we want to pass an object, we need to serialize it
                Text frag = new Text();
                frag.setArguments(bundle);
                fragments.add(frag);
            }

            if (question.getQuestionType().equals("Multiselect")) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", question);
                Multiselect frag = new Multiselect();
                frag.setArguments(bundle);
                fragments.add(frag);
            }

            if (question.getQuestionType().equals("Select")) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", question);
                Select frag = new Select();
                frag.setArguments(bundle);
                fragments.add(frag);
            }
        }

        if (survey.getQuestions().size() == 0) {
            NoQuestions frag = new NoQuestions();
            fragments.add(frag);
        } else {
            SurveyEnd frag = new SurveyEnd();
            fragments.add(frag);
            if (fragments.size() > 2)
                addDotsIndicator(0); //don't show dots when there is only 1 question
        }

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);

        //Listeners
        pager.addOnPageChangeListener(viewPagerListener);
    }

    public boolean isLastQuestion() {
        return currentPage == fragments.size() - 2;
    }

    public void go_to_next(int earnedCredits) {
        totalEarnedCredits += earnedCredits;
        if (currentPage == fragments.size() - 1) event_survey_completed(Answers.getInstance());
        else pager.setCurrentItem(pager.getCurrentItem() + 1);
    }

    public int getTotalEarnedCredits() {
        return totalEarnedCredits;
    }

    public void event_survey_completed(Answers instance) {
        JSONArray answers = instance.getAnswers();

        for (int i = 0; i < answers.length(); i++) {
            try {
                JSONObject answer = answers.getJSONObject(i);
                String id = answer.getString("question_id");
                String text = answer.getString("text");
                addVote(id, text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (totalEarnedCredits > 0) {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/GetBalance/" + userEmail, null, response -> {
                try {
                    updateBalance(response.getJSONObject(0).getInt("balance") + totalEarnedCredits);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
            });
            requestQueue.add(request);
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void updateBalance(int credits) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, "https://studev.groept.be/api/a18_sd308/UpdateBalance/" + credits + "/" + userEmail, null, response -> {
        }, error -> {
        });
        requestQueue.add(request);
    }

    public void addVote(final String id, final String text) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://studev.groept.be/api/a18_sd308/Addvote/" + id + "/" + userEmail + "/" + text, response -> {
        }, error -> {
            error.printStackTrace();
            Log.e("VOLLEY", error.toString());
            Toast.makeText(SurveyActivity.this, "Voting failed", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("questions_question_id", id);
                map.put("users_email", userEmail);
                map.put("text", text);
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void addDotsIndicator(int currentPosition) {
        TextView[] dots = new TextView[fragments.size() - 1];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.indicatorGrey));
            dotLayout.addView(dots[i]);
        }

        if (dots.length > 0 && currentPosition < dots.length) {
            dots[currentPosition].setTextColor(ContextCompat.getColor(this, R.color.indicatorBlue));
        }
    }

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            currentPage = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };
}
