package me.example.paul.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import me.example.paul.R;
import me.example.paul.SessionManager;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;

    private String getBalanceUrl = "https://studev.groept.be/api/a18_sd308/GetBalance/";
    private RequestQueue serverQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverQueue = Volley.newRequestQueue(this);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        Button logInActivityButton = (Button) findViewById(R.id.logInActivityButton);
        logInActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button registerActivityButton = (Button) findViewById(R.id.registerActivityButton);
        registerActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public String getLoggedinUser()
    {
        return sessionManager.getUserDetails().get("EMAIL");
    }
}
