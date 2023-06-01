package com.climbsense.application.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.climbsense.application.LoginActivity;
import com.climbsense.application.R;
import com.climbsense.application.RegisterActivity;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.ui.other.PopupAlertDialog;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsersFunction {

    private DatabaseAccess databaseAccess;
    private InstanceFunction instanceFunction;
    private PopupAlertDialog popupAlertDialog;

    private LinearLayout linearLayout;
    private Activity activity;
    private Context context;

    private TextView form_error;
    private EditText form_email, form_password, form_confirm_password, form_lastname, form_firstname;

    public UsersFunction(Activity activity, Context context, LinearLayout linearLayout) {
        this.databaseAccess = DatabaseAccess.getInstance(activity);
        this.instanceFunction = InstanceFunction.getInstance();
        this.popupAlertDialog = new PopupAlertDialog(activity, context);
        this.activity = activity;
        this.linearLayout = linearLayout;
        this.context = context;
    }

    public Map calculateIMC(DocumentSnapshot document) {
        Map map = new HashMap();
        double height = document.getLong("height");
        double weight = document.getLong("weight");
        if (height != 0 && weight != 0) {
            double bmi = Math.round((weight / (height/100 * height/100)) * 100.0) / 100.0;
            int color, type;
            if (bmi >= 12 && bmi < 18) {
                color = ContextCompat.getColor(context, R.color.cyan);
                type = R.string.imc_thinness;
            } else if (bmi >= 18 && bmi < 24) {
                color = ContextCompat.getColor(context, R.color.green);
                type = R.string.imc_normal;
            } else if (bmi >= 24 && bmi < 29) {
                color = ContextCompat.getColor(context, R.color.yellow);
                type = R.string.imc_overweight;
            } else if (bmi >= 29  && bmi < 39) {
                color = ContextCompat.getColor(context, R.color.orange);
                type = R.string.imc_moderate_obesity;
            } else {
                color = ContextCompat.getColor(context, R.color.red);
                type = R.string.imc_severe_obesity;
            }
            map.put("bmi", bmi);
            map.put("color", color);
            map.put("type", type);
        }
        return map;
    }

    public void climbDownloadPDF() throws FileNotFoundException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(linearLayout.getWidth(), linearLayout.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        linearLayout.draw(canvas);

        document.finishPage(page);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/ClimbSense/", instanceFunction.getIdParameterChart() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, R.string.download_file_success, Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.download_file_error, Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    public void climbShare() {
        Bitmap bitmap = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(context.getColor(R.color.white));
        linearLayout.draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/ClimbSense/", instanceFunction.getIdParameterChart() + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(context, "com.climbsense.application.provider", file);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.setType("image/jpeg");
        activity.startActivity(Intent.createChooser(sharingIntent, activity.getResources().getString(R.string.control_share)));
        bitmap.recycle();
    }
}
