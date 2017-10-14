package com.vinguyen.tutorme3;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by vinguyen on 14/10/17.
 */

public class SortDialog extends Dialog implements
        View.OnClickListener {

    public Button cancel, apply;
    public String sortBy;
    public RadioButton radioButton, name, likes;
    public RadioGroup radioGroupSort;

    public SortDialog(Activity a) {
        super(a);
    }

    public SortDialog(Activity a, SCustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }

    public SortDialog(Activity a, String sortByPass, SCustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.sortBy = sortByPass;
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sort);
        cancel = (Button) findViewById(R.id.cancel);
        apply = (Button) findViewById(R.id.apply);
        radioGroupSort = (RadioGroup) findViewById(R.id.radioGroupSort);
        name = (RadioButton) findViewById(R.id.radioButtonName);
        likes = (RadioButton) findViewById(R.id.radioButtonLikes);

        if (sortBy != null) {
            if (sortBy.equals("Name")) {
                name.setChecked(true);
            }
            else {
                likes.setChecked(true);
            }
        }

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedID = radioGroupSort.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedID);
                sortBy = radioButton.getText().toString();
                onCustomDialogEventListener.customDialogEvent(sortBy);
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

    private SCustomDialogEventListener onCustomDialogEventListener;

    public interface SCustomDialogEventListener {
        public void customDialogEvent(String string);
    }

}