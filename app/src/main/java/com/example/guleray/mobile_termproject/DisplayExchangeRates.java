package com.example.guleray.mobile_termproject;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayExchangeRates.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayExchangeRates#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayExchangeRates extends Fragment {
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
     * @return A new instance of fragment DisplayExchangeRates.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayExchangeRates newInstance(String param1, String param2) {
        DisplayExchangeRates fragment = new DisplayExchangeRates();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    ///
    public DisplayExchangeRates() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        new AccessWebServiceTask().execute("http://www.mb.gov.ct.tr/kur/gunluk.xml");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_exchange_rates, container, false);
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

    private InputStream OpenHttpConnection(String urlString)

            throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            System.out.println(response);
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ""+ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    String[] menuItems;


    //to get XML File
    private ArrayList<String> WordDefinition(String url) {
        InputStream in = null;
        String menuList ="" ;
        ArrayList<String> myStringArray1 = new ArrayList<String>();
        try {
            in = OpenHttpConnection(url);
            Document doc = null;
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();

            //---retrieve all the <id> elements---
            NodeList idElements =  doc.getElementsByTagName("Resmi_Kur");


                System.out.println("count "+ idElements.getLength());
            //---iterate through each <id> elements---
            for (int i = 0; i < idElements.getLength(); i++) {

                Node z = ((Node) idElements.item(i));
                NodeList cNodes = z.getChildNodes();
                String result = "";
                for(int j = 0 ; j < cNodes.getLength(); j++)
                {
                    Node n = cNodes.item(j);

                    // 1 USD (ALIŞ : 2.1157, SATIŞ : 2.8870)
                    switch(n.getNodeName())
                    {
                        case "Birim":
                            result += n.getTextContent() +" ";
                            break;
                        case "Sembol":
                            result += n.getTextContent() +" ";
                            break;
                        case "Isim":
                            break;
                        case "Doviz_Alis":
                            result += "Buy : " + n.getTextContent() +",";
                            break;
                        case "Doviz_Satis":
                            result += "Sell : " + n.getTextContent();
                            break;
                        case "Efektif_Alis":
                            break;
                        case "Efektif_Satis":
                            break;
                    }
                }

                myStringArray1.add(result);


            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        //---return the definitions of the word---


        return myStringArray1;
    }

    private class AccessWebServiceTask extends AsyncTask<String, Void, ArrayList<String>> {

        protected ArrayList<String> doInBackground(String... urls) {
            return WordDefinition(urls[0]);
        }

        protected void onPostExecute(ArrayList<String> val) {
            //Toast.makeText(getActivity().getBaseContext(), result, Toast.LENGTH_LONG).show();
          //  loadDrawer(result);
            System.out.println("after");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, val);
            ListView lv = (ListView)getView().findViewById(R.id.listView3);
            lv.setAdapter(adapter);



        }
    }

}
