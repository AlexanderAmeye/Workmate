package me.example.paul.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.example.paul.R;
import me.example.paul.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText emailLoginText, passwordLoginText;
    private ProgressDialog progressDialog;

    SessionManager sessionManager;

    //Debugging
    private String logcatTag = "Login Activity";

    //Volley
    private String loginUrl;
    private RequestQueue serverQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        emailLoginText = findViewById(R.id.usernameFieldText);
        passwordLoginText = findViewById(R.id.emailRegisterText);
        progressDialog = new ProgressDialog(this);

        Button logInButton = findViewById(R.id.registerButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        //Volley
        serverQueue = Volley.newRequestQueue(this);
        loginUrl = "http://" + PreferenceManager.getDefaultSharedPreferences(this).getString("example_text", "") + "/connect/login.php";
    }

    @Override
    protected void onStart() {  //called whenever application becomes visible
        super.onStart();
    }

    public void LoginUser() {
        final String email = emailLoginText.getText().toString().trim();
        final String password = passwordLoginText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Some fields were left empty", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Login in progress");
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) { //response message from php file
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        if (success.equals("true")) {
                            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                            startActivity(intent);
                            sessionManager.createSession(jsonObject.getJSONArray("login").getString(0), jsonObject.getJSONArray("login").getString(1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "my error :" + error, Toast.LENGTH_SHORT).show();
                    Log.i("My error", "" + error);
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("email", email);
                    map.put("password", password);
                    return map;
                }
            };
            serverQueue.add(request);

        }
    }
}
