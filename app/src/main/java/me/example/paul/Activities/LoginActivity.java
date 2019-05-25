package me.example.paul.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import me.example.paul.Utils.NFCHelper;

public class LoginActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    SessionManager sessionManager;
    NfcAdapter nfcAdapter;

    private EditText emailField, passwordField;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //System
        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAvailable())
            Toast.makeText(LoginActivity.this, "NFC unavailable", Toast.LENGTH_SHORT).show();

        //UI
        emailField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.emailField);
        progressDialog = new ProgressDialog(this);
        Button loginButton = findViewById(R.id.registerButton);
        TextView noAccountYet = findViewById(R.id.noAccount);

        //Listeners
        loginButton.setOnClickListener(loginButtonListener);
        emailField.addTextChangedListener(emailFieldTextWatcher);
        passwordField.addTextChangedListener(passwordFieldTextWatcher);
    }

    public boolean nfcAvailable() {
        return (nfcAdapter != null && nfcAdapter.isEnabled());
    }

    View.OnClickListener loginButtonListener = v -> {
        try {
            Login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else Toast.makeText(LoginActivity.this, "Empty card", Toast.LENGTH_SHORT).show();
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 1) {
            NdefRecord ndefRecord1 = ndefRecords[0];
            NdefRecord ndefRecord2 = ndefRecords[1];
            emailField.setText(NFCHelper.getTextFromNdefRecord(ndefRecord1));
            passwordField.setText(NFCHelper.getTextFromNdefRecord(ndefRecord2));
        } else Toast.makeText(LoginActivity.this, "Empty card", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        if (nfcAvailable())
            NFCHelper.enableForegroundDispatchSystem(this, nfcAdapter, LoginActivity.class);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (nfcAvailable()) NFCHelper.disableForegroundDispatchSystem(this, nfcAdapter);
        super.onPause();
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
            emailField.setBackgroundResource(R.drawable.input_box_default);
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
            passwordField.setBackgroundResource(R.drawable.input_box_default);
        }
    };

    public void Login() throws Exception {
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Some fields were left blank", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Login in progress");
            progressDialog.show();

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a18_sd308/CheckIfValidCredentials/" + email + "/" + AESCrypt.encrypt(password), null, response -> {
                if (response.length() > 0) { //user was found for given email + password combination
                    progressDialog.dismiss();
                    JSONObject object;
                    try {
                        object = response.getJSONObject(0);
                        sessionManager.createSession(object.get("username").toString(), object.get("email").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Email/password not recognised\"", Toast.LENGTH_SHORT).show();
                    passwordField.setBackgroundResource(R.drawable.input_box_incorrect);
                    progressDialog.dismiss();
                }
            }, error -> {
                Toast.makeText(LoginActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            );
            requestQueue.add(request);
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}