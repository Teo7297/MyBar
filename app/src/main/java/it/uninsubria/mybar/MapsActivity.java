package it.uninsubria.mybar;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{


    //location permissions
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    Dialog dialog;
    Dialog menuDialog;
    FirebaseFirestore db;
    String username;
    String tipo;
    String email;
    Marker myMarker;
    private String barName;
    TextView userText;
    GeoPoint oldMarker;
    Marker myOldMarker;
    int userType;  //0: bar owner -- 1: bar client


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dialog = new Dialog(this);
        menuDialog = new Dialog(this);




        //firebase instance setup
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        email = mFirebaseUser.getEmail();
        db = FirebaseFirestore.getInstance();

        //final String mUserId = mFirebaseUser.getUid();

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(email)){
                                    username = document.get("username").toString();
                                    if(document.get("type").equals("possiedo un bar")){
                                        tipo = "possessore del bar:";
                                        userType = 0;
                                        barName = (String)document.get("bar name");
                                        oldMarker = (GeoPoint) document.get("bar location");
                                    } else {tipo = "cliente"; userType = 1;}

                                }
                                Log.d("succ", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("fail", "Error getting documents.", task.getException());
                        }
                    }
                });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void profilePopup(View view){
        dialog.setContentView(R.layout.profile_popup);
        ImageButton image = (ImageButton) dialog.findViewById(R.id.imageButton);
        Button setBarLoc = (Button) dialog.findViewById(R.id.setBarLoc);
        Button editMenu = (Button) dialog.findViewById(R.id.editMenu);
        EditText barNameText = (EditText) dialog.findViewById(R.id.barNameText);


        if(userType == 1) {
            barNameText.setFocusable(false);
            barNameText.setVisibility(View.INVISIBLE);
            barNameText.setClickable(false);
            setBarLoc.setClickable(false);
            setBarLoc.setFocusable(false);
            setBarLoc.setVisibility(View.INVISIBLE);
            editMenu.setClickable(false);
            editMenu.setFocusable(false);
            editMenu.setVisibility(View.INVISIBLE);
        }
        userText = (TextView)dialog.findViewById(R.id.username_text);
        String stringToSet;

        //profile imageView click handler
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO caricare una foto, ricorda di usare setBackground per auto settare le misure
            }
        });
        if(barName != null){
            stringToSet = "Utente:  "+username + "\n"+"\n"+ tipo + "\n" + barName;
        }else {
            stringToSet = "Utente:  " + username + "\n" + "\n" + tipo;
        }
        userText.setText(stringToSet);


        TextView popUpClose = (TextView) dialog.findViewById(R.id.txtClose);
        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void setBarLocation(View view){
        Location location = mMap.getMyLocation();
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();
        final LatLng latLng = new LatLng(lat, lng);

        //get bar name from edit txt
        EditText barNameText = (EditText) dialog.findViewById(R.id.barNameText);
        if((barName = barNameText.getText().toString()).equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("inserire nome bar")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("AGGIUNGI BAR");
            builder.setMessage("il tuo bar si trova qui?"+"\n"+"se hai gia posizionato il tuo bar altrove i dati vengono sovrascritti")
                    .setCancelable(false)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface alertDialog, int id) {
                            //update geopoint to firestore
                            GeoPoint geoPoint = new GeoPoint(lat, lng);

                            Map<String, Object> myBarLocation = new HashMap<>();
                            myBarLocation.put("bar location", geoPoint);
                            myBarLocation.put("bar name", barName);
                            db.collection("users").document(email).set(myBarLocation, SetOptions.merge());


                            //place marker on map and remove old marker

                            if(myOldMarker != null)
                                myOldMarker.remove();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(barName);
                            myMarker = mMap.addMarker(markerOptions);

                            myOldMarker = myMarker;
                            dialog.dismiss();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface alertDialog, int which) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

    }


    public void logout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LOGOUT");
        builder.setMessage("vuoi uscire?")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertDialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Toast toast = Toast.makeText(getApplicationContext(), "logout effettuato!", Toast.LENGTH_LONG);
                        toast.show();
                        Intent loginIntent = new Intent(MapsActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alertDialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    @Override
    public void onBackPressed(){
        //nothing happens
    }
    public void updateMenu(View view){
        Intent menuIntent = new Intent(MapsActivity.this, MenuUpdate.class);
        startActivity(menuIntent);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        final FloatingActionButton goTo = (FloatingActionButton) findViewById(R.id.goToMaps);
        final FloatingActionButton showMenu = (FloatingActionButton) findViewById(R.id.showMenu);
        showMenu.hide();
        goTo.hide();
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        downloadAllBars();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                goTo.hide();
                goTo.setClickable(false);
                goTo.setFocusable(false);
                showMenu.hide();
                showMenu.setClickable(false);
                showMenu.setFocusable(false);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //now visible
                goTo.setClickable(true);
                goTo.setFocusable(true);
                goTo.show();
                showMenu.setClickable(true);
                showMenu.setFocusable(true);
                showMenu.show();
                myMarker = marker;
                marker.showInfoWindow();


                return true;
            }});

    }
    public void goTo(View view){
        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +myMarker.getPosition().latitude+","+myMarker.getPosition().longitude));
        startActivity(navigation);
    }

    ArrayList<String> barMenu;
    ArrayAdapter<String> Menuadapter;
    public void showMenu(View v){

        menuDialog.setContentView(R.layout.menu_popup);
        //aggiungi eventuali elementi del dialog


        final GeoPoint geoPoint = new GeoPoint(myMarker.getPosition().latitude, myMarker.getPosition().longitude);

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.get("bar location") != null && document.get("bar location").equals(geoPoint)){
                            if(document.contains("myMenu")) {
                                barMenu = (ArrayList<String>) document.get("myMenu");
                                adaptList(barMenu);

                            }else barMenu = null; adaptList(barMenu);
                            break;
                        }
                    }
                }
            }
        });


    }
    public void adaptList(ArrayList<String> aList){
        ListView menuList = (ListView) menuDialog.findViewById(R.id.menuList);
        if(barMenu != null) {
            Menuadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    barMenu);
            menuList.setAdapter(Menuadapter);
            Menuadapter.notifyDataSetChanged();
            menuDialog.show();


        }else{
            barMenu = new ArrayList<String>();
            barMenu.add("nessun elemento");
            Menuadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    barMenu);
            menuList.setAdapter(Menuadapter);
            Menuadapter.notifyDataSetChanged();
            menuDialog.show();
            //barMenu.clear();
        }
    }



    private void downloadAllBars() {

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.get("bar location") != null){
                            GeoPoint point = (GeoPoint) document.get("bar location");
                            String name = (String)document.get("bar name");
                            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                            MarkerOptions mOpt = new MarkerOptions();
                            mOpt.position(latLng);
                            mOpt.title(name);
                            Marker marker = mMap.addMarker(mOpt);
                            if(document.getId().equals(email)){
                                myOldMarker = marker;
                            }

                        }
                        Log.d("succ", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w("fail", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void enableMyLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "riposizionamento", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
