package com.lzw.talk.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lzw.talk.base.App;
import com.lzw.talk.entity.Msg;
import com.lzw.talk.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-5-28.
 */
public class DBMsg {
  public static final String MESSAGES = "messages";
  public static final String FROM_PEER_ID = "fromPeerId";
  public static final String TO_PEER_ID = "toPeerId";
  public static final String TIMESTAMP = "timestamp";
  public static final String OBJECT_ID = "objectId";
  public static final String CONTENT = "content";
  public static final String STATUS = "status";
  public static final String TYPE = "type";

  public static void createTable(SQLiteDatabase db) {
    db.execSQL("create table if not exists messages (id integer primary key, objectId varchar(63) unique," +
        "fromPeerId varchar(255), toPeerId varchar(255), content varchar(1023)," +
        " status integer,type integer,timestamp varchar(63))");
  }

  public static int insertMsg(Msg msg) {
    DBHelper dbHelper = new DBHelper(App.ctx, App.DB_NAME, App.DB_VER);
    List<Msg> msgs = new ArrayList<Msg>();
    msgs.add(msg);
    return insertMsgs(dbHelper, msgs);
  }

  public static int insertMsgs(DBHelper dbHelper, List<Msg> msgs) {
    if (msgs == null || msgs.size() == 0) {
      return 0;
    }
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.beginTransaction();
    int n = 0;
    try {
      for (Msg msg : msgs) {
        ContentValues cv = new ContentValues();
        cv.put(OBJECT_ID, msg.getObjectId());
        cv.put(TIMESTAMP, msg.getTimestamp() + "");
        cv.put(FROM_PEER_ID, msg.getFromPeerId());
        cv.put(STATUS,msg.getStatus());
        cv.put(TO_PEER_ID, msg.getToPeerIds().get(0));
        cv.put(TYPE,msg.getType());
        cv.put(CONTENT, msg.getContent());
        db.insert(MESSAGES, null, cv);
        n++;
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
    return n;
  }

  public static List<Msg> getMsgs(DBHelper dbHelper, String me, String he) {
    List<Msg> msgs = new ArrayList<Msg>();
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    assert db != null;
    Cursor c = db.query(MESSAGES, new String[]{FROM_PEER_ID,
            TO_PEER_ID, CONTENT, TIMESTAMP,OBJECT_ID,STATUS,TYPE},
        "fromPeerId =? and toPeerId = ? or fromPeerId = ? and toPeerId = ?",
        new String[]{me, he, he, me}, null, null,
        TIMESTAMP,
        "1000");
    while (c.moveToNext()) {
      Msg msg = new Msg();
      String id = c.getString(c.getColumnIndex(TO_PEER_ID));
      msg.setToPeerIds(Utils.oneToList(id));
      msg.setFromPeerId(c.getString(c.getColumnIndex(FROM_PEER_ID)));
      msg.setContent(c.getString(c.getColumnIndex(CONTENT)));
      msg.setStatus(c.getInt(c.getColumnIndex(STATUS)));
      msg.setObjectId(c.getString(c.getColumnIndex(OBJECT_ID)));
      msg.setTimestamp(Long.parseLong(c.getString(c.getColumnIndex(TIMESTAMP))));
      msg.setType(c.getInt(c.getColumnIndex(TYPE)));
      msgs.add(msg);
    }
    c.close();
    return msgs;
  }

  public static int updateStatusAndTimestamp(Msg msg) {
    ContentValues cv=new ContentValues();
    cv.put(STATUS,Msg.STATUS_SEND_RECEIVED);
    cv.put(TIMESTAMP,msg.getContent());
    String objectId = msg.getObjectId();
    return updateMessage(objectId, cv);
  }

  public static int updateMessage(String objectId, ContentValues cv) {
    DBHelper dbHelper=new DBHelper(App.ctx,App.DB_NAME,App.DB_VER);
    SQLiteDatabase db=dbHelper.getWritableDatabase();
    int updateN = db.update(MESSAGES, cv, "objectId=?", new String[]{objectId});
    return updateN;
  }

  public static int updateStatus(Msg msg,int status){
    ContentValues cv=new ContentValues();
    cv.put(STATUS,status);
    return updateMessage(msg.getObjectId(),cv);
  }

  public static int updateStatusToSendSucceed(Msg msg) {
    return updateStatus(msg, Msg.STATUS_SEND_SUCCEED);
  }

  public static void updateStatusToSendFailed(Msg msg) {
    updateStatus(msg,Msg.STATUS_SEND_FAILED);
  }
}
