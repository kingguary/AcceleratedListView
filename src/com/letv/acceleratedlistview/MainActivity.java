package com.letv.acceleratedlistview;

import java.util.ArrayList;
import java.util.List;

import com.letv.acceleratedlistview.AcceleratedListView.FastScrollMode;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
    AcceleratedListView mListView;
    RadioGroup mRadiogroup;
    List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRadiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        mRadiogroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.radiobutton1) {
                            mListView.setFastScrollMode(FastScrollMode.Normal);
                        } else if(checkedId == R.id.radiobutton2){
                            mListView.setFastScrollMode(FastScrollMode.LinearTime);
                        } else if(checkedId == R.id.radiobutton3){
                            mListView.setFastScrollMode(FastScrollMode.LinearTime15X);
                        }
                    }
                });
        
        mListView = (AcceleratedListView) findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, getData());
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private List<String> getData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }
}
