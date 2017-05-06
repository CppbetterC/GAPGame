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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("主持新遊戲");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

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
                url=new URL("http://140.134.26.31/cppbetterc/launchgame.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            //Log.v("TAG","checkpoint");
            try{
                //Set HttpURLConnection class to send and receive data from php and mysql
                conn=(HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                //set DoInput and DoOutput method depict(描繪) handing of both send and service
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Append(附加) parameters to URL(使用Uri)
                Uri.Builder builder=new Uri.Builder()
                        .appendQueryParameter("username",params[0])
                        .appendQueryParameter("password",params[1]);
                String query = builder.build().getEncodedQuery();

                //Open connection for sending data
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
                //Check if successful connection made
                if(response_code == HttpURLConnection.HTTP_OK){
                    //read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    StringBuilder result = new StringBuilder();
                    String line;

                    while((line = reader.readLine())!=null){
                        result.append(line);
                    }
                    Log.d("result", result.toString());
                    //pass data to onPostExecute method
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
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("true")){
                Intent intent = new Intent(getApplicationContext(), CreateNewGame.class);
                startActivity(intent);
            }
            else if(result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(LaunchGame.this,"Invalid username or password",Toast.LENGTH_LONG).show();
            }
        }
    }
}
