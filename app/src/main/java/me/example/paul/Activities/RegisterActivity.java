package me.example.paul.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import me.example.paul.AESCrypt;
import me.example.paul.R;
import me.example.paul.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private RequestQueue serverQueue;
    SessionManager sessionManager;

    private EditText usernameField, emailField, passwordField, passwordConfirmationField;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //System
        serverQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        //UI
        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        passwordConfirmationField = findViewById(R.id.passwordConfirmationField);
        progressDialog = new ProgressDialog(this);

        final Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> AttemptRegistration());

        emailField.addTextChangedListener(emailFieldTextWatcher);
        passwordField.addTextChangedListener(passwordFieldTextWatcher);
        passwordConfirmationField.addTextChangedListener(passwordConfirmationFieldTextWatcher);
    }

    TextWatcher emailFieldTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String email = emailField.getText().toString().trim();
            if (incorrectEmail(email)) {
                emailField.setBackgroundResource(R.drawable.input_box_incorrect);
            } else {
                emailField.setBackgroundResource(R.drawable.input_box_correct);
            }
        }
    };

    TextWatcher passwordFieldTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = passwordField.getText().toString().trim();

            if (password.length() < 6) {
                passwordField.setBackgroundResource(R.drawable.input_box_incorrect);
            } else {
                passwordField.setBackgroundResource(R.drawable.input_box_correct);
            }
        }
    };

    TextWatcher passwordConfirmationFieldTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = passwordField.getText().toString().trim();
            String passwordConfirmation = passwordConfirmationField.getText().toString().trim();

            if (!password.equals(passwordConfirmation)) {
                passwordConfirmationField.setBackgroundResource(R.drawable.input_box_incorrect);
            } else {
                passwordConfirmationField.setBackgroundResource(R.drawable.input_box_correct);
            }
        }
    };

    public void AttemptRegistration() {
        final String username = usernameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();
        final String passwordConfirmation = passwordConfirmationField.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Some fields were left blank", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordConfirmation)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            passwordConfirmationField.requestFocus();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            passwordField.requestFocus();

        } else if (incorrectEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
        } else {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/GetUser/" + email, null, response -> {
                if (response.length() > 0) {
                    Toast.makeText(RegisterActivity.this, "An account with that email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Register(username, password, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
            });
            serverQueue.add(request);
        }
    }

    public void Register(final String username, final String password, final String email) throws Exception {
        progressDialog.setMessage("Registration in progress");
        progressDialog.show();

        String registerUserUrl = "https://studev.groept.be/api/a18_sd308/AddUser/";
        StringRequest request = new StringRequest(Request.Method.POST, registerUserUrl + username + "/" + email + "/" + AESCrypt.encrypt(password), response -> {
            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            sessionManager.createSession(username, email);
        }, error -> {
            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };
        serverQueue.add(request);
    }

    public static boolean incorrectEmail(String email) {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}