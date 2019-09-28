package com.fir.reader.firreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fir.reader.firreader.R;
import com.fir.reader.firreader.adapter.VideoAdapter;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.dto.VideoDto;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    private Long videoId;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        System.out.println("VideoListActivity.onCreate");
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.video_list);
        final ListView videoList = (ListView)findViewById(R.id.videoList);
        videoList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        videoList.setVisibility(View.VISIBLE);
        final List<VideoDto.ChapterDto> videoDtoList = getVideoList();
        //第二个参数，也可以新建一个布局文件，在这个布局文件的TextView当中设置其他属性。因为每个Item本身就是一个TextView
        VideoAdapter adapter = new VideoAdapter(this, android.R.layout.simple_list_item_1, videoDtoList);
        ((ListView)findViewById(R.id.videoList)).setAdapter(adapter);
        videoList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
                intent.putExtra("chapter",videoDtoList.get(position));
                intent.putExtra("video",getIntent().getSerializableExtra("video"));
                intent.putExtra("position",position);
                startActivity(intent);
                finish();
            }
        });
        videoList.setSelection(getIntent().getIntExtra("position",0));
    }
    private List<VideoDto.ChapterDto> getVideoList(){
        VideoDto video = (VideoDto) this.getIntent().getSerializableExtra("video");
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        System.out.println(dbHelper.getDatabaseName()+":"+db.getPath());
        Cursor videoResult = db.rawQuery("select * from video_chapter where category_id = ? order by id desc", new String[]{video.getId() + ""});
        ArrayList<VideoDto.ChapterDto> videoList = new ArrayList<>();
        while(videoResult.moveToNext()){
            VideoDto.ChapterDto videoDto = new VideoDto().new ChapterDto();
            videoDto.setId(videoResult.getLong(0));
            videoDto.setTitle(videoResult.getString(1));
            videoDto.setChapterUrl(videoResult.getString(2));
            videoDto.setContent(videoResult.getString(3));
            videoList.add(videoDto);
        }

        videoResult.close();
        db.close();
        return videoList;

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, VideoCategoryActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.chapter_view, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
