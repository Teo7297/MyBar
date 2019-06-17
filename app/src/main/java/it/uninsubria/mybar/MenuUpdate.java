package it.uninsubria.mybar;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuUpdate extends ListActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    EditText editName;
    EditText editPrice;
    String email;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_update);

        //firebase instance setup
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        email = mFirebaseUser.getEmail();
        db = FirebaseFirestore.getInstance();

        editName = (EditText) findViewById(R.id.editName);
        editPrice = (EditText) findViewById(R.id.editPrice);
        adapter = ArrayAdapter.createFromResource(this, android.R.layout.simple_list_item_1);//TODO scaricare il menu se era gia stato creato in precedenza
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
    }


    public void addItem(View v){

        if(!editName.getText().toString().equals("") && !editPrice.getText().toString().equals("")) {

            String itemToAdd =  "-  " + editName.getText().toString() + "       " + editPrice.getText().toString() + "€";
            listItems.add(itemToAdd);
            adapter.notifyDataSetChanged();
            editName.setText("");
            editPrice.setText("");
        }
        //store listItems on firebase

    }

    public void uploadList(View v){
        Map<String, Object> myMenu = new HashMap<>();
        myMenu.put("menu", listItems);


        db.collection("menu").document(email).set(myMenu);
    }
}
