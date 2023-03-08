package com.arapa.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arapa.app.R;
import com.arapa.app.util.School;

import org.w3c.dom.Text;

public class ContactFragment extends Fragment {

    private School school;

    private TextView school_contact;
    private TextView school_website;

    private TextView facebookTextView;
    private TextView facebook;
    private TextView instagramTextView;
    private TextView instagram;
    private TextView other_contactTextView;
    private TextView other_contact;
    private TextView other_siteTextView;
    private TextView other_site;

    public ContactFragment(School school) {
        this.school = school;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        school_contact = view.findViewById(R.id.contact);
        school_website = view.findViewById(R.id.weblink);

        school_contact.setText(school.getContact());
        school_website.setText(school.getWeb());


        facebookTextView = view.findViewById(R.id.facebookTextView);
        facebook = view.findViewById(R.id.facebook);
        instagramTextView = view.findViewById(R.id.instagramTextView);
        instagram = view.findViewById(R.id.instagram);
        other_contactTextView = view.findViewById(R.id.other_contactTextView);
        other_contact = view.findViewById(R.id.other_contact);
        other_siteTextView = view.findViewById(R.id.other_siteTextView);
        other_site = view.findViewById(R.id.other_site);

        // facebook
        if (school.getFacebook() != null && !school.getFacebook().equals("")) {
            facebook.setText(school.getFacebook());
        } else {
            facebookTextView.setVisibility(View.GONE);
            facebook.setVisibility(View.GONE);
        }
        // instagram
        if (school.getInstagram() != null && !school.getInstagram().equals("")) {
            instagram.setText(school.getInstagram());
        } else {
            instagramTextView.setVisibility(View.GONE);
            instagram.setVisibility(View.GONE);
        }
        // other contact
        if (school.getOther_contact() != null && !school.getOther_contact().equals("")) {
            other_contact.setText(school.getOther_contact());
        } else {
            other_contactTextView.setVisibility(View.GONE);
            other_contact.setVisibility(View.GONE);
        }
        // other site
        if (school.getOther_site() != null && !school.getOther_site().equals("")) {
            other_site.setText(school.getOther_site());
        } else {
            other_siteTextView.setVisibility(View.GONE);
            other_site.setVisibility(View.GONE);
        }
        return view;
    }
}