package test.kr.drawy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Drawy extends Activity {

    private CanvasView customCanvas;
    InstanceID instanceID;
    GoogleCloudMessaging gcm;
    String GCMId, UID;
    private static final String PROJECT_NUMBER = "253961162580";
    private static final String APP_SERVER_URL = "http://php-sutest.rhcloud.com/gcm_registration.php";
    SharedPreferences userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_drawy);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        userData = getSharedPreferences("userData", Context.MODE_PRIVATE);
        if(!userData.contains("UID"))
            getRegId();
        else{
            UID = userData.getString("UID", "");
            GCMId = userData.getString("GCMId", "");
        }
    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            String id;
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    String token = gcm.register(PROJECT_NUMBER);
                    Log.i("GCM", token);
                    GCMId = token;


                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(APP_SERVER_URL);

                    try{
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("GCMId", token));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        UID = EntityUtils.toString(entity, "UTF-8");

                        SharedPreferences.Editor editor = userData.edit();
                        editor.putString("UID", UID);
                        editor.putString("GCMId", GCMId);

                        editor.commit();
                    } catch (ClientProtocolException e) {
                    }


                }
                catch (IOException ex) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), UID, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    public void clearCanvas(View v) {
        customCanvas.clearCanvas();
    }


}
