package com.arapa.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arapa.app.R;
import com.arapa.app.util.School;
import com.arapa.app.util.Utils;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    ArrayList<School> list;
    ItemSelected itemSelected;
    boolean isSearching;
    String query = "";

    public MyAdapter(Context context, ArrayList<School> list, ItemSelected itemSelected) {
        this.context = context;
        this.list = list;
        this.itemSelected = itemSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.school_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        School school = list.get(position);
        holder.school_logo.setImageBitmap(Utils.getSchoolLogo(context, school));
        holder.name.setText(school.getName());
        holder.address.setText(school.getAddress());
        holder.distance.setText(school.getContact());
        holder.school = school;
        holder.itemView.startAnimation(animation);

        if (isSearching == true) {
            if (query.length() > 0 && school.getName().toLowerCase().contains(query.toLowerCase())) {
                int startIndex = school.getName().toLowerCase().indexOf(query.toLowerCase());
                int endIndex = startIndex + query.length();
                Spannable spannable = new SpannableString(school.getName());
                spannable.setSpan(new ForegroundColorSpan(Color.RED),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(spannable);
            }
        }

    }

    public ArrayList<School> getData() {
        return list;
    }

    public void setData(ArrayList<School> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void isSearching(boolean isSearching, String query) {
        this.isSearching = isSearching;
        this.query = query;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView school_logo;
        TextView name, address, distance;
        School school;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            school_logo = itemView.findViewById(R.id.school_logo);
            name = itemView.findViewById(R.id.school_name);
            address = itemView.findViewById(R.id.school_address);
            distance = itemView.findViewById(R.id.school_distance);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (school != null) {
                        itemSelected.itemSelected(school);
                    } else {
                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public interface ItemSelected{
        void itemSelected(School school);
    }
}
