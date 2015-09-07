package com.medziku.myapplication;

/**
 * Created by Kamil on 2015-09-08.
 */
public class Responder {


    private int waitBeforeHandling = 10000;

    public Responder() {

    }

    public void onSMSReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);

    }

    public void onCallReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);
    }

    public void cancelAllHandling() {

    }

    private void handleIncoming(String phoneNumber) {
        if (this.phoneIsLocked() == false) {
            return;
        }

        this.wait(this.waitBeforeHandling);

        if (this.phoneIsLocked() == false) {
            return;
        }

        if (this.phoneIsMoving() == false) {
            return;
        }

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
