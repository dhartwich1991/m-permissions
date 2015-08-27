package com.xing.mpermissions;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Id to identify a location permission request.
     */
    public static final int REQUEST_LOCATION = 1;
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * Id to identify a contacts permission request.
     */
    public static final int REQUEST_CONTACTS = 2;
    public static final String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.sample_main_layout);
        Button locationButton = (Button) findViewById(R.id.get_location);
        Button contactButton = (Button) findViewById(R.id.insert_contact);
        contactButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                getLastKnownPosition();
                Snackbar.make(mLayout, "Location permission successfully granted", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mLayout, "Location permission NOT granted", Snackbar.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //User Denied and selected never ask again --> Show info that he needs to go to settings
                    showSettingsDialog();
                }
            }
        } else if (requestCode == REQUEST_CONTACTS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                insertDummyContact();
                Snackbar.make(mLayout, "Contacts permission successfully granted", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mLayout, "Contacts permission NOT granted", Snackbar.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                    //User Denied and selected never ask again --> Show info that he needs to go to settings
                    showSettingsDialog();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.get_location) {
            getLastKnownPosition();
        } else if (v.getId() == R.id.insert_contact) {
            insertDummyContact();
        }
    }

    private void getLastKnownPosition() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Intent failedLocationPermission = new Intent(this, LocationPermissionFailedActivity.class);
                startActivity(failedLocationPermission);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
            }
        } else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            Log.d("Longitude", String.valueOf(lastKnownLocation.getLongitude()));
            Log.d("Latitude", String.valueOf(lastKnownLocation.getLatitude()));
            Snackbar.make(mLayout, "Longitude: " + lastKnownLocation.getLongitude() + " Latitude: " + lastKnownLocation.getLatitude(), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Accesses the Contacts content provider directly to insert a new contact.
     * <p/>
     * The contact is called "__DUMMY ENTRY" and only contains a name.
     */
//    @RequiresPermission(allOf = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    private void insertDummyContact() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                Intent failedContactsPermission = new Intent(this, ContactPermissionFailedActivity.class);
                startActivity(failedContactsPermission);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
            }
        } else {
            // Two operations are needed to insert a new contact.
            ArrayList<ContentProviderOperation> operations = new ArrayList<>(2);

            // First, set up a new raw contact.
            ContentProviderOperation.Builder op =
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
            operations.add(op.build());

            // Next, set the name for the contact.
            op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            "__DUMMY ENTRY");
            operations.add(op.build());

            // Apply the operations.
            ContentResolver resolver = getContentResolver();
            try {
                resolver.applyBatch(ContactsContract.AUTHORITY, operations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.d("AddContact", "Could not add a new contact: " + e.getMessage());
            } finally {
                Snackbar.make(mLayout, "Contact added", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates a new instance of a SettingsDialogFragment and shows it to the user
     * */
    public void showSettingsDialog() {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        fragment.show(getSupportFragmentManager(), "SettingsFragment");
    }

    /**
     * The SettingsDialogFragment that is presented to the user whenever
     * he denied a permission with also checking Don't ask again, in case he is
     * trying to use this feature anyways.
     * */
    public static class SettingsDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Permission required");
            builder.setMessage("In order to use this feature you need to enable the appropriate setting.\n" +
                    "Therefor you have to press \"Go to settings\" and enable them in the permissions setting.")
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Open Settings Section of my app
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + "com.xing.mpermissions"));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}