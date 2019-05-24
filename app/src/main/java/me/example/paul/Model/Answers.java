package me.example.paul.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Answers {
    private volatile static Answers uniqueInstance;

    private JSONArray answers;

    private Answers() {
        answers = new JSONArray();
    }

    public void addAnswer(String text, String questionId) {
        JSONObject answer = new JSONObject();
        try {
            answer.put("text", text);
            answer.put("question_id", questionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        answers.put(answer);
    }

    public JSONArray getAnswers() {
        return answers;
    }

    public static Answers getInstance() {
        if (uniqueInstance == null) {
            synchronized (Answers.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Answers();
                }
            }
        }
        return uniqueInstance;
    }

    public void clear() {
        answers = new JSONArray();
    }
}
