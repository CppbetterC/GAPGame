package cppbetterc.gapgame;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class CreateNewGame extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog dialog = null;
    public String [][] data = {
            {"認識逢甲","蔡昌銘"},
            {"認識東海","陳柏翔"},
            {"認識士林","鄭世麟"}
    };
    public static final int CONNECTION_TIMEOUT=1000000;
    public  final int READ_TIMEOUT=1000000;
    private ListView  createGamelistView;
    MainActivity m = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("個人資訊");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.drawable.ic_menu_camera);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private AdapterView.OnItemClickListener onClickListView =new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(CreateNewGame.this,"成功",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater meauInflater = getMenuInflater();
        meauInflater.inflate(R.menu.activity_create_new_game_actions,menu);
        return super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.create_new_game, menu);
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                                Intent intent = new Intent(CreateNewGame.this,MainActivity.class);
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
            case R.id.launch_game_id:{
                Intent intent = new Intent(CreateNewGame.this,LaunchGame.class);
                startActivity(intent);
                return true;
            }
            case R.id.loading_id: {
                // input SQL Query
                final String selectSQL = "SELECT * FROM game_information";
                new CreateNewGame.AsyncGetGameInformation().execute("getGameInformation",selectSQL);

                final View LinearLayout = LayoutInflater.from(CreateNewGame.this).inflate(R.layout.loadgame_click_content,null);
                createGamelistView = (ListView) LinearLayout.findViewById(R.id.list);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LoadingGameAdapter gameAdapter = new LoadingGameAdapter(data,inflater);
                createGamelistView.setAdapter(gameAdapter);
                createGamelistView.setOnItemClickListener(onClickListView);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("LoadingGame")
                        .setView(LinearLayout)
                        .setPositiveButton("取消" , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //to do;
                            }
                        });
                dialog = builder.create();
                dialog.show();
//                Toast.makeText(getApplicationContext(), "JoinGame option selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.joinGame_id: {
                final View LinearLayout = LayoutInflater.from(CreateNewGame.this).inflate(R.layout.joingame_click_content, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("邀請碼")
                        .setView(LinearLayout); //設定內容外觀

                final EditText etGamekey = (EditText) LinearLayout.findViewById(R.id.etGamekey);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                { //設定確定按鈕
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String game_key = etGamekey.getText().toString();
                        new CreateNewGame.AsyncGetGameInformation().execute("getGameKey",game_key );
                    }
                });
                dialog = builder.create(); //建立對話方塊並存成 dialog
                dialog.show();
            }
            default:{
                return super.onOptionsItemSelected(item);
            }

        }
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager(); //error ??

        if (id == R.id.nav_first_layout) {
            fragmentManager.beginTransaction().replace(R.id.LinearLayout, new FirstFragment()).commit();
        } else if (id == R.id.nav_second_layout) {
            fragmentManager.beginTransaction().replace(R.id.LinearLayout, new SecondFragment()).commit();
        } else if (id == R.id.nav_third_layout) {
            fragmentManager.beginTransaction().replace(R.id.LinearLayout, new ThirdFragment()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class AsyncGetGameInformation extends AsyncTask<String, String ,String> {

        ProgressDialog pdLoading = new ProgressDialog(CreateNewGame.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            pdLoading.setMessage("Loading");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String str = params[0];
            try{
                url = new URL("http://127.0.0.1/cppbetterc/"+ str + ".php");
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
                        .appendQueryParameter("game_key", params[1]);
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
                Intent intent = new Intent(CreateNewGame.this, SwipeActivity.class);
                startActivity(intent);
            }
            else if(result.equalsIgnoreCase("false")){
                Toast.makeText(CreateNewGame.this,"Invalid KeyName",Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){
        AlertDialog.Builder ad=new AlertDialog.Builder(CreateNewGame.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                CreateNewGame.this.finish();
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        ad.show();
    }
}
