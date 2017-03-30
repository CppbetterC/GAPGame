package cppbetterc.gapgame;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by l22257810 on 2017/3/31.
 */

public class ReadFile {

    //read the ip address from the file;
    public String readFromFile(Context context) {
        String str = "";
        try {
            InputStream inputStream = context.openFileInput("D:/ANDROID_APP/GAPGame/ip_address.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                str = stringBuilder.toString();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("str", str.toString());
        return str;
    }
}
