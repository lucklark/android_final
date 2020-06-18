package com.homework.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Fragment notes_frag;
    private Fragment class_frag;
    private Fragment usr_frag;

    private ImageButton notes_button;
    private ImageButton class_button;
    private ImageButton usr_button;

    private LinearLayout notes_lay;
    private LinearLayout class_lay;
    private LinearLayout usr_lay;

    private FragmentTransaction ftr;

    private int selected_tab;

    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<NoteItems> items;
    NotesAdapter notesAdapter;
    ListView notes_lv;

    TextView tips;
    public List<NoteItems> getDataForListView()
    {
        items = new NotesDataSource(getApplicationContext()).getAllNotes();
        Collections.sort(items, new Comparator<NoteItems>(){
            public int compare(NoteItems s1, NoteItems s2) {
                return s1.total_reviews.compareToIgnoreCase(s2.total_reviews);
            }
        });
        return this.items;
    }


    /*@Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back once more to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }*/

    public boolean onContextItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            case R.id.delete_item:
                int i = (int)((AdapterView.AdapterContextMenuInfo)paramMenuItem.getMenuInfo()).id;
                new NotesDataSource(getApplicationContext()).deleteOne(((NoteItems)this.items.get(i)).title, ((NoteItems)this.items.get(i)).content);
                this.items.remove(i);
                this.notesAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onContextItemSelected(paramMenuItem);

        }


    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        //setContentView(R.layout.activity_notes);
        setContentView(R.layout.tab_layout);

        initView(); // view initialization(v1)

        initEvent(); // fragmentation event initialization

        setSelected(0);

    }

    private void initEvent() {
        notes_lay.setOnClickListener(this);
        class_lay.setOnClickListener(this);
        usr_lay.setOnClickListener(this);
    }

    private void initView() {
        notes_button = (ImageButton) findViewById(R.id.notes_btn);
        class_button = (ImageButton) findViewById(R.id.class_btn);
        usr_button = (ImageButton) findViewById(R.id.usr_btn);

        notes_lay = (LinearLayout) findViewById(R.id.notes_lay);
        class_lay = (LinearLayout) findViewById(R.id.class_lay);
        usr_lay = (LinearLayout) findViewById(R.id.usr_lay);

        //getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(17170445)));
        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b5b5")));
        TextView localTextView = (TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
    }

    private void code_saved() {
        //localTextView.setTextSize(30.0F);
        Typeface face = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        //localTextView.setTypeface(face);
        Intent localIntent = new Intent(getApplicationContext(), SpacedService.class);
        localIntent.putExtra("KEY1", "Value to be used by the service");
        getApplicationContext().startService(localIntent);
        tips = (TextView) findViewById(R.id.tips);
        tips.setTypeface(face);
        SpannableString localSpannableString = new SpannableString("Humans more easily remember or learn items when they are studied a few times spaced over a long time span rather than repeatedly studied in a short span of time\n\n" +
                "Just click on the + icon and start adding notes and let the app handle the rest\n\n" +
                "Warning: Having too many notes at once could lead to multiple notification making this app unusable, limit to few notes at a time to get the most from the app");
        tips.setText(localSpannableString);
        List<NoteItems> items = MainActivity.this.getDataForListView();
        if (items.size() != 0)
        {
            tips.setVisibility(View.GONE);
        }
        this.notesAdapter = new NotesAdapter(items);
        this.notes_lv = ((ListView)findViewById(R.id.notes_lv));
        this.notes_lv.addFooterView(new View(this), null, false);
        this.notes_lv.addHeaderView(new View(this), null, false);
        this.notes_lv.setAdapter(this.notesAdapter);
        registerForContextMenu(this.notes_lv);
        this.notes_lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
                Intent localIntent = new Intent(MainActivity.this, AnswerCard.class);
                localIntent.putExtra("title", ((NoteItems)MainActivity.this.items.get(paramAnonymousInt - 1)).title);
                localIntent.putExtra("content", ((NoteItems)MainActivity.this.items.get(paramAnonymousInt - 1)).content);
                localIntent.putExtra("from", "app");
                MainActivity.this.startActivity(localIntent);
            }
        });
        this.notes_lv.setLongClickable(true);
    }

    public void onClick(View v) {
        resetBtn();

        switch (v.getId()) {
            case R.id.notes_lay:
                setSelected(0);
                break;
            case R.id.class_lay:
                setSelected(1);
                break;
            case R.id.usr_lay:
                setSelected(2);
                break;
        }
    }

    void resetBtn() {
        notes_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_clipboard_regular));
        class_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_copy_regular));
        usr_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_regular));
    }

    public void setSelected(int i) {
        // get transaction
        FragmentManager fm = getSupportFragmentManager();
        // begin transaction
        ftr = fm.beginTransaction();
        // hide all fragments
        hideTransaction(ftr);

        selected_tab = i;

        switch (i) {
            case 0:
                if(notes_frag == null) {
                    notes_frag = new NotesFragment();
                    ftr.add(R.id.tab_frame, notes_frag);
                }
                notes_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_clipboard_solid));
                ftr.show(notes_frag);
                break;
            case 1:
                if(class_frag == null) {
                    class_frag = new ClassFragment();
                    ftr.add(R.id.tab_frame, class_frag);
                }
                class_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_copy_solid));
                ftr.show(class_frag);
                break;
            case 2:
                if(usr_frag == null) {
                    usr_frag = new UsrFragment();
                    ftr.add(R.id.tab_frame, usr_frag);
                }
                usr_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_solid));
                ftr.show(usr_frag);
                break;
        }

        // summit transaction
        ftr.commit();
    }

    private void hideTransaction(FragmentTransaction ftr) {
        if(notes_frag != null) {
            ftr.hide(notes_frag);
        }
        if(class_frag != null) {
            ftr.hide(class_frag);
        }
        if(usr_frag != null) {
            ftr.hide(usr_frag);
        }
    }

    public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
    {
        super.onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
        getMenuInflater().inflate(R.menu.context, paramContextMenu);
    }

    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
        getMenuInflater().inflate(R.menu.notes, paramMenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        int i = paramMenuItem.getItemId();
        if (i == R.id.newNotes)
        {
            if(selected_tab == 0) {
                startActivity(new Intent(this, NewNotes.class));
            }
            else if(selected_tab == 1) {
                newClassDialog();
            }
            return true;
        }
        if (i == R.id.about)
        {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            View localView = getLayoutInflater().inflate(R.layout.aboutus, null);
            TextView localTextView1 = (TextView)localView.findViewById(R.id.title);
            Typeface localTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
            localTextView1.setTypeface(localTypeface);
            TextView localTextView2 = (TextView)localView.findViewById(R.id.content);
            SpannableString localSpannableString = new SpannableString("Humans more easily remember or learn items when they are studied a few times spaced over a long time span rather than repeatedly studied in a short span of time\n\n" +
                    "Just click on the + icon and start adding notes and let the app handle the remembering part for you\n\n" +
                    "Long press a note to delete it\n\n\n" +
                    "Warning: Having too many notes at once could lead to multiple notification making this app unusable, limit to few notes at a time to get the most from the app");
            Linkify.addLinks(localSpannableString, 15);
            localTextView2.setTypeface(localTypeface);
            localTextView2.setText(localSpannableString);
            localBuilder.setView(localView).setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                }
            });
            localBuilder.show();
            return true;
        }
        return super.onOptionsItemSelected(paramMenuItem);
    }

    public void newClassDialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.new_class,(ViewGroup)findViewById(R.id.new_class_dialog));
        final EditText editText = dialog.findViewById(R.id.new_class_edit_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("New Class Name: ").setView(dialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_class_name = editText.getText().toString();
                        ClassDataSource class_data_src = new ClassDataSource(MainActivity.this);
                        long ret = class_data_src.insertClass(new_class_name);

                        if(ret > 0) {
                            // get transaction
                            FragmentManager fm = getSupportFragmentManager();
                            // begin transaction
                            ftr = fm.beginTransaction();
                            ftr.remove(class_frag);
                            class_frag = new ClassFragment();
                            ftr.add(R.id.tab_frame, class_frag);
                            ftr.commit();

                            Toast t = Toast.makeText(MainActivity.this, "Create new class " + editText.getText().toString(), Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER,0,800);
                            t.show();
                        }
                        else if (ret < 0){
                            Toast t = Toast.makeText(MainActivity.this, "Class " + editText.getText().toString() + "existed", Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER,0,800);
                            t.show();
                        }

                    }
                });
        builder.create().show();

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
                LayoutInflater localLayoutInflater = (LayoutInflater)MainActivity.this.getSystemService("layout_inflater");
                paramView = localLayoutInflater.inflate(R.layout.list_item, paramViewGroup, false);
                localLayoutInflater.inflate(R.layout.list_item, paramViewGroup, false);
            }
            Typeface localTypeface = Typeface.createFromAsset(MainActivity.this.getAssets(), "Roboto-Thin.ttf");
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
}
