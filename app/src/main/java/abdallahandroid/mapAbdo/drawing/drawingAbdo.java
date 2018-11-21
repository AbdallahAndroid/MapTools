package abdallahandroid.mapAbdo.drawing;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import abdallahandroid.maptools.R;

/**
 * Created by abdo on 19/03/2018.
 */

public class drawingAbdo {

    static GoogleMap mMap;
    static Context mContext;

    public drawingAbdo(GoogleMap mMap, Context mContext){
        this.mMap = mMap;
        this.mContext = mContext;

        new innerClassClickPolygon_polyline();  //invoke this class to invoke implement polyline.onClickLisitener
    }

    ////////////////////////////////////////////////////////////// draw mPolyLine between two marker

    private Marker mark1, mark2;
    private Polyline mPolyLine;


    //f; فائدة هذه الميسود تسدعي عند كل نقطة تقف عليها في mapLondClick ليتم تخيصص هل النقطة الأولى ام النقطة الثانية لكتابة الخط
    public void map_drawLine_polyLineBetweenTwoPoint_manager(Marker markAdd){
        //f; بحث عن اي من النقطة الأولى ام الثانية فارغة
        if (mark1 == null ) {
            System.out.println("map - drawLine  m1");
            mark1 = markAdd;
        } else if (mark2 == null ){
            System.out.println("map - drawLine  m2 (draw now)");
            mark2 = markAdd;
            map_drawLine_polyLineBetweenTwoPoint_function(mark1, mark2);
        } else{
            //remove all old
            mark1.remove();  mark1 = null;
            mark2.remove();  mark2 = null;
            //create agin
            mark1 = markAdd;
            System.out.println("map - drawLine  after add  m1 agin:" + mark1);
        }
    }


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
        return mPolyLine;
    }

    class innerClassClickPolygon_polyline implements GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

        public innerClassClickPolygon_polyline(){
            mMap.setOnPolylineClickListener(this);  //must wirte this class to implete onCLick
        }


        @Override
        public void onPolygonClick(Polygon polygon) {

        }

        @Override
        public void onPolylineClick(Polyline polyline) {
            // Create a stroke pattern of a gap followed by a dot.
            final PatternItem DOT = new Dot();
            final PatternItem GAP = new Gap(2);  //PATTERN_GAP_LENGTH_PX
            final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
            //onClickLisitener ::
            // Flip from solid stroke to dotted stroke pattern.
            if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
                polyline.setPattern(PATTERN_POLYLINE_DOTTED);
            } else {
                // The default pattern is a solid stroke.
                polyline.setPattern(null);
            }
            System.out.println("map - innerClassClickPolygon_polyline : " + polyline);
//            Toast.makeText(mContext, "Route type " + polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
        }

    } //end inner class


    //////////////////////////////////////////////////////////////////// draw polypong multi points

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

    ///////////////////////////////////////////////////////////////////////////// draw circle

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



}
