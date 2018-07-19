package com.example.kevin.mytodolist1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_FROM_MAIN=1, REQUEST_FROM_ADD=2, REQUEST_FROM_EDIT=3, REQUEST_FROM_DROP=4;
    private final int RESULT_FROM_MAIN=1, RESULT_FROM_ADD=2, RESULT_FROM_EDIT=3, RESULT_FROM_DROP=4;
    private android.support.v7.widget.Toolbar toolbar;
    private ListView listData;
    private TextView txtNoData;
    private DbAdapter dbAdapter;
    private SimpleCursorAdapter dataAdapter;
    private Intent intent;
    private Cursor contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        listData = findViewById(R.id.listData);
        txtNoData = findViewById(R.id.txtNoData);
        registerForContextMenu(listData);
        dbAdapter = new DbAdapter(this);
        Log.d("MainActivity:dbAdapter=", String.valueOf(dbAdapter.listToDo().getCount()));
        displaylistView();

    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionAdd:
                intent = new Intent();
                intent.putExtra("type", REQUEST_FROM_ADD);
                intent.setClass(MainActivity.this, EditTodoActivity.class );
                startActivityForResult(intent, REQUEST_FROM_MAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contact = (Cursor) listData.getItemAtPosition(info.position);

        // 設定浮動功能表的標題欄
        menu.setHeaderTitle("此記錄要?");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modify_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionUpdate:
                intent = new Intent();
                intent.putExtra("type", REQUEST_FROM_EDIT);
                intent.putExtra("keyid", contact.getInt(contact.getColumnIndex("_id")));
                intent.setClass(MainActivity.this, EditTodoActivity.class );
                startActivityForResult(intent, REQUEST_FROM_MAIN);
                break;
            case R.id.actionDrop:
                intent = new Intent();
                intent.putExtra("type", REQUEST_FROM_DROP);
                intent.putExtra("keyid", contact.getInt(contact.getColumnIndex("_id")));
                intent.setClass(MainActivity.this, EditTodoActivity.class );
                startActivityForResult(intent, REQUEST_FROM_MAIN);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        int id;
        Log.d("MainActivity", "onActivityResult():requestCode="+requestCode+", resultCode="+resultCode);
        if (requestCode==REQUEST_FROM_MAIN)
        {
            switch (resultCode)
            {
                case RESULT_FROM_ADD:
                    id=data.getIntExtra("id", 0);
                    Log.d("onActivityResult()", "RESULT_FROM_ADD, id="+id);
                    displaylistView();
                    break;
                case RESULT_FROM_EDIT:
                    id=data.getIntExtra("id", 0);
                    Log.d("onActivityResult()", ":RESULT_FROM_EDIT, id="+id);
                    displaylistView();
                    break;
                case RESULT_FROM_DROP:
                    id=data.getIntExtra("id", 0);
                    Log.d("onActivityResult()", "RESULT_FROM_DROP, id="+id);
                    displaylistView();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displaylistView()
    {
        Log.d("displaylistView()", "Begin");
        if (dbAdapter.listToDo().getCount()==0)
        {
            listData.setVisibility(View.INVISIBLE);
            txtNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            listData.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.INVISIBLE);
        }
        Cursor cursor = dbAdapter.listToDo();
        String[] columns = new String[]{
                dbAdapter.KEY_SCHEDULED_TIME,
                dbAdapter.KEY_NOTES,
                dbAdapter.KEY_COMPLETE_TIME,
                dbAdapter.KEY_COLOR
        };
        int[] to = new int[]{
                R.id.txtScheduledTime,
                R.id.txtNotes,
                R.id.txtComplete,
                R.id.imgColorBar
        };

        dataAdapter = new SimpleCursorAdapter(this, R.layout.item_view, cursor, columns, to, 0);
        listData.setAdapter(dataAdapter);
        dataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean ret=false;
                String txt=cursor.getString(columnIndex);
                String color=cursor.getString(cursor.getColumnIndex("color"));
                Log.d("setViewValue", "view."+view.getClass().getName()+", columnIndex="+columnIndex);
                switch (view.getId())
                {
                    case R.id.txtComplete:
                        Log.d("setViewValue", "columnIndex="+columnIndex+", getString()="+cursor.getString(columnIndex));
                        if (txt==null)
                        {
                            ((TextView)view).setText(R.string.unfinished);
                        }
                        else
                        {
                            ((TextView)view).setText(getString(R.string.completed)+" "+txt);
                        }
                        ret=true;
                        break;
                    case R.id.imgColorBar:
                        if (color!=null)
                            ((ImageView)view).setBackgroundColor(Color.parseColor(color));
                        break;
                }
                return ret;
            }
        });
        registerForContextMenu(listData);
        listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item_cursor = (Cursor) listData.getItemAtPosition(position);
                int item_id = item_cursor.getInt(item_cursor.getColumnIndexOrThrow("_id"));
                Log.i("item_id=",String.valueOf(item_id));
                intent = new Intent();
                intent.putExtra("type", REQUEST_FROM_EDIT);
                intent.putExtra("keyid",item_id);
                intent.setClass(MainActivity.this, EditTodoActivity.class );
                startActivityForResult(intent, REQUEST_FROM_MAIN);
            }
        });

    }


}
