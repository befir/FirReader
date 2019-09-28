package com.fir.reader.firreader.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.*;

public class DBHelper extends SQLiteOpenHelper {
    private static int VERSION = 4;
    private static String DATEBASE_NAME = "fir.db";

    public DBHelper(Context context){
        super(context,DATEBASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        System.out.println("hahahh");

    }
    public void init(SQLiteDatabase db){
        String filePath = "/data/src/script/v_1.0.sql";
        try {
            System.out.println("executeSqlScript start");
            executeSqlScript(db,filePath);
            System.out.println("executeSqlScript end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void executeSqlScript(SQLiteDatabase db,String filePath) throws IOException{
        File scriptFile = new File(filePath);
        String[] sqls = getSqlFromFile(scriptFile);
        for(String sql:sqls){
            if(!TextUtils.isEmpty(sql)){
//                System.out.println(sql);
                db.execSQL(sql);
            }
        }
    }

    private String[] getSqlFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        for(String line;(line=br.readLine())!=null;){
            sb.append(br.readLine());
        }
        return sb.toString().split(";");
    }
}
