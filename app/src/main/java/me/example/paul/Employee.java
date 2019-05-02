package me.example.paul;

import java.util.ArrayList;

import me.example.paul.Model.Question;

public class Employee {

    private int id;
    private String name;
    private String email;
    private String password;
    private ArrayList<Question> unansweredQuestions;
    private ArrayList<Question> answeredQuestions;

    public Employee(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getUnansweredQuestions()
    {
        return new ArrayList<>();
    }


    public void addQuestion(Question q)
    {
        unansweredQuestions.add(q);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
