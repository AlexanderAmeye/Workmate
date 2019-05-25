package me.example.paul.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Model.Answers;
import me.example.paul.Model.Question;
import me.example.paul.R;

public class Text extends QuestionFragment {

    private EditText answer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateFragment(R.layout.fragment_string, inflater, container);

        //UI
        answer = rootView.findViewById(R.id.answer_text);


        getNextButton().setOnClickListener(nextButtonListener);

        return rootView;
    }

    View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Question q_data = (Question) getArguments().getSerializable("data");
            Answers.getInstance().addAnswer(answer.getText().toString(), q_data.getQuestion_id());
            ((SurveyActivity) getActivity()).go_to_next(q_data.getReward());
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (((SurveyActivity) getActivity()).isLastQuestion()) {
                        getNextButton().setText("Finish");
                    }
                    getNextButton().setVisibility(View.VISIBLE);
                } else {
                    getNextButton().setVisibility(View.INVISIBLE);
                }
            }
        });

        answer.requestFocus();
    }
}
