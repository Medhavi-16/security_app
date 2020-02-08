package com.example.womensecurityapp.ui.trusted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.ui.profile.ProfileModel;

public class TrustedFragment extends Fragment {

    private TrustedViewModel trustedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        trustedViewModel = new ViewModelProvider(this).get(TrustedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trusted, container, false);
        trustedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}