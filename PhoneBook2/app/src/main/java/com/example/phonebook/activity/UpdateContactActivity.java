package com.example.phonebook.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.phonebook.util.Server;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateContactActivity extends AppCompatActivity {

    private EditText edtName, edtMobile;
    private CircleImageView imageView;
    private Button btnUpdate;
    private Contacts contact;
    private Uri imgUri = null;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);
        Intent intent = getIntent();
        contact = (Contacts) intent.getSerializableExtra("contact");
        mapping();

        edtName.setText(contact.getName());
        edtMobile.setText(contact.getMobile());
        bytes = contact.getImage();
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

        getImageFromGalleryOrCamera();
        updateContact();
    }

    private void updateContact() {

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Server.urlUpdateContact,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("success")){
                                    Toast.makeText(UpdateContactActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateContactActivity.this, ContactActivity.class);
                                    intent.putExtra("contact", contact);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(UpdateContactActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateContactActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        imageUriToArray();
                        contact.setName(edtName.getText().toString());
                        contact.setMobile(edtMobile.getText().toString());
                        contact.setImage(bytes);

                        Map<String, String> params = new HashMap<>();
                        params.put("id", String.valueOf(contact.getId()));
                        params.put("name", contact.getName());
                        String base64 = Base64.encodeToString(contact.getImage(), Base64.DEFAULT);
                        params.put("image", base64);
                        params.put("mobile",contact.getMobile());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private void getImageFromGalleryOrCamera() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UpdateContactActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start(101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageUriToArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            if (imgUri != null) {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mapping() {
        edtName = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);
        imageView = findViewById(R.id.img_avatar);
        btnUpdate = findViewById(R.id.btn_update);
    }
}