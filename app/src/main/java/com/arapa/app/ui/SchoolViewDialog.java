package com.arapa.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.arapa.app.HomeActivity;
import com.arapa.app.R;
import com.arapa.app.SchoolActivity;
import com.arapa.app.util.School;
import com.arapa.app.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class SchoolViewDialog extends BottomSheetDialogFragment {


    private TextView school_name;
    private TextView school_address;
    private ImageView school_logo;

    private TextView distance;

    private MaterialButton schoolBtn;

    private Context context;
    private School school;

    public SchoolViewDialog(Context context, School school) {
        this.context = context;
        this.school = school;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.school_view, container, false);
        school_logo = view.findViewById(R.id.logo);
        school_name = view.findViewById(R.id.name);
        school_address = view.findViewById(R.id.address);
        distance = view.findViewById(R.id.distance);
        //details_holder.setVisibility(View.VISIBLE);
        schoolBtn = view.findViewById(R.id.schoolBtn);
        schoolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SchoolActivity.class);
                intent.putExtra("SCHOOL", school);
                startActivity(intent);
            }
        });
        school_logo.setImageBitmap(Utils.getSchoolLogo(context, school));
        school_name.setText(school.getName());
        adjustTextSize(school_name);
        school_address.setText(school.getAddress());
        double distance_km = school.getDistance();
        DecimalFormat df = new DecimalFormat("#.##");
        String distance_format = df.format(distance_km);
        distance.setText("Distance: " + distance_format + "KM");

        return view;
    }


    private void adjustTextSize(TextView school_name) {
        // Set the initial text size of the TextView
        school_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        // Get the bounds of the TextView
        Rect bounds = new Rect();
        school_name.getPaint().getTextBounds(school_name.getText().toString(), 0, school_name.getText().length(), bounds);

        // If the text is too long to fit within the bounds of the TextView, decrease the text size until it fits
        while (bounds.width() > school_name.getWidth() || bounds.height() > school_name.getHeight()) {
            // Decrease the text size by 1 sp
            float newTextSize = school_name.getTextSize() - 1;

            // If the new text size is below the minimum text size limit, stop adjusting the text size
            if (newTextSize < 12) {
                break;
            }

            // If the new text size is above the maximum text size limit, stop adjusting the text size
            if (newTextSize > 22) {
                break;
            }

            // Set the new text size
            school_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSize);

            // Get the new bounds of the text
            school_name.getPaint().getTextBounds(school_name.getText().toString(), 0, school_name.getText().length(), bounds);
        }

    }
}
