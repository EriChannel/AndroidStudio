package com.example.phonebook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.phonebook.R;
import com.example.phonebook.model.Contacts;
import com.example.phonebook.util.CheckConnection;
import com.example.phonebook.util.Server;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity {
    
    private TextView tvName, tvMobile;
    private CircleImageView imgAvatar;
    private Button btnCall, btnMessage;
    private final int PERMISSION_CODE = 100;
    private Contacts contact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = getIntent();
        contact = (Contacts) intent.getSerializableExtra("contact");
        
        mapping();
        tvName.setText(contact.getName());
        tvMobile.setText(contact.getMobile());
        byte[] bytes = contact.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imgAvatar.setImageBitmap(bitmap);

        if(ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        call(contact);
        message(contact);
    }

    private void message(Contacts contact) {
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("sms:"+contact.getMobile()));
                startActivity(i);
            }
        });
    }

    private void call(Contacts contact) {
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + contact.getMobile()));
                startActivity(i);
            }
        });
    }

    private void mapping() {
        tvName = findViewById(R.id.tv_name);
        tvMobile = findViewById(R.id.tv_mobile);
        imgAvatar = findViewById(R.id.img_avatar);
        btnCall = findViewById(R.id.btn_call);
        btnMessage = findViewById(R.id.btn_messsage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_contact:
                Intent intent = new Intent(ContactActivity.this, UpdateContactActivity.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
                break;
            case R.id.delete_contact:
                deleteContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.urlDeleteContact,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(ContactActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ContactActivity.this, MainActivity.class));
                        }else{
                            Toast.makeText(ContactActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContactActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idContact", String.valueOf(contact.getId()));
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }
}