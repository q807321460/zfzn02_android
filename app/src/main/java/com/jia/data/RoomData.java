package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

/**
 * AreaListTable--本地区域表

 列名	数据类型	说明
 user_name	varchar(30)	user_name：user_name与area_name存在一对多的关系
 area_name	varchar(30)	area_name：区域的描述
 index	integer	"index：房间编号，新增区域的编号为已有的最大编号加1；
 根据此编号与用户的电器建立一对多的映射关系"
 images	integer	images：区域图片ID，存储用户所选择的图片的编号
 sequ_number	integer	sequ_number:房间的序号，根据此编号进行房间的排序功能。
 amount	integer	amount：电器总数

 * Created by Jia on 2016/4/26.
 */
public class RoomData {
    private DataControl mDC;

    public RoomData() {
        this.mDC = DataControl.getInstance();
    }

    /**
     *
     * @param roomName
     * @param areaImageNum
     * @return -2:远程失败，-1：本地失败， 1：成功，2：已存在该房间
     */
    public int addArea(String roomName, int areaImageNum)
    {
        Cursor localCursor = mDC.mDB.areaQueryByMasterCode(mDC.sMasterCode);
        int j = localCursor.getCount();
        int maxIndex=-1;
        //得到已有房间的最大编号，
        for(int i=0;i<j;i++) {
            localCursor.moveToPosition(i);
            maxIndex = Math.max(maxIndex, localCursor.getInt(localCursor.getColumnIndex("room_index")));
        }

        //将区域数据添加到本地SQLite数据库中
        //如果该用户已经有一个相同的房间名字，不应该添加
        if (mDC.mDB.areaQueryByRoomName(roomName,mDC.sMasterCode).getCount() == 0) {

            //将区域数据添加到m_AreaList中
            final RoomDataInfo localAreaInfo = new RoomDataInfo();
            //新增方间编号为已有房间最大编号+1
            localAreaInfo.roomIndex = (byte)(maxIndex+1);
            localAreaInfo.roomSequ = (byte) mDC.mAreaList.size();
            localAreaInfo.roomName = roomName;
            localAreaInfo.roomImg = (byte)areaImageNum;
            String result = mDC.mWS.addUserRoom(mDC.sMasterCode, localAreaInfo.roomIndex,localAreaInfo.roomName,
                    localAreaInfo.roomSequ,localAreaInfo.roomImg);
            if(result.startsWith("1")){
                mDC.mAreaList.add(localAreaInfo);

                //将房间信息添加到本地数据库
                ContentValues contentValues = new ContentValues();
                contentValues.put("master_code", mDC.sMasterCode);
                contentValues.put("room_name", roomName);
                contentValues.put("room_sequ", localAreaInfo.roomSequ);
                contentValues.put("room_index", localAreaInfo.roomIndex);
                contentValues.put("room_img", Integer.valueOf(areaImageNum));
                int localresult = mDC.mDB.insertArea(contentValues);
                if(localresult != -1){
                    return 1;
                }else {
                    return -1;
                }
            }else{
                return -2;
            }
        } else {
            return 2;
        }
    }


    public void loadAreaList()
    {
//        int m = 0;
//        mDC.mDB.deleteAccountTable();
//        mDC.mDB.deleteAreaListTable();
        Cursor localCursor = mDC.mDB.areaQueryByMasterCode(mDC.sMasterCode);
        int j = localCursor.getCount();
        mDC.mAreaList.clear();
        mDC.mSensorList.clear();
        mDC.mElectricState.clear();
        mDC.mElectricList.clear();
        for (int i = 0; i < j; i++) {
            localCursor.moveToPosition(i);
            RoomDataInfo localRoomDataInfo = new RoomDataInfo();
            localRoomDataInfo.masterCode = localCursor.getString(localCursor.getColumnIndex("master_code"));
            localRoomDataInfo.roomName = localCursor.getString(localCursor.getColumnIndex("room_name"));
            localRoomDataInfo.roomIndex =  localCursor.getInt(localCursor.getColumnIndex("room_index"));
            localRoomDataInfo.roomImg = localCursor.getInt(localCursor.getColumnIndex("room_img"));
            localRoomDataInfo.roomSequ = localCursor.getInt(localCursor.getColumnIndex("room_sequ"));
            //此处应该查询更新电器
            localRoomDataInfo.mElectricInfoDataList=mDC.mElectricData.updateElectricByAreaIndex(localRoomDataInfo.roomIndex);
            mDC.mAreaList.add(localRoomDataInfo);
        }
    }

//    /**
//     * 从服务器端更新用户区域列表
//     * @param list：用户区域列表
//     */
//    public void loadUserFromWs(ArrayList<RoomDataInfo> list) {
//        mDC.mDB.deleteElectricByName(userName);     //删除某一账户名下的所有电器
//        mDC.mDB.deleteAreaByName(userName);     //删除某一账户名下的所有区域
//        ContentValues contentValues = new ContentValues();
//        for(int i =0;i<list.size();i++) {
//            contentValues.put("user_name",list.get(i).masterCode);
//            contentValues.put("room_name", list.get(i).roomName);
//            contentValues.put("room_sequ", list.get(i).roomSequ);
//            contentValues.put("room_index", list.get(i).roomIndex);
//            contentValues.put("room_img", list.get(i).roomImg);
//            //contentValues.put("amount",list.get(i).iAmount);
//            mDC.mDB.insertAreaNew(contentValues);
//            contentValues.clear();
//        }
//    }

    public void loadUserFromWs(ArrayList<RoomDataInfo> list) {
        mDC.mDB.deleteAreaMasterCode(mDC.sMasterCode);     //删除某一账户名下的所有区域
        ContentValues contentValues = new ContentValues();
        for(int i =0;i<list.size();i++) {
            contentValues.put("master_code",list.get(i).masterCode);
            contentValues.put("room_name", list.get(i).roomName);
            contentValues.put("room_sequ", list.get(i).roomSequ);
            contentValues.put("room_index", list.get(i).roomIndex);
            contentValues.put("room_img", list.get(i).roomImg);
            //contentValues.put("amount",list.get(i).iAmount);
            mDC.mDB.insertArea(contentValues);
            contentValues.clear();
        }
    }

    public void updateAreaSequ(){
        ContentValues contentValues = new ContentValues();
        int count = mDC.mAreaList.size();
        for (int i = 0; i < count; i++) {
            //contentValues.put("sequ_number", mDC.mAreaList.get(i).roomSequ);
            mDC.mDB.updateAreaInfo(mDC.sAccountCode,mDC.mAreaList.get(i).roomName,mDC.mAreaList.get(i).roomSequ);
            //contentValues.clear();
        }
    }

    public void deleteArea(String userName, int index) {
        mDC.mDB.deleteAreaByNameIndex(userName,index);
    }

    public int deleteRoom(String masterCode, int roomIndex, int roomSequ){
        mDC.mDB.deleteRoom(masterCode, roomIndex);
        updateRoomSequ(masterCode, roomSequ);
        return 0;
    }

    private void updateRoomSequ(String masterCode, int roomSequ){
        mDC.mDB.updateRoomSequ(masterCode, roomSequ);
    }

    public void updateRoomNameAndRoomImg(String masterCode, int roomIndex, String roomName, int roomImg){
        ContentValues contentValues = new ContentValues();
        contentValues.put("room_name",roomName);
        contentValues.put("room_img", roomImg);
        mDC.mDB.updateRoom(masterCode, roomIndex, contentValues);
    }

    public int getAreaSize()
    {
        return mDC.mAreaList.size();
    }
    public class RoomDataInfo {
        String masterCode;
        int roomIndex;//区域编号
        String roomName;//区域描述，暨区域名字
        int roomSequ;//区域序号
        int roomImg;//区域图片
        int iAmount;//区域存储电器数目
        //区域的电器的列表
        ArrayList<ElectricInfoData> mElectricInfoDataList;
        public RoomDataInfo() {
            mElectricInfoDataList = new ArrayList<>();
        }

        public String getMasterCode() {
            return masterCode;
        }

        public void setMasterCode(String masterCode) {
            this.masterCode = masterCode;
        }

        public int getRoomIndex() {
            return roomIndex;
        }

        public void setRoomIndex(int roomIndex) {
            this.roomIndex = roomIndex;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public int getRoomSequ() {
            return roomSequ;
        }

        public void setRoomSequ(int roomSequ) {
            this.roomSequ = roomSequ;
        }

        public int getRoomImg() {
            return roomImg;
        }

        public void setRoomImg(int roomImg) {
            this.roomImg = roomImg;
        }

        public int getiAmount() {
            return iAmount;
        }

        public void setiAmount(int iAmount) {
            this.iAmount = iAmount;
        }

        public ArrayList<ElectricInfoData> getmElectricInfoDataList() {
            return mElectricInfoDataList;
        }

        public void setmElectricInfoDataList(ArrayList<ElectricInfoData> mElectricInfoDataList) {
            this.mElectricInfoDataList = mElectricInfoDataList;
        }
    }
}
