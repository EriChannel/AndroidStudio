package com.example.phonebookfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.phonebookfirebase.model.Contact;
import com.example.phonebookfirebase.util.Util;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private EditText edtName, edtMobile;
    private Button btnSave;
    private Uri imageUri;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        init();

        getImageFromGalleryOrCamera();

        insertContacts();

    }

    private void insertContacts() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        if(imageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Contact contact = new Contact();
                                    contact.setId(Util.generateId(5));
                                    contact.setName(edtName.getText().toString());
                                    contact.setPhoneNumber(edtMobile.getText().toString());
                                    contact.setAvatar(uri.toString());

                                    mDatabaseRef.child(contact.getId()).setValue(contact);
                                    Toast.makeText(AddContactActivity.this, "Success!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddContactActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(AddContactActivity.this, "No image selected!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
//        ContentResolver cR = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cR.getType(imageUri));
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    private void getImageFromGalleryOrCamera() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
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
            imgAvatar.setImageURI(imageUri);
        }else{
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        imgAvatar = findViewById(R.id.img_avatar);
        edtName = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);
        btnSave = findViewById(R.id.btn_save);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("contacts");
        mStorageRef = FirebaseStorage.getInstance().getReference("contacts");

    }
}