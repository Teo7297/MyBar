package it.uninsubria.mybar;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);


    }

    /*@Override


    private void updateUI(FirebaseUser currentUser) {
        //se l'utente è registrato accede in automatico se no va alla schermata iniziale/aggiungere un or nell if per controllare la spunta per ricordare l'accesso
    }*/

    public void loginWindow(View v){
        TextView popUpClose;
        EditText usernameTxt;
        EditText pswTxt;
        dialog.setContentView(R.layout.loginpopup);
        popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        usernameTxt = (EditText) dialog.findViewById(R.id.user);
        pswTxt = (EditText) dialog.findViewById(R.id.psw);

        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
