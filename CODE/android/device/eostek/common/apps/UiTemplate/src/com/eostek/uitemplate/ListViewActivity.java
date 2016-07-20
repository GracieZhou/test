
package com.eostek.uitemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        // Normal Style
        ListView listView = (ListView) findViewById(R.id.ll_sytle_1);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.listview_item_normal, new String[] {
            "text"
        }, new int[] {
            R.id.item_txt_normal
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            }

        });

        // Fill Style
        listView = (ListView) findViewById(R.id.ll_sytle_2);

        adapter = new SimpleAdapter(this, getData(), R.layout.listview_item_fill, new String[] {
            "text"
        }, new int[] {
            R.id.item_txt_fill
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            }

        });

        // Button Style
        listView = (ListView) findViewById(R.id.ll_sytle_3);

        adapter = new SimpleAdapter(this, getData(), R.layout.listview_item_button, new String[] {
            "text"
        }, new int[] {
            R.id.item_txt_button
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            }

        });

    }

    private List<Map<String, Object>> getData() {
        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", "ListView Item " + i);
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
