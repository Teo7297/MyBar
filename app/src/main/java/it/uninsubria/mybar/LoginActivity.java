package it.uninsubria.mybar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.firebase_auth.zzes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzv;
import com.google.firebase.auth.zzx;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Dialog dialog;
    EditText emailTxt;
    EditText pswTxt;
    CheckBox saveLoginCheckBox;
    EditText userReg;
    EditText pswReg;
    EditText pswConf;
    EditText emailReg;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    FirebaseDatabase database;
    DatabaseReference myRef; // usare myRef = database.getReference("message"); e myRef.setValue("ciao")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);
        loginPreferences = getApplicationContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            saveLoginCheckBox.setChecked(true);
        }



    }

    //login

    public void loginWindow(View v){



        TextView popUpClose;
        Button logInButton;

        dialog.setContentView(R.layout.loginpopup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        emailTxt = (EditText) dialog.findViewById(R.id.email);
        pswTxt = (EditText) dialog.findViewById(R.id.psw);

        if (saveLogin) {
            emailTxt.setText(loginPreferences.getString("email", ""));
            pswTxt.setText(loginPreferences.getString("password", ""));
        }

        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




        dialog.show();
    }

    public void LoginClick(View view){


        String email = emailTxt.getText().toString().trim();
        String password = pswTxt.getText().toString().trim();



        if(email.isEmpty() || password.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message).setTitle(R.string.login_error_title).setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else{
            if (saveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("email", email);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.apply();                               //apply for async process doesnt block ui main thread
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.apply();
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast toast = Toast.makeText(getApplicationContext(), "login succesful", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);          //name nextact
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(task.getException().getMessage()).setTitle(R.string.login_error_title).setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }






    //registration


    public void registrationWindow(View v){
        TextView popUpClose;

        Button conf;
        dialog.setContentView(R.layout.registration_popup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        userReg = (EditText) dialog.findViewById(R.id.username);
        pswReg = (EditText) dialog.findViewById(R.id.psw);
        pswConf = (EditText) dialog.findViewById(R.id.pswConf);
        emailReg = (EditText) dialog.findViewById(R.id.email);


        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void registrationClick(View view){


        String email = emailReg.getText().toString().trim();
        String password = pswReg.getText().toString().trim();
        String passwordConf = pswConf.getText().toString().trim();




        if(email.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message).setTitle(R.string.login_error_title).setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(!password.equals(passwordConf)){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.psw_not_equal).setTitle(R.string.psw_error).setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else{
            if (saveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("email", email);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.apply();                               //apply for async process doesnt block ui main thread
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.apply();
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //createNewUser(task.getResult().getUser());
                        Toast toast = Toast.makeText(getApplicationContext(), "registration succesful", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(task.getException().getMessage()).setTitle(R.string.login_error_title).setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                /*private void createNewUser(FirebaseUser userFromRegistration) {
                    String username = "username";
                    String email = userFromRegistration.getEmail();
                    String userId = userFromRegistration.getUid();


                    myRef = database.getReference("users");
                    myRef.setValue(userId);
                   // myRef.child("users").child(userId).setValue(username);

                }*/
            });
        }
    }


}
