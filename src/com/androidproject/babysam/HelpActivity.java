package com.androidproject.babysam;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HelpActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        try {   
            InputStream iFile = getResources().openRawResource(R.raw.help);
            TextView helpText = (TextView) findViewById(R.id.TextView_HelpText);
            String strFile = inputStreamToString(iFile);
            helpText.setText(strFile);
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure", e);
        }

    }

    public String inputStreamToString(InputStream is) throws IOException {
            StringBuffer sBuffer = new StringBuffer();
            DataInputStream dataIO = new DataInputStream(is);
            String strLine = null;
            while ((strLine = dataIO.readLine()) != null) {
                sBuffer.append(strLine + "\n");
            }
            dataIO.close();
            is.close();
            return sBuffer.toString();
    }
}