package com.kis.coinmonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.kis.coinmonitor.ui.AssetsListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            showDefaultFragment();
        }
    }

    private void showDefaultFragment() {

        showFragment(AssetsListFragment.newInstance());
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