package com.example.guleray.mobile_termproject;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import com.example.guleray.mobile_termproject.dummy.DbAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MenuActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks ,
        AccountFragment.OnFragmentInteractionListener,
        DisplayAccountMovement.OnFragmentInteractionListener,
        MoneyTransfer.OnFragmentInteractionListener,
        DisplayExchangeRates.OnFragmentInteractionListener,
AddNewMoneyTransfer.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String userName;
    public DbAdapter DBAdapter;

    public ArrayList<HashMap<String,String>> accountsList;
    public ArrayList<HashMap<String,String>> IbanMoveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        ((ListView) findViewById(R.id.navigation_drawer)).setOnItemClickListener(new DrawerItemClickListener());
        new AccessWebServiceTask().execute("http://umut.tekguc.info/webservices/ebanking/menuItems.xml");

        Intent intent = getIntent();

        userName = intent.getStringExtra("userid");
        System.out.println(userName);

        DBAdapter = new DbAdapter(getApplicationContext());
        DBAdapter.open();

       // fetchAndDisplayIBANMovements("GB82WEST12345698765430");
       //long res =  DBAdapter.insertAccountMove("test","test","test",10,"yar",3);
       // System.out.println("result "  + res);

        fetchAndDisplayUserBankAccounts();

        DBAdapter.insertNewSavedIBAN("GB82WEST12345698765430","test");
        DBAdapter.insertNewSavedIBAN("GB82WEST12345698765564","test2");
    }


    public void removeMoneyTransferAccount_OnClick(View e)
    {
        System.out.println("remove clicked");
        ListView lv = (ListView)findViewById(R.id.listView2);
        if(lv.getCheckedItemCount() <= 0)
            return;
        SparseBooleanArray checked = lv.getCheckedItemPositions();

        for (int i = 0; i < lv.getAdapter().getCount(); i++) {
            if (checked.get(i))
            {
                String val =  (String) lv.getItemAtPosition(i);
                System.out.println(val +  " is checked");
                String [] split = val.split("IBAN:");

                String iban = split[1].replace(")","");
                System.out.println(iban);
                DBAdapter.deleteSavedIBAN(iban);
                showToastMessage("Removed money transfer account : " + iban);
            }
        }

        MoneyTransfer mt = (MoneyTransfer) getFragmentManager().findFragmentById(R.id.container);
        mt.refreshList();
    }

    public void addMoneyTransferAccount_OnClick(View e)
    {
        System.out.println("add clicked");
        AddNewMoneyTransfer anmf = new AddNewMoneyTransfer();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,anmf).commit();
    }

    public void addNewMoneyTransferAccount_OnClick(View e)
    {
        EditText etIBAN = (EditText) findViewById(R.id.editText3);
        EditText etDesc = (EditText) findViewById(R.id.editText4);

        long res = DBAdapter.insertNewSavedIBAN(etIBAN.getText().toString(),etDesc.getText().toString());

        if(res > -1)
            showToastMessage("New transfer account successfully added.");
        else
            showToastMessage("A transfer account with given IBAN already defined before.");
    }




    private void DisplayUserBankAccountList(ArrayList<HashMap<String,String>> accountList)
    {
        this.accountsList = accountList;
        AccountFragment af = new AccountFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,af).commit();
    }

    public void DisplayUserBankAccountMovements(ArrayList<HashMap<String,String>> list)
    {
        this.IbanMoveList = list;
        DisplayAccountMovement dam = new DisplayAccountMovement();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,dam).commit();
    }

    public void DisplayExchangeRatesList()
    {
        DisplayExchangeRates der = new DisplayExchangeRates();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,der).commit();
    }

    public void DisplayMoneyTransferAccounts()
    {
        MoneyTransfer mt = new MoneyTransfer();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,mt).commit();
    }

    public void fetchAndDisplayUserBankAccounts()
    {
        String req = String.format("http://umut.tekguc.info/webservices/ebanking/accountBalances.php?UN=%s", userName);
        System.out.println("Requesting account information : " + req);
        new ReadAccountInformationTask().execute(req);
    }

    public void fetchAndDisplayIBANMovements(String iban)
    {
        String req = String.format("http://umut.tekguc.info/webservices/ebanking/accountMovements.php?IBAN=%s", iban);
        System.out.println("Requesting IBAN transaction information : " + req);
        new ReadIBANMovementsTask().execute(req);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void showToastMessage(String msg)
    {
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            showToastMessage("clicked" + position);

            switch(position)
            {
                case 0:
                    fetchAndDisplayUserBankAccounts();
                    // Accounts
                    break;
                case 1:
                    DisplayMoneyTransferAccounts();
                    // Money transfer
                    break;
                case 2:
                    DisplayExchangeRatesList();
                    // Foreign exchange
                    break;
                case 3:
                    finish();
                    // Logoff

                    break;
            }

            // do your thing
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MenuActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
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
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    String[] menuItems;


    //to get XML File
    private String WordDefinition(String url) {
        InputStream in = null;
        String menuList ="" ;
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
            NodeList idElements =
                    doc.getElementsByTagName("m_item");

            //---iterate through each <id> elements---
            for (int i = 0; i < idElements.getLength(); i++) {


                menuList +=((Node) idElements.item(i)).getTextContent() + "-";

            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        //---return the definitions of the word---
        return menuList ;
    }

    void loadDrawer(String res)
    {
        menuItems =res.split("-");
        ListView dList = (ListView) findViewById(R.id.navigation_drawer);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,menuItems);
        dList.setAdapter(mAdapter);
    }

    private class AccessWebServiceTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return WordDefinition(urls[0]);
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(getActivity().getBaseContext(), result, Toast.LENGTH_LONG).show();
           loadDrawer(result);




        }
    }




    // to Consume JSON service
    public String readJSONFeed(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int statusCode = c.getResponseCode();
            switch (statusCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line+"\n");
                    }
                    br.close();
                    return stringBuilder.toString();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }


    private class ReadAccountInformationTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);


                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                //---print out the content of the json feed---
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("account", jsonObject.getString("IBAN"));
                    item.put("balance", jsonObject.getString("Account_Balance") + " " + jsonObject.getString("Currency"));
                    list.add(item);
                }
                DisplayUserBankAccountList(list);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadIBANMovementsTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);


                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                //---print out the content of the json feed---
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("date", jsonObject.getString("date") + " " + jsonObject.getString("description"));
                    item.put("deposit", "move("+jsonObject.getString("move")+")"+ jsonObject.getString("Currency") +  " new balance :" + jsonObject.getString("Balance") + " " +jsonObject.getString("Currency"));
                    list.add(item);
                }

                DisplayUserBankAccountMovements(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
