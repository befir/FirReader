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
import com.fir.reader.firreader.adapter.BookAdapter;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.dto.BookDto;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

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
    private ViewPager mViewPager;
    private List<String> catalogList = new ArrayList<>();
    private int pageNo = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.book_list);
        final ListView bookList = (ListView)findViewById(R.id.bookList);
        bookList.setItemsCanFocus(true);
        bookList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        bookList.setVisibility(View.VISIBLE);
        final List<BookDto> bookDtoList = getBookList();
        //第二个参数，也可以新建一个布局文件，在这个布局文件的TextView当中设置其他属性。因为每个Item本身就是一个TextView
        BookAdapter adapter = new BookAdapter(this, android.R.layout.simple_list_item_1, bookDtoList);
        ((ListView)findViewById(R.id.bookList)).setAdapter(adapter);
        bookList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookListActivity.this, ChapterActivity.class);
                intent.putExtra("book",bookDtoList.get(position));
                startActivity(intent);
                finish();
            }
        });

    }
    private List<BookDto> getBookList(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor bookResult = db.rawQuery("select * from book_resource", null);
//        System.out.println("bookResult.getCount():"+bookResult.getCount());
        ArrayList<BookDto> bookList = new ArrayList<>();
//        System.out.println("bookResult.getCount():"+bookResult.getCount());
        while(bookResult.moveToNext()){
            BookDto bookDto = new BookDto();
            bookDto.setId(bookResult.getLong(0));
            bookDto.setBookName(bookResult.getString(1));
            bookDto.setAuthor(bookResult.getString(2));
            bookDto.setBookUrl(bookResult.getString(3));
            bookDto.setContentAbstract(bookResult.getString(4));
//            System.out.println(bookDto.getId());
            bookList.add(bookDto);
        }

        bookResult.close();
        return bookList;

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
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
