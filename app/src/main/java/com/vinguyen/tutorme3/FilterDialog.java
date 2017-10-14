package com.vinguyen.tutorme3;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by vinguyen on 14/10/17.
 */

public class FilterDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Button cancel, apply;
    public CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    public ArrayList<String> array;

    public FilterDialog(Activity a) {
        super(a);
    }

    public FilterDialog(Activity a, ICustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }

    public FilterDialog(Activity a, ArrayList<String> arrayPass, ICustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.array = arrayPass;
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter);
        cancel = (Button) findViewById(R.id.cancel);
        apply = (Button) findViewById(R.id.apply);

        monday = (CheckBox) findViewById(R.id.checkBoxMonday);
        tuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
        wednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
        thursday = (CheckBox) findViewById(R.id.checkBoxThursday);
        friday = (CheckBox) findViewById(R.id.checkBoxFriday);
        saturday = (CheckBox) findViewById(R.id.checkBoxSaturday);
        sunday = (CheckBox) findViewById(R.id.checkBoxSunday);

        if (array != null) {
            if (array.contains("monday")) {
                monday.setChecked(true);
            }
            if (array.contains("tuesday")) {
                tuesday.setChecked(true);
            }
            if (array.contains("wednesday")) {
                wednesday.setChecked(true);
            }
            if (array.contains("thursday")) {
                thursday.setChecked(true);
            }
            if (array.contains("friday")) {
                friday.setChecked(true);
            }
            if (array.contains("saturday")) {
                saturday.setChecked(true);
            }
            if (array.contains("sunday")) {
                sunday.setChecked(true);
            }
        }

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> availability = new ArrayList<String>();
                if (monday.isChecked()) {
                    availability.add("monday");
                }
                if (tuesday.isChecked()) {
                    availability.add("tuesday");
                }
                if (wednesday.isChecked()) {
                    availability.add("wednesday");
                }
                if (thursday.isChecked()) {
                    availability.add("thursday");
                }
                if (friday.isChecked()) {
                    availability.add("friday");
                }
                if (saturday.isChecked()) {
                    availability.add("saturday");
                }
                if (sunday.isChecked()) {
                    availability.add("sunday");
                }

                onCustomDialogEventListener.customDialogEvent(availability);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private ICustomDialogEventListener onCustomDialogEventListener;

    public interface ICustomDialogEventListener {
        public void customDialogEvent(ArrayList<String> array);
    }

}