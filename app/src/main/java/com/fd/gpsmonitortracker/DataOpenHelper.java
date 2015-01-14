package com.fd.gpsmonitortracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataOpenHelper extends SQLiteOpenHelper {

    public SQLiteDatabase db;

    public static final String DATABASE_NAME = "DBGpsLocation.db";
    /*MESSAGES*/
    public static final String MESSAGE_TABLE_NAME = "messages";
    public static final String MESSAGE_COLUMN_ID = "id";
    public static final String MESSAGE_COLUMN_MSG = "msg";
    public static final String MESSAGE_COLUMN_LOCATION = "location";
    public static final String MESSAGE_COLUMN_DIRECTION = "direction";
    /*LOCATIONS*/
    public static final String LOCATION_TABLE_NAME = "locations";
    public static final String LOCATION_COLUMN_ID = "id";
    public static final String LOCATION_COLUMN_NAME = "name";
    public static final String LOCATION_COLUMN_RADIUS = "radius";
    public static final String LOCATION_COLUMN_LATITUDE = "latitude";
    public static final String LOCATION_COLUMN_LONGITUDE = "longitude";
    public static final String LOCATION_COLUMN_ALTITUDE = "altitude";
    public static final String LOCATION_COLUMN_LAST_MSG = "last_message_send";
    public static final String LOCATION_COLUMN_MSG_IN = "msg_in";
    public static final String LOCATION_COLUMN_MSG_OUT = "msg_out";
    public static final String LOCATION_COLUMN_NOTIFY = "notify";
    /*CELLULARS*/
    public static final String CELLULAR_TABLE_NAME = "cellulars";
    public static final String CELLULAR_COLUMN_ID = "id";
    public static final String CELLULAR_COLUMN_NAME = "name";
    /*CELLULARS_LOCATIONS*/
    public static final String CELLULAR_LOCATION_TABLE_NAME = "cellulars_locations";
    public static final String CELLULAR_LOCATION_COLUMN_ID_CELLULAR = "idCellular";
    public static final String CELLULAR_LOCATION_COLUMN_NUMBER = "number";
    public static final String CELLULAR_LOCATION_COLUMN_NOTIFY = "notify";

    public DataOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        // TODO Auto-generated method stub
        //CREATE TABLE
        db.execSQL(
                "CREATE TABLE messages (id integer PRIMARY KEY autoincrement, msg text, location text, direction text);"
        );

        db.execSQL(
                "CREATE TABLE locations (id integer PRIMARY KEY autoincrement, name text not null unique, " +
                        "radius text, latitude text, longitude text, altitude text, last_message_send text, " +
                        "msg_in integer, msg_out integer, notify text, FOREIGN KEY (msg_in) REFERENCES " +
                        "messages(id), FOREIGN KEY (msg_out) REFERENCES messages(id));"
        );

        db.execSQL(
                "CREATE TABLE cellulars (id integer PRIMARY KEY autoincrement, name text not null unique, number text, notify text);"
        );

        db.execSQL(
                "CREATE TABLE cellulars_locations (idCelLoc integer, idCellular integer, idLocation integer, PRIMARY KEY (idCelLoc, idCellular), FOREIGN KEY (idCellular) REFERENCES cellulars(id), FOREIGN KEY (idLocation) REFERENCES locations(id));"
        );

        db.execSQL("CREATE TABLE settings (id integer PRIMARY KEY autoincrement, autoStart text)");

        //INSERT
        db.execSQL(
                "INSERT INTO settings (autoStart) VALUES ('YES');"
        );

        db.execSQL(
                "INSERT INTO locations (name, radius, latitude, longitude, altitude, last_message_send, msg_in, msg_out, notify) " +
                        "VALUES ('MÃE', '200', '-23.595066', '-46.568768', '0', 'OUT', 0, 0, 'YES');"
        );

        db.execSQL(
                "INSERT INTO messages (msg, location, direction) VALUES ('To chegando em casa, ja podem pedir a pizza o/', 'MÃE', 'IN'); "
        );

        db.execSQL(
                "INSERT INTO messages (msg, location, direction) VALUES ('Tchaaaaaaaaaaau!', 'MÃE', 'OUT');"
        );

        db.execSQL(
                "UPDATE locations set msg_in = 1 WHERE name = 'MÃE';"
        );

        db.execSQL(
                "UPDATE locations set msg_out = 2 WHERE name = 'MÃE';"
        );

        db.execSQL(
                "INSERT INTO cellulars (name, number, notify) VALUES ('MÃE', '998150010', 'YES');"
        );

        db.execSQL(
                "INSERT INTO cellulars (name, number, notify) VALUES ('TIA', '971780710', 'YES');"
        );

        db.execSQL(
                "INSERT INTO cellulars_locations (idCellular, idLocation) VALUES (1, 1);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }


    /*MESSAGES INSERT UPDATE DELETE*/
    public boolean insertMessage(String msg, String location, String direction) {
        Boolean result;

        try{
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("msg", msg);
            contentValues.put("location", location);
            contentValues.put("direction", direction);
            try {
                result = (db.insert(MESSAGE_TABLE_NAME, null, contentValues) != -1) ? true : false;
            }
            catch (SQLiteConstraintException e){
                Log.d("SQLiteConstraintException", e.getMessage());
                return false;
            }
        } catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        } catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        } finally {
            if(db.isOpen())
                db.close();
        }
        return result;
    }

    public boolean updateMessage(Integer id, String msg, String location, String direction) {
        try{
            db = this.getWritableDatabase();
            db.execSQL("UPDATE messages SET msg = '"+msg+"', location = '"+location+"', direction = '"+direction+"' WHERE location = '"+location+"' AND direction = '"+direction+"'");
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen());
                db.close();
        }
        return true;
    }

    public Integer deleteMessage(String msg) {
        int result;
        try {
            db = this.getWritableDatabase();
            result = db.delete("messages",
                    "msg = ? ",
                    new String[]{msg});
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return result;
    }

    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> array_list = new ArrayList<Message>();
        Message msg;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from messages", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                msg = new Message(
                        Integer.parseInt(res.getString(res.getColumnIndex(MESSAGE_COLUMN_ID))),
                        res.getString(res.getColumnIndex(MESSAGE_COLUMN_MSG)),
                        res.getString(res.getColumnIndex(MESSAGE_COLUMN_LOCATION)),
                        res.getString(res.getColumnIndex(MESSAGE_COLUMN_DIRECTION)));
                array_list.add(msg);
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return array_list;
    }

    public List<String> getAllMessagesName() {
        List<String> list = new ArrayList<String>();
        Message msg;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from messages", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                list.add(res.getString(res.getColumnIndex(MESSAGE_COLUMN_LOCATION)) + " " + res.getString(res.getColumnIndex(MESSAGE_COLUMN_DIRECTION)).toUpperCase());
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return list;
    }

    public String getMessageByLocation(String location, String direction) {
        String msg = "";
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select " + MESSAGE_COLUMN_MSG + " from messages where location = '"+location+"' and direction = '"+direction+"';", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                msg = res.getString(res.getColumnIndex(MESSAGE_COLUMN_MSG));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return "";
        }
        finally {
            if(db.isOpen())
                db.close();
        }
        return msg;
    }

    public Message getMessageById(int id) {
        Message message = new Message();
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from messages where id = ? ", new String[]{ Integer.toString(id) });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                message.setId(Integer.parseInt(res.getString(res.getColumnIndex(MESSAGE_COLUMN_ID))));
                message.setMsg(res.getString(res.getColumnIndex(MESSAGE_COLUMN_MSG)));
                message.setLocation(res.getString(res.getColumnIndex(MESSAGE_COLUMN_LOCATION)));
                message.setDirection(res.getString(res.getColumnIndex(MESSAGE_COLUMN_DIRECTION)));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return message;
    }

    public List<String> getMessagesName(String locationName) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from messages where location = ? ", new String[]{ locationName });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(MESSAGE_COLUMN_LOCATION)) + " - " + res.getString(res.getColumnIndex(MESSAGE_COLUMN_DIRECTION)));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if(db.isOpen())
                db.close();
        }
        return array_list;
    }

    /*LOCATIONS INSERT UPDATE DELETE*/
    public boolean insertLocation(String name, String radius, String latitude, String longitude, String altitude, String notify) {
        String query = "INSERT INTO locations (" +
                "  name, radius, latitude, longitude, altitude, last_message_send, msg_in, msg_out, notify\n" +
                ") " +
                "VALUES (" +
                "  '"+name+"', '"+radius+"', '"+latitude+"', '"+longitude+"', '0', 'OUT', 0, 0, '"+notify+"'" +
                ");";


        try{
            db = this.getWritableDatabase();
            db.execSQL(query);
        } catch (SQLiteConstraintException e){
            return false;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }


        return (getLastLocationAdd() > 0);
    }

    public boolean updateLocation(Integer id, String name, String radius, String latitude,
                                  String longitude, String altitude, int last_message_send,
                                  int msg_in, int msg_out, String notify) {
        try{
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(LOCATION_COLUMN_NAME, name.toUpperCase());
            contentValues.put(LOCATION_COLUMN_RADIUS, radius.toUpperCase());
            contentValues.put(LOCATION_COLUMN_LATITUDE, latitude.toUpperCase());
            contentValues.put(LOCATION_COLUMN_LONGITUDE, longitude.toUpperCase());
            contentValues.put(LOCATION_COLUMN_ALTITUDE, altitude.toUpperCase());
            contentValues.put(LOCATION_COLUMN_LAST_MSG, last_message_send);
            contentValues.put(LOCATION_COLUMN_MSG_IN, msg_in);
            contentValues.put(LOCATION_COLUMN_MSG_OUT, msg_out);
            contentValues.put(LOCATION_COLUMN_NOTIFY, notify.toUpperCase());
            db.update( LOCATION_TABLE_NAME, contentValues, "id = ? ", new String[]{ Integer.toString(id) } );
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return true;
    }

    public boolean updateLocationByName(String name, String radius, String latitude,
                                        String longitude, String notify){
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(LOCATION_COLUMN_NAME, name.toUpperCase());
            contentValues.put(LOCATION_COLUMN_RADIUS, radius.toUpperCase());
            contentValues.put(LOCATION_COLUMN_LATITUDE, latitude.toUpperCase());
            contentValues.put(LOCATION_COLUMN_LONGITUDE, longitude.toUpperCase());
            contentValues.put(LOCATION_COLUMN_NOTIFY, notify.toUpperCase());
            db.update( LOCATION_TABLE_NAME, contentValues, "name = ? ", new String[]{ name } );
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return true;
    }

    public Integer deleteLocation(Integer id) {
        int result;
        try {
            db = this.getWritableDatabase();
            result = db.delete(LOCATION_TABLE_NAME,
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return result;
    }

    public boolean deleteLocationByName(String name) {
        int result;
        try {
            db = this.getWritableDatabase();
            result = db.delete(LOCATION_TABLE_NAME,
                    "name = ? ",
                    new String[]{name});
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return (result != 0)?true:false;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> array_list = new ArrayList<Location>();
        Location loc;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from locations", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                loc = new Location(
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_ID))),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_NAME)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_RADIUS)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LATITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LONGITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_ALTITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LAST_MSG)),
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_MSG_IN))),
                        "",
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_MSG_OUT))),
                        "",
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_NOTIFY))
                );

                array_list.add(loc);
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return array_list;
    }

    public ArrayList<String> getAllLocationsName() {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from locations", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(LOCATION_COLUMN_NAME)));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return array_list;
    }

    public int getLocationIdByName(String name) {
        ArrayList<String> array_list = new ArrayList<String>();
        int id = -1;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from locations where name = '"+name+"';", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("id")));
                res.moveToNext();
            }
            res.close();
            if (!array_list.isEmpty()){
                id = Integer.parseInt(array_list.get(0).toString());
            }
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return -1;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return id;
    }

    public ArrayList<Location> getAllLocationsByName(String parameter) {
        ArrayList<Location> array_list = new ArrayList<Location>();
        Location loc;
        try {
            db = this.getReadableDatabase();
            Cursor res;
            res = db.rawQuery("select * from locations where name = ?", new String[]{ parameter });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                loc = new Location(
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_ID))),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_NAME)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_RADIUS)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LATITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LONGITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_ALTITUDE)),
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_LAST_MSG)),
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_MSG_IN))),
                        "",
                        Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_MSG_OUT))),
                        "",
                        res.getString(res.getColumnIndex(LOCATION_COLUMN_NOTIFY))
                );

                array_list.add(loc);
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return array_list;
    }

    public boolean updateLastMsgSendLocation(String location, String direction){
        try {
            db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(LOCATION_COLUMN_LAST_MSG, direction);

            db.update( LOCATION_TABLE_NAME, contentValues, "name = ? ", new String[]{ location } );

        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (SQLException e){
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen());
            db.close();
        }
        return true;
    }


    /*CELULARS INSERT UPDATE DELETE*/
    public boolean insertCellular(String name, String number, String notify) {
        Boolean result = false;
        long idCellular = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CELLULAR_COLUMN_NAME, name);
            contentValues.put("number", number);
            contentValues.put("notify", notify);
            try {
                idCellular = db.insert(CELLULAR_TABLE_NAME, null, contentValues);
            }
            catch (SQLiteConstraintException e){
                Log.d("SQLiteConstraintException", e.getMessage());
                return false;
            }
            if (idCellular != -1) {
                result = true;
            } else
                result = false;
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (Exception e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return result;
    }

    private int getLastCellularAdd() {
        int lastId = 0;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select max(id) as lastId from cellulars", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                lastId = Integer.parseInt(res.getString(res.getColumnIndex("lastId")));
                res.moveToNext();
            }
        }catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return 0;
        }catch (Exception e){
            Log.d("Exception", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return lastId;
    }
    private int getLastLocationAdd() {
        int lastId = 0;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select max(id) as lastId from locations", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                lastId = Integer.parseInt(res.getString(res.getColumnIndex("lastId")));
                res.moveToNext();
            }
        }catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return 0;
        }catch (Exception e){
            Log.d("Exception", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return lastId;
    }
    public boolean updateCellular(Integer id, String name, String num, String notify) {
        id = getIdCellularByName(name);
        try {
            if (id != 0) {
                db = this.getWritableDatabase();
                db.execSQL("UPDATE cellulars set name = '"+name+"' WHERE id = "+id+";");
                db.execSQL("UPDATE cellulars_locations SET number = '"+num+"', notify = '"+notify+"' WHERE idCellular = "+id+";");
                return true;
            } else {
                return false;
            }
        }
        catch (SQLiteConstraintException e){
            e.printStackTrace();
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
    }

    private Integer getIdCellularByName(String name) {
        String id = null;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id from cellulars where name = ? ", new String[] { name });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                id = res.getString(res.getColumnIndex("id"));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return (id != null) ? Integer.parseInt(id) : 0;
    }

    public Integer deleteCellular(Integer id) {
        int result;
        try {
            db = this.getWritableDatabase();
            result = db.delete(CELLULAR_TABLE_NAME,
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return result;
    }

    public ArrayList<Cellular> getAllCellulars() {
        ArrayList<Cellular> array_list = new ArrayList<Cellular>();
        Cellular cel;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id, name, number, notify from cellulars", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                cel = new Cellular(
                        Integer.parseInt(res.getString(res.getColumnIndex("id"))),
                        res.getString(res.getColumnIndex("name")),
                        res.getString(res.getColumnIndex("number")),
                        res.getString(res.getColumnIndex("notify"))
                );
                array_list.add(cel);
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return array_list;
    }

    public List<String> getAllCellularsName() {
        List<String> array_list = new ArrayList<String>();
        try {
            db = this.getReadableDatabase();
            String name;
            Cursor res = db.rawQuery("select name from cellulars", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                name = res.getString(res.getColumnIndex(CELLULAR_COLUMN_NAME));
                array_list.add(name);
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return array_list;
    }

    public ArrayList<String> getCellularByLocation(String location) {
        ArrayList<String> array_list = new ArrayList<String>();
        try{
            db = this.getReadableDatabase();
            String[] args = new String[] { location };
            Cursor res = db.rawQuery("select * from cellulars_locations " +
                    "where location = ?;", args);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(CELLULAR_LOCATION_COLUMN_NUMBER)));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return array_list;
    }

    public Cellular getCellularByName(String parameter) {
        Cellular cellular = new Cellular();
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select cellulars.id, cellulars.name, cellulars_locations.number, " +
                    "cellulars_locations.notify from cellulars join cellulars_locations WHERE cellulars.id = " +
                    "cellulars_locations.idCellular and cellulars.name = ?", new String[]{parameter});
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                cellular.setId(Integer.parseInt(res.getString(res.getColumnIndex("id"))));
                cellular.setName(res.getString(res.getColumnIndex("name")));
                cellular.setNumber(res.getString(res.getColumnIndex("number")));
                cellular.setIsNotify(res.getString(res.getColumnIndex("notify")));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return cellular;
    }

    public boolean deleteCellularByName(String name) throws Exception {
        String id = null;
        try {
            db = this.getReadableDatabase();

            Cursor res = db.rawQuery("SELECT id FROM cellulars WHERE name = ? ", new String[] { name });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                id = res.getString(res.getColumnIndex(CELLULAR_COLUMN_ID));
                res.moveToNext();
            }
            res.close();
            db = this.getWritableDatabase();
            db.execSQL("DELETE FROM cellulars_locations WHERE idCellular = "+id+";");
            db.execSQL("DELETE FROM cellulars WHERE id = " + id + ";");
        }
        catch (SQLiteConstraintException e){
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
        return true;
    }

    /*CELULARS_LOCATIONS INSERT UPDATE DELETE*/
    public boolean insertCellularsLocationsById(int idCellular, int idLocation) {
        Boolean result;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CELLULAR_LOCATION_COLUMN_ID_CELLULAR, idCellular);
            contentValues.put(CELLULAR_LOCATION_COLUMN_ID_CELLULAR, idLocation);
            result = (db.insert(CELLULAR_LOCATION_TABLE_NAME, null, contentValues) != -1) ? true : false;
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (Exception e){
            Log.d("Exception", e.getMessage());
            return false;
        }
        finally {
            db.close();
        }
        return result;
    }

    public int getResultIsNullInCellularsLocations(int idCellular, int idLocation) {
        int id = -1;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from cellulars_locations where " +
                    "idCellular = " + idCellular + " and idLocation = " + idLocation + ";", null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                id = Integer.parseInt(res.getString(res.getColumnIndex("idCellular")));
                res.moveToNext();
            }

            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return -1;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return id;
    }


    public boolean insertCellularsLocationsByIds(int idCellular, int idLocation) {
        Boolean result = false;
        try {

            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("idCellular", idCellular);
            contentValues.put("idLocation", idLocation);
            result = (db.insert(CELLULAR_LOCATION_TABLE_NAME, null, contentValues) != -1) ? true : false;

        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (Exception e){
            Log.d("Exception", e.getMessage());
            return false;
        }
        finally {
            db.close();
        }
        return result;
    }

    /*CELULARS_LOCATIONS INSERT UPDATE DELETE*/
    public boolean insertCellularsLocationsByNames(String celName, String locName) {
        Boolean result;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CELLULAR_LOCATION_COLUMN_ID_CELLULAR, getIdCellularByName(celName));
            contentValues.put(CELLULAR_LOCATION_COLUMN_ID_CELLULAR, getIdLocationByName(locName));
            result = (db.insert(CELLULAR_LOCATION_TABLE_NAME, null, contentValues) != -1) ? true : false;
        }
        catch (SQLiteConstraintException e){
            Log.d("SQLiteConstraintException", e.getMessage());
            return false;
        }
        catch (Exception e){
            Log.d("Exception", e.getMessage());
            return false;
        }
        finally {
            db.close();
        }
        return result;
    }

    private Integer getIdLocationByName(String name) {
        String id = null;
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id from locations where name = ? ", new String[] { name });
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                id = res.getString(res.getColumnIndex("id"));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return 0;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return (id != null) ? Integer.parseInt(id) : 0;
    }


    /*SETTINGS INSERT UPDATE DELETE*/
    public boolean insertSettins(String autoStart){
        Boolean result;
        try {
            db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("autoStart", autoStart);
            result = (db.insert("settings", null, contentValues) != -1) ? true : false;
        }
        catch (SQLiteConstraintException e){
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
        return result;
    }

    public boolean updateSettings(String autoStart) {
        try {
            db = this.getWritableDatabase();
            db.execSQL("UPDATE settings set autoStart = '"+autoStart+"';");
        }
        catch (SQLiteConstraintException e){
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
        return true;
    }

    public String getSettingsAutoStart(){
        String result = "";
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select autoStart from settings", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                result = res.getString(res.getColumnIndex("autoStart"));
                res.moveToNext();
            }
            res.close();
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
        finally {
            db.close();
        }
        return result;
    }

    public void LoadXml(ArrayList<Location> locationsFromXml, ArrayList<Message> messagesFromXml, ArrayList<Cellular> cellularsFromXml) {
        try {
            for (Location l : locationsFromXml){
                if (!getAllLocationsByName(l.getName()).get(0).equals(l.getName())) {
                    insertLocation(l.getName(), l.getRadius(), l.getLatitude(), l.getLongitude(), l.getAltitude(), l.getIsNotify());
                }
            }



            for (Message m : messagesFromXml){
                if (!this.MessageExists(m.getLocation(), m.getDirection())){
                    insertMessage(m.getMsg(), m.getLocation(), m.getDirection());
                }
            }


            for (Cellular c : cellularsFromXml){
                for (String in : getAllCellularsName()) {
                    if (!(c.getName() == in)) {
                        insertCellular(c.getName(), c.getNumber(), c.getIsNotify());
                    }
                }
            }
        }
        catch (Exception e){
            Log.d("Exception", e.getMessage());
        }
    }

    private boolean MessageExists(String location, String direction) {
        Message message = new Message();
        try {
            db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from messages where location='" + location + "' and direction = '" + direction + "';", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                message.setId(Integer.parseInt(res.getString(res.getColumnIndex("id"))));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return false;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return (message != null) ? true : false;
    }

    public ArrayList<String> getNumbersNotifyLocation(String locationName) {
        String query = "select number from cellulars inner join cellulars_locations as cl on cl. cl.idCellular = cellulars.id " +
                "inner join locations as l on l.id = cl.idLocation where l.name = '"+locationName+"';";
        ArrayList<String> list;
        try {
            db = this.getReadableDatabase();
            list = new ArrayList<>();
            Cursor res = db.rawQuery(query, null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                list.add(res.getString(res.getColumnIndex("number")));
                res.moveToNext();
            }
            res.close();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            db.close();
        }
        return list;
    }

    public ArrayList<String> getNumbersNotifyLocation() {
        String query = "select c.id, c.name, c.number, c.notify from cellulars  as c join cellulars_locations as cl " +
                "on c.id = cl.idCellular join locations as l " +
                "on cl.idLocation = l.id;";
        ArrayList<String> list;
        try {
            db = this.getReadableDatabase();
            list = new ArrayList<>();
            Cursor res = db.rawQuery(query, null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                list.add(res.getString(res.getColumnIndex("number")));
                res.moveToNext();
            }
            res.close();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            db.close();
        }
        return list;
    }

    public int getCellularIdByNumber(String number) {
        String query = "select id from cellulars where number = '"+number+"';";
        ArrayList<String> list;
        int id = -1;
        try {
            db = this.getReadableDatabase();
            list = new ArrayList<>();
            Cursor res = db.rawQuery(query, null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                list.add(res.getString(res.getColumnIndex("id")));
                res.moveToNext();
            }
            res.close();
            if (!list.isEmpty()) {
                id = Integer.parseInt(list.get(0));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        finally {
            db.close();
        }
        return id;
    }

    public ArrayList<String> getCellularByLocationByNotify(String location) {
        ArrayList<String> array_list = new ArrayList<String>();
        try{
            db = this.getReadableDatabase();
            String[] args = new String[] { location };
            Cursor res = db.rawQuery("select c.id, c.name, c.number, c.notify from cellulars  as c join cellulars_locations as cl " +
                    "on c.id = cl.idCellular join locations as l " +
                    "on cl.idLocation = l.id and l.name = '"+location+"' and c.notify='YES';", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("number")));
                res.moveToNext();
            }
            res.close();
        }
        catch (SQLException e) {
            Log.d("SQLException", e.getMessage());
            return null;
        }
        finally {
            if (db.isOpen())
                db.close();
        }
        return array_list;
    }

}