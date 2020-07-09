package com.homework.notes.presentation.main.tabpage.notespage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;

import com.homework.notes.R;
import com.homework.notes.persistence.ClassDataSource;
import com.homework.notes.persistence.NotesDataSource;

public class AnswerCard extends AppCompatActivity {
    TextView title_tv;
    TextView content_tv;

    long id;
    String content;
    String title;
    String class_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_card);
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setTitle("Note");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b5b5")));
        int titleId = getResources().getIdentifier("action_bar_title", "id","android");

        Typeface face = Typeface.createFromAsset(getAssets(),"Roboto-Thin.ttf");


        Intent intent = getIntent();
        id = Long.valueOf(intent.getStringExtra("id"));
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        class_name = intent.getStringExtra("class_name");
        String from = intent.getStringExtra("from");

        NotesDataSource nds = new NotesDataSource(this);
        nds.modifyLastSeen(id);

        title_tv = (TextView) findViewById(R.id.title_answercard);
        content_tv = (TextView) findViewById(R.id.content_answercard);

        title_tv.setTypeface(face);
        content_tv.setTypeface(face);

        title_tv.setText(title);
        ImageGetter imageGetter = new ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable d = null;
                try {
                        d = Drawable.createFromPath(source);
                        d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return d;
            }
        };

        content = content.replace("\n","<br>");
        AppTagHandler h = new AppTagHandler(this);
        content_tv.setText(Html.fromHtml(content,imageGetter,h));
        content_tv.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.answer_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

    // ADD: modify review time when stop review
    @Override
    protected void onStop() {
        super.onStop();

        NotesDataSource nds = new NotesDataSource(this);
        long incre_review_time = nds.modifyTotalReviewTime(id);
        if(incre_review_time > 0) {
            ClassDataSource cds = new ClassDataSource(this);
            cds.updateClassReviewTime(incre_review_time, class_name);
        }
    }
}
