package me.example.paul.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
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

    private Survey survey;
    private ViewPager pager;
    ArrayList<Fragment> fragments;
    private LinearLayout dotLayout;
    private TextView[] dots;
    private int currentPage;

    private int totalEarnedCredits;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //System
        requestQueue = Volley.newRequestQueue(this);

        //UI
        dotLayout = findViewById(R.id.dots);

        //Survey
        Answers.getInstance().clear();
        totalEarnedCredits = 0;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            survey = new Gson().fromJson(bundle.getString("json_survey"), Survey.class); //this maps the json onto the survey class
        }

        fragments = new ArrayList<>();

        for (Question q : survey.getQuestions()) {
            if (q.getQuestionType().equals("String")) {
                Text frag = new Text();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", q);
                frag.setArguments(xBundle);
                fragments.add(frag);
            }

            if (q.getQuestionType().equals("Multiselect")) {
                Multiselect frag = new Multiselect();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", q);
                frag.setArguments(xBundle);
                fragments.add(frag);
            }

            if (q.getQuestionType().equals("Select")) {
                Select frag = new Select();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", q);
                frag.setArguments(xBundle);
                fragments.add(frag);
            }
        }

        if (survey.getQuestions().size() == 0) {
            NoQuestions frag = new NoQuestions();
            fragments.add(frag);
        } else {
            SurveyEnd frag = new SurveyEnd();
            fragments.add(frag);
            if (fragments.size() > 2) addDotsIndicator(0);
        }

        pager = findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(viewListener);
    }

    public void go_to_next(int earnedCredits) {
        if (currentPage == fragments.size() - 1) {
            totalEarnedCredits+=earnedCredits;
            event_survey_completed(Answers.getInstance());
        } else {
            totalEarnedCredits+=earnedCredits;
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

    public int getTotalEarnedCredits()
    {
        return totalEarnedCredits;
    }

    public void event_survey_completed(Answers instance) {
        JSONArray answers = instance.getAnswers();
        String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");

        for (int i = 0; i < answers.length(); i++) {
            try {
                JSONObject answer = answers.getJSONObject(i);
                String id = answer.getString("question_id");
                String text = answer.getString("text");
               // totalEarnedCredits += answer.getInt("reward");
                addVote(id, email, text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        calculateTotalCredits(totalEarnedCredits);

        if (totalEarnedCredits > 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "You gained " + totalEarnedCredits + " coins!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 200);
            LinearLayout toastContentView = (LinearLayout) toast.getView();
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.icons8_swiss_franc_48);
            toastContentView.addView(imageView, 0);
            toast.show();
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void calculateTotalCredits(final int newCredits) {
        if (newCredits > 0) {
            String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/GetBalance/" + email, null, response -> {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    int currentbalance = obj.getInt("balance");
                    updateBalance(currentbalance + newCredits);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
            });
            requestQueue.add(request);
        }
    }

    public void updateBalance(int credits) {
        String email = this.getSharedPreferences("LOGIN_SESSION", 0).getString("EMAIL", "");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, "https://studev.groept.be/api/a18_sd308/UpdateBalance/" + credits + "/" + email, null, response -> {
        }, error -> {
        });
        requestQueue.add(request);
    }

    public void addVote(final String id, final String email, final String text) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://studev.groept.be/api/a18_sd308/Addvote/" + id + "/" + email + "/" + text, response -> {
        }, error -> {
            error.printStackTrace();
            Log.e("VOLLEY", error.toString());
            Toast.makeText(SurveyActivity.this, "Voting failed", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("questions_question_id", id);
                map.put("users_email", email);
                map.put("text", text);
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[fragments.size() - 1];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotLayout.addView(dots[i]);
        }

        if (dots.length > 0 && position < dots.length) {
            dots[position].setTextColor(getResources().getColor(R.color.violet));
            dots[position].setTextSize(35);
        }
    }

    public boolean isLastQuestion() {
        return currentPage == fragments.size() - 2;
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
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
