package com.kis.coinmonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.ui.AssetsListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            showDefaultFragment();
        }
        ((BottomNavigationView) findViewById(R.id.bottom_navigation_menu)).setOnNavigationItemSelectedListener(item -> {
                    switchFragment(item.getItemId());
                    return true;
                }
        );
    }


    private void showDefaultFragment() {

        showFragment(AssetsListFragment.newInstance());
    }

    private void switchFragment(Integer id) {
        switch (id) {
            case R.id.navigation_coins:
                showFragment(AssetsListFragment.newInstance());
                break;
            case R.id.navigation_favorites:
                showFragment(AssetsListFragment.newInstance(true));
                break;
        }
    }

    public void showFragment(Fragment fragment, boolean addToBackStack, int transitionAnimation) {

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(transitionAnimation)
                .replace(R.id.fragments_container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, fragment).commit();
    }

}