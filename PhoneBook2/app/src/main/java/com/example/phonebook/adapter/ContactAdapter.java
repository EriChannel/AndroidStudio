package com.example.phonebook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.phonebook.R;
import com.example.phonebook.model.Contacts;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends BaseAdapter {
    ArrayList<Contacts> listContact;
    Context context;

    public ContactAdapter(ArrayList<Contacts> listContact, Context context) {
        this.listContact = listContact;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listContact.size();
    }

    @Override
    public Object getItem(int i) {
        return listContact.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder{
        CircleImageView imageView;
        TextView tvName;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_contacts, null);
            viewHolder.imageView = view.findViewById(R.id.img_avatar);
            viewHolder.tvName = view.findViewById(R.id.tv_name);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();

        }
        Contacts contacts = (Contacts) getItem(i);
        byte[] bytes = contacts.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        viewHolder.imageView.setImageBitmap(bitmap);
        viewHolder.tvName.setText(contacts.getName());
        return view;
    }




}
