package org.coltec.Model;

import java.util.Date;
import java.util.List;

public class JournalEntry {
    private String text;
    private Date date;
    private List<String> categories;

    public JournalEntry(String text, Date date, List<String> categories) {
        this.text = text;
        this.date = date;
        this.categories = categories;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getCategories() {
        return categories;
    }
}
