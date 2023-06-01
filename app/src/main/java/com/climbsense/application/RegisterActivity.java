package com.climbsense.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.function.CalendarFunction;
import com.climbsense.application.function.ConversionFunction;
import com.climbsense.application.function.UsersFunction;
import com.climbsense.application.ui.other.PopupAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseAccess databaseAccess;
    private UsersFunction usersFunction;
    private PopupAlertDialog popupAlertDialog;
    private ConversionFunction conversionFunction;
    private CalendarFunction calendarFunction;

    private LinearLayout activity_register;
    private TextView form_error, change_form, form_date_edit;
    private EditText form_email, form_password, form_confirm_password, form_lastname_edit, form_firstname_edit, form_height_edit, form_weight_edit;
    private Button form_send;
    private RadioGroup form_radiogroup;

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
        setContentView(R.layout.activity_register);

        activity_register       = findViewById(R.id.activity_register);
        form_error              = findViewById(R.id.form_error);
        form_email              = findViewById(R.id.form_email);
        form_password           = findViewById(R.id.form_password);
        form_confirm_password   = findViewById(R.id.form_confirm_password);
        form_lastname_edit      = findViewById(R.id.form_lastname_edit);
        form_firstname_edit     = findViewById(R.id.form_firstname_edit);
        form_date_edit          = findViewById(R.id.form_date_edit);
        form_radiogroup         = findViewById(R.id.form_radiogroup);
        form_height_edit        = findViewById(R.id.form_height_edit);
        form_weight_edit        = findViewById(R.id.form_weight_edit);
        form_send               = findViewById(R.id.form_send);
        change_form             = findViewById(R.id.change_form);

        databaseAccess = DatabaseAccess.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();

        usersFunction = new UsersFunction(this, getBaseContext(), activity_register);
        popupAlertDialog = new PopupAlertDialog(this, getApplicationContext());
        conversionFunction = new ConversionFunction();
        calendarFunction = new CalendarFunction();

        form_date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing() && !isDestroyed()) {
                    calendarFunction.selectDate(RegisterActivity.this, form_date_edit).show();
                }
            }
        });

        form_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = form_email.getText().toString();
                String password = form_password.getText().toString();
                String lastname = form_lastname_edit.getText().toString();
                String firstname = form_firstname_edit.getText().toString();
                String birthday;
                try {
                    birthday = conversionFunction.convertDateDisplayToDBFormat(form_date_edit.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                int radiogroup = form_radiogroup.getCheckedRadioButtonId();
                String height = form_height_edit.getText().toString();
                String weight = form_weight_edit.getText().toString();

                if (!email.equals("") || !password.equals("") || !lastname.equals("") || !firstname.equals("") || birthday != null || radiogroup == -1 || !height.equals("") || !weight.equals("")) {
                    if (password.equals(form_confirm_password.getText().toString()))
                        databaseAccess.registerUser(email, password, lastname, firstname, birthday, radiogroup, Integer.valueOf(height), Integer.valueOf(weight), form_error);
                    else
                        form_error.setText(R.string.register_password_error);
                } else {
                    form_error.setText(R.string.missing_field_error);
                }
            }
        });

        change_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}