package abdallahandroid.mapAbdo.distanceMatrixApi;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abdallahandroid.mapAbdo.IDowloadJSONFromAPI;
import abdallahandroid.maptools.R;


/**
 * //////////// abdo: الخطوات
 * - volley
 1- in depnencece:
 compile 'com.android.volley:volley:1.1.0'

 */

public class DirectionMatrixAPI_abdo {

    IDowloadJSONFromAPI iFinish;
    GoogleMap mMap;
    Context mContext;
    ArrayList<Polyline> listPolyline;
    ArrayList<Marker> listMarker;
    public String resultDistance, resultDuration;     //get duration and DistanceActivity result here
    private String apiKey ;

    public DirectionMatrixAPI_abdo(GoogleMap mMap, Context mContext, String apiKey){
        //intizlie static variable
        this.mMap = mMap;
        this.mContext = mContext;
        this.apiKey = apiKey;
        listPolyline = new ArrayList<>();
        listMarker = new ArrayList<>();
    }


    public void listennerResponse(IDowloadJSONFromAPI iFinish){
        this.iFinish = iFinish;
    }


    ////////////////////////////////////////////////////////////// put two marker on map ((manager code))

    private Marker mark1, mark2;
    private Polyline mPolyLine;

    //f; فائدة هذه الميسود تسدعي عند كل نقطة تقف عليها في mapLondClick ليتم تخيصص هل النقطة الأولى ام النقطة الثانية لكتابة الخط
    public void map_putTwoMarker_manger(Marker markAdd){
        //f; بحث عن اي من النقطة الأولى ام الثانية فارغة
        if (mark1 == null ) {
            System.out.println("DirectionMatrixAPI_abdo - drawLine  m1");
            mark1 = markAdd;
        } else if (mark2 == null ){
            System.out.println("DirectionMatrixAPI_abdo - drawLine  m2 (draw now)");
            mark2 = markAdd;
            map_drawLine_polyLineBetweenTwoPoint_function(mark1, mark2);
            getJSONdata_fromTwoMarker(mark1, mark2);
        } else{
            //remove all old
            mark1.remove();  mark1 = null;
            mark2.remove();  mark2 = null;
            //create agin
            mark1 = markAdd;
            System.out.println("DirectionMatrixAPI_abdo - drawLine  after add  m1 agin:" + mark1);
        }
        listMarker.add(markAdd);
    }


    public void removeAllRoutesAndAllMarker(){
        //remove DirectionMatrixAPI_abdo
        for (int i = 0; i < listPolyline.size() ; i++ ){
            Polyline pl = listPolyline.get(i);
            pl.remove();
        }
        //remove old marker
        for (int i = 0; i < listMarker.size() ; i++ ){
            Marker m = listMarker.get(i);
            m.remove();
        }
    }

    ///////////////////////////////////////////////////////////////////////// draw polyline



    private Polyline map_drawLine_polyLineBetweenTwoPoint_function(Marker m1, Marker m2) {
        PolylineOptions lineOption  = new PolylineOptions();
        lineOption.add(m1.getPosition());
        lineOption.add(m2.getPosition());
        mPolyLine = mMap.addPolyline(lineOption);
        //set style
        mPolyLine.setWidth(3);
        mPolyLine.setClickable(true);
        mPolyLine.setColor(Color.BLUE);
        mPolyLine.setStartCap( new RoundCap());
        mPolyLine.setEndCap(  new CustomCap( BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_blue), 10)   );
        mPolyLine.setJointType(JointType.ROUND);

        /*
        //set style of pattern بيحول شكل الخط الي نقط
        PatternItem DOT = new Dot();
        PatternItem GAP = new Gap(2);  //PATTERN_GAP_LENGTH_PX
        List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
        mPolyLine.setPattern(PATTERN_POLYLINE_DOTTED);
         */
        listPolyline.add(mPolyLine);
        return mPolyLine;
    }


    /////////////////////////////////////////////////////////////////////// download JSON

    /*        exmaple
 LatLng origin = new LatLng(30.739834, 76.782702);
 LatLng dest = new LatLng(30.705493, 76.801256);
 String url = getDirectionsUrl(origin, dest);
  */
    protected void getJSONdata_fromTwoMarker(Marker m1, Marker m2){
        //get url of routs
        double latOrgin = m1.getPosition().latitude;
        double longOrgin = m1.getPosition().longitude;
        double latDest = m2.getPosition().latitude;
        double longDest = m2.getPosition().longitude;
        LatLng origin = new LatLng(latOrgin, longOrgin);
        LatLng dest = new LatLng(latDest, longDest);
        String url = getDirectionsUrl(origin, dest);
        //downlaod json from url
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("DirectionMatrixAPI_abdo - result: " + response);
                //check invaled request: عندما يكون مكتوب خطاء
                if (response.contains("INVALID_REQUEST") ) {
                    System.out.println("DirectionMatrixAPI_abdo - response invaled " );
                    Toast.makeText(mContext, "INVALID_REQUEST at get DistanceActivity", Toast.LENGTH_SHORT).show();
                } else{
                    initlzie_resultDistance_and_resultDuration(response);
                    Toast.makeText(mContext, "Duration: " + resultDuration +", Distance: " + resultDistance, Toast.LENGTH_LONG).show();
                }
                //List<List<HashMap<String, String>>>   DirectionMatrixAPI_abdo = convert_String_to_RoutesList(response);
                //drawRoutesNow(DirectionMatrixAPI_abdo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("DirectionMatrixAPI_abdo - response error " + error);
            }
        });
        mRequestQueue.add(mStringRequest);
    }



    //function: تحويل مكانكك الذي  انت فيه ومكان المراد الوصول اليه الي url link to download routs lat and long
    /*        exmaple
     LatLng origin = new LatLng(30.739834, 76.782702);
     LatLng dest = new LatLng(30.705493, 76.801256);
     String url = getDirectionsUrl(origin, dest);
     //example url:   https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        //example url with lat and long nubmers:  work okkk
        https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=30.739834,76.782702&destinations=30.705493,76.801256
      */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route adn         // Destination of route
        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destinations=" + dest.latitude + "," + dest.longitude;
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&" + str_origin + "&" + str_dest + "&key=" + apiKey;;
        System.out.println("DirectionMatrixAPI_abdo - getDirectionsUrl "  + url );
        return url;
    }


    private void initlzie_resultDistance_and_resultDuration (String jsonData){
        try {
            JSONObject root = new JSONObject(jsonData);
            JSONArray array_rows = root.getJSONArray("rows");
            System.out.println("JSON array_rows: "+array_rows);
            JSONObject object_rows=array_rows.getJSONObject(0);
            System.out.println("JSON object_rows: "+object_rows);
            JSONArray array_elements=object_rows.getJSONArray("elements");
            System.out.println("JSON array_elements: "+array_elements);
            JSONObject  object_elements=array_elements.getJSONObject(0);
            System.out.println("JSON object_elements: "+object_elements);
            JSONObject object_duration=object_elements.getJSONObject("duration");
            System.out.println("DirectionMatrixAPI_abdo - JSON  object_duration: "+object_duration);
            resultDuration =  object_duration.getString("text");
            JSONObject object_distance=object_elements.getJSONObject("DistanceActivity");
            System.out.println("DirectionMatrixAPI_abdo - JSON  object_distance: "+ object_distance);
            resultDistance = object_distance.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("DirectionMatrixAPI_abdo - JSONException "  + e );
        }
    }




}
