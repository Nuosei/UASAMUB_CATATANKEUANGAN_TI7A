package com.example.uasamub_catatankeuangan_ti7a;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.uasamub_catatankeuangan_ti7a.auth.LoginActivity;
import com.example.uasamub_catatankeuangan_ti7a.fragment.FragmentPemasukan;
import com.example.uasamub_catatankeuangan_ti7a.fragment.FragmentPengeluaran;
import com.example.uasamub_catatankeuangan_ti7a.models.ModelKeuangan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth.AuthStateListener fireAuthListener;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private DatabaseReference referencepemasukan, referencepengeluaran;
    FloatingActionButton fab;
    Spinner spinner_kategori;
    EditText nominal, keterangan;
    TextView tgl, saldo, t_pemasukan, t_pengeluaran, v_pemasukan, v_pengeluaran;
    LinearLayout b_pemasukan, b_pengeluaran;
    String getnominal;
    ImageView img_pemasukan, img_pengeluaran;
    ProgressDialog pd;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        firebaseAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        t_pemasukan     = findViewById(R.id.t_pemasukan);
        t_pengeluaran   = findViewById(R.id.t_pengeluaran);
        img_pemasukan   = findViewById(R.id.img_pemasukan);
        img_pengeluaran = findViewById(R.id.img_pengeluaran);

        saldo       = findViewById(R.id.tv_saldo);
        v_pemasukan     = findViewById(R.id.tv_pemasukan);
        v_pengeluaran   = findViewById(R.id.tv_pengeluaran);

        b_pemasukan = findViewById(R.id.b_pemasukan);
        b_pemasukan.setOnClickListener(this);
        b_pengeluaran = findViewById(R.id.b_pengeluaran);
        b_pengeluaran.setOnClickListener(this);

        if (firebaseAuth.getCurrentUser() != null) {
            FragmentPemasukan f_minum = new FragmentPemasukan();
            FragmentManager FM1 = getSupportFragmentManager();
            FragmentTransaction FT1 = FM1.beginTransaction();
            FT1.replace(R.id.L_fragment, f_minum);
            FT1.commit();
            t_pemasukan.setTextColor(Color.parseColor("#00171F"));
            img_pemasukan.setImageResource(R.drawable.ic_pemasukan_on);
        }


        user = firebaseAuth.getCurrentUser();
        fireAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String userid = user.getEmail();

                if (user == null) {
                    com.example.uasamub_catatankeuangan_ti7a.MainActivity.this.startActivity(new Intent(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, LoginActivity.class));
                    com.example.uasamub_catatankeuangan_ti7a.MainActivity.this.finish();
                }
            }
        };


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sumsaldo = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();

                    Object pricesaldo = map.get("nominal");
                    int pValuesaldo   = Integer.parseInt(String.valueOf(pricesaldo));

                    sumsaldo += pValuesaldo;

                    String getuang = String.valueOf(new Integer(sumsaldo));
                    saldo.setText(currencyFormatter(Double.parseDouble(getuang)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referencepemasukan = FirebaseDatabase.getInstance().getReference();
        referencepemasukan.child(user.getUid()).orderByChild("kategori").equalTo("pemasukan")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshotpemasukan) {
                        int sumpemasukan = 0;
                        for(DataSnapshot ds : snapshotpemasukan.getChildren()){
                            Map<String, Object> mappemasukan = (Map<String, Object>) ds.getValue();

                            Object pricepemasukan = mappemasukan.get("nominal");
                            int pValuepemasukan   = Integer.parseInt(String.valueOf(pricepemasukan));

                            sumpemasukan += pValuepemasukan;

                            String getuang = String.valueOf(new Integer(sumpemasukan));
                            v_pemasukan.setText(currencyFormatter(Double.parseDouble(getuang)));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        referencepengeluaran = FirebaseDatabase.getInstance().getReference();
        referencepengeluaran.child(user.getUid()).orderByChild("kategori").equalTo("pengeluaran")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshotpengeluaran) {
                        int sumpengeluaran = 0;
                        for(DataSnapshot ds : snapshotpengeluaran.getChildren()){
                            Map<String, Object> mappengeluaran = (Map<String, Object>) ds.getValue();

                            Object pricepengeluaran = mappengeluaran.get("nominal");

                            String getpengeluaran   = String.valueOf(pricepengeluaran);
                            String inominal         = getpengeluaran.replace("-", "");
                            int pValuepemgeluaran   = Integer.parseInt(inominal);

                            sumpengeluaran += pValuepemgeluaran;
                            String getuang = String.valueOf(new Integer(sumpengeluaran));
                            v_pengeluaran.setText(currencyFormatter(Double.parseDouble(getuang)));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        fab = findViewById(R.id.fab);
        onFabClick();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_pemasukan:

                t_pemasukan.setTextColor(Color.parseColor("#00171F"));
                img_pemasukan.setImageResource(R.drawable.ic_pemasukan_on);

                t_pengeluaran.setTextColor(Color.parseColor("#ffffff"));
                img_pengeluaran.setImageResource(R.drawable.ic_pengeluaran);

                FragmentPemasukan f_pemasukan = new FragmentPemasukan();
                FragmentManager FM1 = getSupportFragmentManager();
                FragmentTransaction FT1 = FM1.beginTransaction();
                FT1.replace(R.id.L_fragment, f_pemasukan);
                FT1.commit();
                break;
            case R.id.b_pengeluaran:

                t_pemasukan.setTextColor(Color.parseColor("#ffffff"));
                img_pemasukan.setImageResource(R.drawable.ic_pemasukan);

                t_pengeluaran.setTextColor(Color.parseColor("#00171F"));
                img_pengeluaran.setImageResource(R.drawable.ic_pengeluaran_on);

                FragmentPengeluaran f_pengeluaran = new FragmentPengeluaran();
                FragmentManager FM2 = getSupportFragmentManager();
                FragmentTransaction FT2 = FM2.beginTransaction();
                FT2.replace(R.id.L_fragment, f_pengeluaran);
                FT2.commit();
                break;

        }
    }


    private void onFabClick() {
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Implementasi klik dari tombol tambah
    @SuppressLint("SimpleDateFormat")
    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_create, null);
        dialogBuilder.setView(dialogView);

        spinner_kategori  = dialogView.findViewById(R.id.spinner_kategori);
        nominal           = dialogView.findViewById(R.id.et_nominal);
        keterangan        = dialogView.findViewById(R.id.et_keterangan);
        tgl               = dialogView.findViewById(R.id.et_tgl);

        tgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.kategori_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_kategori.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String getcategori = spinner_kategori.getSelectedItem().toString();
                String getUserID = user.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getReference;

                if (getcategori.equals("pemasukan")){
                    getnominal = nominal.getText().toString();
                }else{
                    getnominal = "-"+nominal.getText().toString();
                }
                String getket     = keterangan.getText().toString();
                String gettgl     = tgl.getText().toString();

                getReference = database.getReference();

                if(isEmpty(getnominal) && isEmpty(getket) && isEmpty(gettgl)){

                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                }else {

                    getReference.child(getUserID).push()
                            .setValue(new ModelKeuangan(getcategori, getnominal, getket, gettgl))
                            .addOnSuccessListener(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    if (getcategori.equals("pemasukan")){

                                        t_pemasukan.setTextColor(Color.parseColor("#00171F"));
                                        img_pemasukan.setImageResource(R.drawable.ic_pemasukan_on);

                                        t_pengeluaran.setTextColor(Color.parseColor("#ffffff"));
                                        img_pengeluaran.setImageResource(R.drawable.ic_pengeluaran);

                                        FragmentPemasukan f_pemasukan = new FragmentPemasukan();
                                        FragmentManager FM1 = getSupportFragmentManager();
                                        FragmentTransaction FT1 = FM1.beginTransaction();
                                        FT1.replace(R.id.L_fragment, f_pemasukan);
                                        FT1.commit();
                                    }else{

                                        t_pemasukan.setTextColor(Color.parseColor("#ffffff"));
                                        img_pemasukan.setImageResource(R.drawable.ic_pemasukan);

                                        t_pengeluaran.setTextColor(Color.parseColor("#00171F"));
                                        img_pengeluaran.setImageResource(R.drawable.ic_pengeluaran_on);

                                        FragmentPengeluaran f_pengeluaran = new FragmentPengeluaran();
                                        FragmentManager FM2 = getSupportFragmentManager();
                                        FragmentTransaction FT2 = FM2.beginTransaction();
                                        FT2.replace(R.id.L_fragment, f_pengeluaran);
                                        FT2.commit();
                                    }
                                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private void showDateDialog(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn_profile:

                Intent intent = new Intent(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.btn_exit:
                AlertDialog.Builder alertIns = new AlertDialog.Builder(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this);
                alertIns.setPositiveButton("Yes", logout).setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_menu_info_details).setTitle("LOGOUT")
                        .setMessage("Apakah anda ingin keluar dari aplikasi ini? ").show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    DialogInterface.OnClickListener logout = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            firebaseAuth.signOut();
            firebaseAuth.removeAuthStateListener(FirebaseAuth::signOut);
            Intent intent = new Intent(com.example.uasamub_catatankeuangan_ti7a.MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    };


    private String currencyFormatter(Double number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String nominal = formatRupiah.format(number);
        String uang = nominal.replace(",00", "");
        String convertuang = uang.replace("Rp", "");
        return convertuang;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(fireAuthListener != null){
            firebaseAuth.removeAuthStateListener(fireAuthListener);
        }
    }
}