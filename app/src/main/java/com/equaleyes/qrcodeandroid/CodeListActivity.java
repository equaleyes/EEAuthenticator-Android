package com.equaleyes.qrcodeandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.equaleyes.qrcodeandroid.adapters.CodeListAdapter;
import com.equaleyes.qrcodeandroid.databinding.ActivityCodeListBinding;

/**
 * Created by zan on 8/24/16.
 */
public class CodeListActivity extends AppCompatActivity {

    private CodeListAdapter adapter;
    private ActivityCodeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_code_list);
        setSupportActionBar(binding.toolbar);

        adapter = new CodeListAdapter(this);
        binding.codeList.setAdapter(adapter);
        binding.codeList.setLayoutManager(new LinearLayoutManager(this));
    }
}
