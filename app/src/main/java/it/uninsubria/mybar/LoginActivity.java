package it.uninsubria.mybar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.internal.firebase_auth.zzes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzv;
import com.google.firebase.auth.zzx;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String spinnerText;
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
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);
        loginPreferences = getApplicationContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        CallbackManager callbackManager = CallbackManager.Factory.create();


        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookLogin);
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "cancellato", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "errore", Toast.LENGTH_LONG).show();
            }

        });





    }

   /* private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(task.isSuccesful()){
                    FireBaseUser myuserobj = mAuth.getCurrentUser();

                    Toast toast = Toast.makeText(getApplicationContext(), "login effettuato!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);          //name nextact
                    intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                    intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "impossibile registrarsi a firebase", Toast.LENGTH_LONG).show();

                }
            }
        }
    }*/

    //login

    public void loginWindow(View v){



        TextView popUpClose;
        Button logInButton;

        dialog.setContentView(R.layout.loginpopup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        emailTxt = (EditText) dialog.findViewById(R.id.email);
        pswTxt = (EditText) dialog.findViewById(R.id.psw);
        saveLoginCheckBox = (CheckBox)dialog.findViewById(R.id.saveLoginCheckBox);

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            saveLoginCheckBox.setChecked(true);
        }

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
                        Toast toast = Toast.makeText(getApplicationContext(), "login effettuato!", Toast.LENGTH_LONG);
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
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types,android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerText = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void registrationClick(View view){


        final String email = emailReg.getText().toString().trim();
        String password = pswReg.getText().toString().trim();
        String passwordConf = pswConf.getText().toString().trim();
        final String username = userReg.getText().toString();


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

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        setUsername(username, spinnerText, email);
                        Toast toast = Toast.makeText(getApplicationContext(), "registrazione completata!", Toast.LENGTH_LONG);
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

            });
        }
    }
    @Override
    public void onBackPressed(){
    }
    //method to set user extra props
    private void setUsername(String uname,String type, String email){
        //CollectionReference collection = db.collection("users");
        Map<String, Object> user = new HashMap<>();
        user.put("username", uname);
        user.put("type", type);
        //user.put("email", email);

        db.collection("users").document(email).set(user);


    }



}
