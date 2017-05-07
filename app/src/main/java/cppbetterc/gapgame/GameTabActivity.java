package cppbetterc.gapgame;

import android.*;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class GameTabActivity extends Fragment{
    private Button button, scanbutton;
    private ListView listView;
    private TextView textView, scanView;
    private  final int SPEECH_OUTPUT = 143;
    public static final int PEQUEST_CODE = 100;
    public static final int PERMISSION_PEQUEST = 200;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_tab, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        button = (Button) v.findViewById(R.id.b_speech);
        listView = (ListView) v.findViewById(android.R.id.list);
        textView = (TextView) v.findViewById(R.id.t_speech_ans);
        scanbutton = (Button) v.findViewById(R.id.b_scan);
        scanView= (TextView) v.findViewById(R.id.t_qrcode_ans);
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String []{android.Manifest.permission.CAMERA}, PERMISSION_PEQUEST);
        }
        scanbutton.setOnClickListener(scnlistener);
        button.setOnClickListener(listener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        textView.setText(listView.getItemAtPosition(0).toString());
                        break;
                    case 1:
                        textView.setText(listView.getItemAtPosition(1).toString());
                        break;
                    case 2:
                        textView.setText(listView.getItemAtPosition(2).toString());
                        break;
                    case 3:
                        textView.setText(listView.getItemAtPosition(3).toString());
                        break;
                    case 4:
                        textView.setText(listView.getItemAtPosition(4).toString());
                        break;
                    case 5:
                        textView.setText(listView.getItemAtPosition(5).toString());
                        break;
                    case 6:
                        textView.setText(listView.getItemAtPosition(6).toString());
                        break;
                }
            }
        });
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ok google");
            try{
                startActivityForResult(intent, SPEECH_OUTPUT);
            }
            catch(ActivityNotFoundException tim){
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPEECH_OUTPUT && resultCode == RESULT_OK && data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textView.setText(result.get(0));
            listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, result));
        }
        if(requestCode == PERMISSION_PEQUEST && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("BARCODE");
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(barcode.displayValue);
                    }
                });
            }
        }
    }
    private View.OnClickListener scnlistener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            startActivityForResult(intent,200);
        }
    };
}
