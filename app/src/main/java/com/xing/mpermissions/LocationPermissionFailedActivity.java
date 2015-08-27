package com.xing.mpermissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LocationPermissionFailedActivity extends AppCompatActivity implements View.OnClickListener {

    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission_failed);
        mLayout = findViewById(R.id.layout);
        Button grantPermission = (Button) findViewById(R.id.warning_location_permission);
        grantPermission.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_location_permission_failed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.warning_location_permission) {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, MainActivity.PERMISSIONS_LOCATION, MainActivity.REQUEST_LOCATION);
        }else{
            Snackbar.make(mLayout, "You already have this permission, FOOL!", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MainActivity.REQUEST_LOCATION){
            if(PermissionUtil.verifyPermissions(grantResults)){
                Toast.makeText(this, "Awesome, Thanks - Permission granted", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Snackbar.make(mLayout, "Y U NO GIVE PERMISSION", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
