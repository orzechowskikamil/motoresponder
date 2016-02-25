package com.medziku.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2015-09-08.
 */

interface CallCallback {
    void onCall(String phoneNumber);
}

interface CellStateCallback {
    void onCellState();
}

public class CallsUtility {

    private Context context;
    public List<CellInfo> recentCellInfoList;
    private List<CallCallback> callCallbacksList;

    public CallsUtility(Context context) {
        // TODO: 2015-09-08 add more callbacks! 
        this.context = context;
        this.callCallbacksList = new ArrayList<CallCallback>();
    }

    public void listenForCalls(CallCallback callCallback) {
        TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        MyCallListener phoneStateListener = new MyCallListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.callCallbacksList.add(callCallback);

    }

    public void listenForCellState(CellStateCallback cellStateCallback) {
        // TODO: 2015-09-16 implement
    }

    private class MyCallListener extends PhoneStateListener {//Responsible for incoming phone calls, phone state etc
        //        Context mContext;
        public String TAG = MyCallListener.class.getName();
//
//        public MyCallListener(Context context) {
////            this.mContext = context;
//        }

//        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//        @Override
//        public void onCellInfoChanged(List<CellInfo> cellInfo) {
//            super.onCellInfoChanged(cellInfo);
//            CallsUtility.this.recentCellInfoList = cellInfo;
//        }

//        @Override
//        public void onDataActivity(int direction) {
//            super.onDataActivity(direction);
//            switch (direction) {
//                case TelephonyManager.DATA_ACTIVITY_NONE:
//                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_NONE");
//                    break;
//                case TelephonyManager.DATA_ACTIVITY_IN:
//                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_IN");
//                    break;
//                case TelephonyManager.DATA_ACTIVITY_OUT:
//                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_OUT");
//                    break;
//                case TelephonyManager.DATA_ACTIVITY_INOUT:
//                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_INOUT");
//                    break;
//                case TelephonyManager.DATA_ACTIVITY_DORMANT:
//                    Log.i(TAG, "onDataActivity: DATA_ACTIVITY_DORMANT");
//                    break;
//                default:
//                    Log.w(TAG, "onDataActivity: UNKNOWN " + direction);
//                    break;
//            }
//        }

//        @Override
//        public void onServiceStateChanged(ServiceState serviceState) {
//            super.onServiceStateChanged(serviceState);
//            Log.i(TAG, "onServiceStateChanged: " + serviceState.toString());
//            Log.i(TAG, "onServiceStateChanged: getOperatorAlphaLong "
//                    + serviceState.getOperatorAlphaLong());
//            Log.i(TAG, "onServiceStateChanged: getOperatorAlphaShort "
//                    + serviceState.getOperatorAlphaShort());
//            Log.i(TAG, "onServiceStateChanged: getOperatorNumeric "
//                    + serviceState.getOperatorNumeric());
//            Log.i(TAG, "onServiceStateChanged: getIsManualSelection "
//                    + serviceState.getIsManualSelection());
//            Log.i(TAG,
//                    "onServiceStateChanged: getRoaming "
//                            + serviceState.getRoaming());
//
//            switch (serviceState.getState()) {
//                case ServiceState.STATE_IN_SERVICE:
//                    Log.i(TAG, "onServiceStateChanged: STATE_IN_SERVICE");
//                    break;
//                case ServiceState.STATE_OUT_OF_SERVICE:
//                    Log.i(TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
//                    break;
//                case ServiceState.STATE_EMERGENCY_ONLY:
//                    Log.i(TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
//                    break;
//                case ServiceState.STATE_POWER_OFF:
//                    Log.i(TAG, "onServiceStateChanged: STATE_POWER_OFF");
//                    break;
//            }
//        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {//Call state
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    for (CallCallback callCallback : CallsUtility.this.callCallbacksList) {
                        callCallback.onCall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                default:
                    break;
            }
        }
//
//        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//        @Override
//        public void onCellLocationChanged(CellLocation location) {//Gsm location info
//            super.onCellLocationChanged(location);
//            if (location instanceof GsmCellLocation) {
//                GsmCellLocation gcLoc = (GsmCellLocation) location;
//                Log.i(TAG,
//                        "onCellLocationChanged: GsmCellLocation "
//                                + gcLoc.toString());
//                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getCid "
//                        + gcLoc.getCid());
//                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getLac "
//                        + gcLoc.getLac());
//                Log.i(TAG, "onCellLocationChanged: GsmCellLocation getPsc"
//                        + gcLoc.getPsc()); // Requires min API 9
//            } else if (location instanceof CdmaCellLocation) {
//                CdmaCellLocation ccLoc = (CdmaCellLocation) location;
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation "
//                                + ccLoc.toString());
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationId "
//                                + ccLoc.getBaseStationId());
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
//                                + ccLoc.getBaseStationLatitude());
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
//                                + ccLoc.getBaseStationLongitude());
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation getNetworkId "
//                                + ccLoc.getNetworkId());
//                Log.i(TAG,
//                        "onCellLocationChanged: CdmaCellLocation getSystemId "
//                                + ccLoc.getSystemId());
//            } else {
//                Log.i(TAG, "onCellLocationChanged: " + location.toString());
//            }
//        }
//
//        @Override
//        public void onCallForwardingIndicatorChanged(boolean cfi) {
//            super.onCallForwardingIndicatorChanged(cfi);
//            Log.i(TAG, "onCallForwardingIndicatorChanged: " + cfi);
//        }
//
//        @Override
//        public void onMessageWaitingIndicatorChanged(boolean mwi) {
//            super.onMessageWaitingIndicatorChanged(mwi);
//            Log.i(TAG, "onMessageWaitingIndicatorChanged: " + mwi);
//        }
    }


}