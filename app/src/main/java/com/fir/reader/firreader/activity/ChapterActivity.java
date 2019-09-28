package com.fir.reader.firreader.activity;

import android.content.ContentValues;
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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fir.reader.firreader.R;
import com.fir.reader.firreader.adapter.ChapterAdapter;
import com.fir.reader.firreader.common.Crawler;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.dto.BookDto;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     *
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<String> catalogList = new ArrayList<>();
    private BookDto bookDto = new BookDto();
    private int pageNo = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.chapter_view);
        final TextView textView = findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button catalog = (Button) findViewById(R.id.catalog);
        Button lastChapter = (Button) findViewById(R.id.lastChapter);
        Button nextChapter = (Button) findViewById(R.id.nextChapter);
        Button home = (Button) findViewById(R.id.home);
        Button bookHome = (Button) findViewById(R.id.bookHome);
        final ListView catalogList = (ListView)findViewById(R.id.catalogList);
        catalogList.setItemsCanFocus(true);
        catalogList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        catalogList.setVisibility(View.INVISIBLE);
        catalogList.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("onItemClick"+id+":"+position);
                pageNo = position+1;
                toPage(pageNo);
                catalogList.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
        });
        bookDto = (BookDto) this.getIntent().getSerializableExtra("book");
        if(bookDto==null){
            bookDto = new BookDto();
            bookDto.setBookName("丹道宗师");
            bookDto.setAuthor("只是小虾米");
            bookDto.setBookUrl("https://www.biquge.info/36_36097/");
        }else{
//            System.out.println(bookDto.getBookUrl());
        }

        refreshBook(bookDto);
        textView.setText(Crawler.getChapterContent(bookDto.getChapterList().get(pageNo-1)).getContent());
        lastChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = pageNo==1?1:pageNo -1;
                toPage(pageNo);
            }
        });
        nextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageNo==bookDto.getChapterList().size()){
                    refreshBook(bookDto);
                }
                pageNo = pageNo==bookDto.getChapterList().size()?bookDto.getChapterList().size():pageNo+1;
                toPage(pageNo);

            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChapterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bookHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChapterActivity.this, BookListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setVisibility(View.INVISIBLE);
                catalogList.setSelection(pageNo-1);
                catalogList.setVisibility(View.VISIBLE);
            }
        });
    }
    private void refreshBook(BookDto bookDto){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        System.out.println(dbHelper.getDatabaseName()+":"+db.getPath());
        bookDto = Crawler.getBook(bookDto);
        ContentValues book= new ContentValues();
        book.put("name",bookDto.getBookName());
        book.put("author",bookDto.getAuthor());
        book.put("url",bookDto.getBookUrl());
        book.put("contentAbstract",bookDto.getContentAbstract());
        Cursor bookResult = db.rawQuery("select id,last_reading from book_resource where url = ?", new String[]{bookDto.getBookUrl()});
        long bookId = 0;
//        System.out.println("bookResult.getCount():"+bookResult.getCount());
        if(bookResult.getCount()==0){
            bookId = db.insert("book_resource",null,book);
        }else{
            bookResult.moveToFirst();
            bookId = bookResult.getLong(0);
            pageNo = bookResult.getInt(1);
        }
        bookResult.close();
//        System.out.println("bookId:"+bookId);
        bookDto.setId(bookId);
        Cursor chapterResult = db.rawQuery("select count(1) from book_chapter where book_id = ?", new String[]{bookDto.getId() + ""});
        chapterResult.moveToFirst();
        int startIndex = chapterResult.getInt(0);
//        System.out.println("startIndex:"+startIndex);
        List<BookDto.ChapterDto> chapterDtoList = bookDto.getChapterList();

        for(int i=startIndex;i<chapterDtoList.size();i++){
            BookDto.ChapterDto chapterDto = chapterDtoList.get(i);
            ContentValues chapter= new ContentValues();
            chapter.put("book_id",bookDto.getId());
            chapter.put("pageNo",chapterDto.getPageNo());
            chapter.put("title",chapterDto.getTitle());
            chapter.put("url",chapterDto.getChapterUrl());
            chapter.put("content",chapterDto.getContent());
            long chapterId = db.insert("book_chapter",null,chapter);
//            System.out.println("chapterId:"+chapterId +chapterDto.getTitle());
            chapterDto.setId(chapterId);
        }
        db.close();
        for(BookDto.ChapterDto chapterDto:chapterDtoList){
            catalogList.add(chapterDto.getTitle());
        }
        //第二个参数，也可以新建一个布局文件，在这个布局文件的TextView当中设置其他属性。因为每个Item本身就是一个TextView
        ChapterAdapter adapter = new ChapterAdapter(this, android.R.layout.simple_list_item_1, chapterDtoList);
        ((ListView)findViewById(R.id.catalogList)).setAdapter(adapter);
    }
    private void toPage(int pageNo){
        TextView textView = findViewById(R.id.textView);
        textView.scrollTo(0,0);
        textView.setText(Crawler.getChapterContent(bookDto.getChapterList().get(pageNo-1)).getContent());
        DBHelper dbHelper = new DBHelper(textView.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update book_resource set last_reading = ? where id = ?",new String[]{pageNo+"",bookDto.getId()+""});
        db.close();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BookListActivity.class);
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
