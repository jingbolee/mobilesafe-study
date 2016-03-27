package com.lijingbo.mobilesafe;

import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

import com.lijingbo.mobilesafe.bean.AppInfo;
import com.lijingbo.mobilesafe.bean.BlackNumberInfo;
import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;
import com.lijingbo.mobilesafe.engine.AppInfoProvider;

import java.util.List;
import java.util.Random;

/**
 * @FileName: com.lijingbo.mobilesafe.TestBlackNumberDao.java
 * @Author: Li Jingbo
 * @Date: 2016-01-28 11:23
 * @Version V1.0 <描述当前版本功能>
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private static final String TAG = "TestBlackNumberDao";
    private Context mContext;
    private BlackNumberDao dao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.mContext = getContext();
        dao = new BlackNumberDao(mContext);
    }

    public void testAdd() {

        Random random = new Random();
        for ( int i = 0; i < 200; i++ ) {
            long number = 13700000000l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testVersion() {
        int sdkInt = Build.VERSION.SDK_INT;
        assertEquals(16, sdkInt);
    }

    public void testDelete() {
        boolean delete = dao.delete("13700000000");
        assertEquals(true, delete);
    }

    public void testAppManagerProvider() {
        List< AppInfo > appInfos = AppInfoProvider.getAppInfo(getContext());
        for ( AppInfo appInfo : appInfos ) {
            System.out.println(appInfo.toString());
        }
    }

//    public  void testFindNumber(){
//        String mode = dao.findMode("13700000001");
//        System.out.println(mode);
//    }

    public void testChangeNumber() {
        if ( dao.changeNumberMode("13700000002", "3") ) {
            String mode = dao.findMode("13700000002");
            assertEquals("3", mode);
        }

    }

    public void testFindAll() {
        List< BlackNumberInfo > blackNumberInfos = dao.findAll();
        for ( BlackNumberInfo blackNumberInfo : blackNumberInfos ) {
            System.out.println(blackNumberInfo.getMode() + ":" + blackNumberInfo.getNumber());
        }
    }
}
