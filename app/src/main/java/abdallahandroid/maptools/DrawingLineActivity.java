package abdallahandroid.maptools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import abdallahandroid.mapAbdo.directionApi_routes.APIDirection_abdo;
import abdallahandroid.mapAbdo.drawing.drawingAbdo;


//public class DrawingLineActivity extends FragmentActivity {

public class DrawingLineActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

    static GoogleMap mMap;
    static Context mContext;
    static Activity mActivity;

    APIDirection_abdo routesAmdin;
    drawingAbdo mDrawing ;
    Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getBaseContext();
        mActivity = this;
        first_insideOnCreate_intilzieGoogleMap();
    }


    //////////////////////////////////////////////////////////////// first intlize map

    public void first_insideOnCreate_intilzieGoogleMap(){
        //// first: initilize google map then   GoogleMap mMap;
        if (map_CheckGooglePlayServicesInstalledInThisDevice()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            map_initilizeMapFragment();
        } //check google service
    }


    //f; في حالة المستخدم غير مثبت google play service in his device
    private boolean map_CheckGooglePlayServicesInstalledInThisDevice() {
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext); //GooglePlayServicesUtil.isGooglePlayServicesAvailable(this); deprecated
        if (isAvailable == ConnectionResult.SUCCESS) {
            System.out.println("map - map_CheckGooglePlayServicesInstalledInThisDevice: yes");
            return true;
        } else {
            System.out.println("map - map_CheckGooglePlayServicesInstalledInThisDevice: no");
            Toast.makeText(mContext, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    //f; must::   activity must implements OnMapReadyCallback
    private boolean map_initilizeMapFragment() {
        if (mMap == null) {
            SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)); //
            mapFrag.getMapAsync(this);
            System.out.println("map - map_initilizeMapFragment() mapFragment: " + mapFrag);
        }
        return (mMap != null);
    }

    //////////////////////////////////////////////////////


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        System.out.println("map - onMapReady() " + mMap);

//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map_intiButtonGoToMyLocation();

        map_initGoogleApiClientObject();

        //### got to this point
        // map_goToLocation(30.127, 31.26, 15);

        getCurrentLatAndLongNumbers();

        map_informationWindows();

        map_clickOnMapItself();

        map_clickOnMarkerItSelf();

        map_dragMarkerMoveIt();

       // routesAmdin = new APIDirection_abdo(mMap, mContext);  //funciton draw routes

        mDrawing = new drawingAbdo(mMap, mContext);  //drawing line/cirecle/polgonsShapes

    } //end onRaadyMap();

    /////////////////////////////////////////////////////////////////// map Drag/click/long_click  + marker_click

    //f; يعني ماذا تفعل بعد انتهاء تحريك ال marker : user drag marker on map
    private void map_dragMarkerMoveIt() {

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder gc = new Geocoder(mContext);
                List<Address> list = null;
                try {
                    LatLng mLatLng = marker.getPosition(); // mPolyLine initlize LatLng object
                    list = gc.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                    Address add = list.get(0);
                    String locality = add.getLocality();
                    String counteryToSnippet = add.getCountryName(); //f; اسم المكان في اني بلد
                    //after that
                    map_markerCreate(mMarker, true, locality, counteryToSnippet, add.getLatitude(), add.getLongitude(), false, false, 0, true, true);
                } catch (Exception e) {e.printStackTrace();}
            }
        });
    }


    //f; لما تضغط علي هذا marker يفعل شيء معين
    private void map_clickOnMarkerItSelf() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String msg = marker.getTitle() + " (" +
                        marker.getPosition().latitude + ", " +
                        marker.getPosition().longitude + ")";
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }


    //f; فائدتها عندما يقف فترة طوية علي اي جزء من الخريطة يأتي ب lat and long numbers
    private void map_clickOnMapItself() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                System.out.println("map - clickLongOnAnyPointOfMap click ");
                Geocoder gc = new Geocoder(mContext);
                List<Address> list = null;
                try {
                    //appearLatAndLng
                    list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    Address add = list.get(0);
                    String locality = add.getLocality();
                    String counteryToSnippet = add.getCountryName(); //f; اسم المكان في اني بلد

                    Marker markerReturn = map_markerCreate(mMarker, true, locality, counteryToSnippet, latLng.latitude, latLng.longitude, false, false, 0, true, true);

                    //drawline
                    //#### f لو عاوز create polyLine ;map_drawLine_polyLineBetweenTwoPoint_manager(markerReturn);
                    mDrawing.map_drawLine_polyLineBetweenTwoPoint_manager(markerReturn);

                    //draw polgon شكل مثلث او مربع
                    //map_drawLine_polygonMulitPoint_manger(markerReturn,true);

                    //draw circle
                    ///### map_drawLine_circle_manager(markerReturn, latLng, 1000, true);

                    //drawRoutes
                    //### routesAmdin.map_drawroutes_BetweenTwoPoint_manger(markerReturn);
                } catch (Exception e) {
                    System.out.println("map - clickLongOnAnyPointOfMap exception " + e);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }



    /////////////////////////////////////////////////////////////// initlize map code

    private void map_informationWindows() {
        if (mMap != null) {   //mMap it's instance object of class of GoogleMap
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }
                @Override
                public View getInfoContents(Marker marker) {
                    View v = mActivity.getLayoutInflater().inflate(R.layout.inf_windows, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tvLocality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tvLng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tvSnippet);
                    //get lat and lng and title and counteryName
                    LatLng latLng = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + latLng.latitude);
                    tvLng.setText("Longitude: " + latLng.longitude);
                    tvSnippet.setText(marker.getSnippet());
                    return v;
                }
            });
        } //end check null
    }


    ////////////////////////////////////////////////////////// go to location

    // mus: write inside onReadyMap() overriden method
    private void map_goToLocation(double latitude, double longitude, int zoom) {
        LatLng objLatLong = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(objLatLong);
        mMap.moveCamera(cameraUpdate);
        //j القرب من الكورة الارضية يكون بين رقم no 2 to 21   ( ابعد حاجة هي رقم 2 )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(objLatLong, zoom));
    }


    //////////////////////////////////////////////////// another method

    //f; فائدتها كتابة العنوان هنا وسوف يأتي بالخط long and lat لكي تسطيع بعدها فتح الخريطة مباشرة علي العنوان
    public void map_serachByWordLocation(String searchString) {
        try {
            //start reveser word to serach to long and lat
            System.out.println("map - map_serachByWordLocation word: " + searchString);
            Geocoder gc = new Geocoder(mContext);
            List<Address> list = gc.getFromLocationName(searchString, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                String locality = address.getLocality();
                String counteryToSnippet = address.getCountryName(); //f; اسم المكان في اني بلد
                double lat = address.getLatitude();  //get lat
                double lng = address.getLongitude();  //get long
                map_goToLocation(lat, lng, 15);  //this method to open map after get lat,long
                //marker if you want to put
                mMarker = map_markerCreate(mMarker, true,
                        searchString, counteryToSnippet, lat, lng  ,
                        false,
                        true, R.drawable.map_marker,
                        true,
                        true);
                //print
                Toast.makeText(mContext, "Found: " + locality, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            System.out.println("map - map_serachByWordLocation exception " + e);
        }
    }

    //////////////////////////////////////////////////// marker methods


    public Marker map_markerCreate( Marker marker, boolean removeThisOldMarkerFromMap,
                                    String titleOfMarker, String countryAtSnippet, double lat, double lng,
                                    boolean changeColor,
                                    boolean putResourceDrawable, int drwableIdResource,
                                    boolean avaliableDragMarker,
                                    boolean showInfoWindowsNow){
        Marker mReturn = null;
        //remove old
        if (removeThisOldMarkerFromMap) {
            if (marker != null){  marker.remove();  marker = null;  }
        }
        //create marker
        MarkerOptions options = new MarkerOptions().title(titleOfMarker).position(new LatLng(lat, lng));
        if (countryAtSnippet.length() > 0) {options.snippet(countryAtSnippet);} //put snippet وهي التي تقول تفاصيل عن المكان في اني بلد
        if (avaliableDragMarker) options.draggable(true);  //f; تستيطع تحريك الصورة علي الخريطة بتاعة marker
        //change color
        if (changeColor)options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); //HUE_BLUE هذا اللون الذي تريده
        if (putResourceDrawable) options.icon(BitmapDescriptorFactory.fromResource(drwableIdResource)); //as drwableIdResource: R.drwable.marker
        //finish putting option
        mReturn = mMap.addMarker(options);
        //show the infoWindows of marker after create new marker
        if (showInfoWindowsNow)mReturn.showInfoWindow();
        return mReturn;
    }

    /////////////////////////////////////////////////// go to current location

    //f; فائدة هذه الميسود انها تظهر الزرار لوحده شكل الدراع السفينة الذي تضغط عليه يرجعك الي مكانك الذي انت فيه علي الخريطة. ويظهر مكانك علي شكل نقطة
    //f; يجبب يكون المستخدم مشغل gps setting
    public boolean map_intiButtonGoToMyLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return true;
        }
        mMap.setMyLocationEnabled(true);

        return false;
    }

    /////////////////////////////////////////////////// implements GoogleApiClient

    private GoogleApiClient mLocationClient;  //write inside class direct

    private void map_initGoogleApiClientObject() {
        mLocationClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();
    }


    //f; لكي تأتي بأرقام الطول والعرض
    private Location mLocation;
    private void getCurrentLatAndLongNumbers() {
        //check premession
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        //download lat and long numbers
        FusedLocationProviderClient fused = new FusedLocationProviderClient(mContext);
        Task<Location> taskFused = fused.getLastLocation();
        taskFused.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                mLocation = task.getResult();
                if (mLocation != null) {
                    double lat = mLocation.getLatitude();
                    double lang = mLocation.getLongitude();
                    System.out.println("map - onLocationChanged  lat: " + lat + " /lang: " + lang);
                    ////#doWantYouWant
                    map_goToLocation(lat, lang, 17);
                }
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {   //f; لما يكون موصل gps work ok
        System.out.println("map - GoogleApiClient onConnected ok ");
    }

    @Override
    public void onConnectionSuspended(int i) {             //f; when stop gps
        System.out.println("map - GoogleApiClient onConnected Suspended " );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("map - GoogleApiClient onConnected Failed " );
    }






}
