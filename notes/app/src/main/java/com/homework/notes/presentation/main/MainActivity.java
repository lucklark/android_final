package com.homework.notes.presentation.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.homework.notes.presentation.main.tabpage.classpage.ClassFragment;
import com.homework.notes.presentation.main.tabpage.notespage.NewNotes;
import com.homework.notes.presentation.main.tabpage.notespage.NotesFragment;
import com.homework.notes.R;
import com.homework.notes.presentation.main.tabpage.usrpage.UsrFragment;
import com.homework.notes.persistence.ClassDataSource;
import com.homework.notes.persistence.SQLiteHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ClassFragment.ClasstoActivityListener, NotesFragment.toActivityListener {
    private Fragment notes_frag;
    private Fragment class_frag;
    private Fragment usr_frag;

    private ImageButton notes_button;
    private ImageButton class_button;
    private ImageButton usr_button;

    private TextView selected_class_text;

    private LinearLayout notes_lay;
    private LinearLayout class_lay;
    private LinearLayout usr_lay;

    private FragmentTransaction ftr;

    private int selected_tab;
    private String selected_class = DEFAULT_CLASS_NAME;

    static String DEFAULT_CLASS = "default";
    final static String DEFAULT_CLASS_NAME = "default";

    public static final  String TAG = "MainActivity";


    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        //setContentView(R.layout.activity_notes);
        setContentView(R.layout.tab_layout);

        initDatabase();

        initView(); // view initialization(v1)

        initEvent(); // fragmentation event initialization

        setSelected(1);
        selected_class = DEFAULT_CLASS;

        requestAllPower();
    }

    private void initDatabase() {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
        SQLiteDatabase database = sqLiteHelper.getWritableDatabase();
        sqLiteHelper.createTable(database);
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

        selected_class_text = (TextView) findViewById(R.id.current_class);

        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b5b5")));
        TextView localTextView = (TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
    }

    public void onClick(View v) {
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
        selected_class_text.setTextColor(this.getColor(R.color.regular));
        selected_class_text.setAlpha(0.5f);
    }

    public void setSelected(int i) {
        resetBtn();
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
                    notes_frag = NotesFragment.newInstance(selected_class);
                    ftr.add(R.id.tab_frame, notes_frag);
                }
                notes_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_clipboard_solid));
                selected_class_text.setVisibility(View.GONE);
                selected_class_text.setText(selected_class);
                selected_class_text.setVisibility(View.VISIBLE);
                selected_class_text.setTextColor(this.getColor(R.color.solid));
                selected_class_text.setAlpha(1.0f);

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
                            hideTransaction(ftr);
                            ftr.remove(class_frag);
                            class_frag = new ClassFragment();
                            ftr.add(R.id.tab_frame, class_frag);
                            ftr.commit();

                            Toast t = Toast.makeText(MainActivity.this, "Create new class \"" + editText.getText().toString()+"\"", Toast.LENGTH_LONG);
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
                // ADD: send note class to new_note activity
                Intent new_notes_intent = new Intent(this, NewNotes.class);
                new_notes_intent.putExtra("note_class",selected_class);
                startActivity(new_notes_intent);
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

    //权限动态申请
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public void readdNotesFrag() {
        // get transaction
        FragmentManager fm = getSupportFragmentManager();
        // begin transaction
        ftr = fm.beginTransaction();
        hideTransaction(ftr);
        if(notes_frag != null) {
            ftr.remove(notes_frag);
        }
        notes_frag = NotesFragment.newInstance(selected_class);
        ftr.add(R.id.tab_frame, notes_frag);
        ftr.commit();
    }

    public void readdClassFrag() {
        // get transaction
        FragmentManager fm = getSupportFragmentManager();
        // begin transaction
        ftr = fm.beginTransaction();
        hideTransaction(ftr);
        if(class_frag != null) {
            ftr.remove(class_frag);
        }
        class_frag = new ClassFragment();
        ftr.add(R.id.tab_frame, class_frag);
        ftr.commit();
    }

    @Override
    public void setSelectedClass(String selected_class) {
        if(!selected_class.equals(this.selected_class)) {
            this.selected_class = selected_class;
            readdNotesFrag();
        }
        setSelected(0);
    }

    @Override
    public void resetSelectedNotes(String del_class) {
        if(selected_class.equals(del_class)) {
            selected_class = DEFAULT_CLASS;
            selected_class_text.setText(selected_class);
            readdNotesFrag();
            setSelected(1);
        }
    }

    public void setDefaultClass(String first_class) {
        DEFAULT_CLASS = first_class;
    }

    @Override
    public void deleteNotesInform(String del_note_class) {
        readdClassFrag();
        selected_class = del_note_class;
        setSelected(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        readdNotesFrag();

        readdClassFrag();

        setSelected(0);
    }

}
