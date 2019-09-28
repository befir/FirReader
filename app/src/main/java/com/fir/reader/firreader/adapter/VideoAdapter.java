package com.fir.reader.firreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fir.reader.firreader.dto.VideoDto;

import org.jsoup.helper.StringUtil;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<VideoDto.ChapterDto> {
    public VideoAdapter(@NonNull Context context, int resource, @NonNull List<VideoDto.ChapterDto> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        VideoDto.ChapterDto video = getItem(position);
        view.setText(StringUtil.isBlank(video.getTitle())?video.getId()+"":video.getTitle());
        return view;
    }
}
