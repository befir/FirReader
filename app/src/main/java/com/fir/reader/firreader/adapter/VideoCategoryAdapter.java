package com.fir.reader.firreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fir.reader.firreader.common.Crawler;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.R;
import com.fir.reader.firreader.dto.VideoDto;

import java.util.List;

public class VideoCategoryAdapter extends ArrayAdapter<VideoDto> {
    public VideoCategoryAdapter(@NonNull Context context, int resource, @NonNull List<VideoDto> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final VideoDto videoDto = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_category_item, null);
            //可以理解为从vlist获取view  之后把view返回给ListView
            holder.title = convertView.findViewById(R.id.video_title);
            holder.refresh = convertView.findViewById(R.id.video_refresh);
            holder.stop = convertView.findViewById(R.id.video_stop);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.refresh.setText("更新");
        holder.stop.setText("暂停更新");
        holder.title.setText(videoDto.getVideoName());
        holder.refresh.setTag(position);
        //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DBHelper dbHelper = new DBHelper(getContext());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Crawler.initVideo(dbHelper,videoDto);
                    }
                }).start();
            }
        });
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crawler.shutdown();
            }
        });
        return convertView;

    }
    public final class ViewHolder {
        public TextView title;
        public Button refresh;
        public Button stop;
    }
}
