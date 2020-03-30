package com.example.covid_19_emergency_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {
    private Spinner spinner1;
    Button loggin_button;
    private static final String TAG = "loginn";
    TextView oppen_signup, resend_otp;
    FirebaseAuth firebaseAuth;
    String verify_Id;
    PhoneAuthProvider.ForceResendingToken force_token;

    EditText numberToLogin;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            finish();
            Intent i = new Intent(login.this, MainActivity.class);
            i.putExtra("choice", 3);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(i);

        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");

        }

        final Intent i = new Intent(this, SignUp.class);
        firebaseAuth = firebaseAuth.getInstance();
        oppen_signup = findViewById(R.id.open_signup);
        loggin_button = findViewById(R.id.login_button);

        loggin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMobile();
            }
        });
        numberToLogin = findViewById(R.id.num_text);
        oppen_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(login.this, "cannot login" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Toast.makeText(login.this, "cannot login" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                verify_Id = verificationId;
                force_token = token;
                Log.e("onCodeSent", "onCodeSent");
                otpDialog();

            }
        };

    }

    int variable = 1;
    EditText otp_text;

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        String firstItem = String.valueOf(spinner1.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(spinner1.getSelectedItem()))) {
                // ToDo when first item is selected
                variable = 1;
            } else {
                variable = 2;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }

    private void otpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);

        View view = getLayoutInflater().inflate(R.layout.fragment_otp_checker, null);
        resend_otp = view.findViewById(R.id.tv_otp_resend);
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mob_no, force_token);
            }
        });

        otp_text = view.findViewById(R.id.et_otp_dig_1);
        builder.setCancelable(false);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (otp_text.getText().toString().equals("")) {
                    Toast.makeText(login.this, "Cannot leave empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_Id, otp_text.getText().toString().trim());
                signInWithPhoneAuthCredential(credential);

            }
        });
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            user.getUid();

                            Intent inten = new Intent(login.this, MainActivity.class);
                            inten.putExtra("mmobile", numberToLogin.getText().toString());
                            inten.putExtra("choice", variable);
                            finish();
                            startActivity(inten);

                        } else {
                            Toast.makeText(login.this, "failed", Toast.LENGTH_SHORT).show();

                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    String mob_no;

    private void registerMobile() {
        mob_no = "+91" + numberToLogin.getText().toString().trim();

        if (mob_no.length() != 13) {
            Toast.makeText(this, "Enter a valid 10 digit number", Toast.LENGTH_SHORT).show();
            return;
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mob_no,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}