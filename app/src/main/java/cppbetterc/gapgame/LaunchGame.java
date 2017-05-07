package cppbetterc.gapgame;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LaunchGame extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=1000000;
    public static final int READ_TIMEOUT=1000000;
    private AlertDialog dialog = null;
    private EditText etLaunchGame,etHostName;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        button = (Button)findViewById(R.id.bt_launch);
        etLaunchGame = (EditText) findViewById(R.id.ed_game_name);
        etHostName =(EditText) findViewById(R.id.ed_host_name);

        setSupportActionBar(toolbar);
        setTitle("主持新遊戲");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        button.setOnClickListener(listener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater meauInflater = getMenuInflater();
        meauInflater.inflate(R.menu.activity_launch_game_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.action_settings:{
                Toast.makeText(getApplicationContext(),"Setting option selected",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.logout_id:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("即將登出")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LaunchGame.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Canael", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //To Do Thing
                            }
                        });
                dialog = builder.create();
                dialog.show();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String launch_game  = etLaunchGame.getText().toString();
            final String host_name = etHostName.getText().toString();
            new AsyncLaunchGame().execute(launch_game,host_name);
            //bug input "" will insert to the database
            Intent intent = new Intent(LaunchGame.this,CreateNewGame.class);
            startActivity(intent);
        }
    };
    private class AsyncLaunchGame extends AsyncTask<String,String ,String> {

        ProgressDialog pdLoading = new ProgressDialog(LaunchGame.this);
        HttpURLConnection conn;
        URL url=null;

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            pdLoading.setMessage("Loading");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                url=new URL("http://127.0.0.1/cppbetterc/launchgame.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try{
                conn=(HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder=new Uri.Builder()
                        .appendQueryParameter("game_name",params[0])
                        .appendQueryParameter("host_name",params[1]);
                String query = builder.build().getEncodedQuery();
                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", "exception", e);
                return "exception";
            }
            try{
                int response_code = conn.getResponseCode();
                Log.e("exception", conn.toString());
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        result.append(line);
                    }
                    Log.d("result", result.toString());
                    return result.toString();
                }
                else{
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", "exception", e);
                return "exception";
            }
            finally{
                conn.disconnect();
            }
        }
        @Override
        protected  void onPostExecute(String result){
            //this method will be running on UI thread
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("true")){
                Toast.makeText(LaunchGame.this,"Success to add game",Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("false")){
                Toast.makeText(LaunchGame.this,"Faliure to add game",Toast.LENGTH_LONG).show();
            }
        }
    }
}
