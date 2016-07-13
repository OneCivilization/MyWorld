package com.onecivilization.Optimize.Activity;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Fragment.BlankFragment;
import com.onecivilization.Optimize.Fragment.NewTextCareFragment;
import com.onecivilization.Optimize.Interface.NewCareFragment;
import com.onecivilization.Optimize.Model.Care;
import com.onecivilization.Optimize.Model.TextCare;
import com.onecivilization.Optimize.R;

public class NewCareItemActivity extends BaseActivity {

    private long lastBackPressedTime = 0L;
    private Toolbar toolbar;
    private EditText titleEditText;
    private TextView descriptionTitle;
    private Spinner typeChooser;
    private FragmentManager fragmentManager;
    private NewCareFragment newCareFragment;
    private Button createButton;
    private DataManager dataManager = DataManager.getInstance(this);

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleEditText = (EditText) findViewById(R.id.title_editText);
        descriptionTitle = (TextView) findViewById(R.id.description_title);
        typeChooser = (Spinner) findViewById(R.id.type_chooser);
        createButton = (Button) findViewById(R.id.create_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_care_item);
        findViews();

        fragmentManager = getSupportFragmentManager();
        NewTextCareFragment temp = new NewTextCareFragment();
        newCareFragment = temp;
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, temp)
                .commit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        typeChooser.setAdapter(new ArrayAdapter<String>(this, R.layout.item_type, getResources().getStringArray(R.array.care_item_types)));
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
                if (titleEditText.getText().toString().equals("")) {
                    Toast.makeText(NewCareItemActivity.this, R.string.empty_title_warning, Toast.LENGTH_SHORT).show();
                } else {
                    Bundle result = newCareFragment.getResult();
                    switch (result.getInt("type")) {
                        case Care.TEXT:
                            dataManager.addCareItem(new TextCare(titleEditText.getText().toString(), descriptionTitle.getText().toString(), "",
                                    dataManager.getCareList().size() + 1, null, false, false,
                                    System.currentTimeMillis(), 0L, 0L, result.getInt("color")));
                            finish();
                            break;
                    }
                }
            }
        });

    }

    public void replaceFragment(Fragment fragment) {
        newCareFragment = (NewCareFragment) fragment;
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tips:
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

}
