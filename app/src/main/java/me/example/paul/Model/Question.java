package me.example.paul.Model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String id;
    private String title;
 //   private List<String> options = new ArrayList<>();
    private String type;

    public Question(String title, List<String> options) {
        this.title = title;
      //  this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionText() {
        return title;
    }

    public void setQuestionText(String title) {
        this.title = title;
    }

    //public List<String> getOptions() {
      //  return options;
   // }

   // public void setOptions(List<String> options) {
    //    this.options = options;
   // }

    public String getQuestionTitle() {
        return title;
    }

    public void setQuestionTitle(String questionTitle) {
        this.title = questionTitle;
    }

    public String getQuestionType() {
        return type;
    }

    public void setQuestionType(String questionType) {
        this.type = questionType;
    }
}
