package com.kodemerah.android.disabillitytranslator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tersandung on 4/10/16.
 */
public class MainActivity extends Activity {

    private AutoCompleteTextView autoComplete;
    private ArrayAdapter<String> adapter;

    private ScrollView svDetail;
    private VideoView vvPlayer;
    private TextView txtKata, txtLafal, txtDeskripsi;
    private ImageButton replayButton;
    private ProgressBar spinnerView;
    private ProgressDialog dialog;

    String myJSON;

    private static final String TAG_RESULTS="RESULTS";
    private static final String TAG_KATA = "KATA";
    private static final String TAG_LAFAL = "LAFAL";
    private static final String TAG_DESKRIPSI ="DESKRIPSI";
    private static final String TAG_VIDEO ="VIDEO";
    public static final String EXTRA_KATA = "EXTRA_KATA", EXTRA_LINK = "EXTRA_LINK";

    String[] list_kata;
    List<Kata> daftarKata;
    DBHelper db;
    JSONArray words = null;
    AutoCompleteTextView myTextView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("DTPREF", MODE_PRIVATE);
        editor = pref.edit();
        pref.getInt("db_version", 1);


        getDbVersion();
//        this.deleteDatabase("DT.db");

        svDetail = (ScrollView) findViewById(R.id.svDetail);
        vvPlayer = (VideoView) findViewById(R.id.vvPlayer);

        txtKata = (TextView) findViewById(R.id.txtKata);
        txtLafal = (TextView) findViewById(R.id.txtLafal);
        txtDeskripsi = (TextView) findViewById(R.id.txtDeskripsi);

        spinnerView = (ProgressBar) findViewById(R.id.material_design_progressbar);
        replayButton = (ImageButton) findViewById(R.id.imageButton);

        svDetail.setVisibility(View.GONE);
        vvPlayer.setVisibility(View.GONE);
        replayButton.setVisibility(View.GONE);
        spinnerView.setVisibility(View.GONE);

        final Button translate = (Button) findViewById(R.id.button);

        myTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        myTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        myTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                String kata_cari = String.valueOf(myTextView.getText());
                int indexKata = Arrays.asList(list_kata).indexOf(kata_cari);
                if (indexKata >= 0) {
                    replayButton.setVisibility(View.GONE);

                    vvPlayer.setVideoURI(Uri.parse(daftarKata.get(indexKata).getVideo()));
                    vvPlayer.setMediaController(new android.widget.MediaController(v.getContext()));
                    vvPlayer.requestFocus();
                    vvPlayer.start();

                    vvPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            replayButton.getLayoutParams().height = vvPlayer.getHeight();
                            replayButton.getLayoutParams().width = vvPlayer.getWidth();
                            replayButton.setVisibility(View.VISIBLE);
                        }
                    });

                    vvPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                                spinnerView.setVisibility(View.GONE);
                                vvPlayer.setVisibility(View.VISIBLE);
                            }
                            if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                                spinnerView.setVisibility(View.VISIBLE);
                                vvPlayer.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });

                    txtKata.setText(daftarKata.get(indexKata).getKata());
                    txtLafal.setText(daftarKata.get(indexKata).getLafal());
                    txtDeskripsi.setText(daftarKata.get(indexKata).getDeskripsi());

                    svDetail.setVisibility(View.VISIBLE);
                    vvPlayer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void replay(View v){
        replayButton.setVisibility(View.GONE);
        vvPlayer.start();
    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://10.0.3.2/dt/get_data.php");

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();


                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{
                        if(inputStream != null)
                            inputStream.close();
                    }catch(Exception squish){

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                if (myJSON!=null){
                    try {
                        JSONObject jsonObj = new JSONObject(myJSON);
                        words = jsonObj.getJSONArray(TAG_RESULTS);

                        for(int i=0;i<words.length();i++){
                            JSONObject c = words.getJSONObject(i);
                            String kata = c.getString(TAG_KATA);
                            String video = c.getString(TAG_VIDEO);
                            String lafal = "";
                            String deskripsi = "";
                            if (c.getString(TAG_LAFAL) != null){
                                lafal = c.getString(TAG_LAFAL);
                            }
                            if (c.getString(TAG_DESKRIPSI) != null){
                                deskripsi = c.getString(TAG_DESKRIPSI);
                            }
                            db.insertKata(new Kata(kata, lafal, deskripsi, video));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    daftarKata = db.getAllKatas();
                    showList();
                    dialog.dismiss();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void getDbVersion(){
        class GetDbVersion extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(MainActivity.this, "Loading Data", "Please Wait..");
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://10.0.3.2/dt/get_version.php");


                InputStream inputStream = null;
                String result = null;
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();


                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{
                        if(inputStream != null)
                            inputStream.close();
                    }catch(Exception squish){

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                db = new DBHelper(getBaseContext(), null, null, pref.getInt("db_version", 1));
                boolean upgrade = db.getWritableDatabase().needUpgrade(Integer.parseInt(result));
                if(!upgrade){
                    daftarKata = db.getAllKatas();
                    showList();
                    dialog.dismiss();
                    Log.i("READY", "APP READY TO USE");
                } else {
                    Log.i("READY", "APP NOT READY TO USE");
                    db = new DBHelper(getBaseContext(), null, null, Integer.parseInt(result));
                    getData();
                    editor.putInt("db_version", Integer.parseInt(result));
                    editor.commit();
                }

            }
        }
        GetDbVersion g = new GetDbVersion();
        g.execute();
    }

//    public void Splashscreen(){
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
//        anim.reset();
//        FrameLayout l=(FrameLayout) findViewById(R.id.frameSplash);
//        l.clearAnimation();
//        l.startAnimation(anim);
//
//        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
//        anim.reset();
//        ImageView iv = (ImageView) findViewById(R.id.logo);
//        iv.clearAnimation();
//        iv.startAnimation(anim);
//    }

    protected void showList(){
        list_kata = new String[daftarKata.size()];
        for (int i = 0; i<daftarKata.size(); i++ ){
            list_kata[i] = daftarKata.get(i).getKata();
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.atocomplet, list_kata);

        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoComplete);

        // set adapter for the auto complete fields
        autoComplete.setAdapter(adapter);

        // specify the minimum type of characters before drop-down list is shown
        autoComplete.setThreshold(1);

    }
}
