package com.example.team6.quickappt;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    private TableRow selectedRow = null;
    ArrayList<String> specialist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Spinner s = (Spinner) findViewById(R.id.spinner);
        if (specialist.isEmpty()) {
            initSpecialist(s);
        }


        //if(!selectedSpecial.isEmpty())
    }

    public void setOnClick(final TableRow tr) {
        tr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                select(tr);
                //v.setBackgroundColor(Color.YELLOW);
            }

        });
    }

    public void select(TableRow tr) {
        TableLayout table = (TableLayout) findViewById(R.id.table);

        if (selectedRow != tr) {
            unselect(selectedRow);
            selectedRow = tr;
            tr.setBackgroundColor(Color.parseColor("#d6f5f5"));

        }

    }

    public void unselect(TableRow tr) {
        if (tr != null) {
            tr.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public TableRow createTableRow(String zip, String dist, String phys, String spec) {
        TableLayout table = (TableLayout) findViewById(R.id.table);
        View line = new View(this);
        line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
        line.setBackgroundColor(Color.rgb(51, 51, 51));
        table.addView(line);
        return addTableRow(zip, dist, phys, spec);

    }

    public TableRow addTableRow(String zip, String dist, String phys, String spec) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
        TableRow tr1 = new TableRow(this);
        tr1.setClickable(true);
        setOnClick(tr1);
        TextView tv1 = new TextView(this);
        TextView tv2 = new TextView(this);
        TextView tv3 = new TextView(this);
        TextView tv4 = new TextView(this);
        tv1.setText(zip);
        tv2.setText(dist);
        tv3.setText(phys);
        tv4.setText(spec);
        tr1.addView(tv1);
        tr1.addView(tv2);
        tr1.addView(tv3);
        tr1.addView(tv4);
        tr1.setLayoutParams(param);
        return tr1;
    }

    public void findSearch(View v) {

        TableLayout table = (TableLayout) findViewById(R.id.table);
        table.removeViews(1, table.getChildCount() - 1);
        ArrayList<ArrayList<String>> rows2 = new ArrayList<ArrayList<String>>();
        HashMap<String, TableRow> rows = new HashMap<String, TableRow>();
        EditText zip = (EditText) findViewById(R.id.editText);
        String zipCode = zip.getText().toString();

        ArrayList<String> oneRow = new ArrayList<String>(
                Arrays.asList("92617", "1 mi", "Dr. Lee", "Cardiologist"));
        ArrayList<String> twoRow = new ArrayList<String>(
                Arrays.asList("92617", "1.5 mi", "Dr. Sayed", "Gen. Physician"));
        ArrayList<String> threeRow = new ArrayList<String>(
                Arrays.asList("92688", "2 mi", "Dr. Strange", "Optometrist"));
        rows2.add(oneRow);
        rows2.add(twoRow);
        rows2.add(threeRow);

        String specialist = findSpecialist();

        for (ArrayList<String> row : rows2) {
            if (zipCode.equals(row.get(0))) {
                if ((!specialist.isEmpty() && row.get(3).equals(specialist)) || specialist.equals("Any")) {
                    table.addView(createTableRow(row.get(0), row.get(1), row.get(2), row.get(3)));
                }
            }
        }

    }

    public void initSpecialist(Spinner s) {
        specialist.add("Cardiologist");
        specialist.add("Gen. Physician");
        specialist.add("Optometrist");
        specialist.add("Psychologist");
        specialist.add("Neurologist");
        specialist.add("Any");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specialist);
        s.setAdapter(adapter);
    }

    public String findSpecialist() {
        Spinner s = (Spinner) findViewById(R.id.spinner);


        return s.getSelectedItem().toString();
    }
}