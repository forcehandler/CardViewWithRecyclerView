package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.abhiraj.cardviewwithrecyclerview.R;

public class SetLatLngRad extends AppCompatActivity implements View.OnClickListener {

    private EditText latET, lonET, radET;
    private Button okBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lat_lng_rad);

        latET = (EditText) findViewById(R.id.lat_et);
        lonET = (EditText) findViewById(R.id.lon_et);
        radET = (EditText) findViewById(R.id.rad_et);
        okBtn = (Button) findViewById(R.id.ok);

        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view == okBtn)
        {
            Intent returnIntent = new Intent();
            double lat = Double.parseDouble(latET.getText().toString());
            double lon = Double.parseDouble(lonET.getText().toString());
            float rad = Float.parseFloat(radET.getText().toString());
            returnIntent.putExtra("latitude", lat);
            returnIntent.putExtra("longitude", lon);
            returnIntent.putExtra("radius", rad);
            setResult(1, returnIntent);
            finish();
        }
    }
}
