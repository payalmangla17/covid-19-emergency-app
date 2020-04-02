package com.example.covid_19_emergency_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.covid_19_emergency_app.model.User;

import java.util.ArrayList;

import static com.example.covid_19_emergency_app.MainActivity.reff;


public class HelpDesk extends Fragment implements MultipleChoiceDialogFragment.onMultiChoiceListener {

    TextView selected;
    Button choice_btn;
    String choice_data, address_input;
    Button submit_help;
    EditText address_user;

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
    }

    public void uploadToFirebase() {
        // data will be uploaded...
        choice_data = selected.getText().toString().trim();
        address_input = address_user.getText().toString().trim();

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
