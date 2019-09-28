package com.fir.reader.firreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fir.reader.firreader.dto.BookDto;

import java.util.List;

public class ChapterAdapter extends ArrayAdapter<BookDto.ChapterDto> {
    public ChapterAdapter(@NonNull Context context, int resource, @NonNull List<BookDto.ChapterDto> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(getItem(position).getTitle());
        return view;
    }
}
