package com.medziku.motoresponder.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.medziku.motoresponder.R;

import java.util.List;

abstract public class CheckboxPickerActivity extends Activity {

    List<CheckboxPickerRowDefinition> scrollListItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.checkbox_picker);
        this.draw();
    }

    protected abstract List<CheckboxPickerRowDefinition> getRows();

    protected abstract void onChoiceMade(List<CheckboxPickerRowDefinition> rows);


    @Override
    protected void onStop() {
        this.onChoiceMade(this.scrollListItems);
        super.onStop();
    }

    private void draw() {
        ListView scrollList = (ListView) this.findViewById(R.id.checkboxes_rows_scroll);
        this.scrollListItems = this.getRows();

        final ArrayAdapter<CheckboxPickerRowDefinition> adapter = new ArrayAdapter<CheckboxPickerRowDefinition>(
                this.getApplicationContext(),
                R.layout.checkbox_picker_item,
                scrollListItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the data item for this position
                CheckboxPickerRowDefinition row = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_picker_item, parent, false);
                }
                // Lookup view for data population
                TextView descriptionTextView = (TextView) convertView.findViewById(R.id.checkbox_picker_item_description);
                CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_picker_item_checkbox);

                checkbox.setText(row.title);
                descriptionTextView.setText(row.description);
                checkbox.setChecked(row.isChecked);

                return convertView;
            }
        };
        scrollList.setAdapter(adapter);

        scrollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckboxPickerRowDefinition item = (CheckboxPickerRowDefinition) parent.getItemAtPosition(position);
                item.isChecked = !item.isChecked;
                adapter.notifyDataSetChanged();
            }


        });

        Button selectAllButton = (Button) this.findViewById(R.id.checkbox_picker_select_all);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckboxPickerRowDefinition row : scrollListItems) {
                    row.isChecked = true;
                }
                adapter.notifyDataSetChanged();
            }
        });

        Button selectNoneButton = (Button) this.findViewById(R.id.checkbox_picker_select_none);
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckboxPickerRowDefinition row : scrollListItems) {
                    row.isChecked = false;
                }
                adapter.notifyDataSetChanged();
            }
        });


    }
}

class CheckboxPickerRowDefinition {
    public String title;
    public String description;
    public boolean isChecked;
}