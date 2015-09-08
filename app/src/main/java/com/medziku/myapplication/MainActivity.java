package com.medziku.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    private Button buttonSendSMS, buttonSendMMS, buttonCall, buttonGetGpsLocation, buttonSendGpsInfo;
    private EditText incomingNumberET, outgoingNumberET, messageET;
    private TextView gpsPositionTV;
    private Context context;
    private LocationUtility locationUtility;
    private SMSUtility smsUtility;

    private PhoneStateUtility phoneStateUtility;

    private Responder responder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this.getApplicationContext();

        this.locationUtility = new LocationUtility(this);
        this.smsUtility = new SMSUtility(this);
        this.phoneStateUtility = new PhoneStateUtility(this);

        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                MainActivity.this.onSMSReceived(phoneNumber, message);
            }
        });

        this.responder = new Responder();


        this.setContentView(R.layout.activity_main);

//        this.incomingNumberET = (EditText) findViewById(R.id.editText);
//        this.outgoingNumberET = (EditText) findViewById(R.id.editText3);
//        this.messageET = (EditText) findViewById(R.id.editText2);
//        this.gpsPositionTV = (TextView) findViewById(R.id.textView2);

//        this.buttonSendSMS = (Button) findViewById(R.id.button);
//        this.buttonSendSMS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.this.onButtonSendSMSClick();
//            }
//        });

//        this.buttonSendMMS = (Button) findViewById(R.id.button2);

//        this.buttonCall = (Button) findViewById(R.id.button3);
//        this.buttonCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_CALL);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setData(Uri.parse("tel:" + outgoingNumberET.getText().toString()));
//                context.startActivity(intent);
//            }
//        });

//        this.buttonGetGpsLocation = (Button) findViewById(R.id.button4);//TODO change to gps on/off
//        this.buttonGetGpsLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MainActivity.this.onButtonGPSLocationClick();
//            }
//        });
//        this.buttonSendGpsInfo = (Button) findViewById(R.id.button5);//TODO change to phone state listener on/off
//        this.buttonSendGpsInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.this.listenForCalls();
//            }
//        });
    }

    private void listenForCalls() {
        this.phoneStateUtility.listenForCalls();
    }

//    private void onButtonSendSMSClick() {
//        String phoneNumber = outgoingNumberET.getText().toString();
//        String message = messageET.getText().toString();
//
//        try {
//            this.sendSMS(phoneNumber, message);
//        } catch (Exception e) {
//            Log.i(TAG, "Sending message failed");
//        }
//    }
//
//    private void onButtonGPSLocationClick() {
//        this.locationUtility.listenForLocationChanges(new LocationChangedCallback() {
//            public void onLocationChange(Location location, String cityName) {
//                MainActivity.this.onLocationAndCityKnown(location, cityName);
//            }
//
//            public void onLocationChange(Location location) {
//                MainActivity.this.onLocationKnown(location);
//            }
//        }, true);
//    }

//    private void onLocationKnown(Location location) {
//        String textToSet = location.toString();
//        this.gpsPositionTV.setText(textToSet);
//    }
//
//    private void onLocationAndCityKnown(Location location, String cityName) {
//        String textToSet = location.toString() + "cityname: " + cityName;
//
//        this.gpsPositionTV.setText(textToSet);
//    }

    private void onSMSReceived(String phoneNumber, String message) {
        this.showToast("SMS arrived! Phone number: " + phoneNumber + ", message: " + message);


        this.responder.onSMSReceived(phoneNumber);
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

    private void showToast(String toastText) {
        Context baseContext = MainActivity.this.getBaseContext();
        Toast toast = Toast.makeText(baseContext, toastText, Toast.LENGTH_SHORT);
        toast.show();
    }

//    private void sendSMS(String phoneNumber, String message) throws Exception {
//        this.smsUtility.sendSMS(phoneNumber, message, new SendSMSCallback() {
//            public void onSMSDelivered(String status) {
//                if (status != null) {
//                    MainActivity.this.showToast(status);
//                }
//            }
//
//            public void onSMSSent(String status) {
//                if (status != null) {
//                    MainActivity.this.showToast(status);
//                }
//            }
//        });
//    }


}
