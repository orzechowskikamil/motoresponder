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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this.getApplicationContext();

        this.locationUtility = new LocationUtility(this);
        this.smsUtility = new SMSUtility(this);

        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                MainActivity.this.onSMSReceived(phoneNumber, message);
            }
        });

        this.setContentView(R.layout.activity_main);

        this.incomingNumberET = (EditText) findViewById(R.id.editText);
        this.outgoingNumberET = (EditText) findViewById(R.id.editText3);
        this.messageET = (EditText) findViewById(R.id.editText2);
        this.gpsPositionTV = (TextView) findViewById(R.id.textView2);

        this.buttonSendSMS = (Button) findViewById(R.id.button);
        this.buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onButtonSendSMSClick();
            }
        });

        this.buttonSendMMS = (Button) findViewById(R.id.button2);

        this.buttonCall = (Button) findViewById(R.id.button3);
        this.buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + outgoingNumberET.getText().toString()));
                context.startActivity(intent);
            }
        });

        this.buttonGetGpsLocation = (Button) findViewById(R.id.button4);//TODO change to gps on/off
        this.buttonGetGpsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onButtonGPSLocationClick();
            }
        });
        this.buttonSendGpsInfo = (Button) findViewById(R.id.button5);//TODO change to phone state listener on/off
        this.buttonSendGpsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                MyPhoneStateListener phoneStateListener = new MyPhoneStateListener(context);
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        });
    }

    private void onButtonSendSMSClick() {
        String phoneNumber = outgoingNumberET.getText().toString();
        String message = messageET.getText().toString();

        try {
            this.sendSMS(phoneNumber, message);
        } catch (Exception e) {
            Log.i(TAG, "Sending message failed");
        }
    }

    private void onButtonGPSLocationClick() {
        this.locationUtility.listenForLocationChanges(new LocationChangedCallback() {
            public void onLocationChange(Location location, String cityName) {
                MainActivity.this.onLocationAndCityKnown(location, cityName);
            }

            public void onLocationChange(Location location) {
                MainActivity.this.onLocationKnown(location);
            }
        }, true);
    }

    private void onLocationKnown(Location location) {
        String textToSet = location.toString();
        this.gpsPositionTV.setText(textToSet);
    }

    private void onLocationAndCityKnown(Location location, String cityName) {
        String textToSet = location.toString() + "cityname: " + cityName;

        this.gpsPositionTV.setText(textToSet);
    }

    private void onSMSReceived(String phoneNumber, String message) {
        this.showToast("SMS arrived! Phone number: " + phoneNumber + ", message: " + message);
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

    private void sendSMS(String phoneNumber, String message) throws Exception {
        this.smsUtility.sendSMS(phoneNumber, message, new SendSMSCallback() {
            public void onSMSDelivered(String status) {
                if (status != null) {
                    MainActivity.this.showToast(status);
                }
            }

            public void onSMSSent(String status) {
                if (status != null) {
                    MainActivity.this.showToast(status);
                }
            }
        });
    }

    interface LocationChangedCallback {
        void onLocationChange(Location location, String cityName);

        void onLocationChange(Location location);
    }

    interface SendSMSCallback {
        void onSMSSent(String status);

        void onSMSDelivered(String status);
    }

    interface SMSReceivedCallback {
        void onSMSReceived(String phoneNumber, String message);
    }

    class SMSUtility {

        private Context context;
        private SmsManager sms;

        public SMSUtility(Context context) {
            this.context = context;
            this.sms = SmsManager.getDefault();
        }

        public void sendSMS(String phoneNumber, String message, final SendSMSCallback sendSMSCallback) throws Exception {
            if (phoneNumber == null || phoneNumber.length() == 0) {
                throw new Exception("Phone number empty or zero length");
            }


            PendingIntent sentPI = this.createPendingIntent("SMS_SENT", new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    String status = null;

                    switch (this.getResultCode()) {
                        case Activity.RESULT_OK:
                            status = "SMS sent";
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            status = "Generic failure";
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            status = "No service";
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            status = "Null PDU";
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            status = "Radio off";
                            break;
                    }

                    if (sendSMSCallback != null) {
                        sendSMSCallback.onSMSSent(status);
                    }


                }
            });


            PendingIntent deliveredPI = this.createPendingIntent("SMS_DELIVERED", new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    String status = null;
                    switch (this.getResultCode()) {
                        case Activity.RESULT_OK:
                            status = "SMS delivered";
                            break;
                        case Activity.RESULT_CANCELED:
                            status = "SMS not delivered";
                            break;
                    }

                    if (sendSMSCallback != null) {
                        sendSMSCallback.onSMSDelivered(status);
                    }

                }
            });


            this.sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }

        public void listenForSMS(SMSReceivedCallback smsReceivedCallback) {
            this.context.registerReceiver(
                    new IncomingSMSReceiver(smsReceivedCallback),
                    new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            );
        }

        private PendingIntent createPendingIntent(String SENT, BroadcastReceiver broadcastReceiver) {
            PendingIntent sentPI = PendingIntent.getBroadcast(this.context, 0, new Intent(SENT), 0);
            this.context.registerReceiver(broadcastReceiver, new IntentFilter(SENT));
            return sentPI;
        }

        private class IncomingSMSReceiver extends BroadcastReceiver {

            private SMSReceivedCallback smsReceivedCallback;

            public IncomingSMSReceiver(SMSReceivedCallback smsReceivedCallback) {
                this.smsReceivedCallback = smsReceivedCallback;
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean receivedIntentIsSMS = intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED");

                if (receivedIntentIsSMS) {
                    Bundle bundle = intent.getExtras();

                    if (bundle != null) {
                        try {
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            SmsMessage[] smsMessages = null;
                            smsMessages = new SmsMessage[pdus.length];

                            for (int i = 0; i < smsMessages.length; i++) {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                                String phoneNumber = smsMessages[i].getOriginatingAddress();
                                String message = smsMessages[i].getMessageBody();

                                if (this.smsReceivedCallback != null) {
                                    this.smsReceivedCallback.onSMSReceived(phoneNumber, message);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("Exception caught", e.getMessage());
                        }
                    }
                }
            }
        }

    }

    class LocationUtility {

        private LocationManager locationManager;
        private int minimumTimeBetweenUpdates;
        private int minimumDistanceBetweenUpdates;

        public LocationUtility(Context context, int minimumTimeBetweenUpdates, int minimumDistanceBetweenUpdates) {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            this.minimumDistanceBetweenUpdates = minimumDistanceBetweenUpdates;
            this.minimumTimeBetweenUpdates = minimumTimeBetweenUpdates;
        }

        public LocationUtility(Context context) {
            this(context, 5000, 10);
        }

        public void listenForLocationChanges(LocationChangedCallback locationChangedCallback, boolean shouldReceiveCity) {
            this.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    this.minimumTimeBetweenUpdates,
                    this.minimumDistanceBetweenUpdates,
                    new MyLocationListener(locationChangedCallback, shouldReceiveCity)
            );
        }

        private class MyLocationListener implements LocationListener {//responsible for receiving GPS info

            private LocationChangedCallback locationChangedCallback;
            private boolean isTrackingCityEnabled;

            public MyLocationListener(LocationChangedCallback locationChangedCallback) {
                this(locationChangedCallback, false);
            }

            public MyLocationListener(LocationChangedCallback locationChangedCallback, boolean isTrackingCityEnabled) {
                this.locationChangedCallback = locationChangedCallback;
                this.isTrackingCityEnabled = isTrackingCityEnabled;
            }

            @Override
            public void onLocationChanged(Location loc) {
                if (this.isTrackingCityEnabled) {
                    String cityName = null;
                    Geocoder gcd = new Geocoder(MainActivity.this.getBaseContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            System.out.println(addresses.get(0).getLocality());
                        }
                        cityName = addresses.get(0).getLocality();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    this.locationChangedCallback.onLocationChange(loc, cityName);
                } else {
                    this.locationChangedCallback.onLocationChange(loc);
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {//Responsible for incoming phone calls, phone state etc
        Context mContext;
        public String TAG = MyPhoneStateListener.class.getName();

        public MyPhoneStateListener(Context context) {
            this.mContext = context;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            super.onCellInfoChanged(cellInfo);
            Log.i(TAG, "onCellInfoChanged: " + cellInfo);
        }

        @Override
        public void onDataActivity(int direction) {
            super.onDataActivity(direction);
            switch (direction) {
                case TelephonyManager.DATA_ACTIVITY_NONE:
                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_NONE");
                    break;
                case TelephonyManager.DATA_ACTIVITY_IN:
                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_IN");
                    break;
                case TelephonyManager.DATA_ACTIVITY_OUT:
                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_OUT");
                    break;
                case TelephonyManager.DATA_ACTIVITY_INOUT:
                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_INOUT");
                    break;
                case TelephonyManager.DATA_ACTIVITY_DORMANT:
                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_DORMANT");
                    break;
                default:
                    Log.w(TAG, "onDataActivity: UNKNOWN " + direction);
                    break;
            }
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            Log.i(TAG, "onServiceStateChanged: " + serviceState.toString());
            Log.i(TAG, "onServiceStateChanged: getOperatorAlphaLong "
                    + serviceState.getOperatorAlphaLong());
            Log.i(TAG, "onServiceStateChanged: getOperatorAlphaShort "
                    + serviceState.getOperatorAlphaShort());
            Log.i(TAG, "onServiceStateChanged: getOperatorNumeric "
                    + serviceState.getOperatorNumeric());
            Log.i(TAG, "onServiceStateChanged: getIsManualSelection "
                    + serviceState.getIsManualSelection());
            Log.i(TAG,
                    "onServiceStateChanged: getRoaming "
                            + serviceState.getRoaming());

            switch (serviceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    Log.i(TAG, "onServiceStateChanged: STATE_IN_SERVICE");
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    Log.i(TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    Log.i(TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                    break;
                case ServiceState.STATE_POWER_OFF:
                    Log.i(TAG, "onServiceStateChanged: STATE_POWER_OFF");
                    break;
            }
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {//Call state
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_RINGING");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                    break;
                default:
                    Log.i(TAG, "UNKNOWN_STATE: " + state);
                    break;
            }
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onCellLocationChanged(CellLocation location) {//Gsm location info
            super.onCellLocationChanged(location);
            if (location instanceof GsmCellLocation) {
                GsmCellLocation gcLoc = (GsmCellLocation) location;
                Log.i(TAG,
                        "onCellLocationChanged: GsmCellLocation "
                                + gcLoc.toString());
                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getCid "
                        + gcLoc.getCid());
                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getLac "
                        + gcLoc.getLac());
                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getPsc"
                        + gcLoc.getPsc()); // Requires min API 9
            } else if (location instanceof CdmaCellLocation) {
                CdmaCellLocation ccLoc = (CdmaCellLocation) location;
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation "
                                + ccLoc.toString());
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationId "
                                + ccLoc.getBaseStationId());
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
                                + ccLoc.getBaseStationLatitude());
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
                                + ccLoc.getBaseStationLongitude());
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation getNetworkId "
                                + ccLoc.getNetworkId());
                Log.i(TAG,
                        "onCellLocationChanged: CdmaCellLocation getSystemId "
                                + ccLoc.getSystemId());
            } else {
                Log.i(TAG, "onCellLocationChanged: " + location.toString());
            }
        }

        @Override
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            super.onCallForwardingIndicatorChanged(cfi);
            Log.i(TAG, "onCallForwardingIndicatorChanged: " + cfi);
        }

        @Override
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            super.onMessageWaitingIndicatorChanged(mwi);
            Log.i(TAG, "onMessageWaitingIndicatorChanged: " + mwi);
        }
    }
}
