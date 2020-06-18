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
}
