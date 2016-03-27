package com.lijingbo.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * 通过打开复制到files目录下的address.db数据库，通过电话号码获取到该号码的归属地
 */
public class AddressDao {
	private static final String PATH = "data/data/com.lijingbo.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";
		// 打开数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		// 通过SQL语句查询，获取到手机号码的归属地
		// 11位手机号码归属地，使用正则表达式匹配，规则：^1[3-8]\d{9}$
		if (number.matches("^1[3-8]\\d{9}$")) {
			Cursor cursor = database
					.rawQuery(
							"select location  from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) }); // 通过输入手机号码的前6位，通过数据库查询得到归属地
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
		} else if (number.matches("^1\\d{2}$")) {

		} else if (number.matches("^1\\d{4}$")) {
			address = "客服电话";
		} else if (number.matches("^\\d{7}$")) {
			address = "本地电话";
		} else if (number.matches("^\\d{8}$")) {
			address = "本地电话";
		} else if (number.matches("^\\d{4}$")) {
			address = "模拟器";
		} else if (number.matches("^\\d+$")) {
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			default:
				//长途电话  比如01089891234、078956780987
				if (number.startsWith("0") && number.length() > 10) {
					Cursor rawQuery = database.rawQuery(
							"select location  from data2 where area=?",
							new String[] { number.substring(1, 4) });
					if (rawQuery.moveToNext()) {
						address = rawQuery.getString(0);
					} else {
						rawQuery.close();
						rawQuery = database.rawQuery(
								"select location  from data2 where area=?",
								new String[] { number.substring(1, 3) });
						if (rawQuery.moveToNext()) {
							address = rawQuery.getString(0);
						}
						rawQuery.close();
					}
				}
				break;
			}
		}

		return address;

	}
}
