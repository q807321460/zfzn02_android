package com.jia.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String TABLE_ELECTRICS = "electrics";
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ROOMS = "user_rooms";
    private static final String TABLE_ELECTRIC_SHARED = "electric_shared";
    private static final String TABLE_CHILD = "child_nodes";
    private static final String TABLE_SCENE = "scenes";
    private static final String TABLE_SCENE_ELECTRIC = "scene_electrics";
    private static final String TABLE_AIR_LEARN = "air_learning";

    private SQLiteDatabase db;

    public DBHelper(Context paramContext)
    {
        super(paramContext, "zfzn_01", null, 1);
    }

    public void close()
    {
        if (this.db != null) {
            this.db.close();
        }
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
        paramSQLiteDatabase.execSQL("create table if not exists [accounts] " +
                "([account_code] varchar(11),"+
                "[account_name] varchar(30)," +
                "[account_phone] varchar(11),"+
                "[account_address] varchar(200)," +
                "[account_email] varchar(60) ," +
                "[account_photo] binary(102400) ," +
                "[sign_time] varchar(20), " +
                "[account_time] varchar(20), " +
                "[user_time] varchar(20), " +
                "[le_phone] varchar(20), " +
                "[le_sign] integer , " +
                "primary key(account_code));");

        paramSQLiteDatabase.execSQL("create table if not exists [users] " +
                "([account_code] varchar(11),"+
                "[master_code] varchar(50)," +
                "[user_name] varchar(30),"+
                "[user_ip] varchar(30) not null," +
                "[user_sequ] integer,"+
                "[is_admin] integer ," +
                "[area_time] varchar(20) ," +
                "[scene_time] varchar(20), " +
                "[electric_time] varchar(20), " +
                "[scene_electric_time] varchar(20), " +
                "primary key(account_code,master_code));");

        paramSQLiteDatabase.execSQL(" create table  if not exists [user_rooms] " +
                "([master_code] varchar(16) not null,"+
                "[room_index] integer not null, " +
                "[room_name] varchar(50) not null," +
                "[room_img] integer not null," +
                "[room_sequ] integer," +
                "[amount] integer," +
                "primary key (master_code, room_index)); ");
        paramSQLiteDatabase.execSQL("create table if not exists [electrics] " +
                "([master_code] varchar(50) not null," +
                "[account_code] varchar(11) not null, " +
                "[room_index] integer not null," +
                "[electric_index] integer not null," +
                "[electric_name] varchar(50) not null," +
                "[electric_sequ] integer not null," +
                "[electric_code] varchar(20)," +
                "[electric_type] integer not null," +
                "[belong] char(1) default null," +
                "[extras] varchar(50) default null," +
                "[scene_index] integer," +
                "[order_info] varchar(2) default null," +
                "primary key (account_code,master_code, electric_index));");
        paramSQLiteDatabase.execSQL("create table if not exists [electric_shared] " +
                "([master_code] varchar(50) not null," +
                "[account_code] varchar(11) not null, " +
                "[electric_code] varchar(8)," +
                "[electric_index] integer not null," +
                "[electric_type] integer not null," +
                "[room_index] integer not null," +
                "[order_info] varchar(2) not null," +
                "[electric_name] varchar(50) not null," +
                "[is_shared] integer not null," +
                "primary key (account_code,master_code, electric_index));");
        paramSQLiteDatabase.execSQL("create table if not exists [child_nodes] " +
                "([master_code] varchar(50) not null," +
                "[electric_code] varchar(8) not null," +
                "[electric_state] varchar(4) not null," +
                "[state_info] varchar(4) default null," +
                "[change_time] varchar(20) default null," +
                "primary key (master_code, electric_code));");

        paramSQLiteDatabase.execSQL("create table if not exists [scenes] " +
                "([master_code] varchar(16)," +
                "[scene_name] varchar(30),"+
                "[scene_index] integer," +
                "[scene_sequ] integer," +
                "[scene_img] integer," +
                "[build_time] varchar(20) ," +
                "primary key(master_code, scene_index));");

        paramSQLiteDatabase.execSQL("create table if not exists [scene_electrics] " +
                "([master_code] varchar(16),"+
                "[electric_index] integer," +
                "[electric_name] varchar(30),"+
                "[electric_code] varchar(8)," +
                "[electric_order] varchar(4),"+
                "[order_info] varchar(10),"+
                "[room_index] integer," +
                "[electric_type] integer," +
                "[scene_index] integer," +
                "primary key(master_code,electric_code,electric_index,scene_index));");
        paramSQLiteDatabase.execSQL("create table if not exists [air_learning] " +
                "([master_code] varchar(16),"+
                "[electric_index] integer," +
                "[key_value] varchar(60),"+
                "[key_key] integer,"+
                "primary key(master_code,electric_index));");
        this.db = paramSQLiteDatabase;
    }


    /**
     * 插入一个子节点
     * @param contentValues
     */
    public void insertChildNode(ContentValues contentValues){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        long i = localSQLiteDatabase.insert(TABLE_CHILD,null,contentValues);
    }
    public void insertAirLearning(ContentValues contentValues){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        long i = localSQLiteDatabase.insert(TABLE_AIR_LEARN,null,contentValues);
    }

    public void updateChildNode(ContentValues contentValues,String masterCode, String electricCode){
        getWritableDatabase().update(TABLE_CHILD, contentValues, "master_code = ? and electric_code = ?",
                new String[]{masterCode,electricCode});
    }
    /**
     * 根据主节点编码和子节点编码来查询
     * @param masterCode
     * @param electricCode
     * @return
     */
    public Cursor queryChildNode(String masterCode, String electricCode){
        return getWritableDatabase().query(TABLE_CHILD, null,
                "master_code =? and electric_code =?", new String[]{masterCode,electricCode}, null, null, null);
    }
    public Cursor queryAirLearning(String masterCode,Integer key){
        return getWritableDatabase().query(TABLE_AIR_LEARN, null,
                "master_code =? and key=?", new String[]{masterCode, String.valueOf(key)}, null, null, null);
    }

    /**
     * 从Ws更新一个用户电器
     * @param contentValues
     */
    public void insertElectric(ContentValues contentValues)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        localSQLiteDatabase.insert(TABLE_ELECTRICS,null,contentValues);
        localSQLiteDatabase.close();
    }

    public void insertSharedElectric(ContentValues contentValues)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        localSQLiteDatabase.insert(TABLE_ELECTRIC_SHARED,null,contentValues);
        localSQLiteDatabase.close();
    }

    public void deleteSharedElectricByMasterCode(String masterCode){
        getWritableDatabase().delete(TABLE_ELECTRIC_SHARED, "master_code=?", new String[]{masterCode});
    }

    public Cursor querySharedElectricByAccountCode(String accountCode){
        return getWritableDatabase().query(TABLE_ELECTRIC_SHARED, null,
                "account_code =?", new String[]{accountCode}, null, null, null);
    }



    /**
     * 查询某用户的特定区域房间的电器信息
     * @param masterCode
     * @param roomIndex
     * @return
     */
    public Cursor queryElectricCountOfUserArea(String accountCode, String masterCode, int roomIndex)
    {
        return getWritableDatabase().query(TABLE_ELECTRICS, null,
                "account_code = ? and master_code=? and room_index =?", new String[]{accountCode,masterCode,String.valueOf(roomIndex)}, null, null, null);
    }



    /**
     * 删除某账户下某一主节点的电器
     * @param accountCode
     * @param masterCode
     */
    public void deleteElectricByAccount(String accountCode,String masterCode) {
        getWritableDatabase().delete(TABLE_ELECTRICS, "account_code = ? and master_code=?", new String[]{accountCode,masterCode});
    }
    public void deleteAreaMasterCode(String masterCode){
        getWritableDatabase().delete(TABLE_ROOMS, "master_code=?", new String[]{masterCode});
    }
    public void deleteSceneMasterCode(String masterCode){
        getWritableDatabase().delete(TABLE_SCENE, "master_code=?", new String[]{masterCode});
    }
    public void deleteSceneElectricByMasterCode(String masterCode){
        getWritableDatabase().delete(TABLE_SCENE_ELECTRIC, "master_code=?", new String[]{masterCode});
    }

    /**
     * 删除某一账户名下的某一区域
     * @param userName
     */
    public void deleteAreaByNameIndex(String userName,int index) {
        getWritableDatabase().delete(TABLE_ROOMS, "[user_name]=? and [room_index]=?", new String[]{userName,String.valueOf(index)});
    }

    public void updateAreaInfo(String userName,String roomName,int roomSequ) {
        System.out.println(String.format("update user_rooms set sequ_number = %d where user_name='%s' and area_name='%s';",
                new Object[]{roomSequ,userName,roomName}));
        getWritableDatabase().execSQL(String.format("update user_rooms set room_sequ = %d where user_name='%s' and room_name='%s';",
                new Object[]{roomSequ, userName, roomName}));
    }

    /**
     * android.database.sqlite.SQLiteConstraintException: columns account_code, master_code are not unique (code 19)
     * @param contentValues
     * @return
     */
    public int insertUser(ContentValues contentValues){
        return (int)getWritableDatabase().insert(TABLE_USERS,null,contentValues);
    }
    public int insertAccount(ContentValues contentValues){

        return (int)getWritableDatabase().insert(TABLE_ACCOUNTS,null,contentValues);
    }
    public int updateAccount(ContentValues contentValues, String accountCode){
        return getWritableDatabase().update(TABLE_ACCOUNTS,contentValues,"account_code = ?",new String[]{accountCode});
    }
    public int updateUser(ContentValues contentValues, String accountCode, String masterCode){
        return getWritableDatabase().update(TABLE_USERS,contentValues,"account_code = ? and master_code=?",new String[]{accountCode,masterCode});
    }

    public int updateUserIsAdmin(ContentValues contentValues,String masterCode, String accountCode){
        return getWritableDatabase().update(TABLE_USERS,contentValues,
                "[master_code] = ? and account_code = ?",new String[]{masterCode,accountCode});
    }
    public int updateUserName(ContentValues contentValues,String accountCode, String masterCode){
        return getWritableDatabase().update(TABLE_USERS,contentValues,
                "[master_code] = ? and account_code = ?",new String[]{masterCode,accountCode});
    }

    /**
     * 删除某一账户名下的全部user
     * @param accountCode
     */
    public void deleteUserByAccountCode(String accountCode){
        getWritableDatabase().delete(TABLE_USERS, "account_code=?", new String[]{accountCode});
    }

    public void deleteUser(String accountCode, String masterCode){
        getWritableDatabase().delete(TABLE_USERS, "account_code=? and master_code = ?", new String[]{accountCode,masterCode});
    }

    /**
     * 查询某一账户名下的全部user
     * @param accountCode
     * @return
     */
    public Cursor queryUserByAccountCode(String accountCode){
        return getWritableDatabase().query(TABLE_USERS, null, "account_code = ?", new String[]{accountCode}, null, null, "user_sequ asc");
    }

    public Cursor queryUserByAccountAndMaster(String accountCode,String masterCode){
        return getWritableDatabase().query(TABLE_USERS, null, "account_code = ? and master_code=?", new String[]{accountCode,masterCode}, null, null, null);
    }

    public void sortUser(String accountCode, String masterCode){
        String sql1 = "UPDATE users SET user_sequ = user_sequ+1 WHERE account_code = ? AND master_code != ?";
        String sql2 = "UPDATE users SET user_sequ = 0 WHERE account_code = ? AND master_code = ?";
        getWritableDatabase().execSQL(sql1,new String[]{accountCode,masterCode});
        getWritableDatabase().execSQL(sql2,new String[]{accountCode,masterCode});
    }

    public void deleteRoom(String masterCode,int roomIndex) {
        getWritableDatabase().delete(TABLE_ROOMS, "[master_code]=? and [room_index]=?",
                new String[]{masterCode, String.valueOf(roomIndex)});
    }

    public void updateRoom(String masterCode, int roomIndex, ContentValues contentValues){
        getWritableDatabase().update(TABLE_ROOMS, contentValues, "master_code=? AND room_index = ?",
                new String[]{masterCode, String.valueOf(roomIndex)});
    }

    public  void updateRoomSequ(String masterCode, int roomSequ){
        String sql = "UPDATE user_rooms SET room_sequ = room_sequ-1 WHERE master_code=? and room_sequ > ?";
        getWritableDatabase().execSQL(sql, new String[]{masterCode, String.valueOf(roomSequ)});
    }

    public void deleteElectric(String masterCode, int electricIndex){
        getWritableDatabase().delete(TABLE_ELECTRICS, "[master_code]=? and [electric_index]=?",
                new String[]{masterCode, String.valueOf(electricIndex)});
    }

    public  void updateElectricSequ(String masterCode, int electricSequ, int roomIndex){
        String sql = "UPDATE electrics SET electric_sequ = electric_sequ-1 WHERE master_code=? and electric_sequ > ? AND room_index=?";
        getWritableDatabase().execSQL(sql, new String[]{masterCode, String.valueOf(electricSequ), String.valueOf(roomIndex)});
    }
    public void updateElectricSequ1(String masterCode,int electricIndex, int roomIndex,int oldElectricSequ, int newElectricSequ){
        if(oldElectricSequ<newElectricSequ){
            String sql = "UPDATE electrics SET electric_sequ = electric_sequ-1 WHERE master_code=? and electric_sequ > ? and electric_sequ<=? and room_index=?";
            getWritableDatabase().execSQL(sql, new String[]{masterCode, String.valueOf(oldElectricSequ), String.valueOf(newElectricSequ), String.valueOf(roomIndex)});
        }else {
            String sql = "UPDATE electrics SET electric_sequ = electric_sequ+1 WHERE master_code=? and electric_sequ >= ? and electric_sequ<? and room_index=?";
            getWritableDatabase().execSQL(sql, new String[]{masterCode, String.valueOf(newElectricSequ), String.valueOf(oldElectricSequ), String.valueOf(roomIndex)});

        }
        String sql="UPDATE electrics SET electric_sequ=?  WHERE master_code=? AND room_index=? AND electric_index=?";
        getWritableDatabase().execSQL(sql,new String[]{ String.valueOf(newElectricSequ), masterCode, String.valueOf(roomIndex),String.valueOf(electricIndex)});



    }


    /**
     * 根据用户名更新修改用户表信息
     * @param userName
     * @param contentValues
     */
    public void updateAccount(String userName,ContentValues contentValues)
    {
        getWritableDatabase().update(TABLE_USERS, contentValues, "user_name=?", new String[]{userName});
    }

    /**
     * 修改电器的分发权限
     * @param accountCode
     * @param masterCode
     * @param electricIndex
     * @param contentValues
     */
    public void updateSharedElectric(String accountCode,String masterCode,int electricIndex,ContentValues contentValues){
        getWritableDatabase().update(TABLE_ELECTRIC_SHARED, contentValues,
                "account_code = ? and master_code = ? and electric_index = ?",
                new String[]{accountCode, masterCode, String.valueOf(electricIndex)});
    }

    public void updateElectricState(String masterCode, String electricCode, ContentValues contentValues){
        getWritableDatabase().update(TABLE_CHILD, contentValues, "master_code = ? and electric_code=?", new String[]{masterCode,electricCode});
    }

    public void updateElectric(String masterCode, int electricIndex, ContentValues contentValues){
        getWritableDatabase().update(TABLE_ELECTRICS, contentValues, "master_code = ? and electric_index=?", new String[]{masterCode,""+electricIndex});
    }


    public int insertArea(ContentValues contentValues){
        return (int)getWritableDatabase().insert(TABLE_ROOMS, null, contentValues);
    }

    public int insertScene(ContentValues contentValues){
        return (int)getWritableDatabase().insert(TABLE_SCENE, null, contentValues);
    }

    public int deleteScene(String masterCode, int sceneIndex) {
        return getWritableDatabase().delete(TABLE_SCENE, "master_code = ? AND scene_index = ?"
                , new String[]{masterCode, String.valueOf(sceneIndex)});
    }
    public void updateScene(String masterCode,int sceneIndex,ContentValues contentValues){
        getWritableDatabase().update(TABLE_SCENE, contentValues, "master_code=? AND scene_index = ?",
                new String[]{masterCode, String.valueOf(sceneIndex)});
    }

    public  void updateSceneSequ(String masterCode, int sceneSequ){
        String sql = "UPDATE scenes SET scene_sequ = scene_sequ-1 WHERE master_code=? and scene_sequ > ?";
        getWritableDatabase().execSQL(sql,new String[]{masterCode,String.valueOf(sceneSequ)});
    }

    public int insertSceneElectric(ContentValues contentValues){
        return (int)getWritableDatabase().insert(TABLE_SCENE_ELECTRIC, null, contentValues);
    }
    public void updateSceneElectric(String masterCode, int electricIndex, ContentValues contentValues){
        getWritableDatabase().update(TABLE_SCENE_ELECTRIC, contentValues, "master_code = ? and electric_index=?", new String[]{masterCode,""+electricIndex});

    }

    public int deleteSceneElectric(String masterCode, int electricIndex, int sceneIndex){
        return getWritableDatabase().delete(TABLE_SCENE_ELECTRIC, "master_code = ? AND electric_index = ? AND scene_index = ?"
                , new String[]{masterCode, String.valueOf(electricIndex), String.valueOf(sceneIndex)});
    }

    public int deleteSceneElectric(String masterCode, int sceneIndex){
        return getWritableDatabase().delete(TABLE_SCENE_ELECTRIC, "master_code = ? AND scene_index = ?"
                , new String[]{masterCode, String.valueOf(sceneIndex)});
    }




    /**
     * 查询某一主节点下的全部房间
     * @param masterCode
     * @return
     */
    public Cursor areaQueryByMasterCode(String masterCode)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_ROOMS, null, "[master_code]=?", new String[] { masterCode }, null, null, "room_sequ asc");
    }
    /**
     * 查询某一主节点下的全部情景模式，按照scene_index增序排列
     * @param masterCode
     * @return
     */
    public Cursor sceneQueryByMasterCodeIndex(String masterCode)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_SCENE, null, "[master_code]=?", new String[] { masterCode }, null, null, "scene_index asc");
    }

    /**
     * 查询某一主节点下的全部情景模式,按照secene_sequ增序排列
     * @param masterCode
     * @return
     */
    public Cursor sceneQueryByMasterCodeSequ(String masterCode)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_SCENE, null, "[master_code]=?", new String[] { masterCode }, null, null, "scene_sequ asc");
    }

    /**
     * 获得主控
     * @param accountCode
     * @return
     */
    public Cursor queryAccountByAccountCode(String accountCode){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_ACCOUNTS, null, "[account_code]=?", new String[] { accountCode }, null, null, null);
    }

    public Cursor areaQueryByRoomName(String masterCode, String roomName){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_ROOMS, null, "[master_code]=? and [room_name]=?", new String[] { masterCode, roomName }, null, null, "room_sequ asc");
    }

    public Cursor sceneQueryByRoomName(String masterCode, String sceneName){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_SCENE, null, "[master_code]=? and [scene_name]=?", new String[] { masterCode, sceneName }, null, null, "scene_sequ asc");
    }



    /**
     * 通过用户名和区域编号查询电器表，用于更新该用户的某一区域的电器列表
     * @param accountCode
     * @param roomIndex
     * @return
     */
    public Cursor eleQueryByAccountCodeAndArea(String accountCode,String masterCode,int roomIndex)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_ELECTRICS, null, "[account_code]=? and [master_code] = ? and [room_index]=?",
                new String[] { accountCode ,masterCode,roomIndex+""}, null, null, "electric_sequ");
    }

    public Cursor querySceneElectrics(String masterCode,int sceneIndex){
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_SCENE_ELECTRIC, null, "[master_code] = ? and [scene_index]=?",
                new String[] {masterCode,sceneIndex+""}, null, null, null);
    }


    /**
     * 获得某用户主节点下的全部电器
     * @param accountCode
     * @param masterCode
     * @return
     */
    public Cursor queryElectricByMasterCode(String accountCode,String masterCode)
    {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        return localSQLiteDatabase.query(TABLE_ELECTRICS, null, "[account_code] = ? and [master_code]=? ", new String[] { accountCode,masterCode }, null, null, null);
    }





    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}

}
