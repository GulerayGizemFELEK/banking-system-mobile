package com.example.guleray.mobile_termproject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends Activity implements LoginFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

      displayLoginFragment();
    }

    public void displayLoginFragment()
    {
        LoginFragment lf = new LoginFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container,lf).commit();
    }

    public void displayMainMenu()
    {
        Intent intent = new Intent(this, MenuActivity.class);
        EditText etName = (EditText)findViewById(R.id.editText);
        intent.putExtra("userid",etName.getText().toString());
        startActivity(intent);
    }

    public void onLoginClick(View e)
    {
        System.out.println("login clicked");
        EditText etName = (EditText)findViewById(R.id.editText);
        EditText etPass = (EditText)findViewById(R.id.editText2);
        String requestUrl = String.format("http://umut.tekguc.info/webservices/ebanking/checkLogin.php?UN=%s&Pass=%s",etName.getText(),etPass.getText());
        new ReadLoginResponseTask().execute(requestUrl);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void showToastMessage(String msg)
    {
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
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

    private class ReadLoginResponseTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                //---print out the content of the json feed---
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if(jsonObject.getString("correct").compareToIgnoreCase("YES") == 0)
                    {
                        /// Login succeeded
                        showToastMessage("Login succeeded.");
                        displayMainMenu();
                    }
                    else
                        showToastMessage("Login failed");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
