package com.medziku.motoresponder.activity;


import com.medziku.motoresponder.activity.base.ContactsCheckboxPickerActivity;

import java.util.List;

public class WhiteListCheckboxPickerActivity extends ContactsCheckboxPickerActivity {


    @Override
    public void onContactsChoosen(List<String> checkedNumbers) {
        this.settings.setWhitelistedContactsList(checkedNumbers);
    }

    public List<String> getCheckedNumbers() {
        return this.settings.getWhitelistedContactsList();
    }


}
