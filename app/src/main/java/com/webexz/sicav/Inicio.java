package com.webexz.sicav;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Button comenzar = (Button) findViewById(R.id.menu);
        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarMenu();
            }
        });
    }

    public void lanzarMenu() {
        Intent menu = new Intent(getApplicationContext(), Menu.class);
        startActivity(menu);
    }
}
