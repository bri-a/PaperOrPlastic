package edu.pacificu.cs493f15_1.paperorplasticjava;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jo9026 on 4/17/16.
 */
public class ExecuteUPCScanTask extends AsyncTask<String, Void, Void>
{
  //Setup for API connection
  String website = "http://api.nutritionix.com/v1_1/item/";
  StringBuilder sb = new StringBuilder ();
  JSONArray returnJSONArray = new JSONArray();
  String barcode;


  @Override
  protected Void doInBackground (String... UPC) {
    try
    {
      URL url = new URL (website);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
      urlConnection.setDoOutput(true);
      urlConnection.setDoInput(true);
      urlConnection.setRequestProperty("Content-Type", "application/json");
      urlConnection.setRequestMethod("POST");
      urlConnection.connect ();

      barcode = UPC[0];

      JSONObject login = new JSONObject ();


      login.put ("appId", "0f0b5b93");
      login.put ("appKey", "f468c4aec88a5de24bf91e30a9f491bf");
      login.put ("upc", barcode);


      OutputStreamWriter out = new OutputStreamWriter (urlConnection.getOutputStream());
      out.write (login.toString ());
      out.close ();

      // Status Code Returns here.
      int HttpResult = urlConnection.getResponseCode ();
      System.out.println("" + HttpResult);

      if (HttpResult == HttpURLConnection.HTTP_OK)
      {
        BufferedReader buffer = new BufferedReader (new InputStreamReader(urlConnection.getInputStream (), "UTF-8"));


        String line = null;
        int total;

        while ((line = buffer.readLine ()) != null)
        {
          sb.append (line + "\n");
        }

        buffer.close();
        System.out.println("" + sb.toString());

      }

      else
      {
        System.out.println (urlConnection.getResponseMessage());
      }

      return null;
    }

    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }

    catch (IOException i)
    {
      i.printStackTrace ();
    }

    catch (JSONException j)
    {
      j.printStackTrace ();
    }

    return null;
  }
}
