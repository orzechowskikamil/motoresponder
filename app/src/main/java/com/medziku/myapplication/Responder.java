package com.medziku.myapplication;

/**
 * Created by Kamil on 2015-09-08.
 */
public class Responder {


    private int waitBeforeHandling = 10000;

    public Responder() {
        // probably we have to start every onsmsreceived in new thread
    }

    public void onSMSReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);

    }

    public void onCallReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);
    }
    public void cancelAllHandling() {

    }
    

   

    private boolean phoneIsMoving() {
        return false;
        // accelerometer
    }

    private void wait(int amountMs) {
    }

    private boolean phoneIsLocked() {
        return false;
    }


}
