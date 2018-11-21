package abdallahandroid.maptools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class DrawingCircle extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //LocationListener

    private GoogleMap mMap;
    EditText editText1;
    Button button1;
    Context mContext;
    Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getBaseContext();

        //button flat
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLatAndLongNumbers();
            }
        });

        //// first: initilize google map then   GoogleMap mMap;
        if (map_CheckGooglePlayServicesInstalledInThisDevice()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            map_initilizeMapFragment();
        } //check google service


        //EditText
        editText1 = (EditText) findViewById(R.id.editText1);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //button serach
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = editText1.getText().toString();
                map_serachByWordLocation(searchString);
                editText1.setText(""); //empty agin

            }
        });

    }


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

        map_clickLongOnAnyPointOfMap_appearLatAndLng();

        map_markerClickListener();

        map_dragMarkerMoveIt();

    } //end onRaadyMap();

    ////////////////////////////////////////////////////////////// google licensis لم تعد يهتم بها
//
//    //google License : لازم تطبعها علي كلام الفديو lynda it's requipment
//
//    public void printGoogleLicense(Context mContext, TextView tvToDisplayLicense){
//    //    String licenseText = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
//
//        //f; بسبب التأخر في التحميل ممكن تحمل عن طريق Task class
//        //TextView tv = (TextView) findViewById(R.id.legal);
//        if (licenseText == null) {
//            Toast.makeText(mContext, "Google Play services isn't available on this device", Toast.LENGTH_SHORT).show();
//        } else {
//            tvToDisplayLicense.setText(licenseText);
//        }
//    }

    ////////////////////////////////////////////////////////////// draw circle

    private Circle shapeCircle;
    private List<Marker> markerListCircle = new ArrayList<>();

    //f; فائدتها ترسم دائرة ثابتة في مركز قط ععين
    //    - LatLng  pLatLung :  this object LatLng obj = new LatLng (latNumber, lngNubmer);  بيأتي عن طريق Geocdoer class
    public void map_drawLine_circle_manager(Marker mark, LatLng pLatLung, int radus, boolean wantRemoveOldCircle){
        //check remove old circle
        if (wantRemoveOldCircle ) if (shapeCircle != null ){
            shapeCircle.remove();
            shapeCircle = null;
            for (Marker m: markerListCircle ){
                m.remove();
            }
            markerListCircle.clear();
        }
        //option of circle : شكلها وحجمها ولونها ومركز القطر بتعاها
        CircleOptions options = new CircleOptions();
        options.fillColor(0x330000ff);
        options.strokeWidth(3);
        options.strokeColor(Color.BLUE);
        options.center(pLatLung);  // عني مركز بتاعها
        options.radius(radus); // حجمها
        shapeCircle = mMap.addCircle(options); // now print shape
        //add new marker to list of markers
        markerListCircle.add(mark);
    }

    ///////////////////////////////////////////////////////////// draw polypong multi points

    private int POLYGON_POINTS = 3; // example 5 or 3  اهم واحد يتغير  //ex: 3 تعني شكل مثلث  //ex4: يعني شكل مربع  // f; هي عدد النقط التي عندما تكتمل ترسم المطلق من عدد هذه النقط
    private List<Marker> markerList = new ArrayList<>();
    private Polygon shape;

    //f; function: ترسم مثلا شكل مثلث لو ثلاثة نقط ام ل و شكل 5 نقط تعلم علي شكل حقل من 5 زواية
    //        -  wantToRemoveOldShape:  فائدته هل تريد ازالة الشكل القديم الذي صمم ام ترد بقاء الشكل القديم الذي حددته
    public void map_drawLine_polygonMulitPoint_manger(Marker mSerachToAdd, boolean wantToRemoveOldShape){
        //check to remove old
        if (markerList.size() == POLYGON_POINTS ){
            removeAllMarkerList();                      // remove marker old
            if (wantToRemoveOldShape){shape.remove();} //remove shape old
        }
        //add this marker to list
        markerList.add(mSerachToAdd);
        // draw when complete marker
        if (markerList.size() == POLYGON_POINTS ) printShape();

    }


    private void printShape(){
        //init option of colors
        PolygonOptions options = new PolygonOptions();
        options.fillColor(0x330000ff);
        options.strokeWidth(3);
        options.strokeColor(Color.BLUE);
        //set option polgin to marker
        for ( int i  =0; i <POLYGON_POINTS; i++ ){
            options.add(markerList.get(i).getPosition());
        }
        //now print shape to user
        shape = mMap.addPolygon(options);
    }


    private void removeAllMarkerList(){
        for (Marker m : markerList ){
            m.remove();
        }
        markerList.clear(); // reIntlize to zero lenght agin
    }
    ////////////////////////////////////////////////////////////// draw lineBetween2Marker between two marker

    private Marker m1SerachBetween2Marker, m2SerachBetween2Marker;
    private Polyline lineBetween2Marker;

    //f; فائدة هذه الميسود تسدعي عند كل نقطة تقف عليها في mapLondClick ليتم تخيصص هل النقطة الأولى ام النقطة الثانية لكتابة الخط
    public void map_drawLine_polyLineBetweenTwoPoint_manger(Marker mSerachToAdd){
        //f; بحث عن اي من النقطة الأولى ام الثانية فارغة
        if (m1SerachBetween2Marker == null ) {
            System.out.println("map - drawLine  m1");
            m1SerachBetween2Marker = mSerachToAdd;
        } else if (m2SerachBetween2Marker == null ){
            System.out.println("map - drawLine  m2 (draw now)");
            m2SerachBetween2Marker = mSerachToAdd;
            map_drawLine_polyLineBetweenTwoPoint_function(m1SerachBetween2Marker, m2SerachBetween2Marker);
        } else{
            //remove all old
            m1SerachBetween2Marker.remove();  m1SerachBetween2Marker = null;
            m2SerachBetween2Marker.remove();  m2SerachBetween2Marker = null;
            //create agin
            m1SerachBetween2Marker = mSerachToAdd;
            System.out.println("map - drawLine  after add m1:" + m1SerachBetween2Marker );
        }
    }

    private Polyline map_drawLine_polyLineBetweenTwoPoint_function(Marker m1, Marker m2) {
        PolylineOptions lineOption  = new PolylineOptions();
        lineOption.add(m1.getPosition());
        lineOption.add(m2.getPosition());
        lineBetween2Marker = mMap.addPolyline(lineOption);
        return lineBetween2Marker;
    }


    /////////////////////////////////////////////////////////////////// map Drag

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
                    LatLng mLatLng = marker.getPosition(); // lineBetween2Marker initlize LatLng object
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
    private void map_markerClickListener() {
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
    private void map_clickLongOnAnyPointOfMap_appearLatAndLng() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                System.out.println("map - clickLongOnAnyPointOfMap click ");
                Geocoder gc = new Geocoder(mContext);
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    Address add = list.get(0);
                    String locality = add.getLocality();
                    String counteryToSnippet = add.getCountryName(); //f; اسم المكان في اني بلد

                    Marker mReturn = map_markerCreate(mMarker, true, locality, counteryToSnippet, latLng.latitude, latLng.longitude, false, false, 0, true, true);

                    //drawline
                    //#### f لو عاوز create polyLine ;map_drawLine_polyLineBetweenTwoPoint_manager(mReturn);

                    //craete polgon شكل مثلث او مربع
                    //#### map_drawLine_polygonMulitPoint_manger(mReturn,true);

                    //draw circle
                    map_drawLine_circle_manager(mReturn, latLng, 1000, true);
                } catch (Exception e) {
                    System.out.println("map - clickLongOnAnyPointOfMap exception " + e);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }


    private void map_informationWindows() {
        if (mMap != null) {   //mMap it's instance object of class of GoogleMap
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }
                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.inf_windows, null);
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


    /////////////////////////////////////////////////////////////// libarray abdo code

    //f; في حالة المستخدم غير مثبت google play service in his device
    public boolean map_CheckGooglePlayServicesInstalledInThisDevice() {
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this); //GooglePlayServicesUtil.isGooglePlayServicesAvailable(this); deprecated
        if (isAvailable == ConnectionResult.SUCCESS) {
            System.out.println("map - map_CheckGooglePlayServicesInstalledInThisDevice: yes");
            return true;
        } else {
            System.out.println("map - map_CheckGooglePlayServicesInstalledInThisDevice: no");
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
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
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(searchString, 1);
            if (list.size() > 0) {
                Address add = list.get(0);
                String locality = add.getLocality();
                String counteryToSnippet = add.getCountryName(); //f; اسم المكان في اني بلد
                double lat = add.getLatitude();  //get lat
                double lng = add.getLongitude();  //get long
                map_goToLocation(lat, lng, 15);  //this method to open map after get lat,long
                //marker if you want to put
                mMarker = map_markerCreate(mMarker, true,
                        searchString, counteryToSnippet, lat, lng  ,
                        false,
                        true, R.drawable.map_marker,
                        true,
                        true);
                //print
                Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();
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
        mLocationClient = new GoogleApiClient.Builder(this)
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
