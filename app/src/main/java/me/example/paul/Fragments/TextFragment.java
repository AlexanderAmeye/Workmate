package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.example.paul.Model.Question;
import me.example.paul.R;

public class TextFragment extends Fragment {
    private FragmentActivity context;
    private TextView question_title;
    private EditText answer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_text, container, false);

        question_title = rootView.findViewById(R.id.question_title);
        answer = rootView.findViewById(R.id.answer_text);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
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
                if (s.length() > 3) {
                  //  continue_button.setVisibility(View.VISIBLE);
                } else {
                  //  continue_button.setVisibility(View.GONE);
                }
            }
        });

        question_title.setText(Html.fromHtml(q_data.getQuestionTitle()));
        answer.requestFocus();
       // InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
       // imm.showSoftInput(answer, 0);
    }
}
