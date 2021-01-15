package com.example.uasamub_catatankeuangan_ti7a.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasamub_catatankeuangan_ti7a.R;
import com.example.uasamub_catatankeuangan_ti7a.adapter.KeuanganAdapter;
import com.example.uasamub_catatankeuangan_ti7a.models.ModelKeuangan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentPengeluaran extends Fragment {
    private FirebaseAuth.AuthStateListener fireAuthListener;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ProgressDialog pd;
    private DatabaseReference reference;
    private ArrayList<ModelKeuangan> dataKeuangan;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_pengeluaran,container, false);

        pd = new ProgressDialog(requireContext());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        recyclerView = view.findViewById(R.id.datalist);
        auth = FirebaseAuth.getInstance();
        MyRecyclerView();
        if (firebaseAuth.getCurrentUser() != null) {
            GetData();
        }

        return view;

    }


    private void GetData(){
        pd.setMessage("tunggu sebentar");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(auth.getUid()).orderByChild("kategori").equalTo("pengeluaran")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataKeuangan = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            ModelKeuangan keuangan = snapshot.getValue(ModelKeuangan.class);
                            keuangan.setKey(snapshot.getKey());
                            dataKeuangan.add(keuangan);
                        }

                        adapter = new KeuanganAdapter(dataKeuangan, getActivity());
                        recyclerView.setAdapter(adapter);
                        pd.cancel();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            Toast.makeText(getActivity(),"Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void MyRecyclerView(){
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.line));
        recyclerView.addItemDecoration(itemDecoration);
    }

}