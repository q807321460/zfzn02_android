package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class SceneElectricData {
    private DataControl mDC;

    public int addSceneElectric(String masterCode, String electricCode, String electricOrder,
                                String accountCode,int sceneIndex, String orderInfo,int electricIndex,
                                String electricName, int roomIndex, int electricType){

        ContentValues contentValues = new ContentValues();
        contentValues.put("master_code", masterCode);
        contentValues.put("electric_code", electricCode);
        contentValues.put("electric_order", electricOrder);
        contentValues.put("scene_index", sceneIndex);
        contentValues.put("order_info", orderInfo);
        contentValues.put("electric_index", electricIndex);
        contentValues.put("electric_name", electricName);
        contentValues.put("room_index", roomIndex);
        contentValues.put("electric_type", electricType);
        return mDC.mDB.insertSceneElectric(contentValues);
    }
    public int updateSceneElectric(int electricIndex,String electricName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("electric_name",electricName);
        mDC.mDB.updateSceneElectric(mDC.sMasterCode, electricIndex, contentValues);
        return 0;
    }
    public int deleteSceneElectric(String masterCode, int electricIndex, int sceneIndex){
        return mDC.mDB.deleteSceneElectric(masterCode, electricIndex, sceneIndex);
    }

    public int deleteSceneElectric(String masterCode,int sceneIndex){
        return mDC.mDB.deleteSceneElectric(masterCode,sceneIndex);
    }


    public List<SceneElectricInfo> loadSceneElectricListBySceneIndex(int sceneIndex){
        Cursor localCursor = mDC.mDB.querySceneElectrics(mDC.sMasterCode,sceneIndex);
        int j = localCursor.getCount();
        ArrayList<SceneElectricInfo> locallist = new ArrayList<>();
        for (int i = 0; i < j; i++) {
            localCursor.moveToPosition(i);
            SceneElectricInfo sceneElectricInfo = new SceneElectricInfo();
            sceneElectricInfo.masterCode = localCursor.getString(localCursor.getColumnIndex("master_code"));
            sceneElectricInfo.electricCode = localCursor.getString(localCursor.getColumnIndex("electric_code"));
            sceneElectricInfo.electricOrder = localCursor.getString(localCursor.getColumnIndex("electric_order"));
            sceneElectricInfo.electricName = localCursor.getString(localCursor.getColumnIndex("electric_name"));
            sceneElectricInfo.electricType = localCursor.getInt(localCursor.getColumnIndex("electric_type"));
            sceneElectricInfo.electricIndex = localCursor.getInt(localCursor.getColumnIndex("electric_index"));
            sceneElectricInfo.roomIndex = localCursor.getInt(localCursor.getColumnIndex("room_index"));
            sceneElectricInfo.sceneIndex = localCursor.getInt(localCursor.getColumnIndex("scene_index"));
            sceneElectricInfo.orderInfo = localCursor.getString(localCursor.getColumnIndex("order_info"));
            locallist.add(sceneElectricInfo);
        }
        return locallist;
    }

    public SceneElectricData(){
        mDC = DataControl.getInstance();
    }

    public void loadSceneElectricFromWs(ArrayList<SceneElectricInfo> list) {
        mDC.mDB.deleteSceneElectricByMasterCode(mDC.sMasterCode);     //删除某一账户名下的所有区域
        ContentValues contentValues = new ContentValues();
        for(int i =0;i<list.size();i++) {
            contentValues.put("master_code",list.get(i).masterCode);
            contentValues.put("electric_index",list.get(i).electricIndex);
            contentValues.put("electric_name",list.get(i).electricName);
            contentValues.put("electric_code", list.get(i).electricCode);
            contentValues.put("electric_order", list.get(i).electricOrder);
            contentValues.put("room_index", list.get(i).roomIndex);
            contentValues.put("electric_type", list.get(i).electricType);
            contentValues.put("scene_index", list.get(i).sceneIndex);
            contentValues.put("order_info", list.get(i).orderInfo);
            mDC.mDB.insertSceneElectric(contentValues);
            contentValues.clear();
        }
    }

    public class SceneElectricInfo{
        private String masterCode;
        private int electricIndex;
        private String electricName;
        private String electricCode;
        private String electricOrder;
        private int roomIndex;
        private int electricType;
        private int sceneIndex;
        private String extraTime;
        private String orderInfo;

        public String getMasterCode() {
            return masterCode;
        }

        public void setMasterCode(String masterCode) {
            this.masterCode = masterCode;
        }

        public int getElectricIndex() {
            return electricIndex;
        }

        public void setElectricIndex(int electricIndex) {
            this.electricIndex = electricIndex;
        }

        public String getElectricName() {
            return electricName;
        }

        public void setElectricName(String electricName) {
            this.electricName = electricName;
        }

        public String getElectricCode() {
            return electricCode;
        }

        public void setElectricCode(String electricCode) {
            this.electricCode = electricCode;
        }

        public String getElectricOrder() {
            return electricOrder;
        }

        public void setElectricOrder(String electricOrder) {
            this.electricOrder = electricOrder;
        }

        public int getRoomIndex() {
            return roomIndex;
        }

        public void setRoomIndex(int roomIndex) {
            this.roomIndex = roomIndex;
        }

        public int getElectricType() {
            return electricType;
        }

        public void setElectricType(int electricType) {
            this.electricType = electricType;
        }

        public int getSceneIndex() {
            return sceneIndex;
        }

        public void setSceneIndex(int sceneIndex) {
            this.sceneIndex = sceneIndex;
        }

        public String getOrderInfo() {
            return orderInfo;
        }

        public void setOrderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
        }

        public String getExtraTime() {
            return extraTime;
        }

        public void setExtraTime(String extraTime) {
            this.extraTime = extraTime;
        }

        @Override
        public String toString() {
            return "SceneElectricInfo{" +
                    "masterCode='" + masterCode + '\'' +
                    ", electricIndex=" + electricIndex +
                    ", electricName='" + electricName + '\'' +
                    ", electricCode='" + electricCode + '\'' +
                    ", electricOrder='" + electricOrder + '\'' +
                    ", roomIndex=" + roomIndex +
                    ", electricType=" + electricType +
                    ", sceneIndex=" + sceneIndex +
                    ", extraTime='" + extraTime + '\'' +
                    ", orderInfo='" + orderInfo + '\'' +
                    '}';
        }
    }
}
