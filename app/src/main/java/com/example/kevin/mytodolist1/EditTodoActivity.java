package com.example.kevin.mytodolist1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditTodoActivity extends AppCompatActivity implements View.OnClickListener{
    private final int REQUEST_FROM_MAIN=1, REQUEST_FROM_ADD=2, REQUEST_FROM_EDIT=3, REQUEST_FROM_DROP=4;
    private final int RESULT_FROM_MAIN=1, RESULT_FROM_ADD=2, RESULT_FROM_EDIT=3, RESULT_FROM_DROP=4;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    EditText edtScheduledTime, edtNotes, edtSchdDate, edtSchdTime;
    TextView txtEditTitle, txtCompleteTime, txtCreateTime;
    Button btnCancel, btnSubmit;
    CheckBox chkComplete;
    Spinner spinner;
    TypedArray colorCode, colorName;
    ArrayList<ColorItem> colorList=null;
    ColorAdapter colorAdapter=null;
    ColorItem colorItem=null;
    ConstraintLayout vEdit;
    private Bundle bData;
    private DbAdapter dbAdapter;
    private int index, id, type;
    private String name, notes, color="#FFFFFF";
    private Timestamp scheduled_time, create_time, complete_time;
    boolean isChanged=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        sdf.setTimeZone(TimeZone.getDefault());
        sdfDate.setTimeZone(TimeZone.getDefault());
        sdfTime.setTimeZone(TimeZone.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        initView();

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
*/

        bData=this.getIntent().getExtras();
        id=bData.getInt("keyid");
        type=bData.getInt("type");
        dbAdapter=new DbAdapter(this);
        switch (type)
        {
            case REQUEST_FROM_ADD:
                txtEditTitle.setText(getResources().getString(R.string.add_data));
                setEmptyView();
                break;
            case REQUEST_FROM_EDIT:
                txtEditTitle.setText(getResources().getString(R.string.update_data));
                refreshView();
                break;
            case REQUEST_FROM_DROP:
                txtEditTitle.setText(getResources().getString(R.string.drop_data));
                edtSchdDate.setEnabled(false);
                edtSchdTime.setEnabled(false);
                edtNotes.setEnabled(false);
                chkComplete.setEnabled(false);
                refreshView();
                break;
        }
    }



    private void initView()
    {
        vEdit=findViewById(R.id.vEdit);
        txtEditTitle=findViewById(R.id.txtEditTitle);
//        edtScheduledTime=findViewById(R.id.edtScheduledTime);
        edtSchdDate=findViewById(R.id.edtSchdDate);
        edtSchdTime=findViewById(R.id.edtSchdTime);
        edtNotes=findViewById(R.id.edtNotes);
        chkComplete=findViewById(R.id.chkComplete);
        txtCompleteTime=findViewById(R.id.txtCompleteTime);
        txtCreateTime=findViewById(R.id.txtCreateTime);
        btnCancel=findViewById(R.id.btnCancel);
        btnSubmit=findViewById(R.id.btnSubmit);
        edtSchdDate.setOnClickListener(this);
        edtSchdTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        chkComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(EditTodoActivity.this, "chkComplete.isChecked="+((chkComplete.isChecked()?"true":"false")), Toast.LENGTH_SHORT).show();
                if (chkComplete.isChecked())
                {
                    chkComplete.setText(R.string.completed);
                    Calendar calendar= Calendar.getInstance(TimeZone.getDefault());
                    complete_time=new Timestamp(calendar.getTimeInMillis());
                    txtCompleteTime.setText(sdf.format(complete_time));
                }
                else
                {
                    chkComplete.setText(R.string.unfinished);
                    complete_time = null;
                    txtCompleteTime.setText("");
                }
            }
        });
        spinner=findViewById(R.id.spinner);
        colorList=new ArrayList<ColorItem>();
        readColor();
        colorAdapter=new ColorAdapter(this, colorList);
        spinner.setAdapter(colorAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                EditTodoActivity.this.color=EditTodoActivity.this.colorList.get(position).getCode();
                color=colorList.get(position).getCode();
//                vEdit.setBackgroundColor(Color.parseColor(color));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void readColor()
    {
        int count;
        String c, s;
        Resources res = getResources();
        colorCode = res.obtainTypedArray(R.array.mycolor_code);
        colorName = res.obtainTypedArray(R.array.mycolor_name);
        for (int i=0; i<colorCode.length(); i++)
        {
            c=colorCode.getString(i);
            if (i<colorName.length())
                s=colorName.getString(i);
            else
                s=c;
            Log.d("readColor()", "#"+i+": colorName="+s+", colorCode="+c);
            colorList.add(new ColorItem(c, s));
        }
//        TypedArray colors = res.obtainTypedArray(R.array.colors);
//        int color = colors.getColor(0,0);
    }

    private Timestamp str2Timestamp(String s)
    {
        Timestamp t;
        if (s==null || s.trim().length()==0)
            return null;
        Calendar c=Calendar.getInstance();
        try
        {
            c.setTime(sdf.parse(s));
            t=new Timestamp(c.getTimeInMillis());
        }
        catch (Exception e)
        {
            t=new Timestamp(0);
        }
/*
        s=s.trim();
        switch (s.length())
        {
            case 16:    // YYYY-MM-DD 00:00
                s=s.concat(":00");
                break;
            case 19:    // YYYY-MM-DD 00:00:00
                break;
        }
        if (s.length()>15)
            try {
                t = Timestamp.valueOf(s);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                t=null;
            }
        else
            t=null;
*/
        return t;
    }

    private void refreshView()
    {
        Cursor cursor=dbAdapter.queryById(id);
        String strDateTime, strDate, strTime;
        index=cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ID));
        scheduled_time=str2Timestamp(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_SCHEDULED_TIME)));
        create_time=str2Timestamp(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_CREATE_TIME)));
        complete_time=str2Timestamp(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_COMPLETE_TIME)));
        notes=cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NOTES));
        color=cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_COLOR));
        edtSchdDate.setText((scheduled_time==null?"":sdfDate.format(scheduled_time)));
        edtSchdTime.setText((scheduled_time==null?"":sdfTime.format(scheduled_time)));
        txtCreateTime.setText((create_time==null?"":sdf.format(create_time)));
        txtCompleteTime.setText((complete_time==null?"":sdf.format(complete_time)));
        edtNotes.setText(notes);
        if (complete_time==null)
        {
            chkComplete.setChecked(false);
            chkComplete.setText(R.string.unfinished);
            txtCompleteTime.setText("");
        }
        else
        {
            chkComplete.setChecked(true);
            chkComplete.setText(R.string.completed);
            txtCompleteTime.setText(sdf.format(complete_time));
        }
        for (int i=0; i<colorCode.length();i++)
        {
            if (colorCode.getString(i).equals(color)) {
                spinner.setSelection(i);
                break;
            }
        }
//        vEdit.setBackgroundColor(Color.parseColor(color));
    }

    private void setEmptyView()
    {
        Date tmpDate=new Date();
        index=0;

        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
        edtSchdDate.setText(sdfDate.format(tmpDate));
        Log.d("EditTodoActivity", "setEmptyView():edtSchdDate="+edtSchdDate.getText().toString());
        edtSchdTime.setText(sdfTime.format(tmpDate));
        Log.d("EditTodoActivity", "setEmptyView():edtSchdTime="+edtSchdTime.getText().toString());
        txtCreateTime.setText(sdf.format(tmpDate));
        Log.d("EditTodoActivity", "setEmptyView():txtCreateTime="+txtCreateTime.getText().toString());
        txtCompleteTime.setText("");
        Log.d("EditTodoActivity", "setEmptyView():txtCompleteTime="+txtCompleteTime.getText().toString());
        edtNotes.setText("");
        Log.d("EditTodoActivity", "setEmptyView():edtNotes="+edtNotes.getText().toString());
        color="#FFFFFF";
        Log.d("EditTodoActivity", "setEmptyView():color="+color);
    }

    @Override
    public void onClick(View v) {
        int obj_id=v.getId();
        long dbRet;
        final int mYear, mMonth, mDay, mHour, mMinute, mSec;
        String create_time, scheduled_time, complete_time, notes, color;
        final Calendar calendar;
        create_time=txtCreateTime.getText().toString().trim();
        scheduled_time=edtSchdDate.getText().toString().trim().concat(" ").concat(edtSchdTime.getText().toString().trim());
        complete_time=txtCompleteTime.getText().toString().trim();
        complete_time=(complete_time.length()==0?null:complete_time);
        notes=edtNotes.getText().toString().trim();
        color=this.color;
        Log.d("onClick()", "spinner.getSelectedItem().toString()="+((ColorItem)spinner.getSelectedItem()).getCode());
        Intent retIntent=getIntent();
        Log.d("EditTodoActivity", "onClck():"+v.getClass().getName()+", type="+type);
        switch (obj_id)
        {
            case R.id.btnSubmit:
                switch (type)
                {
                    case REQUEST_FROM_ADD:
                        Log.d("EditTodoActivity", "onClck():btnSubmit:REQUEST_FROM_ADD");
                        dbRet=dbAdapter.createTodo(scheduled_time, notes, color);
                        if (dbRet>0)
                            retIntent.putExtra("id", (int)dbRet);
                        else
                            retIntent.putExtra("id", 0);
                        setResult(RESULT_FROM_ADD, retIntent);
                        finish();
                        break;
                    case REQUEST_FROM_EDIT:
                        Log.d("EditTodoActivity", "onClck():btnSubmit:REQUEST_FROM_EDIT");
                        dbRet=dbAdapter.updateToDo(id, scheduled_time, complete_time, notes, color);

                        if (dbRet>0)
                            retIntent.putExtra("id", (int)dbRet);
                        else
                            retIntent.putExtra("id", 0);
                        setResult(RESULT_FROM_EDIT, retIntent);
                        finish();
                        break;
                    case REQUEST_FROM_DROP:
                        Log.d("EditTodoActivity", "onClck():btnSubmit:REQUEST_FROM_DROP");
                        if (dbAdapter.dropTodo(id))
                            retIntent.putExtra("id", index);
                        else
                            retIntent.putExtra("id", 0);
                        setResult(RESULT_FROM_ADD, retIntent);
                        finish();
                }
                break;
            case R.id.btnCancel:
                Intent intent=getIntent();
                intent.putExtra("id", 0);
                switch (type)
                {
                    case REQUEST_FROM_ADD:
                        Log.d("EditTodoActivity", "onClck():btnCancel:REQUEST_FROM_ADD");
                        setResult(RESULT_FROM_ADD, intent);
                        break;
                    case REQUEST_FROM_EDIT:
                        Log.d("EditTodoActivity", "onClck():btnCancel:REQUEST_FROM_EDIT");
                        setResult(RESULT_FROM_EDIT, intent);
                        break;
                    case REQUEST_FROM_DROP:
                        Log.d("EditTodoActivity", "onClck():btnCancel:REQUEST_FROM_DROP");
                        setResult(RESULT_FROM_DROP, intent);
                        break;
                }
                finish();
                break;
            case R.id.edtSchdDate:
                Log.d("EditTodoActivity", "onClck():edtSchdDate");
                calendar = Calendar.getInstance(TimeZone.getDefault());
                if (edtSchdDate.getText().toString().length()>0)
                {
                    try {
                        calendar.setTime(sdfDate.parse(edtSchdDate.getText().toString().trim()));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //將選定日期設定至edt_birth                            calendar
                        calendar.set(year, month, dayOfMonth);
                        edtSchdDate.setText(sdfDate.format(calendar.getTime()));}
                }, mYear,mMonth,mDay);
                datePickerDialog.show();
                break;
            case R.id.edtSchdTime:
                Log.d("EditTodoActivity", "onClck():edtSchdTime");
                calendar = Calendar.getInstance(TimeZone.getDefault());
                if (edtSchdTime.getText().toString().length()>0)
                {
                    try {
                        calendar.setTime(sdfTime.parse(edtSchdTime.getText().toString().trim()));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        edtSchdTime.setText(sdfTime.format(calendar.getTime()));}
                }, mHour, mMinute, true);
                timePickerDialog.show();
                break;

        }
    }
}
