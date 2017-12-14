package com.jia.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */
public class SceneData {
    DataControl mDC;
    public SceneData(){
        mDC = DataControl.getInstance();
    }

    public void loadSceneList()
    {
        Cursor localCursor = mDC.mDB.sceneQueryByMasterCodeSequ(mDC.sMasterCode);
        int j = localCursor.getCount();
        mDC.mSceneList.clear();
        for (int i = 0; i < j; i++) {
            localCursor.moveToPosition(i);
            SceneDataInfo sceneDataInfo = new SceneDataInfo();
            sceneDataInfo.masterCode = localCursor.getString(localCursor.getColumnIndex("master_code"));
            sceneDataInfo.sceneName = localCursor.getString(localCursor.getColumnIndex("scene_name"));
            sceneDataInfo.sceneIndex =  localCursor.getInt(localCursor.getColumnIndex("scene_index"));
            sceneDataInfo.sceneImg = localCursor.getInt(localCursor.getColumnIndex("scene_img"));
            sceneDataInfo.sceneSequ = localCursor.getInt(localCursor.getColumnIndex("scene_sequ"));
            //此处应该查询更新电器
            sceneDataInfo.sceneElectricInfos=mDC.mSceneElectricData.loadSceneElectricListBySceneIndex(sceneDataInfo.sceneIndex);
            mDC.mSceneList.add(sceneDataInfo);
        }
    }

    /**
     * 得到待添加的情景的sceneIndex，情景编号只能是0~9，删除一个情景后，其编号应该被重用
     * @return
     */
    public int getAddSceneIndex(){
        Cursor localCursor = mDC.mDB.sceneQueryByMasterCodeIndex(mDC.sMasterCode);
        //已有情景的个数，按照index曾序排列，
        int j = localCursor.getCount();

        int i;
        for(i=0;i<j;i++) {
            localCursor.moveToPosition(i);
            if (i != localCursor.getInt(localCursor.getColumnIndex("scene_index"))) {
                return i;
            }
        }
        return i;
    }

    /**
     * 得到待添加的情景的sceneSequ
     * @return
     */
    public int getAddSceneSequ(){
        return mDC.mSceneList.size();
    }

    public int addScene( String sceneName,int sceneImg, int sceneIndex, int sceneSequ){
        //将区域数据添加到m_AreaList中
        final SceneDataInfo sceneDataInfo = new SceneDataInfo();

        sceneDataInfo.masterCode = mDC.sMasterCode;
        sceneDataInfo.sceneName = sceneName;
        sceneDataInfo.sceneImg = sceneImg;
        sceneDataInfo.sceneSequ = sceneSequ;
        sceneDataInfo.sceneIndex = sceneIndex;
        mDC.mSceneList.add(sceneDataInfo);

        //将房间信息添加到本地数据库
        ContentValues contentValues = new ContentValues();
        contentValues.put("master_code", mDC.sMasterCode);
        contentValues.put("scene_name", sceneName);
        contentValues.put("scene_index", sceneDataInfo.sceneIndex);
        contentValues.put("scene_sequ", sceneDataInfo.sceneSequ);
        contentValues.put("scene_img", sceneDataInfo.sceneImg);
        int localresult = mDC.mDB.insertScene(contentValues);
        if(localresult != -1){
            return 1;
        }else {
            return -1;
        }
    }

    public void deleteScene(String masterCode,int sceneIndex, int sceneSequ){
        mDC.mSceneElectricData.deleteSceneElectric(masterCode,sceneIndex);
        mDC.mDB.deleteScene(masterCode, sceneIndex);
        updateSceneSequ(masterCode,sceneSequ);
    }

    public void updateSceneSequ(String masterCode, int sceneSequ) {
        mDC.mDB.updateSceneSequ(masterCode,sceneSequ);
    }

    public void loadSceneFromWs(ArrayList<SceneDataInfo> list) {
        mDC.mDB.deleteSceneMasterCode(mDC.sMasterCode);     //删除某一账户名下的所有区域
        ContentValues contentValues = new ContentValues();
        for(int i =0;i<list.size();i++) {
            contentValues.put("master_code",list.get(i).masterCode);
            contentValues.put("scene_name", list.get(i).sceneName);
            contentValues.put("scene_index", list.get(i).sceneIndex);
            contentValues.put("scene_sequ", list.get(i).sceneSequ);
            contentValues.put("scene_img", list.get(i).sceneImg);
            int flag = mDC.mDB.insertScene(contentValues);
            contentValues.clear();
        }
    }
    public void updateSceneNewname(String masterCode, int sceneIndex, String sceneName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("scene_name",sceneName);
        mDC.mDB.updateScene(masterCode, sceneIndex, contentValues);
    }



    public class SceneDataInfo{
        private String accountCode;
        private String masterCode;
        private String sceneName;
        private int sceneIndex;
        private int sceneSequ;
        private int sceneImg;
        private String buildTime;
        private List<SceneElectricData.SceneElectricInfo> sceneElectricInfos;
        public SceneDataInfo(){}
        public int getSceneSequ() {
            return sceneSequ;
        }
        public void setSceneSequ(int sceneSequ) {
            this.sceneSequ = sceneSequ;
        }
        public int getSceneImg() {
            return sceneImg;
        }
        public void setSceneImg(int sceneImg) {
            this.sceneImg = sceneImg;
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
        public String getSceneName() {
            return sceneName;
        }
        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }
        public int getSceneIndex() {
            return sceneIndex;
        }
        public void setSceneIndex(int sceneIndex) {
            this.sceneIndex = sceneIndex;
        }
        public String getBuildTime() {
            return buildTime;
        }
        public void setBuildTime(String buildTime) {
            this.buildTime = buildTime;
        }

        public List<SceneElectricData.SceneElectricInfo> getSceneElectricInfos() {
            return sceneElectricInfos;
        }

        public void setSceneElectricInfos(List<SceneElectricData.SceneElectricInfo> sceneElectricInfos) {
            this.sceneElectricInfos = sceneElectricInfos;
        }

        @Override
        public String toString() {
            return "accountCode=" + accountCode + ", masterCode=" + masterCode
                    + ", sceneName=" + sceneName + ", sceneIndex=" + sceneIndex + ", sceneSequ=" + sceneSequ + ", sceneImg="
                    + sceneImg + ", buildTime=" + buildTime + "]";
        }
    }
}
