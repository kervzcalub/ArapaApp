package com.arapa.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arapa.app.R;
import com.arapa.app.util.SchoolImages;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    SchoolImages images;
    Context context;

    int current_position;

    public ImageAdapter(Context context, SchoolImages images) {
        this.images = images;
        this.context = context;
    }


    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        String path = images.getImagePath(position);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent_position(position);
                showImage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.getImage_paths().size();
    }


    private void showImage() {
        final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.image_dialog);
        ImageButton btnClose = (ImageButton) nagDialog.findViewById(R.id.image_close);
        ImageButton mv_left = (ImageButton) nagDialog.findViewById(R.id.mv_left);
        ImageButton mv_right = (ImageButton) nagDialog.findViewById(R.id.mv_right);
        ImageView image = (ImageView) nagDialog.findViewById(R.id.imagedialog_placeholder);

        int max_position = images.getMaxIndex() - 1;

        Bitmap bitmap = BitmapFactory.decodeFile(images.getImagePath(getCurrent_position()));
        image.setImageBitmap(bitmap);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nagDialog.dismiss();
            }
        });
        mv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String path = "";
                if (getCurrent_position() == 0) {
                    path = images.getImagePath(max_position);
                    setCurrent_position(max_position);
                } else {
                    int position = getCurrent_position() - 1;
                    path = images.getImagePath(position);
                    setCurrent_position(position);
                }
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image.setImageBitmap(bitmap);
            }
        });
        mv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String path = "";
                if (getCurrent_position() == max_position) {
                    path = images.getImagePath(0);
                    setCurrent_position(0);
                } else {
                    int position = getCurrent_position() + 1;
                    path = images.getImagePath(position);
                    setCurrent_position(position);
                }
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image.setImageBitmap(bitmap);
            }
        });
        nagDialog.show();
    }

    public int getCurrent_position() {
        return current_position;
    }

    public void setCurrent_position(int current_position) {
        this.current_position = current_position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.school_image);
        }
    }
}
