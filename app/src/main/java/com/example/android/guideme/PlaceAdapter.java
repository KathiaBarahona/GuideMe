package com.example.android.guideme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Agile 2016 on 1/31/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceAdapterViewHolder> {
    private String [] placeData;
    private final PlaceAdapterOnClickHandler mClickHandler;

    //--------------------------Class Constructor----------------------------------------------//
    public PlaceAdapter(PlaceAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }
    //------------Override Method needed to inflate the main layout with the new Item----------//
    @Override
    public PlaceAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflates the view with the place item
        Context context = parent.getContext();
        int idForItem = R.layout.activity_place_item;
        //Inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttach = false;
        View view = inflater.inflate(idForItem,parent,shouldAttach);
        return new PlaceAdapterViewHolder(view);
    }
    //---------Override Method used to bind required information to view, in this case place's name --//
    @Override
    public void onBindViewHolder(PlaceAdapterViewHolder holder, int position) {
        String placeName = placeData[position];
        holder.placeTextView.setText(placeName);
    }
    //---------Override Method used to return the amount of places available-----------------------//
    @Override
    public int getItemCount() {
        return placeData == null ? 0 : placeData.length;
    }
    //--------------Set Place Data ---------------------------------------------------------------//
    public void setPlaceData(String [] places){
        placeData = places;
    }
    //-------------------Place AdapterOnClickHandler Interface ---------------------------------//
    public interface PlaceAdapterOnClickHandler {
        void onClick(String placeSelected);
        void onDoubleTap(String placeSelected);
    }
    //--------------------Place AdapterViewHolder Class ----------------------------------------//
    public class PlaceAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView placeTextView;//Text view that shows place's name
        //-----------------Class Constructor----------------------------------------------------//
        public PlaceAdapterViewHolder(View view){
            super(view);
            final GestureDetector gestureDetector  = new GestureDetector(view.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                public boolean onDoubleTap(MotionEvent e) {
                    int itemPosition = getAdapterPosition();
                    String destinyPlace = placeData[itemPosition];
                    mClickHandler.onDoubleTap(destinyPlace);
                    return true;
                }
            });
            placeTextView = (TextView)(view.findViewById(R.id.place_data));
            view.setOnClickListener(this);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            String placeSelected = placeData[itemPosition];
            mClickHandler.onClick(placeSelected);
            //------Method  that handles click event on place item-------//
                //-------Text Speech Implementation goes here-----------//
        }
        //-----Must implement double click to assign the place as destination.-----//

    }


}
