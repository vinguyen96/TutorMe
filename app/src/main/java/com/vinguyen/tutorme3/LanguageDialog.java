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

public class LanguageDialog extends Dialog implements
        View.OnClickListener {

    public Button cancel, apply;
    public String language;
    public RadioButton radioButton, english, chinese, korean;
    public RadioGroup radioGroupSort;

    public LanguageDialog(Activity a) {
        super(a);
    }

    public LanguageDialog(Activity a, SCustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }

    public LanguageDialog(Activity a, String language, SCustomDialogEventListener onCustomDialogEventListener) {
        super(a);
        this.language = language;
        this.onCustomDialogEventListener = onCustomDialogEventListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_language);
        cancel = (Button) findViewById(R.id.cancel);
        apply = (Button) findViewById(R.id.apply);
        radioGroupSort = (RadioGroup) findViewById(R.id.radioGroupSort);
        english = (RadioButton) findViewById(R.id.radioButtonEnglish);
        chinese = (RadioButton) findViewById(R.id.radioButtonChinese);
        korean = (RadioButton) findViewById(R.id.radioButtonKorean);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedID = radioGroupSort.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedID);
                language = radioButton.getText().toString();
                onCustomDialogEventListener.customDialogEvent(language);
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