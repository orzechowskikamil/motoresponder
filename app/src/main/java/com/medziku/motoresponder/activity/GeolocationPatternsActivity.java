package com.medziku.motoresponder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.GeolocationRequestRecognition;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.List;

public class GeolocationPatternsActivity extends Activity {

    private Settings settings;
    private GeolocationRequestRecognition responseRecognition;
    private String NO_GEOLOCATION_LABEL;
    private String YES_GEOLOCATION_LABEL;
    private String ADD_DIALOG_BUTTON;
    private String CANCEL_DIALOG_BUTTON;
    private String ADD_DIALOG_MESSAGE;
    private String ADD_DIALOG_TITLE;
    private String TEST_DIALOG_MESSAGE;
    private String TEST_DIALOG_TITLE;
    private String TEST_DIALOG_BUTTON;
    private String RESULT_DIALOG_TITLE;

    private List<String> scrollListItems;
    private ArrayAdapter scrollListAdapter;
    private String ACCEPT_RESULT_BUTTON;

    public boolean isMessageGeolocationRequest(String message) {
        return this.responseRecognition.isGeolocationRequest(message);
    }

    public List<String> getGeolocationPatterns() {
        return this.settings.getGeolocationRequestPatterns();
    }

    public void setGeolocationPatterns(List<String> patterns) {
        this.settings.setGeolocationRequestPatterns(patterns);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.NO_GEOLOCATION_LABEL = this.getString(R.string.geolocation_patterns_tester_no_geolocation_label);
        this.YES_GEOLOCATION_LABEL = this.getString(R.string.geolocation_patterns_tester_yes_geolocation_label);
        this.ADD_DIALOG_BUTTON = this.getString(R.string.geolocation_patterns_add_dialog_button);
        this.CANCEL_DIALOG_BUTTON = this.getString(R.string.geolocation_patterns_cancel_dialog_button);
        this.ADD_DIALOG_MESSAGE = this.getString(R.string.geolocation_patterns_add_dialog_message);
        this.ADD_DIALOG_TITLE = this.getString(R.string.geolocation_patterns_add_dialog_title);
        this.TEST_DIALOG_MESSAGE = this.getString(R.string.geolocation_patterns_test_dialog_message);
        this.TEST_DIALOG_TITLE = this.getString(R.string.geolocation_patterns_test_dialog_title);
        this.TEST_DIALOG_BUTTON = this.getString(R.string.geolocation_patterns_test_dialog_button);
        this.RESULT_DIALOG_TITLE=this.getString(R.string.geolocation_patterns_tester_result_label);
        this.ACCEPT_RESULT_BUTTON=this.getString(R.string.geolocation_patterns_accept_result_button);

        this.settings = new Settings(new SharedPreferencesUtility(this));
        this.responseRecognition = new GeolocationRequestRecognition(this.settings);
        this.setContentView(R.layout.geolocation_patterns);

        this.getTesterButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeolocationPatternsActivity.this.onClickOnTestCurrentMessage();
            }
        });


        this.getAddNewPatternButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeolocationPatternsActivity.this.onClickOnAddPattern();
            }
        });

        this.handleList();
    }

    private void onClickOnAddPattern() {
        this.showAddNewPatternDialog(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                GeolocationPatternsActivity.this.addPatternToList(input);
                return false;
            }
        });
    }

    private void addPatternToList(String patternText) {
        GeolocationPatternsActivity.this.scrollListItems.add(patternText);
        GeolocationPatternsActivity.this.scrollListAdapter.notifyDataSetChanged();
        this.storeCurrentPatterns();
    }

    private void showAddNewPatternDialog(final Predicate<String> addCallback) {
        String cancelButtonLabel = GeolocationPatternsActivity.this.CANCEL_DIALOG_BUTTON;
        String okButtonLabel = GeolocationPatternsActivity.this.ADD_DIALOG_BUTTON;


        AlertDialog.Builder alertBuilder = this.buildEditTextDialog(addCallback, cancelButtonLabel, okButtonLabel);


        alertBuilder.setMessage(this.ADD_DIALOG_MESSAGE);
        alertBuilder.setTitle(this.ADD_DIALOG_TITLE);

        alertBuilder.show();
    }

    private void showTestPatternDialog(final Predicate<String> addCallback) {
        String cancelButtonLabel = GeolocationPatternsActivity.this.CANCEL_DIALOG_BUTTON;
        String okButtonLabel = GeolocationPatternsActivity.this.TEST_DIALOG_BUTTON;


        AlertDialog.Builder alertBuilder = this.buildEditTextDialog(addCallback, cancelButtonLabel, okButtonLabel);


        alertBuilder.setMessage(this.TEST_DIALOG_MESSAGE);
        alertBuilder.setTitle(this.TEST_DIALOG_TITLE);

        alertBuilder.show();
    }


    private AlertDialog.Builder buildEditTextDialog(final Predicate<String> addCallback, String cancelButtonLabel, String okButtonLabel) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);

        alertBuilder.setView(editText);

        editText.requestFocus();
        alertBuilder.setPositiveButton(okButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editTextValue = editText.getText().toString();
                addCallback.apply(editTextValue);
            }
        });

        alertBuilder.setNegativeButton(cancelButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return alertBuilder;
    }

    private void onClickOnTestCurrentMessage() {
        this.showTestPatternDialog(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                boolean isGeolocationRequest = GeolocationPatternsActivity.this.isMessageGeolocationRequest(input);

                String message = (isGeolocationRequest)
                        ? GeolocationPatternsActivity.this.YES_GEOLOCATION_LABEL
                        : GeolocationPatternsActivity.this.NO_GEOLOCATION_LABEL;

                GeolocationPatternsActivity.this.showSimpleDialog(GeolocationPatternsActivity.this.RESULT_DIALOG_TITLE, message);
                return false;
            }
        });
    }

    private void showSimpleDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GeolocationPatternsActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(this.ACCEPT_RESULT_BUTTON, null);
        builder.show();
    }

    private Button getAddNewPatternButton() {
        return (Button) this.findViewById(R.id.geolocation_patterns_new_pattern_add_button);
    }

    private Button getTesterButton() {
        return (Button) this.findViewById(R.id.geolocation_patterns_tester_button);
    }


    private void handleList() {
        final ListView scrollList = this.getScrollListView();

        this.scrollListItems = this.getGeolocationPatterns();

        this.scrollListAdapter = new ArrayAdapter<String>(
                this.getApplicationContext(),
                R.layout.geolocation_patterns_list_item,
                scrollListItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the data item for this position
                String pattern = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.geolocation_patterns_list_item, parent, false);
                }
                // Lookup view for data population
                TextView patternTextView = (TextView) convertView.findViewById(R.id.geolocation_patterns_item_pattern);

                patternTextView.setText(pattern);

                return convertView;
            }

        };
        scrollList.setAdapter(this.scrollListAdapter);

        scrollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                GeolocationPatternsActivity.this.onClickOnItem(position);
            }
        });
    }

    private ListView getScrollListView() {
        return (ListView) this.findViewById(R.id.geolocation_patterns_list);
    }


    private void onClickOnItem(int position) {
        this.removePatternAtPosition(position);
    }

    private void removePatternAtPosition(int position) {
        String item = (String) this.getScrollListView().getItemAtPosition(position);
        GeolocationPatternsActivity.this.scrollListItems.remove(item);
        this.scrollListAdapter.notifyDataSetChanged();
        this.storeCurrentPatterns();
    }

    private void storeCurrentPatterns() {
        this.setGeolocationPatterns(this.scrollListItems);
    }
}
