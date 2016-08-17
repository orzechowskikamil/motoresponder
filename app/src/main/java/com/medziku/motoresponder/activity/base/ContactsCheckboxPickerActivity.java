package com.medziku.motoresponder.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import com.medziku.motoresponder.utils.structures.ContactDefinition;

import java.util.ArrayList;
import java.util.List;

public abstract class ContactsCheckboxPickerActivity extends CheckboxPickerActivity {

    protected Context context;
    protected ContactsUtility contactsUtility;
    protected Settings settings;


    public abstract void onContactsChoosen(List<String> checkedNumbers);

    public abstract List<String> getCheckedNumbers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = this.getApplicationContext();
        this.settings = new Settings(new SharedPreferencesUtility(this.context));
        this.contactsUtility = new ContactsUtility(this.context);
        super.onCreate(savedInstanceState);
    }


    protected List<CheckboxPickerRowDefinition> getRows() {
        List<CheckboxPickerRowDefinition> rows = new ArrayList<>();
        List<String> checkedNumbers = this.getCheckedNumbers();

        if (checkedNumbers != null) {
            for (ContactDefinition contact : this.getAllContacts()) {
                CheckboxPickerRowDefinition row = new CheckboxPickerRowDefinition();

                boolean isChecked = checkedNumbers.indexOf(contact.phoneNumber) > -1;

                row.title = contact.name;
                row.description = contact.phoneNumber;
                row.isChecked = isChecked;
                rows.add(row);
            }
        }

        return rows;
    }

    @Override
    protected void onChoiceMade(List<CheckboxPickerRowDefinition> rows) {
        List<String> checkedNumbers = new ArrayList<>();
        for (CheckboxPickerRowDefinition row : rows) {
            if (row.isChecked) {
                checkedNumbers.add(row.description);
            }
        }
        this.onContactsChoosen(checkedNumbers);
    }

    private List<ContactDefinition> getAllContacts() {
        return this.contactsUtility.getAllContacts();
    }

}
