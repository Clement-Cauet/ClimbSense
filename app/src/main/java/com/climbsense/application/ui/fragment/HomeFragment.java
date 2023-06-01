package com.climbsense.application.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentHomeBinding;
import com.climbsense.application.function.ConversionFunction;
import com.climbsense.application.function.InstanceFunction;
import com.climbsense.application.function.UsersFunction;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private DatabaseAccess databaseAccess;
    private InstanceFunction instanceFunction;
    private ConversionFunction conversionFunction;
    private UsersFunction usersFunction;

    private LinearLayout fragment_home, dashboard_button, climb_button, connection_button, profile_button;
    private TextView imc_legend_text;
    private ProgressBar progressBar;
    private Marker marker;
    private MapView mapView;
    private Button start_climb_button, stop_climb_button;
    private SeekBar imc_seekbar;
    //private ColorSeekBar imc_seekbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.databaseAccess = databaseAccess.getInstance(getActivity());
        this.instanceFunction = instanceFunction.getInstance();
        this.usersFunction = new UsersFunction(getActivity(), getContext(), null);
        this.conversionFunction = new ConversionFunction();

        fragment_home       = root.findViewById(R.id.fragment_home);
        progressBar         = root.findViewById(R.id.progressBar);
        mapView             = root.findViewById(R.id.mapView);
        dashboard_button    = root.findViewById(R.id.dashboard_button);
        climb_button        = root.findViewById(R.id.climb_button);
        connection_button   = root.findViewById(R.id.connection_button);
        profile_button      = root.findViewById(R.id.profile_button);
        imc_seekbar         = root.findViewById(R.id.imc_seekbar);
        imc_legend_text     = root.findViewById(R.id.imc_legend_text);

        mapView.setVisibility(View.GONE);

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                    final boolean[] centerCamera = {false};
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull android.location.Location location) {
                            if (!centerCamera[0]) {
                                CameraAnimationsUtils.flyTo(mapView.getMapboxMap(),
                                        new CameraOptions.Builder().center(Point.fromLngLat(location.getLongitude(), location.getLatitude())).build(),
                                        new MapAnimationOptions.Builder().duration(500).build());
                                centerCamera[0] = true;
                                progressBar.setVisibility(View.GONE);
                                mapView.setVisibility(View.VISIBLE);
                            }
                        }
                    };
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });

        databaseAccess.selectInfoUser()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                Map imc_map = usersFunction.calculateIMC(querySnapshot.getDocuments().get(0));
                                imc_seekbar.setProgress((int) (double) imc_map.get("bmi"));
                                imc_seekbar.setEnabled(false);
                                imc_seekbar.getThumb().setColorFilter((int) imc_map.get("color"), PorterDuff.Mode.SRC_IN);
                                imc_legend_text.setText(getContext().getString((int) imc_map.get("type")) + " : " + imc_map.get("bmi"));
                            }
                        }
                    }
                });

        dashboard_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instanceFunction.getNavController().navigate(R.id.navigation_dashboard);
            }
        });

        climb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instanceFunction.getNavController().navigate(R.id.navigation_climb);
            }
        });

        connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instanceFunction.getNavController().navigate(R.id.navigation_connection);
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instanceFunction.getNavController().navigate(R.id.navigation_profile);
            }
        });

        return root;
    }

    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        binding = null;
    }

}