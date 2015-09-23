package com.medziku.motoresponder.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.Responder;
import com.medziku.motoresponder.callbacks.CallCallback;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SensorsUtility;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    //    private Button buttonSendSMS, buttonSendMMS, buttonCall, buttonGetGpsLocation, buttonSendGpsInfo;
//    private EditText incomingNumberET, outgoingNumberET, messageET;
//    private TextView gpsPositionTV;
//    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        this.setContentView(R.layout.activity_main);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
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
/*
    private void showToast(String toastText) {
        Context baseContext = MainActivity.this.getBaseContext();
        Toast toast = Toast.makeText(baseContext, toastText, Toast.LENGTH_SHORT);
        toast.show();
    }*/


}
