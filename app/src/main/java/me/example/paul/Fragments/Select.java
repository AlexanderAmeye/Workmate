package me.example.paul.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Model.Answers;
import me.example.paul.Model.Question;
import me.example.paul.R;

public class Select extends Fragment {

    private TextView question_title;
    private RadioGroup radioGroup;
    private final ArrayList<RadioButton> radioButtons = new ArrayList<>();
    private Button next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_select, container, false);

        question_title = rootView.findViewById(R.id.question_title);
        radioGroup = rootView.findViewById(R.id.radiogroup);

        next_button = rootView.findViewById(R.id.button_next);
        Button skip_button = rootView.findViewById(R.id.button_skip);

        next_button.setVisibility(View.INVISIBLE);
        skip_button.setVisibility(View.VISIBLE);

        next_button.setOnClickListener(nextButtonListener);
        skip_button.setOnClickListener(v -> ((SurveyActivity) getActivity()).go_to_next(0));
        return rootView;
    }

    View.OnClickListener nextButtonListener = v -> {

        String selection = "";
        for (RadioButton rb : radioButtons) {
            if (rb.isChecked())  selection = rb.getText().toString();
        }
        Question q_data = (Question) getArguments().getSerializable("data");
        Answers.getInstance().addAnswer(selection,q_data.getQuestion_id());
        ((SurveyActivity) getActivity()).go_to_next(q_data.getReward());
    };

    private void evaluateSelection() {
        boolean at_least_one_checked = false;

        for (RadioButton rb : radioButtons) {
            if (rb.isChecked()) at_least_one_checked = true;
        }

        if(at_least_one_checked)
        {
            if(((SurveyActivity) getActivity()).isLastQuestion()) next_button.setText("Finish");
            next_button.setVisibility(View.VISIBLE);
        }
        else next_button.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Question q_data = (Question) getArguments().getSerializable("data");
        question_title.setText(q_data != null ? q_data.getQuestionTitle() : "");

        String id = q_data.getQuestion_id();
        String getOptionsURL = "https://studev.groept.be/api/a18_sd308/GetOptions/";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                getOptionsURL + id,
                null,
                response -> {
                    List<String> options = new ArrayList<>();
                    if (response != null) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                options.add(object.get("text").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        for (String choice : options) {
                            RadioButton rb = new RadioButton(getActivity());
                            rb.setText(choice);
                            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            rb.setTextColor(getResources().getColor(R.color.almost_black));

                            rb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.almost_black)));

                            rb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            radioGroup.addView(rb);
                            radioButtons.add(rb);

                            rb.setOnCheckedChangeListener((buttonView, isChecked) -> evaluateSelection());
                        }
                    }
                },
                error -> {
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
