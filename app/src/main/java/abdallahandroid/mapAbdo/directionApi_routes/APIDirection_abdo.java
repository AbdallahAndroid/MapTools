package abdallahandroid.mapAbdo.directionApi_routes;

import android.content.Context;
import android.graphics.Color;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abdallahandroid.mapAbdo.IDowloadJSONFromAPI;
import abdallahandroid.maptools.R;


/**
 * //////////// abdo: الخطوات
 * - volley
 0- get direction server key : لاحظ هو مختلف عن  api key !!
  map api key يستخدم لتصفح الخريطة اما   direction server key بيستخدم لكي تستطيع تستخدم هذه الخاصية

 - to get direction api key:: go to console >> choose project >> go to "library" select direction API
   select direction api notttt android sdk map !! هذه غير هذه

 - qoute free 1 day 1 request !! فقط ببلاش هذه

 1- in depnencece:
 compile 'com.android.volley:volley:1.1.0'

 2- instante class : new APIDirection_abdo( googleMap, context);

 3- use method map_drawroutes_BetweenTwoPoint_manger( marker);  لكي تضع مكان الماركر وبعد اثنان ماركر يرسم الطريق لك

 - use method removeAllRoutesAndAllMarker()  to remvoe all routss and marker
 */

public class APIDirection_abdo {

    IDowloadJSONFromAPI iFinish;
    GoogleMap mMap;
    Context mContext;
    ArrayList<Polyline> listPolyline;
    ArrayList<Marker> listMarker;

    String apiKeyGoogle; //api key of google console

    public APIDirection_abdo(GoogleMap mMap, Context mContext, String directionAPIServerKey ){
        //intizlie static variable
        this.mMap = mMap;
        this.mContext = mContext;
        this.apiKeyGoogle = directionAPIServerKey;

        //reinizlie arraylist
        listPolyline = new ArrayList<>();
        listMarker = new ArrayList<>();
    }

    public void listennerResponse(IDowloadJSONFromAPI iFinish){
        this.iFinish = iFinish;
    }

    ////////////////////////////////////////////////////////////// draw lineBetween2Marker between two marker

    private Marker mark1, mark2;

    //f; فائدة هذه الميسود تسدعي عند كل نقطة تقف عليها في mapLondClick ليتم تخيصص هل النقطة الأولى ام النقطة الثانية لكتابة الخط
    public void map_drawroutes_BetweenTwoPoint_manger(Marker markAdd){
        //f; بحث عن اي من النقطة الأولى ام الثانية فارغة
        if (mark1 == null ) {
            System.out.println("abdoAminDrawRoutes - drawLine  m1");
            mark1 = markAdd;
        } else if (mark2 == null ){
            System.out.println("abdoAminDrawRoutes - drawLine  m2 (draw now)");
            mark2 = markAdd;
            getJSONdata_fromTwoMarker(mark1, mark2);
        } else{
            //remove all old
            mark1.remove();  mark1 = null;
            mark2.remove();  mark2 = null;
            //create agin
            mark1 = markAdd;
            System.out.println("abdoAminDrawRoutes - drawLine  after add  m1 agin:" + mark1);
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
    ///////////////////////////////////////////////////////////////////////////////// code of class

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
        String url = getDirectionsUrl(origin, dest, apiKeyGoogle);
        System.out.println("abdoAminDrawRoutes - getDirectionsUrl  url: " + url);
        //downlaod json from url
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("abdoAminDrawRoutes - result: " + response);
                List<  List<HashMap<String, String>>  >   routes = convert_String_to_RoutesList(response);
                if (routes != null )  {
                    drawRoutesNow(routes);
                    if (iFinish != null )iFinish.success_data();
                } else {
                    boolean checkResponseHaveQoute = response.contains("quota");
                    if (checkResponseHaveQoute  &&  iFinish != null )iFinish.faild_quota(mark1, mark2);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("abdoAminDrawRoutes - response error " + error);
                if (iFinish != null ) iFinish.faild_response();
            }
        });
        mRequestQueue.add(mStringRequest);
    }


    //function: تحويل مكانكك الذي  انت فيه ومكان المراد الوصول اليه الي url link to download routs lat and long
    /*        exmaple
     LatLng origin = new LatLng(30.739834, 76.782702);
     LatLng dest = new LatLng(30.705493, 76.801256);
     String url = getDirectionsUrl(origin, dest);

     // url must be see doucment: ( https://developers.google.com/maps/documentation/directions/get-api-key )

    example url:
     https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=YOUR_API_KEY

      */
    private String getDirectionsUrl(LatLng origin, LatLng dest, String apiKey) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKey;
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +keyApi;

        System.out.println("abdoAminDrawRoutes - getDirectionsUrl "  + url );
        return url;
    }


    private List<  List<HashMap<String, String>>  >   convert_String_to_RoutesList(String jsonString ){
        JSONObject jObject;
        List<  List<HashMap<String, String>>  > routes = null;
        try {
            jObject = new JSONObject( jsonString);
            //check  not have message error
            boolean checkApiDailyUsed = jsonString.contains("error_message");
            System.out.println("abdoAminDrawRoutes - convert_String_to_RoutesList - checkApiDailyUsed "  + checkApiDailyUsed );
            if (! checkApiDailyUsed) {
                routes = new DirectionsJSONParser().getRoutes_FromJsonObject(jObject);
            } else{
                //Toast.makeText(mContext, "error message may be: You have exceeded your daily request quota for this API.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("abdoAminDrawRoutes - convert_String_to_RoutesList excepiton "  +e );
        }
        return routes;
    }


    private void drawRoutesNow(List<  List<HashMap<String, String>>  >   routes){
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(8);                 //abdo: change width of line
            lineOptions.color(Color.BLACK);       //abdo: change color of line  //abdo لو اردت تغير جزء جزء من الخط تكون من هنا كل مرة تغير لون
            lineOptions.geodesic(true);


            //change color of single line every time
            int numberIsOneToFour = check_OneOrTwoOrThreeOrFour_second(i);
            System.out.println("abdoAminDrawRoutes - color of sinlge line: " + numberIsOneToFour);
            switch ( numberIsOneToFour ){
                case 1 : lineOptions.color(Color.RED);
                case 2 : lineOptions.color(Color.YELLOW);
                case 3 : lineOptions.color(Color.BLUE);
                case 4 : lineOptions.color(Color.GREEN);
                default: lineOptions.color(Color.BLACK);
            }

        }

        // Drawing polyline in the Google Map for the i-th route
        Polyline mPolyLine = mMap.addPolyline(lineOptions);
        mPolyLine.setStartCap( new RoundCap());
        mPolyLine.setEndCap(  new CustomCap( BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_black), 10)   );
        mPolyLine.setJointType(JointType.ROUND);

            /*   //set style polyline
        mPolyLine.setWidth(3);
        mPolyLine.setClickable(true);
        mPolyLine.setColor(Color.BLUE);
        mPolyLine.setStartCap( new RoundCap());
        mPolyLine.setEndCap(  new CustomCap( BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_blue), 10)   );
        mPolyLine.setJointType(JointType.ROUND);

             */


        //add to arrayList all polyLine
        listPolyline.add(mPolyLine);
    }





    //f; اريد كل اربع ثواني يرجع ترتيب الثانية رقم 1 او 2 او 3 او 4  :   int x : variable of courrent second now
    public int check_OneOrTwoOrThreeOrFour_second(int x ){
        //this reSmaller x between 10 and 20 number
        /* //f; هذه الطريقة البدائية ولكن لها نهاية
        if (x > 24 ) x = x - 8; //smaller one time
        if (x > 24 ) x = x - 8; //smaller two time : when still x more than 24
        if (x > 24 ) x = x - 8; // smaller three times : when still x more than 24
        ......... want many lines for infinte
         */
        for (int i = 0 ; i < 1000000000 ; i++){
            if (x > 22 ) {
                x = x -8;
                // System.out.println("still loop to re smaller x");
            } else {
                //System.out.println("stop loop berak loop");
                break; //j خلاص هنا اقف اللفة
            }
        }
        //System.out.println("after finish loop reSmaller x is " + x);
        int [] arrayOneRecord =   {0,4,8,12,16,20};
        int [] arrayTwoRecord =   {1,5,9,13,17,21};
        int [] arrayThreeRecord = {2,6,10,14,18,22};
        for (int d = 0; d < arrayOneRecord.length ; d++ ){
            if (x == arrayOneRecord[d] ) return 1;
        }
        for (int d = 0; d < arrayTwoRecord.length ; d++ ){
            if (x == arrayTwoRecord[d] ) return 2;
        }
        for (int d = 0; d < arrayThreeRecord.length ; d++ ){
            if (x == arrayThreeRecord[d] ) return 3;
        }
        //f; if not 1,2,3 array then four array
        return 4;
    }




}

