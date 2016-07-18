package com.medziku.motoresponder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class WizardActivity extends WizardActivityBase {

    // TODO K. Orzechowski: translate to polish
    // TODO K. Orzechowski: ask about limitation of responses

    protected boolean displayStepOfWizard(int step) {
        switch (step) {
            case 0:
                this.loadEnableSensorCheckStepOfWizard();
                return true;
            case 1:
                this.loadAllowGeolocationStepOfWizard();
                return true;
            case 2:
                this.loadWhenAppNotWorkStepOfWizard();
                return true;
        }

        return false;
    }

    private void loadWhenAppNotWorkStepOfWizard() {
        this.loadInformativeStepOfWizard(
                R.string.wizard_when_app_not_work_title,
                R.string.wizard_when_app_not_work_description,
                R.string.wizard_when_app_not_work_yes_label);
    }

    private void loadAllowGeolocationStepOfWizard() {
        this.loadYesNoWizardStep(
                R.string.wizard_geolocation_title,
                R.string.wizard_geolocation_description,
                R.string.wizard_geolocation_yes_label,
                R.string.wizard_geolocation_no_label,
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        WizardActivity.this.settings.setRespondingWithGeolocationEnabled(true);
                        return false;
                    }
                },
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        WizardActivity.this.settings.setRespondingWithGeolocationEnabled(false);
                        return false;
                    }
                });
    }

    private void loadEnableSensorCheckStepOfWizard() {
        this.loadYesNoWizardStep(
                R.string.wizard_sensor_check_title,
                R.string.wizard_sensor_check_description,
                R.string.wizard_sensor_check_yes_label,
                R.string.wizard_sensor_check_no_label,
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        WizardActivity.this.settings.setSensorCheckEnabled(true);
                        return false;
                    }
                },
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        WizardActivity.this.settings.setSensorCheckEnabled(false);
                        return false;
                    }
                });
    }
}


abstract class WizardActivityBase extends Activity {

    protected SharedPreferencesUtility sharedPreferencesUtility;
    protected Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(new SharedPreferencesUtility(this));

        this.displayCurrentStepOfWizardOrSetFinished();
    }

    /**
     * @return false if given step of wizard doesn't exist, true if it exist and it will be displayed.
     */
    abstract protected boolean displayStepOfWizard(int whichStep);

    protected void onFinishingStepOfWizard() {
        this.incrementWizardStepCounter();
        this.displayCurrentStepOfWizardOrSetFinished();

        if (this.isWizardCompleted()) {
            this.exitActivity();
        }
    }

    protected void loadWizardStep(int layoutID, Integer yesButtonLabelResID, final Predicate<Boolean> yesCallback) {
        this.setContentView(layoutID);
        Button yesButton = (Button) this.findViewById(R.id.wizard_yes_button);

        if (yesButtonLabelResID != null) {
            yesButton.setText(yesButtonLabelResID);
        }


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesCallback != null) {
                    yesCallback.apply(true);
                }
                WizardActivityBase.this.onFinishingStepOfWizard();
            }
        });
    }

    protected void loadYesNoWizardStep(int titleTextResID,
                                       int descriptionTextResID,
                                       int yesButtonLabelResID,
                                       int noButtonLabelResID,
                                       final Predicate<Boolean> yesCallback,
                                       final Predicate<Boolean> noCallback) {

        int layoutID = R.layout.wizard_yes_no;
        this.loadWizardStepWithTitleAndDescription(layoutID, titleTextResID, descriptionTextResID, yesButtonLabelResID, yesCallback);

        Button noButton = (Button) this.findViewById(R.id.wizard_no_button);
        noButton.setText(noButtonLabelResID);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noCallback.apply(true);
                WizardActivityBase.this.onFinishingStepOfWizard();
            }
        });
    }

    protected void loadInformativeStepOfWizard(int titleResID, int descriptionResID, int yesButtonLabelResID) {
        this.loadWizardStepWithTitleAndDescription(R.layout.wizard_informative, titleResID, descriptionResID, yesButtonLabelResID, null);
    }

    private void loadWizardStepWithTitleAndDescription(int layoutID, int titleTextResID, int descriptionTextResID, int yesButtonLabelResID, Predicate<Boolean> yesCallback) {
        this.loadWizardStep(layoutID, yesButtonLabelResID, yesCallback);

        TextView titleTextView = (TextView) this.findViewById(R.id.wizard_step_title);
        TextView descriptionTextView = (TextView) this.findViewById(R.id.wizard_step_description);

        titleTextView.setText(titleTextResID);
        descriptionTextView.setText(descriptionTextResID);
    }

    private void exitActivity() {
        this.finish();
    }


    private void incrementWizardStepCounter() {
        this.settings.setCurrentStepOfWizard(this.settings.getCurrentStepOfWizard() + 1);
    }

    /**
     * This method will display current step of wizard, according to the settings.
     */
    private void displayCurrentStepOfWizardOrSetFinished() {
        boolean isWizardFinished = !this.displayStepOfWizard(this.settings.getCurrentStepOfWizard());

        if (isWizardFinished) {
            this.setWizardAsCompleted();
        }
    }

    /**
     * Why I use this?
     * Because, I can calculate how much steps is performed, AND i can set wizard into completed state. Then if I will
     * add in future additional step, calculating last performed step would show that previously completed wizard is not completed
     * now, and with this solution once wizard is set to be complete, it will be complete even if new step will be added in future
     */
    private boolean isWizardCompleted() {
        return this.settings.isWizardCompleted();
    }

    private void setWizardAsCompleted() {
        this.settings.setWizardAsCompleted();
    }
}
