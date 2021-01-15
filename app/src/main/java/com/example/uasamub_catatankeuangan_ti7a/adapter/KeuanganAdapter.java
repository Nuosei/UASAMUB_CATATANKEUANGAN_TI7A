package com.example.uasamub_catatankeuangan_ti7a.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasamub_catatankeuangan_ti7a.DetailKeuangan;
import com.example.uasamub_catatankeuangan_ti7a.R;
import com.example.uasamub_catatankeuangan_ti7a.models.ModelKeuangan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class KeuanganAdapter extends RecyclerView.Adapter<KeuanganAdapter.ViewHolder>{

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private DatabaseReference reference;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ArrayList<ModelKeuangan> listKeuangan;
    private Context context;
    Spinner spinner_kategori;
    EditText nominal, keterangan;
    TextView tgl;


    //Membuat Konstruktor, untuk menerima input dari Database
    public KeuanganAdapter(ArrayList<ModelKeuangan> listKeuangan, Context context) {
        this.listKeuangan = listKeuangan;
        this.context = context;
    }

    //ViewHolder Digunakan Untuk Menyimpan Referensi Dari View-View
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView Nominal, Kategori, Tanggal;
        private CardView ListItem;

        ViewHolder(View itemView) {
            super(itemView);
            Kategori = itemView.findViewById(R.id.tx_kategori);
            Nominal = itemView.findViewById(R.id.tx_nominal);
            Tanggal = itemView.findViewById(R.id.tx_tanggal);
            ListItem = itemView.findViewById(R.id.cvmain);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Membuat View untuk Menyiapkan dan Memasang Layout yang Akan digunakan pada RecyclerView
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keuangan, parent, false);
        return new ViewHolder(V);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Mengambil Nilai/Value yenag terdapat pada RecyclerView berdasarkan Posisi Tertentu
        final String kategori = listKeuangan.get(position).getKategori();
        final String nominal = listKeuangan.get(position).getNominal();
        final String tanggal = listKeuangan.get(position).getTanggal();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        String uang = nominal.replace("-", "");
        String convertuang = currencyFormatter(Double.parseDouble(uang));
        holder.Kategori.setText(kategori);
        holder.Nominal.setText(convertuang);
        holder.Tanggal.setText(tanggal);

        holder.ListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] action = {"Detail", "Update", "Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setItems(action,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:

                                String ikategori   = listKeuangan.get(position).getKategori();
                                String inominal    = listKeuangan.get(position).getNominal();
                                String iketerangan = listKeuangan.get(position).getKeterangan();
                                String itanggal    = listKeuangan.get(position).getTanggal();
                                String iuang = inominal.replace("-", "");
                                String setnomi         = currencyFormatter(Double.parseDouble(iuang));

                                Intent intent = new Intent(context, DetailKeuangan.class);
                                intent.putExtra("kategori",ikategori);
                                intent.putExtra("nominal",setnomi);
                                intent.putExtra("tanggal",itanggal);
                                intent.putExtra("keterangan",iketerangan);
                                context.startActivity(intent);

                                break;
                            case 1:
                                String gkategori   = listKeuangan.get(position).getKategori();
                                String gnominal    = listKeuangan.get(position).getNominal();
                                String gketerangan = listKeuangan.get(position).getKeterangan();
                                String gtanggal    = listKeuangan.get(position).getTanggal();
                                String gkey        = listKeuangan.get(position).getKey();

                                String uang = gnominal.replace("-", "");
                                showAddDialogEdit(gkategori, uang, gketerangan, gtanggal, gkey);

                                break;
                            case 2:

                                String userID         = user.getUid();
                                String gkeydel        = listKeuangan.get(position).getKey();
                                String gkat           = listKeuangan.get(position).getKategori();
                                String gnom           = listKeuangan.get(position).getNominal();

                                String iuangs         = gnom.replace("-", "");
                                String setnom         = currencyFormatter(Double.parseDouble(iuangs));

                                AlertDialog.Builder alertIns = new AlertDialog.Builder(context);
                                alertIns.setMessage("apakah anda ingin menghapus catatan "+ gkat +", dengan nominal : "+ setnom);
                                alertIns.setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deletedata(userID, gkeydel);
                                    }}).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                alertIns.show();

                            break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
    }

    private void deletedata(String userID, String gkeydel){
        if(reference != null){
            reference.child(userID)
                    .child(gkeydel)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void showAddDialogEdit(String skategori, String snominal, String sketerangan, String stanggal, String skey) {

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();


        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create,null);
        dialogBuilder.setView(dialogView);

        spinner_kategori  = dialogView.findViewById(R.id.spinner_kategori);
        nominal           = dialogView.findViewById(R.id.et_nominal);
        keterangan        = dialogView.findViewById(R.id.et_keterangan);
        tgl               = dialogView.findViewById(R.id.et_tgl);

        nominal.setText(snominal);
        keterangan.setText(sketerangan);
        tgl.setText(stanggal);

        tgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.kategori_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_kategori.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String userid      = user.getUid();
                String getcategori = spinner_kategori.getSelectedItem().toString();
                ModelKeuangan setKeuangan = new ModelKeuangan();
                setKeuangan.setKategori(getcategori);
                setKeuangan.setNominal(nominal.getText().toString());
                setKeuangan.setKeterangan(keterangan.getText().toString());
                setKeuangan.setTanggal(tgl.getText().toString());
                updateKeuangan(setKeuangan, userid, skey);

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
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateKeuangan(ModelKeuangan keuangan, String userid, String key){
        database.child(userid)
                .child(key)
                .setValue(keuangan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String currencyFormatter(Double number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String nominal = formatRupiah.format(number);
        String uang = nominal.replace(",00", "");
        return uang;
    }


    @Override
    public int getItemCount() {
        //Menghitung Ukuran/Jumlah Data Yang Akan Ditampilkan Pada RecyclerView
        return listKeuangan.size();
    }

}