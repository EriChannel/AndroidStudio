package com.example.phonebook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.phonebook.R;
import com.example.phonebook.adapter.ContactAdapter;
import com.example.phonebook.model.Contacts;
import com.example.phonebook.util.CheckConnection;
import com.example.phonebook.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvContact;
    ArrayList<Contacts> listContact;
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapping();
        getInfoContact();
        if(CheckConnection.isNetworkAvailable(getApplicationContext())){
            getAllContacts();

        }else{
            Toast.makeText(MainActivity.this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
            finish();
        }





    }

    private void getInfoContact() {
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                intent.putExtra("contact", listContact.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_contact:
                Intent i = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(i);
                break;
            case R.id.search_contact:
                //TO DO
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mapping() {
        lvContact = findViewById(R.id.lv_contact);
        listContact = new ArrayList<>();
        contactAdapter = new ContactAdapter(listContact, getApplicationContext());
        lvContact.setAdapter(contactAdapter);
    }

    private void getAllContacts() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                Server.urlGetAllContact,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null){
                            for(int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    int id = jsonObject.getInt("Id");
                                    String name = jsonObject.getString("Name");
                                    String imageBase64 = jsonObject.getString("Image");
                                    byte[] imageBitmap = Base64.decode(imageBase64.getBytes(), Base64.DEFAULT);
                                    String mobile = jsonObject.getString("PhoneNumber");
                                    listContact.add(new Contacts(id, name, imageBitmap, mobile));
                                    contactAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

    }
}