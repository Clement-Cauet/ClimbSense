package com.climbsense.application.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentDashboardBinding;
import com.climbsense.application.function.ConversionFunction;
import com.climbsense.application.function.InstanceFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private InstanceFunction instanceFunction;
    private ConversionFunction conversionFunction;

    private DatabaseAccess databaseAccess;

    private LinearLayout fragment_dashboard;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.instanceFunction = instanceFunction.getInstance();
        this.databaseAccess = databaseAccess.getInstance(getActivity());
        this.conversionFunction = new ConversionFunction();

        fragment_dashboard = root.findViewById(R.id.fragment_dashboard);

        databaseAccess.selectClimbs(null, Query.Direction.DESCENDING)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (int i = 0; i < querySnapshot.size(); i++) {

                                    DocumentSnapshot document = querySnapshot.getDocuments().get(i);

                                    LinearLayout dashboard_layout = new LinearLayout(getContext());
                                    dashboard_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    dashboard_layout.setOrientation(LinearLayout.HORIZONTAL);
                                    dashboard_layout.setId(i);
                                    dashboard_layout.setWeightSum(5);
                                    dashboard_layout.setGravity(Gravity.CENTER_VERTICAL);
                                    dashboard_layout.setBackground(getContext().getDrawable(R.drawable.rounded_background_item));

                                    fragment_dashboard.addView(dashboard_layout);

                                    TextView name_text = new TextView(getContext());
                                    name_text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
                                    name_text.setTextSize(16);
                                    name_text.setText(document.getString("name"));
                                    dashboard_layout.addView(name_text);

                                    TextView date_text = new TextView(getContext());
                                    date_text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
                                    date_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                                    date_text.setTextSize(16);
                                    try {
                                        date_text.setText(conversionFunction.convertDBFormatToDateTimeDisplay(document.getString("date_start")));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                    dashboard_layout.addView(date_text);


                                    dashboard_layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!document.getString("date_end").equals("")) {
                                                instanceFunction.setParameterChart(document.getId());
                                                instanceFunction.getNavController().navigate(R.id.navigation_chart);
                                            } else {
                                                Toast.makeText(getContext(), R.string.dashboard_access_error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }
                            }
                        }
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