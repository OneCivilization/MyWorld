package com.onecivilization.Optimize.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/11.
 */
public class DescriptionActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText description_title;
    private EditText description_content;
    private TextView lastEditedTime;
    private String description_content_text;
    private long descriptionLastEditedTime;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        description_title = (EditText) findViewById(R.id.description_title);
        description_content = (EditText) findViewById(R.id.description_content);
        lastEditedTime = (TextView) findViewById(R.id.last_edited_time);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        findViews();
        Intent data = getIntent();
        description_title.setText(data.getStringExtra("descriptionTitle"));
        description_content_text = data.getStringExtra("descriptionContent");
        description_content.setText(description_content_text);
        descriptionLastEditedTime = data.getLongExtra("descriptionLastEditedTime", System.currentTimeMillis());
        lastEditedTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(descriptionLastEditedTime)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_changes:
                setResult();
                finish();
                return true;
            case R.id.action_tips:
                return true;
        }
        return false;
    }

    private void setResult() {
        setResult(RESULT_OK, new Intent()
                .putExtra("descriptionTitle", description_title.getText().toString())
                .putExtra("descriptionContent", description_content.getText().toString())
                .putExtra("descriptionLastEditedTime", description_content_text.equals(description_content.getText().toString())
                        ? descriptionLastEditedTime : System.currentTimeMillis())
        );
    }

    @Override
    public void onBackPressed() {
        setResult();
        finish();
    }
}
