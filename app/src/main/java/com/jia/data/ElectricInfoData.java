package com.jia.data;


/**
 * Created by Jia on 2016/5/6.
 */

public class ElectricInfoData {
    //byte mAlarmZoneIndex = -1;
    String accountCode; //电器属于哪一个账户
    String masterCode;//电器属于哪一个主节点
    int roomIndex;//电器所属房间
    int electricIndex;//电器编号，第多少个电器
    String electricCode;//电器编码，即某一电器的唯一识别编号
    String electricName;//电器描述，电器名字
    int electricSequ;
    int electricType;//电器种类，在给定的电器列表中的种类编号
    int sceneIndex;     //情景控制开关控制那种情景模式
    char belong;
    String extras;
    String orderInfo;

    String electricState;
    String stateInfo;

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

    public int getRoomIndex() {
        return roomIndex;
    }

    public void setRoomIndex(int roomIndex) {
        this.roomIndex = roomIndex;
    }

    public int getElectricIndex() {
        return electricIndex;
    }

    public void setElectricIndex(int electricIndex) {
        this.electricIndex = electricIndex;
    }

    public String getElectricCode() {
        return electricCode;
    }

    public void setElectricCode(String electricCode) {
        this.electricCode = electricCode;
    }

    public String getElectricName() {
        return electricName;
    }

    public void setElectricName(String electricName) {
        this.electricName = electricName;
    }

    public int getElectricSequ() {
        return electricSequ;
    }

    public void setElectricSequ(int electricSequ) {
        this.electricSequ = electricSequ;
    }

    public int getElectricType() {
        return electricType;
    }

    public void setElectricType(int electricType) {
        this.electricType = electricType;
    }

    public char getBelong() {
        return belong;
    }

    public void setBelong(char belong) {
        this.belong = belong;
    }


    public void setExtra(String extras) {
        this.extras = extras;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getElectricState() {
        return electricState;
    }

    public void setElectricState(String electricState) {
        this.electricState = electricState;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getSceneIndex() {
        return sceneIndex;
    }

    public void setSceneIndex(int sceneIndex) {
        this.sceneIndex = sceneIndex;
    }

    @Override
    public String toString() {
        return "ElectricInfoData{" +
                "accountCode='" + accountCode + '\'' +
                ", masterCode='" + masterCode + '\'' +
                ", roomIndex=" + roomIndex +
                ", electricIndex=" + electricIndex +
                ", electricCode='" + electricCode + '\'' +
                ", electricName='" + electricName + '\'' +
                ", electricSequ=" + electricSequ +
                ", electricType=" + electricType +
                ", sceneIndex=" + sceneIndex +
                ", belong=" + belong +
                ", extras='" + extras + '\'' +
                ", orderInfo='" + orderInfo + '\'' +
                ", electricState='" + electricState + '\'' +
                ", stateInfo='" + stateInfo + '\'' +
                '}';
    }
}