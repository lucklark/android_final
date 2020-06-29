package com.homework.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ClassFragment extends Fragment {

    private toActivityListener mToActivityListener;

    private Activity mActivity;

    private ListView class_lv;

    private ClassAdapter class_adapter;

    private ClassDataSource class_data_src;

    final static String DEFAULT_CLASS = "default";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mToActivityListener = (toActivityListener)activity;
        mActivity = activity;
    }

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
        class_data_src.insertClass(DEFAULT_CLASS);

        class_adapter = new ClassAdapter(class_data_src.getAllClass());

        class_lv.setAdapter(class_adapter);

        registerForContextMenu(this.class_lv);
        class_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassItems item = (ClassItems) class_adapter.getItem(position);
                mToActivityListener.setSelectedClass(item.class_name);
            }
        });
        class_lv.setLongClickable(true);
        /*
        class_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ImageButton del_class_btn = view.findViewById(R.id.del_class_btn);
                del_class_btn.setVisibility(View.VISIBLE);
                return true;
            }
        });
        */
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

            ImageButton del_class_btn = v.findViewById(R.id.del_class_btn);

            return v;
        }

        public void update(ArrayList<ClassItems> class_data_list_t) {
            class_data_list.clear();
            class_data_list.addAll(class_data_list_t);

            notifyDataSetChanged();
        }

        public void remove(int idx) {
            class_data_list.remove(idx);
        }
    }

    public interface toActivityListener {
        void setSelectedClass(String selected_class);
        void resetSelectedNotes(String del_class);
    }

    public boolean onContextItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            case R.id.delete_item:
                final int i = (int)((AdapterView.AdapterContextMenuInfo)paramMenuItem.getMenuInfo()).id;
                ClassItems selected_class_item = (ClassItems) class_adapter.getItem(i);
                final String selected_class_name = selected_class_item.class_name;
                int selected_class_notes_num = selected_class_item.notes_num;
                final String msg = "Delete " +selected_class_name+" and "+String.valueOf(selected_class_notes_num)+" notes of it";
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setMessage(msg).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        class_data_src.deleteOne(selected_class_name);
                        class_adapter.remove(i);
                        class_adapter.notifyDataSetChanged();

                        NotesDataSource notes_data_src = new NotesDataSource(mActivity);
                        notes_data_src.deleteByClass(selected_class_name);
                        Toast.makeText(mActivity,msg+" succeed",Toast.LENGTH_LONG);

                        mToActivityListener.resetSelectedNotes(selected_class_name);
                    }
                }).setNegativeButton("Return", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return true;

            default:
                return super.onContextItemSelected(paramMenuItem);

        }
    }

}
