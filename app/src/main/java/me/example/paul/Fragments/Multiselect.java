package me.example.paul.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.example.paul.Activities.SurveyActivity;
import me.example.paul.Answers;
import me.example.paul.Model.Question;
import me.example.paul.R;

public class Multiselect extends Fragment {

    private TextView question_title;
    private LinearLayout checkboxLayout;
    private final ArrayList<CheckBox> checkboxes = new ArrayList<>();
    private Button next_button;
    private Button skip_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_multiselect, container, false);

        question_title = (TextView) rootView.findViewById(R.id.question_title);
        checkboxLayout = (LinearLayout) rootView.findViewById(R.id.checkboxes);

        next_button = rootView.findViewById(R.id.button_next);
        skip_button = rootView.findViewById(R.id.button_skip);

        next_button.setVisibility(View.INVISIBLE);
        skip_button.setVisibility(View.VISIBLE);

        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SurveyActivity) getActivity()).go_to_next();
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Question q_data = (Question) getArguments().getSerializable("data");

                for (String choice : getSelections()) {
                    Answers.getInstance().addAnswer(choice, " ", q_data.getQuestion_id(), q_data.getReward()/getSelections().size());
                }
                ((SurveyActivity) getActivity()).go_to_next();
            }
        });
        return rootView;
    }

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
                next_button.setText("Finish");
            }
            next_button.setVisibility(View.VISIBLE);
        } else {
            next_button.setVisibility(View.INVISIBLE);
        }
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
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> options = new ArrayList<String>();
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
                                cb.setTextColor(getResources().getColor(R.color.colorWhite));
                                cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                checkboxLayout.addView(cb);
                                checkboxes.add(cb);

                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        collect_data();
                                    }
                                });
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
