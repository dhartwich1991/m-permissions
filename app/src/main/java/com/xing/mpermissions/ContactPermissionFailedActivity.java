package com.xing.mpermissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ContactPermissionFailedActivity extends AppCompatActivity implements View.OnClickListener {

    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_permission_failed);
        mLayout = findViewById(R.id.layout);
        Button grantPermission = (Button) findViewById(R.id.warning_contacts_permission);
        grantPermission.setOnClickListener(this);
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
        if (v.getId() == R.id.warning_contacts_permission) {
            requestContactsPermission();
        }
    }

    private void requestContactsPermission() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                showSettingsDialog();
            } else {
                ActivityCompat.requestPermissions(this, MainActivity.PERMISSIONS_CONTACT, MainActivity.REQUEST_CONTACTS);
            }
        } else {
            Snackbar.make(mLayout, "You already have this permission, FOOL!", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_CONTACTS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Toast.makeText(this, "Awesome, Thanks - Permission granted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Snackbar.make(mLayout, "Y U NO GIVE PERMISSION?! ლ(ಠ_ಠლ)", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates a new instance of a SettingsDialogFragment and shows it to the user
     */
    public void showSettingsDialog() {
        MainActivity.SettingsDialogFragment fragment = new MainActivity.SettingsDialogFragment();
        fragment.show(getSupportFragmentManager(), "SettingsFragment");
    }
}

