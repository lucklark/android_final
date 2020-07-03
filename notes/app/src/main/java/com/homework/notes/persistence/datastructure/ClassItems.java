package com.homework.notes.persistence.datastructure;

public class ClassItems {
    public String class_name;
    public int notes_num;
    // ADD: class review time
    public String class_review_time;

    public ClassItems(String class_name_t, int notes_num_t, String class_review_time_t) {
        class_name = class_name_t;
        notes_num = notes_num_t;
        class_review_time = class_review_time_t;
    }
}
