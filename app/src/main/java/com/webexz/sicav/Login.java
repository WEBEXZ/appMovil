package com.webexz.sicav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText usuario, password;
    private Button aceptar;
    private String user, pass;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //VISTA
        usuario = (EditText) findViewById(R.id.txt_username);
        password = (EditText) findViewById(R.id.txt_password);
        aceptar = (Button) findViewById(R.id.acceder);

        //PROCESOS
        AgentesDB agentes = new AgentesDB(this, "Agentes", null, 2);
        db = agentes.getWritableDatabase();

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = usuario.getText().toString();
                pass = password.getText().toString();
                if(user.length() > 0 && pass.length() > 0) {
                    String query = "SELECT id FROM Agentes WHERE numeroControl = '" + user +"' and password = '" +pass +"';";
                    Cursor c = db.rawQuery(query, null);
                    if(c.getCount() > 0){
                        SharedPreferences setting = getSharedPreferences("Sesion", 0);
                        SharedPreferences.Editor editor = setting.edit();
                        editor.putString("usuario", user);
                        editor.commit();

                        Intent home = new Intent(getApplicationContext(), Home.class);
                        startActivity(home);
                        usuario.setText("");
                        password.setText("");
                    }else{
                        Toast.makeText(getApplicationContext(), "No existe el usuario", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Faltan campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
