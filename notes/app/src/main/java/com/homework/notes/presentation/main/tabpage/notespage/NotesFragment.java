package com.homework.notes.presentation.main.tabpage.notespage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.homework.notes.R;
import com.homework.notes.persistence.ClassDataSource;
import com.homework.notes.toolkit.SpacedService;
import com.homework.notes.persistence.datastructure.NoteItems;
import com.homework.notes.persistence.NotesDataSource;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotesFragment extends Fragment {
    private Context mContext;
    private toActivityListener myToActivityListener;

    ArrayList<NoteItems> items;
    NotesAdapter notesAdapter;
    ListView notes_lv;

    TextView tips;

    String notes_class;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        myToActivityListener = (toActivityListener) context;
    }

    public static NotesFragment newInstance(String note_class) {
        NotesFragment notes_frag = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("note_class",note_class);
        Log.d("Notes", note_class);
        notes_frag.setArguments(bundle);
        return notes_frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_notes,container,false);

        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Thin.ttf");
        Intent localIntent = new Intent(mContext.getApplicationContext(), SpacedService.class);
        localIntent.putExtra("KEY1", "Value to be used by the service");
        mContext.getApplicationContext().startService(localIntent);
        tips = (TextView) v.findViewById(R.id.tips);
        tips.setTypeface(face);
        SpannableString localSpannableString = new SpannableString("当你在长时间下间隔几次对于知识复习将会更容易记住知识，而不是短时间内多次记忆\n\n" +
                "只需要选择下方中间按钮即对应的类别，之后再点击+号进行添加相应类别下的知识笔记即可\n\n" +
                "注意：最好一次只添加单个笔记，而不是快速创建多个，不然会使得该时刻通知过多导致程序出错！");
        tips.setText(localSpannableString);

        Bundle bundle = getArguments();
        notes_class = bundle.getString("note_class");
        List<NoteItems> items = getDataForListView();
        if (items.size() != 0) {
            tips.setVisibility(View.GONE);
        }
        this.notesAdapter = new NotesAdapter(items);
        this.notes_lv = ((ListView)v.findViewById(R.id.notes_lv));
        this.notes_lv.addFooterView(new View(mContext), null, false);
        this.notes_lv.addHeaderView(new View(mContext), null, false);
        this.notes_lv.setAdapter(this.notesAdapter);
        registerForContextMenu(this.notes_lv);
        final ArrayList<NoteItems> items_t = new ArrayList<NoteItems>(items);
        this.notes_lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
                Intent localIntent = new Intent(mContext, AnswerCard.class);
                localIntent.putExtra("id",String.valueOf(((NoteItems)items_t.get(paramAnonymousInt - 1)).id));
                localIntent.putExtra("title", ((NoteItems)items_t.get(paramAnonymousInt - 1)).title);
                localIntent.putExtra("content", ((NoteItems)items_t.get(paramAnonymousInt - 1)).content);
                localIntent.putExtra("from", "app");
                localIntent.putExtra("class_name", notes_class);
                mContext.startActivity(localIntent);
            }
        });
        this.notes_lv.setLongClickable(true);

        return v;
    }

    public List<NoteItems> getDataForListView() {
        items = new NotesDataSource(mContext.getApplicationContext()).getNotesOfClass(notes_class);
        Collections.sort(items, new Comparator<NoteItems>(){
            public int compare(NoteItems s1, NoteItems s2) {
                return s1.total_reviews.compareToIgnoreCase(s2.total_reviews);
            }
        });
        return this.items;
    }

    public class NotesAdapter extends BaseAdapter {
        List<NoteItems> items = null;

        public NotesAdapter(List<NoteItems> items)
        {
            this.items = items;
        }

        public int getCount()
        {
            return this.items.size();
        }

        public Object getItem(int paramInt)
        {
            return null;
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            if (paramView == null)
            {
                LayoutInflater localLayoutInflater = (LayoutInflater)mContext.getSystemService("layout_inflater");
                paramView = localLayoutInflater.inflate(R.layout.list_item, paramViewGroup, false);
                localLayoutInflater.inflate(R.layout.list_item, paramViewGroup, false);
            }
            Typeface localTypeface = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Thin.ttf");
            TextView localTextView1 = (TextView)paramView.findViewById(R.id.title);
            TextView localTextView2 = (TextView)paramView.findViewById(R.id.last_reviewed);
            TextView localTextView3 = (TextView)paramView.findViewById(R.id.total_reviews);
            TextView localTextView4 = (TextView)paramView.findViewById(R.id.total_review_time);

            localTextView1.setTypeface(localTypeface);
            localTextView2.setTypeface(localTypeface);
            localTextView3.setTypeface(localTypeface);
            localTextView4.setTypeface(localTypeface);

            NoteItems localNoteItems = (NoteItems)this.items.get(paramInt);
            localTextView1.setText(localNoteItems.title);
            long l1 = Long.valueOf(localNoteItems.last_reviewed).longValue();
            long l2 = System.currentTimeMillis() - l1;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(l2));
            arrayOfObject[1] = Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(l2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l2)));
            String str = String.format("%d min, %d sec", arrayOfObject);

            long review_time = Long.valueOf(localNoteItems.total_review_time).longValue();
            Object[] review_time_min_s = new Object[2];
            review_time_min_s[0] = Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(review_time));
            review_time_min_s[1] = Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(review_time)) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(review_time));
            String review_time_str = String.format("%d min, %d sec", review_time_min_s);

            localTextView2.setText("最后查看时间: " + str + " 之前");
            localTextView3.setText("通知发送次数: " + localNoteItems.total_reviews + " 次");
            localTextView4.setText("总复习时间: " + review_time_str);
            return paramView;
        }
    }

    public boolean onContextItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            case R.id.delete_item:
                final int i = (int)((AdapterView.AdapterContextMenuInfo)paramMenuItem.getMenuInfo()).id;
                final long id = items.get(i).id;
                final String note_title = items.get(i).title;
                final String note_content = items.get(i).content;
                final String msg = "Delete \"" +note_title+"\" from \""+notes_class+"\"";
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setMessage(msg).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new NotesDataSource(mContext.getApplicationContext()).deleteOne(id);
                        items.remove(i);
                        notesAdapter.notifyDataSetChanged();

                        Toast t = Toast.makeText(mContext, msg+" succeed",Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER,0,800);
                        t.show();

                        new ClassDataSource(mContext.getApplicationContext()).decrementNotesNum(notes_class);
                        myToActivityListener.deleteNotesInform(notes_class);
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

    public interface toActivityListener {
        void deleteNotesInform(String del_note_class);
    }
}
