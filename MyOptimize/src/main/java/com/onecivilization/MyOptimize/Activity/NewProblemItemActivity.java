package com.onecivilization.MyOptimize.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/8/15.
 */
public class NewProblemItemActivity extends BaseActivity {

    private long lastBackPressedTime = 0L;
    private Toolbar toolbar;
    private EditText title;
    private EditText description;
    private Spinner rankChooser;
    private Button createButton;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        rankChooser = (Spinner) findViewById(R.id.rank_chooser);
        createButton = (Button) findViewById(R.id.create_button);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_problem_item);
        findViews();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rankChooser.setAdapter(new ArrayAdapter<>(this, R.layout.item_type, getResources().getStringArray(R.array.problem_item_ranks)));
        rankChooser.setSelection(1);
        rankChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                switch (position) {
                    case 0:
                        view.setBackgroundColor(getResources().getColor(R.color.state_true));
                        break;
                    case 1:
                        view.setBackgroundColor(getResources().getColor(R.color.state_false));
                        break;
                    case 2:
                        view.setBackgroundColor(getResources().getColor(R.color.state_warning));
                        break;
                    case 3:
                        view.setBackgroundColor(getResources().getColor(R.color.state_extra_high));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProblemItem();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tips:
                return true;
            case R.id.action_save_changes:
                addProblemItem();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressedTime < 2000) {
            finish();
        } else {
            lastBackPressedTime = now;
            Toast.makeText(this, R.string.giving_up_modifies, Toast.LENGTH_SHORT).show();
        }
    }

    private void addProblemItem() {
        if (title.getText().toString().equals("")) {
            Toast.makeText(NewProblemItemActivity.this, R.string.empty_title_warning, Toast.LENGTH_SHORT).show();
        } else {
            DataManager.getInstance().addProblemItem(new Problem(title.getText().toString(), description.getText().toString(),
                    rankChooser.getSelectedItemPosition() + 1, DataManager.getInstance().getMaxProblemOrder() + 1, System.currentTimeMillis()));
            finish();
        }
    }
}
