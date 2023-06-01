package com.climbsense.application.ui.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

public class PopupAlertDialog extends AlertDialog {

    private Activity activity;
    private Context context;

    private DatabaseAccess databaseAccess;

    public PopupAlertDialog(Activity activity, Context context) {
        super(context);

        this.activity   = activity;
        this.context    = context;

        this.databaseAccess = DatabaseAccess.getInstance(activity);

    }

    public AlertDialog popupAccountDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_account_title);
        builder.setMessage(R.string.delete_account_label);

        LinearLayout popup_layout = new LinearLayout(context);
        popup_layout.setOrientation(LinearLayout.VERTICAL);
        popup_layout.setPadding(20, 0, 20, 0);
        builder.setView(popup_layout);

        TextView popup_error_text = new TextView(context);
        popup_error_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popup_error_text.setTextColor(context.getColor(R.color.red));
        popup_layout.addView(popup_error_text);

        EditText popup_password_edit = new EditText(context);
        popup_password_edit.setHint(R.string.password);
        popup_password_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        popup_layout.addView(popup_password_edit);

        builder.setPositiveButton(R.string.control_delete, null);

        builder.setNegativeButton(R.string.control_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = popup_password_edit.getText().toString();
                        if (!password.equals(""))
                            databaseAccess.deleteUser(password, popup_error_text);
                        else
                            popup_error_text.setText(R.string.missing_field_error);
                    }
                });
            }
        });

        return dialog;
    }

    public AlertDialog popupChangeEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.change_password_text);

        LinearLayout popup_layout = new LinearLayout(context);
        popup_layout.setOrientation(LinearLayout.VERTICAL);
        popup_layout.setPadding(20, 0, 20, 0);
        builder.setView(popup_layout);

        TextView popup_error_text = new TextView(context);
        popup_error_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popup_error_text.setTextColor(context.getColor(R.color.red));
        popup_layout.addView(popup_error_text);

        EditText popup_password_edit = new EditText(context);
        popup_password_edit.setHint(R.string.password);
        popup_password_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        popup_layout.addView(popup_password_edit);

        EditText popup_email_edit = new EditText(context);
        popup_email_edit.setHint(R.string.email);
        popup_email_edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        popup_layout.addView(popup_email_edit);

        builder.setPositiveButton(R.string.control_save, null);

        builder.setNegativeButton(R.string.control_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = popup_password_edit.getText().toString();
                        String email    = popup_email_edit.getText().toString();
                        if (!password.equals("") && !email.equals("")) {
                            databaseAccess.changeEmailUser(email, password, dialog, popup_error_text);
                        } else {
                            popup_error_text.setText(R.string.missing_field_error);
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public AlertDialog popupChangePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.change_password_text);

        LinearLayout popup_layout = new LinearLayout(context);
        popup_layout.setOrientation(LinearLayout.VERTICAL);
        popup_layout.setPadding(20, 0, 20, 0);
        builder.setView(popup_layout);

        TextView popup_error_text = new TextView(context);
        popup_error_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popup_error_text.setTextColor(context.getColor(R.color.red));
        popup_layout.addView(popup_error_text);

        EditText popup_password_edit = new EditText(context);
        popup_password_edit.setHint(R.string.password);
        popup_password_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        popup_layout.addView(popup_password_edit);

        EditText popup_new_password_edit = new EditText(context);
        popup_new_password_edit.setHint(R.string.change_password_label);
        popup_new_password_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        popup_layout.addView(popup_new_password_edit);

        EditText popup_confirm_password_edit = new EditText(context);
        popup_confirm_password_edit.setHint(R.string.confirm_password);
        popup_confirm_password_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        popup_layout.addView(popup_confirm_password_edit);

        builder.setPositiveButton(R.string.control_save, null);

        builder.setNegativeButton(R.string.control_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = popup_password_edit.getText().toString();
                        String new_password = popup_new_password_edit.getText().toString();
                        String confirm_password = popup_confirm_password_edit.getText().toString();
                        if (!password.equals("") && !new_password.equals("") && !confirm_password.equals("")) {
                            if (new_password.equals(confirm_password)) {
                                databaseAccess.changePasswordUser(password, new_password, dialog, popup_error_text);
                            } else {
                                popup_error_text.setText(R.string.register_password_error);
                            }
                        } else {
                            popup_error_text.setText(R.string.missing_field_error);
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public AlertDialog popupResetPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.change_password_text);

        LinearLayout popup_layout = new LinearLayout(context);
        popup_layout.setOrientation(LinearLayout.VERTICAL);
        popup_layout.setPadding(20, 0, 20, 0);
        builder.setView(popup_layout);

        TextView popup_error_text = new TextView(context);
        popup_error_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popup_error_text.setTextColor(context.getColor(R.color.red));
        popup_layout.addView(popup_error_text);

        EditText popup_email_edit = new EditText(context);
        popup_email_edit.setHint(R.string.email);
        popup_email_edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        popup_layout.addView(popup_email_edit);

        builder.setPositiveButton(R.string.control_yes, null);

        builder.setNegativeButton(R.string.control_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = popup_email_edit.getText().toString();
                        if (!email.equals("")) {
                            databaseAccess.resetPasswordUser(email, dialog, popup_error_text);
                        } else {
                            popup_error_text.setText(R.string.missing_field_error);
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
