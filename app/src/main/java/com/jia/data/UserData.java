package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 列名	数据类型	说明
 * user_name	varchar(30)	user_name：用户的唯一标识，主键
 * master_node	varchar(12)	"master_node：主节点的唯一标识，一个用户可以有多个主节点，
 *                           master_node与user_name存在一对多的关系，一个主节点可以被多个用户使用。"
 * user_password	varchar(30)	user_password：如果选择记住密码，则此处记录用户输入的密码，并非数据库的密码
 * user_ip	varchar(14)
 * user_port	integer
 * if_password	varchar(1)	if_password：默认否0，记住为1
 * login_time	varchar(14)	"login_time：新建用户时为0，标识该用户在本手机上是否是第一次使用
                            （0代表第一次使用，非0则不是），若是第一次使用，则必须在本地登录，并下载更新数据库。"
 * info_time	varchar(14)	info_time：该用户的数据的更新时间，作为登录时是否需要从数据库更新数据的依据

 *
 */
public class UserData
{
    private DataControl mDC;

    public UserData() {
        this.mDC = DataControl.getInstance();
    }

    /**
     * 将本地数据库中的用户表导入到用户list中
     */
    public void loadUserList()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor localCursor = mDC.mDB.queryUserByAccountCode(mDC.sAccountCode);
        int j = localCursor.getCount();
        mDC.mUserList.clear();
        while (localCursor.moveToNext()) {
            UserDataInfo localUserDataInfo = new UserDataInfo();
            localUserDataInfo.masterCode = localCursor.getString(localCursor.getColumnIndex("master_code"));
            localUserDataInfo.accountCode = localCursor.getString(localCursor.getColumnIndex("account_code"));
            localUserDataInfo.userName = localCursor.getString(localCursor.getColumnIndex("user_name"));
            localUserDataInfo.userIP= localCursor.getString(localCursor.getColumnIndex("user_ip"));
            localUserDataInfo.userPort = 8899;
            localUserDataInfo.isAdmin = localCursor.getInt(localCursor.getColumnIndex("is_admin"));
            localUserDataInfo.sceneTime = localCursor.getString(localCursor.getColumnIndex("scene_time"));
            localUserDataInfo.areaTime = localCursor.getString(localCursor.getColumnIndex("area_time"));
            localUserDataInfo.electricTime = localCursor.getString(localCursor.getColumnIndex("electric_time"));
            localUserDataInfo.sceneElectricTime = localCursor.getString(localCursor.getColumnIndex("scene_electric_time"));
            mDC.mUserList.add(localUserDataInfo);
        }
    }

    public void loadUserFromWs(List<UserDataInfo> userList){
        mDC.mDB.deleteUserByAccountCode(mDC.sAccountCode);
        if(userList.size() == 0){
            return;
        }
        ContentValues contentValues = new ContentValues();
        for(int i=0;i<userList.size();i++){
            contentValues.put("master_code",userList.get(i).masterCode);
            contentValues.put("account_code",userList.get(i).accountCode);
            contentValues.put("user_name",userList.get(i).userName);
            contentValues.put("user_ip",userList.get(i).userIP);
            contentValues.put("user_sequ", i);
            contentValues.put("is_admin", userList.get(i).isAdmin);
            mDC.mDB.insertUser(contentValues);
            contentValues.clear();
        }

    }
    public void updateUserData(ContentValues contentValues){
        mDC.mDB.updateUser(contentValues, mDC.sAccountCode, mDC.sMasterCode);
    }

    public int giveUpAdmin(String masterCode, String accountCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_admin",0);
        return mDC.mDB.updateUserIsAdmin(contentValues,masterCode,accountCode);
    }
    public int updateUserName(String accountCode, String masterCode, String userName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_name",userName);
        return mDC.mDB.updateUserName(contentValues,accountCode,masterCode);
    }

    public int getAdmin(String masterCode, String accountCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_admin",1);
        return mDC.mDB.updateUserIsAdmin(contentValues,masterCode,accountCode);
    }

    public void updateUserSequ(String masterCode){
        mDC.mDB.sortUser(mDC.sAccountCode,masterCode);
    }


    /**
     * 添加一个用户
     * @param userName
     * @param masterCode
     * @param userIp
     * @return -3：已有同名user,不能添加 -1：本地添加失败，远程不再添加   -2：本地添加成功，远程添加失败
     */
    public void addUser(final String userName, final String masterCode, final String userIp){
        Cursor cursor = mDC.mDB.queryUserByAccountAndMaster(mDC.sAccountCode,masterCode);
        //如果同一账号下已经存在该主节点用户
        if(cursor.getCount() > 0){
            return ;
        }
        cursor = mDC.mDB.queryUserByAccountCode(mDC.sAccountCode);
        //将用户数据添加到本地SQLite数据库中
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_code", mDC.sAccountCode);
        contentValues.put("master_code",masterCode);
        contentValues.put("user_name",userName);
        contentValues.put("user_ip",userIp);
        contentValues.put("user_sequ", cursor.getCount());
        contentValues.put("is_admin",1);
        int result = mDC.mDB.insertUser(contentValues);

        //若本地数据库添加成功，则添加到内存当中
        if(result != -1){
            //将用户数据添加到DataControl.m_AccountList中
            UserDataInfo localUserDataInfo = new UserDataInfo();
            //房间编号从一开始
            localUserDataInfo.userIP = userIp;
            localUserDataInfo.userName = userName;
            localUserDataInfo.masterCode = masterCode;
            mDC.mUserList.add(localUserDataInfo);
            //result = Integer.parseInt(mDC.mWS.addUser(mDC.sAccountCode,masterCode,userName,userIp));

        }
    }


    /**
     * 根据用户名更新用户的登录时间、是否记住密码并更新密码
     * @param userName
     * @param password
     * @param rememberPwd
     */
    public void updateAccountTimePwd(String userName,String password, boolean rememberPwd)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("login_time", format.format(new Date()));

        if (rememberPwd) {
            localContentValues.put("password",password);
            localContentValues.put("re_password", 1);
        } else {
            localContentValues.put("password","");
            localContentValues.put("if_password", 0);
        }
        mDC.mDB.updateAccount(userName, localContentValues);
       // mDC.mDB.getWritableDatabase().update("AccountTable", localContentValues, "user_name=?", new String[]{userName});
    }

    /**
     * 更新账户主节点信息
     * @param position
     * @param masterCode
     */
    public void updateAccountMasterNode(int position,String masterCode) {
        mDC.mUserList.get(position).setMasterCode(masterCode);     //更改用户列表中的主节点编号
        String userName =  mDC.mUserList.get(position).getAccountCode();

        ContentValues localContentValues = new ContentValues();
        localContentValues.put("master_code", masterCode);
        mDC.mDB.updateAccount(userName,localContentValues);
    }

    /**
     * 根据用户名删除账户
     * @param accountIndex
     */
    public void deleteUser(int accountIndex)
    {
        String  accountCode = mDC.mUserList.get(accountIndex).accountCode;
        String masterCode = mDC.mUserList.get(accountIndex).getMasterCode();
        mDC.mUserList.remove(accountIndex);
        mDC.mDB.deleteUser(accountCode,masterCode);
        //mDC.mDB.getWritableDatabase().delete("AccountTable", "user_name=?", new String[]{str});

    }



    //内部类，账户信息
    public class UserDataInfo
    {
        String accountCode;
        String masterCode;
        String userName;
        String userIP;
        int userPort;
        int isAdmin;
        String sceneTime;
        String areaTime;
        String electricTime;
        String sceneElectricTime;

        public String getElectricTime() {
            return electricTime;
        }

        public void setElectricTime(String electricTime) {
            this.electricTime = electricTime;
        }

        public String getSceneElectricTime() {
            return sceneElectricTime;
        }

        public void setSceneElectricTime(String sceneElectricTime) {
            this.sceneElectricTime = sceneElectricTime;
        }

        public String getAccountCode() {
            return accountCode;
        }

        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }

        public String getMasterCode() {
            return masterCode;
        }

        public void setMasterCode(String masterCode) {
            this.masterCode = masterCode;
        }

        public String getUserIP() {
            return userIP;
        }

        public void setUserIP(String userIP) {
            this.userIP = userIP;
        }

        public int getUserPort() {
            return userPort;
        }

        public void setUserPort(int userPort) {
            this.userPort = userPort;
        }

        public String getUserName() {
            return userName;
        }
        public int getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(int isAdmin) {
            this.isAdmin = isAdmin;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getSceneTime() {
            return sceneTime;
        }

        public void setSceneTime(String sceneTime) {
            this.sceneTime = sceneTime;
        }

        public String getAreaTime() {
            return areaTime;
        }

        public void setAreaTime(String areaTime) {
            this.areaTime = areaTime;
        }

        public UserDataInfo() {}
    }

}

