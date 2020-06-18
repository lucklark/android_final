package com.homework.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class NotesFragment extends Fragment {
    private Context mContext;

    ArrayList<NoteItems> items;
    NotesAdapter notesAdapter;
    ListView notes_lv;

    TextView tips;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_notes,container,false);

        //localTextView.setTextSize(30.0F);
        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Thin.ttf");
        //localTextView.setTypeface(face);
        Intent localIntent = new Intent(mContext.getApplicationContext(), SpacedService.class);
        localIntent.putExtra("KEY1", "Value to be used by the service");
        mContext.getApplicationContext().startService(localIntent);
        tips = (TextView) v.findViewById(R.id.tips);
        tips.setTypeface(face);
        SpannableString localSpannableString = new SpannableString("Humans more easily remember or learn items when they are studied a few times spaced over a long time span rather than repeatedly studied in a short span of time\n\n" +
                "Just click on the + icon and start adding notes and let the app handle the rest\n\n" +
                "Warning: Having too many notes at once could lead to multiple notification making this app unusable, limit to few notes at a time to get the most from the app");
        tips.setText(localSpannableString);
        List<NoteItems> items = getDataForListView();
        if (items.size() != 0)
        {
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
                localIntent.putExtra("title", ((NoteItems)items_t.get(paramAnonymousInt - 1)).title);
                localIntent.putExtra("content", ((NoteItems)items_t.get(paramAnonymousInt - 1)).content);
                localIntent.putExtra("from", "app");
                mContext.startActivity(localIntent);
            }
        });
        this.notes_lv.setLongClickable(true);

        return v;
    }

    public List<NoteItems> getDataForListView()
    {
        items = new NotesDataSource(mContext.getApplicationContext()).getAllNotes();
        Log.d(TAG, "getDataForListView: "+items.get(0).toString());
        Collections.sort(items, new Comparator<NoteItems>(){
            public int compare(NoteItems s1, NoteItems s2) {
                return s1.total_reviews.compareToIgnoreCase(s2.total_reviews);
            }
        });
        return this.items;
    }

    public class NotesAdapter extends BaseAdapter
    {
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

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
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
            localTextView1.setTypeface(localTypeface);
            localTextView2.setTypeface(localTypeface);
            localTextView3.setTypeface(localTypeface);
            NoteItems localNoteItems = (NoteItems)this.items.get(paramInt);
            localTextView1.setText(localNoteItems.title);
            long l1 = Long.valueOf(localNoteItems.last_reviewed).longValue();
            long l2 = System.currentTimeMillis() - l1;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(l2));
            arrayOfObject[1] = Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(l2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l2)));
            String str = String.format("%d min, %d sec", arrayOfObject);
            localTextView2.setText("Last seen: " + str + " ago");
            localTextView3.setText("Notification sent: " + localNoteItems.total_reviews + " times");
            return paramView;
        }
    }

    public boolean onContextItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            case R.id.delete_item:
                int i = (int)((AdapterView.AdapterContextMenuInfo)paramMenuItem.getMenuInfo()).id;
                new NotesDataSource(mContext.getApplicationContext()).deleteOne(((NoteItems)this.items.get(i)).title, ((NoteItems)this.items.get(i)).content);
                this.items.remove(i);
                this.notesAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onContextItemSelected(paramMenuItem);

        }
    }
}
