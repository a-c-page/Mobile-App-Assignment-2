package com.example.a2;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddDialog.AddDialogListener, DeleteDialog.DeleteDialogListener, UpdateDialog.UpdateDialogListener {
    DBHelper DB;
    Button add;
    Button update;
    Button delete;
    Geocoder geocoder;
    EditText et;
    TableLayout tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB = new DBHelper(this);

        update = (Button)findViewById(R.id.update);
        delete = (Button)findViewById(R.id.delete);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText)findViewById(R.id.query_text);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText text = (EditText)findViewById(R.id.query_text);
                String value = text.getText().toString();

                fillTable(DB.query("SELECT * FROM locations WHERE address LIKE '%"+ value +"%'"));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        fillTable(DB.query("Select * from locations"));

        add = findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddDialog();
            }
        });

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDeleteDialog();
            }
        });

        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openUpdateDialog();
            }
        });
    }

    public void openAddDialog() {
        AddDialog addDialog = new AddDialog();
        addDialog.show(getSupportFragmentManager(), "Add Dialog");
    }

    public void openDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.show(getSupportFragmentManager(), "Delete Dialog");
    }

    public void openUpdateDialog() {
        UpdateDialog updateDialog = new UpdateDialog();
        updateDialog.show(getSupportFragmentManager(), "Update Dialog");
    }


    public void fillTable(Cursor result) {
        tl = (TableLayout) findViewById(R.id.main_table);
        tl.removeAllViews();

        if (result.getCount() == 0) {
            Toast.makeText(MainActivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return;
        }

        while (result.moveToNext()) {
            TableRow tr = new TableRow(this);

            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);
            TextView tv4 = new TextView(this);

            tv1.setText(result.getString(0));
            tv2.setText(result.getString(1));
            tv3.setText(result.getString(2));
            tv4.setText(result.getString(3));

            tv1.setPadding(20, 10, 20, 10);
            tv2.setPadding(20, 10, 20, 10);
            tv3.setPadding(20, 10, 20, 10);
            tv4.setPadding(20, 10, 20, 10);

            tr.addView(tv1);
            tr.addView(tv2);
            tr.addView(tv3);
            tr.addView(tv4);
            tl.addView(tr);
        }
    }

    @Override
    public void applyText(String address) {
        geocoder = new Geocoder(this);
        try {
            List<Address> results = geocoder.getFromLocationName(address, 1);
            double latitude = results.get(0).getLatitude();
            double longitude = results.get(0).getLongitude();
            String addy = results.get(0).getAddressLine(0);
            System.out.println(addy);
            System.out.println(latitude);
            System.out.println(longitude);
            DB = new DBHelper(this);
            DB.addAddress(addy, latitude, longitude);
            fillTable(DB.query("Select * from locations"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void applyIndex(String ID) {
        DB = new DBHelper(this);
        DB.delete(ID);
        fillTable(DB.query("Select * from locations"));
    }

    @Override
    public void updatedAddress(String ID, String address) {
        DB = new DBHelper(this);
        DB.update(ID, address, this);
        fillTable(DB.query("Select * from locations"));
    }
}