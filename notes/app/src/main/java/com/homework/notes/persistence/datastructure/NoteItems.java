package com.homework.notes.persistence.datastructure;

public class NoteItems

{

    public long id;
    public String title;
    public String last_reviewed;
    public String total_reviews;
    // ADD: total review time
    public String total_review_time;
    public String content;
    public String note_class;

    public NoteItems(String title, String last_reviewed, String total_reviews, String total_review_time,String content, String note_class)
    {
        this.title = title;
        this.last_reviewed = last_reviewed;
        this.total_reviews = total_reviews;
        this.total_review_time = total_review_time;
        this.content = content;
        this.note_class = note_class;
    }
}