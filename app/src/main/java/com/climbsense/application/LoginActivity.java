package com.climbsense.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.function.UsersFunction;
import com.climbsense.application.ui.other.PopupAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseAccess databaseAccess;
    private UsersFunction usersFunction;
    private PopupAlertDialog popupAlertDialog;

    private LinearLayout activity_login;
    private TextView form_error, reset_password, change_form;
    private EditText form_email, form_password;
    private Button form_send;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity_login  = findViewById(R.id.activity_login);
        form_error      = findViewById(R.id.form_error);
        form_email      = findViewById(R.id.form_email);
        form_password   = findViewById(R.id.form_password);
        form_send       = findViewById(R.id.form_send);
        reset_password  = findViewById(R.id.reset_password);
        change_form     = findViewById(R.id.change_form);

        databaseAccess = DatabaseAccess.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();

        usersFunction = new UsersFunction(this, getBaseContext(), activity_login);
        popupAlertDialog = new PopupAlertDialog(this, getApplicationContext());

        form_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = form_email.getText().toString();
                String password = form_password.getText().toString();
                if (!email.equals("") || !password.equals("")) {
                    databaseAccess.authenticationUser(email, password, form_error);
                } else {
                    form_error.setText(R.string.missing_field_error);
                }
            }
        });

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing() && !isDestroyed()) {
                    popupAlertDialog.popupResetPassword().show();
                }
            }
        });

        change_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}