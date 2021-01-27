package com.sz.huanxue.androidapp.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sz.huanxue.androidapp.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.11.
 */
public abstract class MyBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finish:
                onBackPressed();
                return true;
        }
        return false;
    }
}
