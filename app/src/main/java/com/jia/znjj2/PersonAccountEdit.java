package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PersonAccountEdit extends Activity {

    private String paramKey;
    private String paramValue;
    private String result;
    private TextView tvTitle;
    private EditText etEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_account_edit);
        tvTitle = (TextView) findViewById(R.id.person_account_edit_title_text);
        etEdit = (EditText) findViewById(R.id.person_account_edit_et);
        paramKey = getIntent().getStringExtra("paramKey");
        paramValue = getIntent().getStringExtra("paramValue");
        tvTitle.setText(paramKey);
        etEdit.setText(paramValue);
    }

    public void personAccountBack(View view){
        returnResult(paramValue);
    }
    public void personAccountEditSave(View view) {
        String result = etEdit.getText().toString();
        if(result.equals("")){
            Toast.makeText(PersonAccountEdit.this, paramKey + "不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        returnResult(result);
    }

    private void returnResult(String result){
        Intent intent = getIntent();
        intent.putExtra("result", result);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnResult(paramValue);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
