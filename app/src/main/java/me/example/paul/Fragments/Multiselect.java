package me.example.paul.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

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

public class Multiselect extends QuestionFragment {

    private LinearLayout checkboxLayout;
    private final ArrayList<CheckBox> checkboxes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateFragment(R.layout.fragment_multiselect, inflater, container);

        //UI
        checkboxLayout = rootView.findViewById(R.id.checkboxes);

        //Listeners
        getNextButton().setOnClickListener(nextButtonListener);
        return rootView;
    }

    View.OnClickListener nextButtonListener = v -> {
        Question q_data = (Question) getArguments().getSerializable("data");

        int earnedCredits = 0;
        for (String choice : getSelections()) {
            if (getSelections().get(getSelections().size() - 1).equals(choice)) {
                Answers.getInstance().addAnswer(choice, q_data.getQuestion_id());
                earnedCredits += q_data.getReward();
            } else Answers.getInstance().addAnswer(choice, q_data.getQuestion_id());
        }
        ((SurveyActivity) getActivity()).go_to_next(earnedCredits);

    };

    private ArrayList<String> getSelections() {
        ArrayList<String> selections = new ArrayList<>();
        for (CheckBox cb : checkboxes) {
            if (cb.isChecked()) {
                selections.add(cb.getText().toString());
            }
        }
        return selections;
    }

    private void collect_data() {
        StringBuilder the_choices = new StringBuilder();
        boolean at_least_one_checked = false;
        for (CheckBox cb : checkboxes) {
            if (cb.isChecked()) {
                at_least_one_checked = true;
                the_choices.append(cb.getText().toString()).append(", ");
            }
        }

        if (at_least_one_checked) {
            if (((SurveyActivity) getActivity()).isLastQuestion()) {
                getNextButton().setText("Finish");
            }
            getNextButton().setVisibility(View.VISIBLE);
        } else {
            getNextButton().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Question q_data = (Question) getArguments().getSerializable("data");

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://studev.groept.be/api/a18_sd308/GetOptions/" + q_data.getQuestion_id(),
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
                            CheckBox cb = new CheckBox(getActivity());
                            cb.setText(choice);
                            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            cb.setTextColor(getResources().getColor(R.color.almost_black));
                            cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.almost_black)));
                            cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkboxLayout.addView(cb);
                            checkboxes.add(cb);

                            cb.setOnCheckedChangeListener((buttonView, isChecked) -> collect_data());
                        }
                    }
                },
                error -> {
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
