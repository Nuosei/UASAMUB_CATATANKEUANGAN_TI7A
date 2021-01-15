package com.example.uasamub_catatankeuangan_ti7a;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailKeuangan extends AppCompatActivity {
    TextView t_kategori, t_nominal, t_tanggal, t_keterangan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_keuangan);

        t_kategori   = findViewById(R.id.tx_kategori);
        t_tanggal    = findViewById(R.id.tx_tgl);
        t_nominal    = findViewById(R.id.tx_nominal);
        t_keterangan = findViewById(R.id.tx_detail_ket);

        Intent intent = getIntent();
        String kategori   = intent.getStringExtra("kategori");
        String nominal    = intent.getStringExtra("nominal");
        String tanggal    = intent.getStringExtra("tanggal");
        String keterangan = intent.getStringExtra("keterangan");

        t_kategori.setText(kategori);
        t_tanggal.setText(tanggal);
        t_nominal.setText(nominal);
        t_keterangan.setText(keterangan);

    }
}