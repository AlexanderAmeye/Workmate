package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.R;

public class NoQuestions extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_noquestions, container, false);

        //UI
        Button next_button = rootView.findViewById(R.id.button_next);
        next_button.setVisibility(View.VISIBLE);

        //Listeners
        next_button.setOnClickListener(nextButtonListener);
        return rootView;
    }

    View.OnClickListener nextButtonListener = v -> ((SurveyActivity) getActivity()).go_to_next(0);
}
