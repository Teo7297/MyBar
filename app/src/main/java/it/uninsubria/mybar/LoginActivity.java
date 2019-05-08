package it.uninsubria.mybar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        mAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);


    }

    /*@Override


    private void updateUI(FirebaseUser currentUser) {
        //se l'utente è registrato accede in automatico se no va alla schermata iniziale/aggiungere un or nell if per controllare la spunta per ricordare l'accesso
    }*/

    public void loginWindow(View v){
        TextView popUpClose;
        final EditText emailTxt;
        final EditText pswTxt;
        Button logInButton;

        dialog.setContentView(R.layout.loginpopup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        emailTxt = (EditText) dialog.findViewById(R.id.email);
        pswTxt = (EditText) dialog.findViewById(R.id.psw);
        logInButton = (Button)findViewById(R.id.confirm);

        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email = emailTxt.getText().toString().trim();
                String password = pswTxt.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message).setTitle(R.string.login_error_title).setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else{
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, ActivityMain.class);          //name nextact
                                intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                                intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                startActivity(intent);
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
        });
        dialog.show();
    }
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
