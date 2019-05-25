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

        String questionTitle = q_data.getQuestionTitle();
        String addSpaces = splitCamelCase(questionTitle);
        StringBuilder removeUpperCase = removeUpperCase(addSpaces);

        question_title.setText(removeUpperCase);
    }

    static StringBuilder removeUpperCase(String s) {
        String[] words = s.split("\\s+");
        StringBuilder parsedTitle = new StringBuilder();
        for (String word : words) {
            String newWord = "";
            if (!words[0].equals(word)) {
                newWord = word.substring(0, 1).toLowerCase() + word.substring(1);
                parsedTitle.append(" ");
                parsedTitle.append(newWord);
            } else {
                parsedTitle.append(" ");
                parsedTitle.append(word);
            }
        }

        return parsedTitle;
    }

    static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])"
                ),
                " "
        );
    }
}
