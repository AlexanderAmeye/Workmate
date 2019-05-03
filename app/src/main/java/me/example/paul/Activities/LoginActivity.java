package me.example.paul.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.example.paul.AESCrypt;
import me.example.paul.R;
import me.example.paul.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText emailLoginText, passwordLoginText;
    private ProgressDialog progressDialog;

    SessionManager sessionManager;

    private String loginUserURL = "https://studev.groept.be/api/a18_sd308/UserLogin/";
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

        serverQueue = Volley.newRequestQueue(this);
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

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, loginUserURL + email, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONArray array = response;
                    if (array.length() != 0) //email was found
                    {
                        try {
                            JSONObject object = array.getJSONObject(0);
                            String found_password = object.get("password").toString();
                            String found_username = object.get("username").toString();
                            String found_email = object.get("password").toString();

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
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
            );
            serverQueue.add(request);
        }
    }
}