package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Model.Question;
import me.example.paul.R;
import me.example.paul.Utils.StringParser;

public class QuestionFragment extends Fragment {

    private TextView question_title;
    private Button next_button;

    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(resId, container, false);

        //UI
        question_title = view.findViewById(R.id.question_title);
        Button skip_button = view.findViewById(R.id.button_skip);
        skip_button.setVisibility(View.VISIBLE);
        next_button = view.findViewById(R.id.button_next);
        next_button.setVisibility(View.INVISIBLE);

        //Listeners
        skip_button.setOnClickListener(skipButtonListener);

        setHasOptionsMenu(true);
        return view;
    }

    public Button getNextButton() {
        return next_button;
    }

    View.OnClickListener skipButtonListener = v -> ((SurveyActivity) getActivity()).go_to_next(0);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Question q_data = (Question) getArguments().getSerializable("data");
        question_title.setText(StringParser.parseSentence(q_data.getQuestionTitle()));
    }
}
