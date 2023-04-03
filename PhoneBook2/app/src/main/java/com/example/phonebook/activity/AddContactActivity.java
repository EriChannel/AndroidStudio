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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactActivity extends AppCompatActivity {
    private Button btnSave;
    private CircleImageView image;
    private EditText edtName, edtMobile;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mapping();
        getImageFromGalleryOrCamera();
        saveContact();
    }

    private void saveContact() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contacts contact = createNewContact();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Server.urlInsertContact,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("success")){
                                    Toast.makeText(AddContactActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddContactActivity.this, MainActivity.class));
                                }else{
                                    Toast.makeText(AddContactActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddContactActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", contact.getName());
                        byte[] bytes = contact.getImage();
                        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        params.put("image", base64);
                        params.put("mobile", contact.getMobile());
                        return params;
                    }
                };

                requestQueue.add(stringRequest);


            }
        });
    }

    private Contacts createNewContact() {
        Contacts contacts = new Contacts();
        contacts.setName(edtName.getText().toString());
        contacts.setMobile(edtMobile.getText().toString());
        byte[] bytes = imageUriToArray();
        contacts.setImage(bytes);

        return contacts;
    }

    private void getImageFromGalleryOrCamera() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddContactActivity.this)
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
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }else{
            Toast.makeText(getApplicationContext(), "No image Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void mapping() {
        btnSave = findViewById(R.id.btn_save);
        image = findViewById(R.id.img_avatar);
        edtName = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);
    }

    private byte[] imageUriToArray(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            Bitmap imageBitmap;
            //Log.e("Image URI", imageUri.toString());
            if(imageUri == null){
                imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            }else{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}