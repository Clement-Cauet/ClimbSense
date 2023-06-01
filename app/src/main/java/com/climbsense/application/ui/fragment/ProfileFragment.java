package com.climbsense.application.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.climbsense.application.LoginActivity;
import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentProfileBinding;
import com.climbsense.application.function.CalendarFunction;
import com.climbsense.application.function.ConversionFunction;
import com.climbsense.application.function.InstanceFunction;
import com.climbsense.application.ui.other.PopupAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private DatabaseAccess databaseAccess;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private InstanceFunction instanceFunction;
    private ConversionFunction conversionFunction;
    private CalendarFunction calendarFunction;
    private PopupAlertDialog popupAlertDialog;

    private LinearLayout fragment_profile;
    private TextView profile_date_edit;
    private EditText profile_lastname_edit, profile_firstname_edit, profile_height_edit, profile_weight_edit;
    private Button profile_save_button, email_button, password_button, logout_button, delete_button;
    private ImageButton profile_image_button;
    private RadioGroup profile_radiogroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.databaseAccess     = DatabaseAccess.getInstance(getActivity());
        this.firebaseAuth       = FirebaseAuth.getInstance();
        this.firebaseFirestore  = FirebaseFirestore.getInstance();
        this.instanceFunction   = InstanceFunction.getInstance();
        this.conversionFunction = new ConversionFunction();
        this.calendarFunction   = new CalendarFunction();
        this.popupAlertDialog   = new PopupAlertDialog(getActivity(), getContext());

        fragment_profile        = root.findViewById(R.id.fragment_profile);
        profile_image_button    = root.findViewById(R.id.profile_image_button);
        profile_lastname_edit   = root.findViewById(R.id.form_lastname_edit);
        profile_firstname_edit  = root.findViewById(R.id.form_firstname_edit);
        profile_radiogroup      = root.findViewById(R.id.form_radiogroup);
        profile_date_edit       = root.findViewById(R.id.form_date_edit);
        profile_height_edit     = root.findViewById(R.id.form_height_edit);
        profile_weight_edit     = root.findViewById(R.id.form_weight_edit);
        profile_save_button     = root.findViewById(R.id.profile_save_button);
        email_button            = root.findViewById(R.id.email_button);
        password_button         = root.findViewById(R.id.password_button);
        logout_button           = root.findViewById(R.id.logout_button);
        delete_button           = root.findViewById(R.id.delete_button);

        databaseAccess.selectInfoUser()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                profile_lastname_edit.setText(document.getString("lastName"));
                                profile_firstname_edit.setText(document.getString("firstName"));
                                profile_radiogroup.check(document.getLong("sexe").intValue());
                                try {
                                    profile_date_edit.setText(conversionFunction.convertDBFormatToDateDisplay(document.getString("birthday")));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                profile_height_edit.setText(String.valueOf(document.getLong("height").intValue()));
                                profile_weight_edit.setText(String.valueOf(document.getLong("weight").intValue()));
                            }
                        }
                    }
                });

        instanceFunction.setImageUriAward(profile_image_button);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/ClimbSense/profile.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        profile_image_button.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bitmap != null) {
                    float scaleWidth = ((float) v.getWidth()) / bitmap.getWidth();
                    float scaleHeight = ((float) v.getHeight()) / bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    profile_image_button.setImageBitmap(scaledBitmap);
                }
            }
        });

        profile_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 3);
            }
        });

        profile_date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarFunction.selectDate(getContext(), profile_date_edit);
            }
        });

        profile_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("lastName", profile_lastname_edit.getText().toString());
                data.put("firstName", profile_firstname_edit.getText().toString());
                data.put("sexe", profile_radiogroup.getCheckedRadioButtonId());
                try {
                    data.put("birthday", conversionFunction.convertDateDisplayToDBFormat(profile_date_edit.getText().toString()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                data.put("height", Integer.valueOf(profile_height_edit.getText().toString()));
                data.put("weight", Integer.valueOf(profile_weight_edit.getText().toString()));

                databaseAccess.updateFirebase("users", data);
            }
        });

        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAlertDialog.popupChangeEmail().show();
            }
        });

        password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAlertDialog.popupChangePassword().show();
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAlertDialog.popupAccountDelete().show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}