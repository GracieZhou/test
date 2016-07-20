
package com.eostek.uitemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

    Class<?>[] mStyleClasses = new Class<?>[] {
            com.eostek.uitemplate.ButtonActivity.class, com.eostek.uitemplate.EditTextActivity.class,
            com.eostek.uitemplate.ListViewActivity.class, com.eostek.uitemplate.ProgressBarActivity.class,
            com.eostek.uitemplate.DialogActivity.class, com.eostek.uitemplate.CheckBoxActivity.class,
            com.eostek.uitemplate.ToastActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.ll_main);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.listview_item, new String[] {
            "text"
        }, new int[] {
            R.id.item_txt
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, mStyleClasses[position]));
            }

        });

    }

    private List<Map<String, Object>> getData() {

        String[] styleNames = getResources().getStringArray(R.array.style_names);

        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < styleNames.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", styleNames[i]);
            list.add(map);
        }

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
