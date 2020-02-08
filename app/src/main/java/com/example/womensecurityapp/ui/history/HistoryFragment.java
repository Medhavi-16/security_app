package com.example.womensecurityapp.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.Report.AdapterReport;
import com.example.womensecurityapp.Report.ModelReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";

    private HistoryViewModel historyViewModel;
    private RecyclerView recyclerView;
    private AdapterReport adapterReport;
    List<ModelReport> modelReportList;
    DatabaseReference databaseReferenceReport;
    DatabaseReference dbRefProblem;
    Spinner spinner;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        spinner = root.findViewById(R.id.history_spinner);
        dbRefProblem = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = root.findViewById(R.id.history_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        modelReportList = new ArrayList<>();
        databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                .child("1").child("Report");
        makeTimelineReport();

        return root;
    }

    private void makeTimelineReport(){

        Log.d(TAG, "makeTimelineReport: called");

        databaseReferenceReport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                modelReportList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelReport modelReport = ds.getValue(ModelReport.class);
                    modelReportList.add(modelReport);

                    adapterReport = new AdapterReport(getActivity(), modelReportList);
                    recyclerView.setAdapter(adapterReport);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG,"makeTimelineReport: Something went wrong");
            }
        });

    }

}