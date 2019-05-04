package me.example.paul.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import me.example.paul.Answers;
import me.example.paul.Fragments.Multiselect;
import me.example.paul.Fragments.Select;
import me.example.paul.Fragments.TextFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        dotLayout = findViewById(R.id.dots);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            survey = new Gson().fromJson(bundle.getString("json_survey"), Survey.class);
        }

        fragments = new ArrayList<>();

        for (Question q : survey.getQuestions()) {
            if (q.getQuestionType().equals("String")) {
                TextFragment frag = new TextFragment();
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

        pager = findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);

        addDotsIndicator(0);
        pager.addOnPageChangeListener(viewListener);
    }

    public void go_to_next() {
        if (currentPage == fragments.size() - 1) {
            event_survey_completed(Answers.getInstance());
        } else {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

    public void event_survey_completed(Answers instance) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("answers", instance.get_json_object());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[fragments.size()];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public boolean isLastQuestion() {
        return currentPage == fragments.size()-1;
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
