package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Model.Answers;
import me.example.paul.Model.Question;
import me.example.paul.R;

public class Text extends Fragment {

    private TextView question_title;
    private EditText answer;
    private Button next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_string, container, false);

        //UI
        question_title = rootView.findViewById(R.id.question_title);
        answer = rootView.findViewById(R.id.answer_text);
        next_button = rootView.findViewById(R.id.button_next);
        Button skip_button = rootView.findViewById(R.id.button_skip);
        next_button.setVisibility(View.INVISIBLE);
        skip_button.setVisibility(View.VISIBLE);

        //Listeners
        skip_button.setOnClickListener(skipButtonListener);
        next_button.setOnClickListener(nextButtonListener);
        return rootView;
    }

    View.OnClickListener skipButtonListener = v -> ((SurveyActivity) getActivity()).go_to_next(0);

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

        Question q_data = (Question) getArguments().getSerializable("data");

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
                        next_button.setText("Finish");
                    }
                    next_button.setVisibility(View.VISIBLE);
                } else {
                    next_button.setVisibility(View.INVISIBLE);
                }
            }
        });

        question_title.setText(Html.fromHtml(q_data.getQuestionTitle()));
        answer.requestFocus();
    }
}
