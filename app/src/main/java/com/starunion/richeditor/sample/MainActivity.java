package com.starunion.richeditor.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.starunion.lib.HFRichEditor;
import com.starunion.richeditor.R;

public class MainActivity extends AppCompatActivity {
    HFRichEditor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnInsert = findViewById(R.id.btn_insertimage_main);
        editor = findViewById(R.id.re_editor_main);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.insertImageView("http://pic.58pic.com/58pic/14/61/42/51s58PICuGy_1024.jpg");
            }
        });
    }
}
