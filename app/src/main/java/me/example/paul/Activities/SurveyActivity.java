package me.example.paul.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import me.example.paul.Answers;
import me.example.paul.Fragments.MultiSelectFragment;
import me.example.paul.Fragments.TextFragment;
import me.example.paul.Model.Question;
import me.example.paul.Model.Survey;
import me.example.paul.PagerAdapter;
import me.example.paul.R;

public class SurveyActivity extends AppCompatActivity {

    private ViewPager pager;
    private LinearLayout dotLayout;
    private Survey survey;

    ArrayList<Fragment> fragments;

    private TextView[] dots;

    private Button nextButton;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        dotLayout = (LinearLayout) findViewById(R.id.dots);


        nextButton = findViewById(R.id.button_next);

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

            if (q.getQuestionType().equals("MultiSelect")) {
                MultiSelectFragment frag = new MultiSelectFragment();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", q);
                frag.setArguments(xBundle);
                fragments.add(frag);
            }
        }

        pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);

        addDotsIndicator(0);
        pager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage == fragments.size()-1) {
                    event_survey_completed(Answers.getInstance());
                }
                else go_to_next();
            }
        });
    }

    public void go_to_next() {
        pager.setCurrentItem(pager.getCurrentItem() + 1);
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

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            currentPage = i;

            if (i == 0) {
                nextButton.setEnabled(true);
                nextButton.setText("Next");
            } else if (i == dots.length-1) {
                nextButton.setEnabled(true);
                nextButton.setText("Finish");
            } else {
                nextButton.setEnabled(true);
                nextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };
}
