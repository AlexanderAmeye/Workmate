package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.R;

public class SurveyEnd extends Fragment {


    private int credits;
private TextView earnedCredits;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_end_of_survey, container, false);

         earnedCredits = rootView.findViewById(R.id.earnedCredits);

        // earnedCredits.setText(Locale.getDefault(), Integer.toString(((SurveyActivity) getActivity()).getTotalEarnedCredits()));
        Button next_button = rootView.findViewById(R.id.button_next);
        next_button.setVisibility(View.VISIBLE);
        next_button.setOnClickListener(v -> ((SurveyActivity) getActivity()).go_to_next(0));
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        { credits = ((SurveyActivity) getActivity()).getTotalEarnedCredits();
            Log.e("REWARD:",Integer.toString(credits));
            earnedCredits.setText(String.format(Locale.getDefault(), "%d", credits));



        }
    }
}
