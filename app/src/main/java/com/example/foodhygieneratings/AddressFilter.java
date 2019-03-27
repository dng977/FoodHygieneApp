package com.example.foodhygieneratings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFilter extends Fragment {


    private ArrayList<String> localAuthorities;
    private ArrayAdapter authoritiesAdapter;
    private Spinner authoritiesSpinner;
    private int selectedAuthorityPosition;


    public AddressFilter() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_address_filter, container, false);
        selectedAuthorityPosition = -1;
        //LOCAL AUTHORITIES
        this.localAuthorities = getArguments().getStringArrayList("authorities");
        Log.e("address Filter: ", "authorities size : " + localAuthorities.size());
        if(localAuthorities != null) {
            authoritiesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,localAuthorities);
        }
        authoritiesSpinner = v.findViewById(R.id.authoritiesSpinner);
        authoritiesSpinner.setAdapter(authoritiesAdapter);
        return v;
    }
    public int getSelectedAuthorityPosition() {
        return selectedAuthorityPosition;
    }

    public String getAddress(){
        EditText text = this.getView().findViewById(R.id.addressEditText);
        return text.getText().toString();
    }

    public ArrayAdapter getAdapter() {
        return authoritiesAdapter;
    }
    public Spinner getSpinner(){
        return authoritiesSpinner;
    }

    public void setAuthorities(ArrayList<String> authorities) {
        this.localAuthorities = authorities;
    }
}
