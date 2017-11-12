package com.jia.data;

import android.content.ContentValues;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/11/1.
 */
public class AccountData {
    DataControl mDC;
    public AccountData(){
        mDC = DataControl.getInstance();
    }
    public int addAccount(String accountCode){
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_code", accountCode);
        return mDC.mDB.insertAccount(contentValues);

    }
    public void updateAccount(AccountDataInfo accountDataInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_name",accountDataInfo.getAccountName());
        contentValues.put("account_phone",accountDataInfo.getAccountPhone());
        contentValues.put("account_address",accountDataInfo.getAccountAddress());
        contentValues.put("account_email",accountDataInfo.getAccountEmail());
        contentValues.put("account_time",accountDataInfo.getAccountTime());
        contentValues.put("account_photo",accountDataInfo.getAccountPhoto());
        contentValues.put("user_time",accountDataInfo.getUserTime());
        contentValues.put("sign_time",accountDataInfo.getSignTime());
        contentValues.put("le_phone",accountDataInfo.getLePhone());
        contentValues.put("le_sign", accountDataInfo.getLeSign());
        mDC.mDB.updateAccount(contentValues, accountDataInfo.getAccountCode());
    }

    /**
     * 用于更新账号的更新时间
     * @param contentValues
     */
    public void updateAccount(ContentValues contentValues){
        mDC.mDB.updateAccount(contentValues, mDC.sAccountCode);
    }

    public void loadSharedAccountFromWs(List<AccountDataInfo> list){
        ContentValues contentValues = new ContentValues();
        for (AccountDataInfo accountDataInfo:list) {
            contentValues.put("account_code", accountDataInfo.getAccountCode());
            contentValues.put("account_name",accountDataInfo.getAccountName());
            contentValues.put("account_phone",accountDataInfo.getAccountPhone());
            contentValues.put("account_address",accountDataInfo.getAccountAddress());
            contentValues.put("account_email",accountDataInfo.getAccountEmail());
            contentValues.put("account_photo",accountDataInfo.getAccountPhoto());
            contentValues.put("le_phone",accountDataInfo.getLePhone());
            contentValues.put("le_sign", accountDataInfo.getLeSign());
            saveOrUpdate(contentValues);
            //mDC.mDB.insertAccount(contentValues);
            contentValues.clear();
        }
    }

    public void saveOrUpdate(ContentValues contentValues){
        if(isExist(contentValues.getAsString("account_code"))){
            mDC.mDB.updateAccount(contentValues, contentValues.getAsString("account_code"));
        }else {
            mDC.mDB.insertAccount(contentValues);
        }
    }

    private boolean isExist(String accountCode){
        return mDC.mDB.queryAccountByAccountCode(accountCode).getCount() > 0;
    }




    public class AccountDataInfo{
        private String accountCode;
        private String accountName;
        private String accountPhone;
        private String accountAddress;
        private String accountEmail;
        private byte[] accountPhoto;
        private String signTime;
        private String accountTime;
        private String userTime;
        private String lePhone;
        private int leSign;
        public AccountDataInfo() {
            super();
        }
        public String getAccountCode() {
            return accountCode;
        }
        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }
        public String getSignTime() {
            return signTime;
        }
        public void setSignTime(String signTime) {
            this.signTime = signTime;
        }

        public String getAccountTime() {
            return accountTime;
        }
        public void setAccountTime(String accountTime) {
            this.accountTime = accountTime;
        }

        public String getUserTime() {
            return userTime;
        }

        public void setUserTime(String userTime) {
            this.userTime = userTime;
        }

        public String getAccountName() {
            return accountName;
        }
        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
        public String getAccountPhone() {
            return accountPhone;
        }
        public void setAccountPhone(String accountPhone) {
            this.accountPhone = accountPhone;
        }
        public String getAccountAddress() {
            return accountAddress;
        }
        public void setAccountAddress(String accountAddress) {
            this.accountAddress = accountAddress;
        }

        public byte[] getAccountPhoto() {
            return accountPhoto;
        }

        public void setAccountPhoto(byte[] accountPhoto) {
            this.accountPhoto = accountPhoto;
        }

        public String getAccountEmail() {
            return accountEmail;
        }
        public void setAccountEmail(String accountEmail) {
            this.accountEmail = accountEmail;
        }

        public String getLePhone() {
            return lePhone;
        }

        public void setLePhone(String lePhone) {
            this.lePhone = lePhone;
        }

        public int getLeSign() {
            return leSign;
        }

        public void setLeSign(int leSign) {
            this.leSign = leSign;
        }

        @Override
        public String toString() {
            return "AccountDataInfo{" +
                    "accountCode='" + accountCode + '\'' +
                    ", accountName='" + accountName + '\'' +
                    ", accountPhone='" + accountPhone + '\'' +
                    ", accountAddress='" + accountAddress + '\'' +
                    ", accountEmail='" + accountEmail + '\'' +
                    ", accountPhoto=" + Arrays.toString(accountPhoto) +
                    ", signTime='" + signTime + '\'' +
                    ", accountTime='" + accountTime + '\'' +
                    ", userTime='" + userTime + '\'' +
                    ", lePhone='" + lePhone + '\'' +
                    ", leSign=" + leSign +
                    '}';
        }
    }
}
