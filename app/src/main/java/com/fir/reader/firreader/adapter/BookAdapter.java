package com.fir.reader.firreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fir.reader.firreader.dto.BookDto;

import java.util.List;

public class BookAdapter extends ArrayAdapter<BookDto> {
    public BookAdapter(@NonNull Context context, int resource, @NonNull List<BookDto> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        BookDto book = getItem(position);
        view.setText(book.getBookName());
        view.append("\n");
        view.append("-------");
        view.append(book.getAuthor());
        return view;
    }
}
