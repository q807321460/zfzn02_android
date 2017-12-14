package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ElectricData
{
    private DataControl mDC;


    public ElectricData() {
        mDC = DataControl.getInstance();
    }


    public ArrayList<ElectricInfoData> updateElectricByAreaIndex(int areaIndex) {
        Cursor localCursor = mDC.mDB.eleQueryByAccountCodeAndArea(mDC.sAccountCode,mDC.sMasterCode,areaIndex);
        int j = localCursor.getCount();
        ArrayList<ElectricInfoData> locallist = new ArrayList<>();
        for (int i = 0; i < j; i++) {
            localCursor.moveToPosition(i);
            ElectricInfoData localElectricInfoData = new ElectricInfoData();
            localElectricInfoData.accountCode = localCursor.getString(localCursor.getColumnIndex("account_code"));
            localElectricInfoData.masterCode = localCursor.getString(localCursor.getColumnIndex("master_code"));
            localElectricInfoData.electricCode = localCursor.getString(localCursor.getColumnIndex("electric_code"));
            localElectricInfoData.roomIndex = localCursor.getInt(localCursor.getColumnIndex("room_index"));
            localElectricInfoData.electricName = localCursor.getString(localCursor.getColumnIndex("electric_name"));
            localElectricInfoData.electricType = localCursor.getInt(localCursor.getColumnIndex("electric_type"));
            localElectricInfoData.electricIndex = localCursor.getInt(localCursor.getColumnIndex("electric_index"));
            localElectricInfoData.electricSequ = localCursor.getInt(localCursor.getColumnIndex("electric_sequ"));
            localElectricInfoData.sceneIndex = localCursor.getInt(localCursor.getColumnIndex("scene_index"));
            localElectricInfoData.extras = localCursor.getString(localCursor.getColumnIndex("extras"));
            localElectricInfoData.orderInfo = localCursor.getString(localCursor.getColumnIndex("order_info"));
            if(localElectricInfoData.electricCode !=null) {
                Cursor childCursor = mDC.mDB.queryChildNode(mDC.sMasterCode, localElectricInfoData.electricCode);
                if (childCursor.getCount() != 0 && childCursor.moveToNext()) {
                    localElectricInfoData.electricState = childCursor.getString(childCursor.getColumnIndex("electric_state"));
                    localElectricInfoData.stateInfo = childCursor.getString(childCursor.getColumnIndex("state_info"));
                }
            }

            String[] strings = {localElectricInfoData.electricState,localElectricInfoData.stateInfo};

            locallist.add(localElectricInfoData);
            mDC.mElectricList.add(localElectricInfoData);
            if(localElectricInfoData.getElectricCode().startsWith("0D")){
                mDC.mSensorList.add(localElectricInfoData);
            }
            if(localElectricInfoData.electricCode.startsWith("0A")){
                mDC.mSceneSwiftList.add(localElectricInfoData);
            }

            //将电器的编号和状态存到一个HasMap中
            mDC.mElectricState.put(localElectricInfoData.getElectricCode(),strings);
        }
        return locallist;
    }

    public void addElectric(int electricType,String electricCode, String electricName, int roomIndex,int areaSequ, String extras) {
        if(electricType == 1) {
            addElectric(electricType, electricCode, electricName, roomIndex, areaSequ, null, "00");
        }else if(electricType == 2){
            String[] orderInfos = extras.split("\\|");
            addElectric(electricType, electricCode, orderInfos[0], roomIndex, areaSequ, null, "01");
            addElectric(electricType, electricCode, orderInfos[1], roomIndex, areaSequ, null, "02");
        }else if(electricType == 3){
            String[] orderInfos = extras.split("\\|");
            addElectric(electricType, electricCode, orderInfos[0], roomIndex, areaSequ, null, "01");
            addElectric(electricType, electricCode, orderInfos[1], roomIndex, areaSequ, null, "02");
            addElectric(electricType, electricCode, orderInfos[2], roomIndex, areaSequ, null, "03");
        }else if(electricType == 4 || electricType == 10){
            String[] orderInfos = extras.split("\\|");
            addElectric(electricType, electricCode, orderInfos[0], roomIndex, areaSequ, null, "01");
            addElectric(electricType, electricCode, orderInfos[1], roomIndex, areaSequ, null, "02");
            addElectric(electricType, electricCode, orderInfos[2], roomIndex, areaSequ, null, "03");
            addElectric(electricType, electricCode, orderInfos[3], roomIndex, areaSequ, null, "04");
        }else {
                addElectric(electricType, electricCode, electricName, roomIndex, areaSequ, extras, "**");
            }

    }

    public void addElectric(ContentValues contentValues){
        mDC.mDB.insertElectric(contentValues);
    }

    /**
     * 添加电器
     * @param electricType
     * @param electricCode
     * @param electricName
     * @param areaSequ
     */
    public void addElectric(int electricType,String electricCode, String electricName, int roomIndex,int areaSequ, String extras,
                            String orderInfo) {

        /*
        * 先判断是否是子节点类电器。判断依据是是否有子节点编号
        * 判断电器是否要添加到子节点表中
        * 1.若子节点表中不存在，则添加   2.若子节点表中存在，则不添加
         */
        if(electricCode != null) {
            Cursor childCursor = mDC.mDB.queryChildNode(mDC.sMasterCode, electricCode);
            if (childCursor.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("master_code", mDC.sMasterCode);
                contentValues.put("electric_code", electricCode);
                contentValues.put("electric_state", "Z0");
                contentValues.put("state_info", "0000000000");

                mDC.mDB.insertChildNode(contentValues);
            }
        }

        int maxIndex=getMaxElectricIndex(mDC.sAccountCode,mDC.sMasterCode);


        int i = getElectricCountOfUserArea(roomIndex);    //得到某一房间区域的电器的数目
        ElectricInfoData localElectricInfoData = new ElectricInfoData();
        localElectricInfoData.accountCode = mDC.sAccountCode;
        localElectricInfoData.masterCode = mDC.sMasterCode;
        localElectricInfoData.roomIndex = roomIndex;
        localElectricInfoData.electricCode = electricCode;
        localElectricInfoData.electricIndex = maxIndex+1;
        localElectricInfoData.electricName = electricName;
        localElectricInfoData.electricType = electricType;
        localElectricInfoData.electricSequ = i;
        localElectricInfoData.sceneIndex = -1;
        localElectricInfoData.orderInfo = orderInfo;
        mDC.mElectricList.add(localElectricInfoData);
        //更新 DataControl.m_AreaList
        mDC.mAreaList.get(areaSequ).mElectricInfoDataList.add(localElectricInfoData);

        //将电器数据添加到本地SQLite数据库中
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_code",mDC.sAccountCode);
        contentValues.put("master_code",  mDC.sMasterCode);
        contentValues.put("room_index", roomIndex);
        contentValues.put("electric_index",maxIndex+1);
        contentValues.put("electric_name", electricName);
        contentValues.put("electric_sequ", i);
        contentValues.put("electric_code", electricCode);
        contentValues.put("electric_type", electricType);
        contentValues.put("scene_index", -1);
        contentValues.put("extras",extras);
        contentValues.put("order_info",orderInfo);
        mDC.mDB.insertElectric(contentValues);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String elecInfoTime = format.format(new Date());
        mDC.mWS.addElectric(mDC.sMasterCode,maxIndex+1,electricCode,roomIndex,electricName,i,electricType,extras,orderInfo);

        //每成功添加一个电器后，同时将该该电器所在区域的电器总数+1
//        Cursor cursor = mDC.mDB.getWritableDatabase().query("AreaListTable", null,
//                "user_name=? and index=?",new String[]{mDC.sUSerName,String.valueOf(areaNum)},null,null,null);
//        int amountElec = cursor.getInt(5);
//        ContentValues contentValues1 = new ContentValues();
//        contentValues1.put("amount",amountElec+1);
//        mDC.mDB.getWritableDatabase().update("AreaListTable",contentValues1,
//                "user_name=? and index=?",new String[]{mDC.sUSerName,String.valueOf(areaNum)});

    }

    public int getMaxElectricIndex(String accountCode, String masterCode) {
        Cursor localCursor = mDC.mDB.queryElectricByMasterCode(accountCode,masterCode);
        int j = localCursor.getCount();
        int maxIndex=-1;
        //得到已有电器的最大编号，
        for(int i=0;i<j;i++) {
            localCursor.moveToPosition(i);
            maxIndex = Math.max(maxIndex, localCursor.getInt(localCursor.getColumnIndex("electric_index")));
        }
        return maxIndex;
    }

    /**
     * 查询某用户user的某一区域房间的电器的个数
     * @param roomIndex
     * @return
     */
    public int getElectricCountOfUserArea(int roomIndex)
    {
        Cursor cursor = mDC.mDB.queryElectricCountOfUserArea(mDC.sAccountCode, mDC.sMasterCode, roomIndex);
        return cursor.getCount();
    }

    public void loadElectricFromWs(ArrayList<ElectricInfoData> list)
    {
        mDC.mDB.deleteElectricByAccount(mDC.sAccountCode,mDC.sMasterCode);     //删除某一账户名下的所有电器
        ContentValues contentValues = new ContentValues();
        for(int i =0;i<list.size();i++) {
            //将电器数据添加到本地SQLite数据库中
            contentValues.put("account_code",mDC.sAccountCode);
            contentValues.put("master_code",list.get(i).masterCode);
            contentValues.put("electric_code", list.get(i).electricCode);
            contentValues.put("room_index", list.get(i).roomIndex);
            contentValues.put("electric_index",list.get(i).electricIndex);
            contentValues.put("electric_name", list.get(i).electricName);
            contentValues.put("electric_type", list.get(i).electricType);
            contentValues.put("electric_sequ", list.get(i).electricSequ);
            contentValues.put("scene_index",list.get(i).getSceneIndex());
            contentValues.put("extras",list.get(i).extras);
            contentValues.put("order_info",list.get(i).orderInfo);
            Cursor childCursor = mDC.mDB.queryChildNode(mDC.sMasterCode,list.get(i).electricCode);
            //判断子节点表中是否存在该电器，若存在，则不添加，若不存在，添加
            if(childCursor.getCount() == 0){
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("master_code",mDC.sMasterCode);
                contentValues1.put("electric_code",list.get(i).electricCode);
                contentValues1.put("electric_state","Z0");
                contentValues1.put("state_info","0000000000");
                mDC.mDB.insertChildNode(contentValues1);
            }
            mDC.mDB.insertElectric(contentValues);
            contentValues.clear();
        }
    }

    public void updateElectricState(String electricCode, String electricState, String stateInfo){
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("electric_state", electricState);
        localContentValues.put("state_info", stateInfo);
        mDC.mDB.updateElectricState(mDC.sMasterCode,electricCode,localContentValues);
    }

    public int updateElectric(int electricIndex,String electricName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("electric_name",electricName);
        mDC.mDB.updateElectric(mDC.sMasterCode, electricIndex, contentValues);
        return 0;
    }

    public int updateElectricExtras(int electricIndex,String extras){
        ContentValues contentValues = new ContentValues();
        contentValues.put("extras",extras);
        mDC.mDB.updateElectric(mDC.sMasterCode, electricIndex,contentValues);
        return 0;
    }

    public int updateElectric(int electricIndex,String electricName, int sceneIndex){
        ContentValues contentValues = new ContentValues();
        contentValues.put("electric_name",electricName);
        contentValues.put("scene_index",sceneIndex);
        mDC.mDB.updateElectric(mDC.sMasterCode, electricIndex,contentValues);
        return 0;
    }
    public int updateElectric1(int electricIndex,String electricName, int sceneIndex,String extras){
        ContentValues contentValues = new ContentValues();
        contentValues.put("electric_name",electricName);
        contentValues.put("scene_index",sceneIndex);
        contentValues.put("extras",extras);
        mDC.mDB.updateElectric(mDC.sMasterCode, electricIndex,contentValues);
        return 0;
    }
    public void deleteElectric(String masterCode, int electricIndex, int electricSequ, int roomIndex){
        mDC.mDB.deleteElectric(masterCode,electricIndex);
        updateElectricSequ(masterCode, electricSequ, roomIndex);
    }

    public void updateElectricSequ(String masterCode, int electricSequ, int roomIndex){
        mDC.mDB.updateElectricSequ(masterCode,electricSequ, roomIndex);
    }
    public void updateElectricSequ1(String masterCode,int electricIndex, int roomIndex,int oldElectricSequ, int newElectricSequ){
        mDC.mDB.updateElectricSequ1(masterCode,electricIndex, roomIndex,oldElectricSequ,newElectricSequ);
    }
    public void moveRoomIndex(String masterCode,int electricIndex,int newroomid){
        ContentValues contentValues = new ContentValues();
        contentValues.put("room_index",newroomid);
        mDC.mDB.updateElectric(masterCode,electricIndex, contentValues);
    }
    public void moveElectricSequ(String masterCode,int electricIndex,int maxElectricSequ){
        ContentValues contentValues = new ContentValues();
        contentValues.put("electric_sequ",maxElectricSequ);
        mDC.mDB.updateElectric(masterCode,electricIndex, contentValues);
    }

    //整型转换为字符型
    public static byte[] int2byte(int res)
    {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) ((res >> 24) & 0xff);
        return targets;
    }


}
