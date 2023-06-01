package com.climbsense.application.ui.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentConnectionBinding;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.camera.CameraConfigurationUtils;

public class ConnectionFragment extends Fragment {

    private FragmentConnectionBinding binding;

    private DatabaseAccess databaseAccess;
    private CodeScanner codeScanner;

    private LinearLayout fragment_connexion;
    private EditText code_scanner_edit;
    private Button code_scanner_button;
    private CodeScannerView code_scanner_view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentConnectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.databaseAccess = databaseAccess.getInstance(getActivity());

        fragment_connexion  = root.findViewById(R.id.fragment_connection);
        code_scanner_view   = root.findViewById(R.id.code_scanner_view);
        code_scanner_edit   = root.findViewById(R.id.code_scanner_edit);
        code_scanner_button = root.findViewById(R.id.code_scanner_button);

        codeScanner = new CodeScanner(requireContext(), code_scanner_view);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        code_scanner_edit.setText(result.getText());

                        codeScanner.releaseResources();
                        codeScanner.startPreview();
                    }
                });
            }
        });

        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.startPreview();

        code_scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code_scanner_edit.getText();
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