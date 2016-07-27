package com.onecivilization.MyOptimize.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.BlankFragment;
import com.onecivilization.MyOptimize.Fragment.NewNonperiodicCareFragment;
import com.onecivilization.MyOptimize.Fragment.NewPeriodicCareFragment;
import com.onecivilization.MyOptimize.Fragment.NewTextCareFragment;
import com.onecivilization.MyOptimize.Interface.NewCareFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.TextCare;
import com.onecivilization.MyOptimize.R;

public class NewCareItemActivity extends BaseActivity {

    private long lastBackPressedTime = 0L;
    private Toolbar toolbar;
    private EditText titleEditText;
    private LinearLayout description;
    private TextView descriptionTitle;
    private Spinner typeChooser;
    private FragmentManager fragmentManager;
    private NewCareFragment newCareFragment;
    private Button createButton;
    private DataManager dataManager = DataManager.getInstance();
    private String descriptionContent = "";
    private long descriptionLastEditedTime = System.currentTimeMillis();

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleEditText = (EditText) findViewById(R.id.title_editText);
        description = (LinearLayout) findViewById(R.id.description);
        descriptionTitle = (TextView) findViewById(R.id.description_title);
        typeChooser = (Spinner) findViewById(R.id.type_chooser);
        createButton = (Button) findViewById(R.id.create_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_care_item);
        findViews();

        fragmentManager = getSupportFragmentManager();
        NewPeriodicCareFragment temp = new NewPeriodicCareFragment();
        newCareFragment = temp;
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, temp)
                .commit();
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NewCareItemActivity.this, DescriptionActivity.class)
                        .putExtra("descriptionTitle", descriptionTitle.getText().toString())
                        .putExtra("descriptionContent", descriptionContent)
                        .putExtra("descriptionLastEditedTime", System.currentTimeMillis()), 1);
            }
        });

        typeChooser.setAdapter(new ArrayAdapter<String>(this, R.layout.item_type, getResources().getStringArray(R.array.care_item_types)));
        typeChooser.setSelection(2);
        typeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                switch (position) {
                    case 0:
                        replaceFragment(new NewTextCareFragment());
                        break;
                    case 1:
                        replaceFragment(new NewNonperiodicCareFragment());
                        break;
                    case 2:
                        replaceFragment(new NewPeriodicCareFragment());
                        break;
                    default:
                        replaceFragment(new BlankFragment());
                        Toast.makeText(NewCareItemActivity.this, position + "", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCareItem();
            }
        });

    }


    private void addCareItem() {
        if (titleEditText.getText().toString().equals("")) {
            Toast.makeText(NewCareItemActivity.this, R.string.empty_title_warning, Toast.LENGTH_SHORT).show();
        } else {
            Bundle result = newCareFragment.getResult();
            switch (result.getInt("type")) {
                case Care.TEXT:
                    dataManager.addCareItem(new TextCare(titleEditText.getText().toString(), descriptionTitle.getText().toString(),
                            descriptionContent, descriptionLastEditedTime, dataManager.getMaxCareOrder() + 1,
                            System.currentTimeMillis(), result.getInt("color")));
                    finish();
                    break;
                case Care.NONPERIODIC:
                    dataManager.addCareItem(new NonperiodicCare(titleEditText.getText().toString(), descriptionTitle.getText().toString(),
                            descriptionContent, descriptionLastEditedTime, dataManager.getMaxCareOrder() + 1,
                            System.currentTimeMillis(), result.getInt("goal", 1), result.getInt("punishment", 1)));
                    finish();
                    break;
                case Care.PERIODIC:
                    dataManager.addCareItem(new PeriodicCare(titleEditText.getText().toString(), descriptionTitle.getText().toString(),
                            descriptionContent, descriptionLastEditedTime, dataManager.getMaxCareOrder() + 1,
                            System.currentTimeMillis(), result.getInt("goal", 1), result.getInt("punishment", 1),
                            result.getInt("periodUnit", PeriodicCare.DAY), result.getInt("periodLength", 1)));
                    finish();
                    break;
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        newCareFragment = (NewCareFragment) fragment;
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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
                addCareItem();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    descriptionTitle.setText(data.getStringExtra("descriptionTitle"));
                    descriptionContent = data.getStringExtra("descriptionContent");
                    descriptionLastEditedTime = data.getLongExtra("descriptionLastEditedTime", 0L);
                }
                break;
        }
    }
}
