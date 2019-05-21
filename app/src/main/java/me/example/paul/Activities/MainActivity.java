package me.example.paul.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import me.example.paul.R;
import me.example.paul.SessionManager;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //System
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        //UI
        Button logInActivityButton = findViewById(R.id.logInActivityButton);
        Button registerActivityButton = findViewById(R.id.registerActivityButton);

        //Listeners
        logInActivityButton.setOnClickListener(loginButtonListener);
        registerActivityButton.setOnClickListener(registerButtonListener);
    }

    View.OnClickListener loginButtonListener = v -> {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    };

    View.OnClickListener registerButtonListener = v -> {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    };
}
