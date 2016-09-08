package com.example.guleray.mobile_termproject;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoneyTransfer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoneyTransfer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneyTransfer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoneyTransfer.
     */
    // TODO: Rename and change types and number of parameters
    public static MoneyTransfer newInstance(String param1, String param2) {
        MoneyTransfer fragment = new MoneyTransfer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MoneyTransfer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void refreshList()
    {
        MenuActivity ma = (MenuActivity)getActivity();
        Cursor crs = ma.DBAdapter.getAllSavedIBANS();
        ArrayList<String> myStringArray1 = new ArrayList<String>();
        //  myStringArray1.add("something");
        System.out.println("docrs");

        if(crs.moveToFirst())
        {
            do{

                String iban = crs.getString(0);
                String desc = crs.getString(1);
                myStringArray1.add(desc + " (IBAN:" + iban + ")");


            }while(crs.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, myStringArray1);
            ListView lv = (ListView)getView().findViewById(R.id.listView2);
            lv.setAdapter(adapter);

            lv.setItemsCanFocus(false);
            // we want multiple clicks
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.destroyDrawingCache();
            lv.setVisibility(ListView.INVISIBLE);
            lv.setVisibility(ListView.VISIBLE);
            adapter.notifyDataSetChanged();
            // lv.

        }
        else
        {
            ListView lv = (ListView)getView().findViewById(R.id.listView2);
            lv.setAdapter(null);
            lv.destroyDrawingCache();
            lv.setVisibility(ListView.INVISIBLE);
            lv.setVisibility(ListView.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_money_transfer, container, false);
        System.out.println("docrs1");
        MenuActivity ma = (MenuActivity)getActivity();
        Cursor crs = ma.DBAdapter.getAllSavedIBANS();

        ArrayList<String> myStringArray1 = new ArrayList<String>();
      //  myStringArray1.add("something");
        System.out.println("docrs");

        if(crs.moveToFirst())
        {
            do{

                String iban = crs.getString(0);
                String desc = crs.getString(1);
                myStringArray1.add(desc + " (IBAN:" + iban + ")");


            }while(crs.moveToNext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, myStringArray1);

            ListView lv = (ListView)v.findViewById(R.id.listView2);
            lv.setAdapter(null);
             lv.setAdapter(adapter);

            lv.setItemsCanFocus(false);
            // we want multiple clicks
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
           // lv.

        }

        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
