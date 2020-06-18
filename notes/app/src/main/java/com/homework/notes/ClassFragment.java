package com.homework.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ClassFragment extends Fragment {

    private ListView class_lv;

    private ClassAdapter class_adapter;

    private ClassDataSource class_data_src;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.class_layout,container,false);

        class_lv = v.findViewById(R.id.class_lv);

        class_data_src = new ClassDataSource(getContext());

        class_adapter = new ClassAdapter(class_data_src.getAllClass());

        class_lv.setAdapter(class_adapter);

        return v;
    }

    public class ClassAdapter extends BaseAdapter{
        ArrayList<ClassItems> class_data_list;

        public ClassAdapter(ArrayList<ClassItems> class_data_list_t) {
            class_data_list = class_data_list_t;
        }

        public int getCount()
        {
            return class_data_list.size();
        }

        public Object getItem(int paramInt)
        {
            return class_data_list.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassItems class_item = (ClassItems) getItem(position);
            View v;
            if(convertView == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.class_item,null);
            }
            else {
                v = convertView;
            }
            TextView class_name = v.findViewById(R.id.class_name);
            TextView notes_num = v.findViewById(R.id.notes_num);
            class_name.setText(class_item.class_name);
            notes_num.setText("containing " + String.valueOf(class_item.notes_num) + " notes");

            return v;
        }

        public void update(ArrayList<ClassItems> class_data_list_t) {
            class_data_list.clear();
            class_data_list.addAll(class_data_list_t);

            notifyDataSetChanged();
        }
    }

    public void test() {

    }
}
