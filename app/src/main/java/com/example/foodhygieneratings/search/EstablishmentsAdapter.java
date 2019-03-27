package com.example.foodhygieneratings.search;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhygieneratings.R;

import org.w3c.dom.Text;

import java.util.List;

public class EstablishmentsAdapter extends RecyclerView.Adapter<EstablishmentsAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView resultInfoText;
        public TextView additionalInfoText;
        public TextView ratingText;
        public TextView addressText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingText = itemView.findViewById(R.id.ratingText);
            resultInfoText = itemView.findViewById(R.id.resultNameText);
            additionalInfoText = itemView.findViewById(R.id.moreInfoText);
            addressText = itemView.findViewById(R.id.resultAddressText);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Establishment establishment = eResults.get(position);

                FragmentTransaction fT;
                String tag = Integer.toString(R.id.searchButton);
                SingleResultFragment singleResultFragment = new SingleResultFragment();
                Bundle resultBundle = new Bundle();
                resultBundle.putString("resultJSON", establishment.getJsonObject());
                singleResultFragment.setArguments(resultBundle);
                //Log.e(" SearchResult:", resultsArray.toString());
                fT = fragmentManager.beginTransaction();
                fT.replace(R.id.frag_frame, singleResultFragment, tag).addToBackStack(null);
                fT.commit();
            }
        }
    }

    private OnBottomReachedListener onBottomReachedListener;
    private List<Establishment> eResults;
    private FragmentManager fragmentManager;

    private Resources resources;

    public EstablishmentsAdapter(List<Establishment> eResults, Resources resources, FragmentManager fragmentManager) {
        this.resources = resources;
        this.eResults = eResults;
        this.fragmentManager = fragmentManager;

    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }
    @NonNull
    @Override
    public EstablishmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View establishmentView = inflater.inflate(R.layout.single_result_layout,parent, false);
        ViewHolder viewHolder = new ViewHolder(establishmentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Establishment establishment = eResults.get(position);

        TextView resultInfoText = viewHolder.resultInfoText;
        resultInfoText.setText(establishment.getName());
        TextView additionalInfoText = viewHolder.additionalInfoText;
        additionalInfoText.setText(establishment.getDistanceString() + " " + resources.getString(R.string.distance));
        TextView ratingText = viewHolder.ratingText;
        setRating(resources, establishment.getRatingValue(),ratingText);
        TextView addressText = viewHolder.addressText;
        addressText.setText(establishment.getAddress());

        if (onBottomReachedListener!= null && position == eResults.size() - 1){
            onBottomReachedListener.onBottomReached(position);

        }
    }

    public static void setRating(Resources resources, String rating, TextView ratingText){
        ratingText.setText(rating);
        switch(rating){
            case "0":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating0));
                break;
            case "1":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating1));
                break;
            case "2":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating2));
                break;
            case "3":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating3));
                break;
            case "4":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating4));
                break;
            case "5":
                ratingText.setBackgroundColor(resources.getColor(R.color.rating5));
                break;
            default:
                ratingText.setBackgroundColor(resources.getColor(R.color.ratingDue));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return eResults.size();
    }
}
