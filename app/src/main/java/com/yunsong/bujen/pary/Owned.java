package com.yunsong.bujen.pary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.yunsong.bujen.R;

public class Owned extends AppCompatActivity {
    Button btn_write,btn_write2,btn_write3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owned);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intiView();
    }

    private void intiView() {
        btn_write = findViewById(R.id.btn_write);
        btn_write2 = findViewById(R.id.btn_write2);
        btn_write3 = findViewById(R.id.btn_write3);
        Intent intent = new Intent(Owned.this, ParyWrite.class);
        btn_write.setOnClickListener(v -> {
            intent.putExtra("type","纯文");
            startActivity(intent);
        });
        btn_write2.setOnClickListener(v -> {
            intent.putExtra("type","图文");
            startActivity(intent);
        });
        btn_write3.setOnClickListener(v -> {
            intent.putExtra("type","语音");
            startActivity(intent);
        });
    }
}