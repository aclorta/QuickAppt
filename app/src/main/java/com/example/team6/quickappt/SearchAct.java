package com.example.team6.quickappt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class SearchAct extends AppCompatActivity {

    private TableRow selectedRow = null;
    ArrayList<String> specialist = new ArrayList<String>();
    EditText date, time, time2;
    private String u_ampm, u_ampm2;
    private int hour, minute, year, month, day, u_month, u_year, u_day, u_hour, u_minute, u_hour2, u_minute2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchact);
        Spinner s = (Spinner) findViewById(R.id.spinner);

        if (specialist.isEmpty()) {
            initSpecialist(s);
        }
        date  = (EditText) findViewById(R.id.date);
        time  = (EditText) findViewById(R.id.time1);
        time2 = (EditText) findViewById(R.id.time2);

        time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("clicked time");
                onSetTime(v, time);
            }
        });
        time2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSetTime(v, time2);
            }
        });

        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSetDate(v);
            }

        });

        //if(!selectedSpecial.isEmpty())
    }

    public void onSetDate(View v)
    {
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_WEEK);
        //.out.println("calendar: "+"year: "+year);

        DatePickerDialog tpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day){
               // System.out.println("date year: "+year);
                u_month=month+1;
                u_day = day;
                u_year = year;
                date.setText((1+month) + "/" + day + "/" + year);

            }
        },year,month,day);
        tpd.show();
    }
    public void onSetTime(View v, final EditText txtTime)
    {
        final Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        final EditText txt = txtTime;
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override

            public void onTimeSet(TimePicker view, int currHour, int minute){

                SimpleDateFormat df = new SimpleDateFormat("HH:mm a");
                String am_pm = "AM";
                String m="";
                if(txtTime == time) {
                    u_minute = minute;
                    u_hour = currHour;
                }
                else if(txtTime == time2){
                    u_minute2 = minute;
                    u_hour2 = currHour;
                }
                if(minute < 10)
                {
                    m="0"+Integer.toString(minute);
                }
                else if(minute >= 10)
                {
                    m = Integer.toString(minute);
                }
                if(currHour > 12) {
                    currHour -= 12;
                    am_pm = "PM";
                }
                else if (currHour == 0) {
                    currHour = 12;
                    if(cal.get(Calendar.AM_PM) == Calendar.AM)
                    {
                        am_pm="AM";
                    }
                    else{
                        am_pm="PM";
                    }
                }
                if(txtTime == time) {

                    u_ampm = am_pm;
                }
                else if (txtTime == time2){

                    u_ampm2 = am_pm;
                }
                txt.setText(currHour+" : "+m+" "+am_pm);
            }
        },hour,minute,false);
        tpd.show();
    }
    public void bookNow(View v){
//        Intent intent = new Intent(this, PatientCalendar.class);
//        TextView tv = (TextView)selectedRow.getChildAt(2) ;
//        String physician = tv.getText().toString();
//        //System.out.println("phys chosen: "+physician);
//        intent.putExtra("Physician",physician);
//        startActivity(intent);

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
        //ArrayList<ArrayList<String>> rows2 = new ArrayList<ArrayList<String>>();
        ArrayList<Physician> phys = new ArrayList<Physician>();
        HashMap<String, TableRow> rows = new HashMap<String, TableRow>();
        EditText zip = (EditText) findViewById(R.id.editText);
        String zipCode = zip.getText().toString();

        CalendarSched cal = new CalendarSched();
        cal.initCalendar();
        cal.initTime();

        System.out.println("time: "+u_hour+": "+u_minute+" to "+u_hour2+": "+u_minute2 +" date: "+u_year+", "+u_month+" "+u_day );


        Physician p1 = new Physician("Dr. Lee", cal, "Cardiologist", "92617", "1 mi");
        Physician p2 = new Physician("Dr. Sayed", cal, "Gen. Physician", "92617", "1.5 mi");
        Physician p3 = new Physician("Dr. Strange", cal, "Optometrist", "92688", "2 mi");

        phys.add(p1);
        phys.add(p2);
        phys.add(p3);




        String specialist = findSpecialist();

        for (Physician p : phys) {
            for(myTime t : p.getAppointments().getTimes()) {
                if (zipCode.equals(p.getzipCode())) {
                    myTime t1 = new myTime(u_hour, u_minute);
                    myTime t2 = new myTime(u_hour2, u_minute2);
                    if (convertToMin(t1) <= convertToMin(t) && convertToMin(t)<= convertToMin(t2)) {
                        //int min = convertToMin(t);

                        if (( p.getSpecialty().equals(specialist)) || specialist.equals("Any")) {
                            table.addView(createTableRow(p.getzipCode(), p.getdistance(), p.getName(), p.getSpecialty()));
                        }
                    }
                }
            }
        }

    }

    public int convertToMin(myTime t){
        int time;

        if(t.getHour() == 0)
            time = 12 * 60 + t.getMinute();
        else
            time = t.getHour() * 60 + t.getMinute();
        System.out.println("begin: "+t.getHour()+": "+t.getMinute()+" to minutes: "+time);
        return time;
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

class myTime {

    int h;
    int m;
    String ampm;

    public myTime(int hour, int minute){
        h = hour;
        m = minute;
    }

    public int getHour(){
        return h;
    }

    public int getMinute(){
        return m;
    }



    public void setHour(int newHour){
        h = newHour;
    }

    public void setMinute(int newMinute){
        m = newMinute;
    }



}

class CalendarSched {

    ArrayList<Calendar> sched;
    ArrayList<myTime> times;
//    cal = Calendar.getInstance();
//    cal.set(2016, 11, 22);
//    cal2 = Calendar.getInstance();
//    cal2.set(2016, 11, 23);
//    cal3 = Calendar.getInstance();
//    cal3.set(2016, 11, 24);
//    cal4 = Calendar.getInstance();
//    cal4.set(2016, 11, 25);

    public CalendarSched(){
        sched = new ArrayList<Calendar>();
        times = new ArrayList<myTime>();
    }

    public ArrayList<Calendar> initCalendar(){
        for(int i=21; i<26 ; i++){
            Calendar cal = Calendar.getInstance();
            cal.set(2016,11,i+1);
            sched.add(cal);
        }
        return sched;
    }

    public ArrayList<Calendar> getSched(){
        return sched;
    }
    public void addSced(Calendar c){
        sched.add(c);
    }

    public void removeSched(Calendar c){
        sched.remove(c);
    }
    public ArrayList<myTime> initTime(){
//        for(int i=1; i <= 5; i++)
//        {
//            myTime time = new myTime(i,0);
//            times.add(time);
//        }
        for(int i = 9; i <= 17; i++ ){
            myTime time = new myTime(i,0);
            times.add(time);
        }
      //  myTime time = new myTime(12,0);
       // times.add(time);
        return times;
    }

    public ArrayList<myTime> getTimes(){
        return times;
    }

    public ArrayList<myTime> addTime(myTime time){
        times.add(time);
        return times;
    }

    public ArrayList<myTime> removeTime(myTime time){
        times.remove(time);
        return times;
    }



}


class Physician {

    String phys_name;
    CalendarSched phys_appointments;
    String phys_specialty;
    String phys_zipCode;
    String phys_distance;
    public Physician(String name, CalendarSched appointments, String specialty, String zipCode, String distance){
        phys_name = name;
        phys_appointments = appointments;
        phys_specialty = specialty;
        phys_zipCode = zipCode;
        phys_distance = distance;
    }



    public String getName(){
        return phys_name;
    }

    public CalendarSched getAppointments(){
        return phys_appointments;
    }

    public String getSpecialty(){
        return phys_specialty;
    }

    public String getzipCode(){
        return phys_zipCode;
    }

    public String getdistance(){
        return phys_distance;
    }

    public void setName(String newName){
        phys_name = newName;
    }

//    public void addAppointment(Calendar cal){
//        phys_appointments.add(cal);
//    }

    public void setSpecialty(String newSpecialty){
        phys_specialty = newSpecialty;
    }

    public void setZipCode(String zip){
        phys_zipCode = zip;
    }

    public void setDistance(String dist){
        phys_distance = dist;
    }




}