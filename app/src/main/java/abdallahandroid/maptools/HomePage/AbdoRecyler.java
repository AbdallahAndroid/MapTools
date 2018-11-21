package abdallahandroid.maptools.HomePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import abdallahandroid.maptools.R;
import abdallahandroid.maptools.*;

/**
 * Created by abdo on 31/12/2017.
 */

public class AbdoRecyler extends RecyclerView.Adapter<AbdoRecyler.MyViewHolder> {
    //data
    String [] dataName = { "Routes" , "Distance", "Drawing Triangle", "Drawing Circle", "Drawing Lines" };
    int [] dataImage = { R.drawable.routes, R.drawable.ruler, R.drawable.map_triangle,
            R.drawable.map_circle, R.drawable.map_line};

    //varialbe
    private LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;

    // data is passed into the constructor
    public AbdoRecyler(Context mContext, Activity mActivity) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mActivity = mActivity;
    }


    // inflates the row layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycler, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        //set data
        holder.nameTextView.setText( dataName[position]);
        holder.imageImageVIew.setImageResource( dataImage[position]);

        //cllick
        holder.allItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("abdo_recycler", "select position: " + position );
                switchWhatToOpen(position);
                //Toast.makeText(mContext, "image selected " + position, Toast.LENGTH_SHORT).show();
            }
        });
    } //end method


    public void switchWhatToOpen(int Position){
        try {
            Intent i =  null;
            //choose intent
            switch (Position){
                case 0: i = new Intent(mContext, DirectionActivity.class); break;
                case 1: i = new Intent(mContext, DistanceActivity.class); break;
                case 2: i = new Intent(mContext, DrawingTriangle.class); break;
                case 3: i = new Intent(mContext, DrawingCircle.class); break;
                case 4: i = new Intent(mContext, DrawingLineActivity.class); break;
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //this line بسببه اتحلت المشكلة بأذن الله
            mContext.startActivity(i);
        } catch (Exception e){
            Toast.makeText(mContext, "Open exception: " +  e , Toast.LENGTH_SHORT).show();
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return dataName.length;
    }


    //function: اكتب  findviewById here
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageImageVIew;
        public LinearLayout allItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            imageImageVIew = (ImageView) itemView.findViewById(R.id.image);
            allItem = itemView.findViewById(R.id.allItem);
        }
    } //end inner class


}