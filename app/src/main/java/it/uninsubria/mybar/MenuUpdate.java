package it.uninsubria.mybar;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuUpdate extends ListActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems;

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

        DocumentReference docRef = db.collection("users").document(email);

        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();
                        listItems = new ArrayList<String>();
                        if(document.contains("myMenu")) {

                            listItems.clear();
                            listItems = (ArrayList<String>) document.get("myMenu");
                            setMyAdapter();
                        }



                    }

                });


       final ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int row, long arg3) {
                listItems.remove(row);
                lv.invalidateViews();
                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }


    public void setMyAdapter(){
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();


    }



    public void addItem(View v){

        if(!editName.getText().toString().equals("") && !editPrice.getText().toString().equals("")) {

            String itemToAdd =  "-  " + editName.getText().toString() + "       " + editPrice.getText().toString() + "€";
            listItems.add(itemToAdd);
            adapter.notifyDataSetChanged();
            editName.setText("");
            editPrice.setText("");
        }
    }

    public void uploadList(View v){
        if(listItems != null) {
            Map<String, Object> myMenu = new HashMap<>();
            myMenu.put("myMenu", listItems);
            db.collection("users").document(email).set(myMenu, SetOptions.merge());
        }
    }
}
