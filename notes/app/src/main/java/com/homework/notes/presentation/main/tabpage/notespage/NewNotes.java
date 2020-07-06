package com.homework.notes.presentation.main.tabpage.notespage;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.homework.notes.R;
import com.homework.notes.toolkit.RichText;
import com.homework.notes.persistence.ClassDataSource;
import com.homework.notes.persistence.NotesDataSource;
import com.homework.notes.presentation.main.MainActivity;


public class NewNotes extends AppCompatActivity {

    EditText title;
    RichText content;
    Button start_remembering;

    private String imagePath;
    private int RESULT_LOAD_IMAGE = 200;

    private String note_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setTitle("Add Notes");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b5b5")));
        int titleId = getResources().getIdentifier("action_bar_title", "id","android");
        Typeface face = Typeface.createFromAsset(getAssets(),"Roboto-Thin.ttf");

        // ADD: get note_class from main_activity
        Intent r_intent = getIntent();
        note_class = r_intent.getStringExtra("note_class");

        title = (EditText) findViewById(R.id.title);
        content = (RichText) findViewById(R.id.content);

        title.setTypeface(face);
        content.setTypeface(face);

        start_remembering = (Button) findViewById(R.id.start_remembering);
        start_remembering.setTypeface(face);
        start_remembering.setTextSize(30);

        start_remembering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    NotesDataSource nds = new NotesDataSource(getApplicationContext());
                    if (!title.getText().toString().trim().isEmpty() && !content.getText().toString().trim().isEmpty() )
                    {
                        nds.insertNotes(title.getText().toString(),content.getText().toString(),note_class);
                        ClassDataSource cds = new ClassDataSource(getApplicationContext());
                        cds.incrementNotesNum(note_class);
                        Toast t = Toast.makeText(getApplicationContext(),"Note Added.",Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER,0,800);
                        t.show();
                        Intent i = new Intent(NewNotes.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("new_note","success");
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please write something.",Toast.LENGTH_LONG).show();
                    }


                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error Please try again",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.about)
        {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            View localView = getLayoutInflater().inflate(R.layout.aboutus, null);
            TextView localTextView1 = (TextView)localView.findViewById(R.id.title);
            Typeface localTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
            localTextView1.setTypeface(localTypeface);
            TextView localTextView2 = (TextView)localView.findViewById(R.id.content);
            SpannableString localSpannableString = new SpannableString("当你在长时间下间隔几次对于知识复习将会更容易记住知识，而不是短时间内多次记忆\n\n" +
                    "只需要选择下方中间按钮即对应的类别，之后再点击+号进行添加相应类别下的知识笔记即可\n\n" +
                    "注意：最好一次只添加单个笔记，而不是快速创建多个，不然会使得该时刻通知过多导致程序出错！");
            localTextView2.setTypeface(localTypeface);
            localTextView2.setText(localSpannableString);
            localBuilder.setView(localView).setPositiveButton("好的", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                }
            });
            localBuilder.show();
            return true;
        }
        if (id == R.id.image) {

            //相册上传
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            //通过Cursor读取图片对应本地地址
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();
            //向RichText中插入图片
            content.insertImage(imagePath);
        }

    }
}
