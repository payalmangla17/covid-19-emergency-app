package com.example.covid_19_emergency_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.covid_19_emergency_app.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.covid_19_emergency_app.MainActivity.reff;


public class HelpDesk extends Fragment implements MultipleChoiceDialogFragment.onMultiChoiceListener {

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    TextView selected;
    Button choice_btn, loc_btn;
    String choice_data, address_input,address_lat,address_long;
    Button submit_help;
    EditText address_user;
    Geocoder geocoder;
    List<Address> addresses;
    List<Address> acordinates;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private StringBuilder stringBuilder;
    Context mContext;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public HelpDesk() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_help_desk, container, false);



        return RootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selected = view.findViewById(R.id.tvSelectedChoices);
        selected.setText("Empty");
        choice_btn = view.findViewById(R.id.btnSelectChoices);
        submit_help = view.findViewById(R.id.submit_help);
        address_user = view.findViewById(R.id.address_helper);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        choice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        submit_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase();
            }
        });
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        loc_btn = view.findViewById(R.id.loc);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            address_user.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            address_user.setText(stringBuilder.toString());
                            getAddress(location);
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
        loc_btn.setOnClickListener(v -> {

            if (!isGPS) {
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                alertDialogBuilder
                        .setMessage(
                                "GPS is disabled on your device. Would you like to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Open Settings",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // set intent to open settings
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        getContext().startActivity(callGPSSettingIntent);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

                //Toast.makeText(getActivity(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            isContinue = true;
            stringBuilder=new StringBuilder();
            getLocation();
        });

    }
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permissionName}, permissionRequestCode);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                      //  address_user.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        getAddress(location);
                    }
                     else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                               // address_user.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                                getAddress(location);
                            }
                            else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
    public void getAddress(Location location){
        try {
         //   Toast.makeText(mContext, "dkfjhdfhjdfj", Toast.LENGTH_SHORT).show();
            geocoder = new Geocoder(mContext, Locale.ENGLISH);
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
             //   Toast.makeText(getContext(),
               //         "geocoder present", Toast.LENGTH_LONG).show();
                Address returnAddress = addresses.get(0);
                String address=returnAddress.getAddressLine(0);
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();
                String region_code = returnAddress.getCountryCode();
                String zipcode = returnAddress.getPostalCode();
                str.append(address+"  ");
                str.append(localityString + " ");
                str.append(city + " " + region_code + " ");
                str.append(zipcode + " ");

                address_user.setText(str.toString());
              //  Toast.makeText(getActivity(), str,
              //          Toast.LENGTH_SHORT).show();
               // System.out.println(str);
            } else {
                Toast.makeText(getActivity(),
                        "geocoder not present", Toast.LENGTH_SHORT).show();
            }

// } else {
// Toast.makeText(getApplicationContext(),
// "address not available", Toast.LENGTH_SHORT).show();
// }
        } catch (IOException e) {
// TODO Auto-generated catch block

            Log.e("tag", e.getMessage());
        }
    }




    public void uploadToFirebase() {
        // data will be uploaded...
        choice_data = selected.getText().toString().trim();
        address_input = address_user.getText().toString().trim();
       // Toast.makeText(getActivity(), address_input,Toast.LENGTH_SHORT).show();
        try {
            if(address_input!=null) {
                acordinates = geocoder.getFromLocationName(address_input, 5);

            }
            if (acordinates == null) {
                Toast.makeText(getActivity(), "empty fields required", Toast.LENGTH_SHORT).show();
               // return;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        if(acordinates!=null&&acordinates.size()>0) {
            Address location = acordinates.get(0);
            address_lat = String.valueOf((location.getLatitude() * 1E6));
            address_long = String.valueOf(location.getLongitude() * 1E6);
         //   Toast.makeText(getContext(),address_lat,Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(address_input) || selected.getText().toString().trim() == "Empty") {
            Toast.makeText(getActivity(), "empty fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Toast.makeText(getActivity(), "Doneee", Toast.LENGTH_SHORT).show();
        User user = new User(choice_data,address_input);
        reff.child("z_Helper_type").setValue(user);


        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Thanx for providing your Sahyog :)");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
        public void showDialog() {
        DialogFragment multiChoiceDialog = new MultipleChoiceDialogFragment(this);
        multiChoiceDialog.setCancelable(false);
        multiChoiceDialog.show(getActivity().getSupportFragmentManager(), "Multichoice Dialog");

    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemList) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        for (String str : selectedItemList) {
            stringBuilder.append(str + ", ");
        }
        selected.setText(stringBuilder);

    }

    @Override
    public void onNegativeButtonClicked() {
        //selected.setText("Dialog Cancel");
    }


}

