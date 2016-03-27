package com.lijingbo.mobilesafe;

import android.test.AndroidTestCase;

import com.lijingbo.mobilesafe.bean.TaskInfo;
import com.lijingbo.mobilesafe.engine.TaskInfoProvider;

import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.TestTaskInfoProvider.java
 * @Author: Li Jingbo
 * @Date: 2016-03-03 21:34
 * @Version V1.0 <描述当前版本功能>
 */
public class TestTaskInfoProvider extends AndroidTestCase {
    private static final String TAG = "TestTaskInfoProvider";

    public void testTaskInfoProvider() {
        List< TaskInfo > infos = TaskInfoProvider.getTaskInfoLists(getContext());
        for ( TaskInfo info : infos ) {
            System.out.println(info.toString());
        }
    }
}
