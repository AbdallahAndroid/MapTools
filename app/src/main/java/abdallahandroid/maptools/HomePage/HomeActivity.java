package abdallahandroid.maptools.HomePage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import abdallahandroid.maptools.HomePage.AbdoRecyler;
//import abdallahandroid.maptools.Manifest;
import abdallahandroid.maptools.R;

public class HomeActivity extends AppCompatActivity {

    Context mContext;
    Activity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        mContext = getBaseContext();
        mActivity = this;

        ///// check permmsion
        checkPermissions();

        ///draw
        drawItems();

    }



    private void drawItems() {
        ////////////////// recylere
        // set a LinearLayoutManager with orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( mContext);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        // call the constructor of CustomAdapter to send the reference and data to Adapter
        AbdoRecyler customAdapter = new AbdoRecyler(mContext,mActivity);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView


        //////////////// Alpha
        LinearLayout allLayoutAlpha = findViewById(R.id.allLayoutAlpha);
        allLayoutAlpha.getBackground().setAlpha(70);
    }


    ///////////////////////////////////////////////////////////////////// code permisison

    String [] arrayAbdallahPerm = {

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,

    };


    public void checkPermissions() {

        try{

            //abdallah: add some permision
            ActivityCompat.requestPermissions(this, arrayAbdallahPerm, 1);
        } catch (Exception e){
        } catch ( Error e){
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length >= arrayAbdallahPerm.length
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    System.out.println("Permission grantResults.length " + grantResults.length);
                    //###whatetodo



                } else {
                    System.out.println("Permission denied " + grantResults.length);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied the app may cause stop!", Toast.LENGTH_SHORT ).show();


                }
                return;
            }

        }
    }




}
