package it.uninsubria.mybar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Dialog dialog;
    EditText emailTxt;
    EditText pswTxt;
    CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        FirebaseApp.initializeApp(this);
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
        EditText usernameTxt;
        EditText pswTxt;
        EditText pswConf;
        EditText email;
        Button conf;
        dialog.setContentView(R.layout.registration_popup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        usernameTxt = (EditText) dialog.findViewById(R.id.username);
        pswTxt = (EditText) dialog.findViewById(R.id.psw);
        pswConf = (EditText) dialog.findViewById(R.id.pswConf);
        email = (EditText) dialog.findViewById(R.id.email);
        conf = (Button) dialog.findViewById(R.id.confirm);

        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
