package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class ElectricSharedData {
    private DataControl mDC;
    public ElectricSharedData() {
        mDC = DataControl.getInstance();
    }

    public List<ElectricSharedLoacl> loadSharedElectricByAccountCode(String accountCode){
        List<ElectricSharedLoacl> list = new ArrayList<>();
        Cursor cursor = mDC.mDB.querySharedElectricByAccountCode(accountCode);
        int count = cursor.getCount();
        for(int i = 0;i<count;i++) {
            cursor.moveToPosition(i);
            ElectricSharedLoacl electricSharedLoacl = new ElectricSharedLoacl();
            electricSharedLoacl.setMasterCode(cursor.getString(cursor.getColumnIndex("master_code")));
            electricSharedLoacl.setAccountCode(cursor.getString(cursor.getColumnIndex("account_code")));
            electricSharedLoacl.setElectricCode(cursor.getString(cursor.getColumnIndex("electric_code")));
            electricSharedLoacl.setElectricName(cursor.getString(cursor.getColumnIndex("electric_name")));
            electricSharedLoacl.setElectricType(cursor.getInt(cursor.getColumnIndex("electric_type")));
            electricSharedLoacl.setOrderInfo(cursor.getString(cursor.getColumnIndex("order_info")));
            electricSharedLoacl.setRoomIndex(cursor.getInt(cursor.getColumnIndex("room_index")));
            electricSharedLoacl.setElectricIndex(cursor.getInt(cursor.getColumnIndex("electric_index")));
            electricSharedLoacl.setIsShared(cursor.getInt(cursor.getColumnIndex("is_shared")));
            list.add(electricSharedLoacl);
        }
        return list;
    }

    public void updateLocalShared(String accountCode,String masterCode,int electricIndex,int isShared){
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_shared",isShared);
        mDC.mDB.updateSharedElectric(accountCode, masterCode, electricIndex, contentValues);
    }

    public void loadSharedElectricFromWs(ArrayList<ElectricSharedLoacl> list)
    {

        mDC.mDB.deleteSharedElectricByMasterCode(mDC.sMasterCode);     //删除某一主控所有分享给别的用户的所有电器
        ContentValues contentValues = new ContentValues();
        for(int i =0;i<list.size();i++) {
            //将电器数据添加到本地SQLite数据库中
            contentValues.put("account_code",list.get(i).accountCode);
            contentValues.put("master_code",list.get(i).masterCode);
            contentValues.put("electric_code", list.get(i).electricCode);
            contentValues.put("electric_index",list.get(i).electricIndex);
            contentValues.put("electric_type",list.get(i).electricType);
            contentValues.put("order_info",list.get(i).orderInfo);
            contentValues.put("room_index",list.get(i).roomIndex);
            contentValues.put("electric_name", list.get(i).electricName);
            contentValues.put("is_shared", list.get(i).isShared);
            mDC.mDB.insertSharedElectric(contentValues);
            contentValues.clear();
        }
    }

    public class ElectricSharedLoacl {
        private String masterCode;
        private String accountCode;
        private String electricCode;
        private int electricIndex;
        private int electricType;
        private String orderInfo;
        private String electricName;
        private int roomIndex;
        private int isShared;
        public String getMasterCode() {
            return masterCode;
        }
        public void setMasterCode(String masterCode) {
            this.masterCode = masterCode;
        }
        public String getAccountCode() {
            return accountCode;
        }
        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }
        public String getElectricCode() {
            return electricCode;
        }
        public void setElectricCode(String electricCode) {
            this.electricCode = electricCode;
        }
        public int getElectricIndex() {
            return electricIndex;
        }
        public void setElectricIndex(int electricIndex) {
            this.electricIndex = electricIndex;
        }
        public int getElectricType() {
            return electricType;
        }
        public void setElectricType(int electricType) {
            this.electricType = electricType;
        }
        public String getOrderInfo() {
            return orderInfo;
        }
        public void setOrderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
        }
        public String getElectricName() {
            return electricName;
        }
        public void setElectricName(String electricName) {
            this.electricName = electricName;
        }

        public int getRoomIndex() {
            return roomIndex;
        }
        public void setRoomIndex(int roomIndex) {
            this.roomIndex = roomIndex;
        }
        public int getIsShared() {
            return isShared;
        }
        public void setIsShared(int isShared) {
            this.isShared = isShared;
        }
        @Override
        public String toString() {
            return "ElectricSharedLoacl [masterCode=" + masterCode + ", accountCode=" + accountCode + ", electricCode="
                    + electricCode + ", electricIndex=" + electricIndex + ", electricType=" + electricType + ", orderInfo="
                    + orderInfo + ", electricName=" + electricName + ", roomIndex=" + roomIndex + ", isShared=" + isShared
                    + "]";
        }



    }

}
