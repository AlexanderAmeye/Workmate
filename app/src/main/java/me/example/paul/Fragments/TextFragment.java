package me.example.paul.Fragments;

import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Answers;
import me.example.paul.Model.Question;
import me.example.paul.R;

public class TextFragment extends Fragment {
    private FragmentActivity context;
    private Button continue_button;
    private TextView question_title;
    private EditText answer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_simple_text, container, false);

        continue_button = rootView.findViewById(R.id.button_continue);
        question_title = rootView.findViewById(R.id.textview_q_title);
        answer = rootView.findViewById(R.id.editText_answer);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().put_answer(question_title.getText().toString(), answer.getText().toString().trim());
                ((SurveyActivity) context).go_to_next();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        Question q_data = (Question) getArguments().getSerializable("data");

        continue_button.setVisibility(View.GONE);
        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    continue_button.setVisibility(View.VISIBLE);
                } else {
                    continue_button.setVisibility(View.GONE);
                }
            }
        });

        question_title.setText(Html.fromHtml(q_data.getQuestionTitle()));
        answer.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(answer, 0);
    }
}
