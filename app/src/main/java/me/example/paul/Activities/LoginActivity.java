package me.example.paul.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import me.example.paul.AESCrypt;
import me.example.paul.R;
import me.example.paul.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private RequestQueue serverQueue;
    SessionManager sessionManager;

    private EditText emailField, passwordField;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //System
        serverQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        //UI
        emailField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.emailField);
        progressDialog = new ProgressDialog(this);

        Button loginButton = findViewById(R.id.registerButton);
        loginButton.setOnClickListener(v -> Login());
    }

    public void Login() {
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Some fields were left blank", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Login in progress");
            progressDialog.show();

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/GetUser/" + email, null, response -> {
                if (response.length() != 0) { //user was found for given email
                    try {
                        JSONObject object = response.getJSONObject(0);
                        String found_password = object.get("password").toString();
                        String found_username = object.get("username").toString();
                        String found_email = object.get("email").toString();

                        if (AESCrypt.decrypt(found_password).equals(password)) //correct password
                        {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            sessionManager.createSession(found_username, found_email);
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Failed to get data from server", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        e.printStackTrace();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Failed to decrypt password", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Email not recognised", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }, error -> {
                Toast.makeText(LoginActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            );
            serverQueue.add(request);
        }
    }
}