package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.adapter.ContactsAdapter;
import com.lijingbo.mobilesafe.bean.ContactsBean;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity {

	private ListView lvContactsList;
	private List<ContactsBean> contactsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		contactsList = new ArrayList<>();
		getContactsData();
		lvContactsList = (ListView) findViewById(R.id.lv_contacts_list);
		lvContactsList.setAdapter(new ContactsAdapter(this, contactsList));
		lvContactsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactsBean contactsBean = contactsList.get(position);
				String phone = contactsBean.getPhone();
				Intent intent = new Intent(ContactsActivity.this,
						Setup3Activity.class);
				intent.putExtra("phone", phone);
//				System.out.println(intent.toString());
				setResult(RESULT_OK, intent);
				finish();

			}
		});
	}

	private void getContactsData() {
		//从raw_contacts表获取联系人的contactsid
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		//从data表获取姓名，电话等信息
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor contactsIdCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (contactsIdCursor != null) {
			while (contactsIdCursor.moveToNext()) {
				String contactId = contactsIdCursor.getString(contactsIdCursor
						.getColumnIndex("contact_id"));
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "mimetype", "data1" },
						"raw_contact_id=?", new String[] { contactId }, null);
				if (dataCursor != null) {
					ContactsBean contacts = new ContactsBean();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(dataCursor
								.getColumnIndex("data1"));
						String mimetypeId = dataCursor.getString(dataCursor
								.getColumnIndex("mimetype"));
						if (mimetypeId.equals("vnd.android.cursor.item/name")) {
							contacts.setName(data1);
						} else if (mimetypeId
								.equals("vnd.android.cursor.item/phone_v2")) {
							contacts.setPhone(data1);
						}
					}
					contactsList.add(contacts);
				}
				dataCursor.close();
			}
		}
		contactsIdCursor.close();
	}

}
