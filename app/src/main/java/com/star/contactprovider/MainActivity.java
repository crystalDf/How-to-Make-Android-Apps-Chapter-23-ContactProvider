package com.star.contactprovider;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mContactNameEditText;
    private Button mAddContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactNameEditText = (EditText) findViewById(R.id.contact_name_edit_text);
        mAddContactButton = (Button) findViewById(R.id.add_contact_button);
        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mContactNameEditText.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(ContactProvider.COLUMN_NAME, name);

                Uri uri = getContentResolver().insert(ContactProvider.CONTENT_URI, contentValues);

                Toast.makeText(MainActivity.this, "New Contact Added " + uri,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
