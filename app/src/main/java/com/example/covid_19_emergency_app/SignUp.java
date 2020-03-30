package com.example.covid_19_emergency_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.covid_19_emergency_app.model.helper_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "siggnup";
    FirebaseAuth fauth;
    TextView loggin_page, goto_nomo, category_opener, resend_otp;
    Button signup_click;
    TextInputEditText t_name, t_age, t_mobile;
    String mobile;
    Spinner spinner;
    FirebaseDatabase mDatabase;
    DatabaseReference signupRef;
    String verify_Id;
    PhoneAuthProvider.ForceResendingToken force_token;
    int variable = 1;
    EditText otp_text;
    String mob_no;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        mDatabase = FirebaseDatabase.getInstance();
        spinner = findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        loggin_page = findViewById(R.id.open_login);

        loggin_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        t_name = findViewById(R.id.help_name);
        t_age = findViewById(R.id.help_age);
        t_mobile = findViewById(R.id.helper_number);

        signup_click = findViewById(R.id.btn_signUp);
        fauth = FirebaseAuth.getInstance();

       /* signupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.toString();
                // String value = dataSnapshot.getValue(String.cvalass);
                Log.d(TAG, "sign up value read  is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
*/


        signup_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (variable == 1) {
                    signupRef = mDatabase.getReference("Nomodular");
                } else {
                    signupRef = mDatabase.getReference("Aid_Helper");
                }
                registerMobile();
            }
        });
        fauth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Log.e("uid new :",""+firebaseAuth.getUid());

            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);
                verify_Id = verificationId;
                force_token = token;
                Log.e("onCodeSent", "onCodeSent");
                otpDialog();
            }
        };

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
            String name,age;
    private void saveDataInFirebase(String uid) {
         name = t_name.getText().toString().trim();
        age = t_age.getText().toString().trim();

        mobile = t_mobile.getText().toString().trim();

        helper_user user = new helper_user(name, mobile, age);


        Log.e("uid : ", "" + uid);
        uid = t_mobile.getText().toString();
        signupRef.child(uid).setValue(user);

    }

    private void otpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);

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
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (otp_text.getText().toString().equals("")) {
                    Toast.makeText(SignUp.this, "Invalid otp", Toast.LENGTH_SHORT).show();
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
        fauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");


                            FirebaseUser user = task.getResult().getUser();

                            saveDataInFirebase(user.getUid());

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {


                            }
                            finish();
                            Intent inten = new Intent(SignUp.this, MainActivity.class);
                            inten.putExtra("mmobile", t_mobile.getText().toString().trim());
                            inten.putExtra("choice", variable);
                            startActivity(inten);
                            // ...
                        } else {


                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    private void registerMobile() {
        mob_no = "+91" + t_mobile.getText().toString();
        name = t_name.getText().toString();
        age = t_age.getText().toString();

        Log.e("registerMobile", "mob_no length  :" + mob_no.length() + " mob no: " + mob_no);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age)) {
            Toast.makeText(SignUp.this, "empty fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mob_no.length() != 13) {
            Toast.makeText(this, "Enter a valid 10 digit number", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Please wait !!", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mob_no,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        String firstItem = String.valueOf(spinner.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(spinner.getSelectedItem()))) {
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
}

