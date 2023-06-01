package com.climbsense.application.database;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.climbsense.application.LoginActivity;
import com.climbsense.application.MainActivity;
import com.climbsense.application.R;
import com.climbsense.application.function.CalendarFunction;
import com.climbsense.application.function.ConversionFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAccess {

    private Activity activity;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private CalendarFunction calendarFunction;
    private ConversionFunction conversionFunction;
    private static DatabaseAccess instance;

    //Return instance DatabaseAccess class
    public static DatabaseAccess getInstance(Activity activity) {
        if(instance == null)
            instance = new DatabaseAccess(activity);
        return instance;
    }

    //Constructor DatabaseAccess class
    public DatabaseAccess(Activity activity) {
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        calendarFunction = new CalendarFunction();
        conversionFunction = new ConversionFunction();
    }

    public FirebaseAuth getFirebaseAuth() {
        return this.firebaseAuth;
    }

    public void registerUser(String email, String password, String lastname, String firstname, String birthday, int radiogroup, int height, int weight, TextView textView) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(activity.getBaseContext(), MainActivity.class);
                            activity.startActivity(intent);
                            activity.finish();

                            addUser(lastname, firstname, birthday, radiogroup, height, weight);
                        } else {
                            textView.setText(R.string.register_exists_error);
                        }
                    }
                });

    }

    public void authenticationUser(String email, String password, TextView textView) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(activity.getBaseContext(), MainActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            textView.setText(R.string.authentication_error);
                        }
                    }
                });
    }

    public void changeEmailUser(String email, String password, Dialog dialog, TextView textView) {
        firebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().updateEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(activity, R.string.change_email_success, Toast.LENGTH_SHORT).show();
                                            } else {

                                            }
                                        }
                                    });
                        } else {
                            textView.setText(R.string.authentication_delete_error);
                        }
                    }
                });
    }

        public void changePasswordUser(String password, String new_password, Dialog dialog, TextView textView) {
            firebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().updatePassword(new_password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialog.dismiss();
                                                    Toast.makeText(activity, R.string.change_password_success, Toast.LENGTH_SHORT).show();
                                                } else {

                                                }
                                            }
                                        });
                            } else {
                                textView.setText(R.string.authentication_delete_error);
                            }
                        }
                    });
        }

    public void resetPasswordUser(String email, Dialog dialog, TextView textView) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                        } else {
                            textView.setText(R.string.email_error);
                        }
                    }
                });
    }

    public Task selectFirebase(String collection) {
        return firebaseFirestore.collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getData();
                        }
                    } else {
                        Log.d("Firebase", "Value don't exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Request error" + collection, e);
                });
    }

    public void addFirebase(String collection, Map data) {
        DocumentReference documentReference = firebaseFirestore.collection(collection).document(firebaseAuth.getUid());
        documentReference.set(data, SetOptions.merge()).addOnFailureListener(e -> {
            Log.e("Firebase", "Request error" + collection, e);
        });;
    }

    public void updateFirebase(String collection, Map data) {
        DocumentReference documentReference = firebaseFirestore.collection(collection).document(firebaseAuth.getUid());
        documentReference.update(data).addOnFailureListener(e -> {
            Log.e("Firebase", "Request error" + collection, e);
        });
    }

    public Task selectUser() {
        String collection = "users";
        return selectFirebase(collection);
    }

    public Task selectInfoUser() {
        String collection = "users";
        return firebaseFirestore.collection(collection)
                .where(Filter.equalTo(FieldPath.documentId(), firebaseAuth.getCurrentUser().getUid()))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getData();
                        }
                    } else {
                        Log.d("Firebase", "Value don't exist");
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Request error" + collection, e);
                });

    }

    public void addUser(String lastname, String firstname, String birthday, int radiogroup, int height, int weight) {

        Map<String, Object> map = new HashMap<>();
        map.put("lastName", lastname);
        map.put("firstName", firstname);
        map.put("sexe", radiogroup);
        map.put("birthday", birthday);
        map.put("height", height);
        map.put("weight", weight);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        documentReference.set(map, SetOptions.merge());

    }

    public void deleteUser(String password, TextView textView) {
        firebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("users").document(firebaseAuth.getUid()).delete();
                                                Intent intent = new Intent(activity.getBaseContext(), LoginActivity.class);
                                                activity.startActivity(intent);
                                                activity.finish();
                                            }
                                        }
                                    });
                        } else {
                            textView.setText(R.string.authentication_delete_error);
                        }
                    }
                });
    }

    public Task selectClimbs(Filter filter, Query.Direction direction) {
        String collection = "users/" + firebaseAuth.getCurrentUser().getUid() + "/climbs";
        Query query = firebaseFirestore.collection(collection);

        if (filter != null) {
            query = query.where(filter);
        } else if (direction != null) {
            query = query.orderBy("date_start", direction);
        }

        return query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getData();
                        }
                    } else {
                        Log.d("Firebase", "Value don't exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Request error" + collection, e);
                });
    }

    public void addClimb() {
        long id = new Date().getTime();
        String collection = "users/" + firebaseAuth.getCurrentUser().getUid() + "/climbs";
        Map<String, Object> map = new HashMap();
        map.put("name", "Climb " + id);
        map.put("date_start", conversionFunction.convertDateToDBFormat(new Date()));
        map.put("date_end", "");

        DocumentReference documentReference = firebaseFirestore.document(collection + "/" + "climb" + id);
        documentReference.set(map).addOnFailureListener(e -> {
            Log.e("Firebase", "Request error" + collection, e);
        });
    }

    public void updateClimb() {
        String collection = "users/" + firebaseAuth.getCurrentUser().getUid() + "/climbs";
        firebaseFirestore.collection(collection)
                .orderBy("date_start", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        Map<String, Object> map = new HashMap<>();
                        map.put("date_end", conversionFunction.convertDateToDBFormat(new Date()));

                        DocumentReference documentReference = firebaseFirestore.document(collection + "/" + documentSnapshot.getId());
                        documentReference.update(map);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Request error" + collection, e);
                });
    }

    public void updateClimb(Filter filter, Map map) {
        String collection = "users/" + firebaseAuth.getCurrentUser().getUid() + "/climbs";
        firebaseFirestore.collection(collection)
                .where(filter)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        DocumentReference documentReference = firebaseFirestore.document(collection + "/" + documentSnapshot.getId());
                        documentReference.update(map);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Request error" + collection, e);
                });
    }
}
