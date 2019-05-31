/*
package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bt.elderbracelet.adapter.CallingAdapter;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.others.CallNumInfo;
import com.bt.elderbracelet.entity.others.EventPosition;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

*/
/**
 * 拨打最亲近的人的电话号码
 *//*

public class CallClosestPeopleActivity extends Activity implements View.OnClickListener {
    int title_right = R.string.delete;
    private TitleView titleView;
    private TextView tvdate;
    private ListView lv;
    private CallingAdapter adapter;
    private ArrayList<CallNumInfo> sosList;
    private ModelDao model;
    private Button btn_add_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sos);
        init();
        EventBus.getDefault().register(this);
    }

    private void init() {
        titleView = (TitleView) findViewById(R.id.titleview);
        lv = (ListView) findViewById(R.id.LVlist);
        tvdate = (TextView) findViewById(R.id.tvdate_title);
        tvdate.setTextColor(Color.rgb(248, 101, 113));
        btn_add_num = (Button) findViewById(R.id.btn_add_num);
        btn_add_num.setOnClickListener(this);
        titleView.setTitle(R.string.sos_title);
        titleView.setbg(R.drawable.heart_titlebg);
        //加载数据
        model = new ModelDao(getApplicationContext());
        addDate();
        if (null == sosList) {
            sosList = new ArrayList<CallNumInfo>();
        }
        adapter = new CallingAdapter(getApplicationContext(), sosList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sosList.get(position).getPhone_num()));
                startActivity(intent);
            }
        });
        titleView.setBack(R.drawable.steps_back, new onBackLister() {

            @Override
            public void onClick(View button) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        titleView.setText(title_right);
        titleView.right(new TitleView.onSetLister() {
            @Override
            public void onClick(View button) {
                if (title_right == R.string.delete) {
                    title_right = R.string.back;
                    titleView.setText(title_right);
                    if (null != sosList && sosList.size() > 0) {
                        for (int i = 0; i < sosList.size(); i++) {
                            sosList.get(i).setHave_cbox(true);
                            sosList.set(i, sosList.get(i));
                        }
                        adapter.changeData(sosList);
                    }

                } else {
                    title_right = R.string.delete;
                    titleView.setText(title_right);
                    if (null != sosList && sosList.size() > 0) {
                        for (int i = 0; i < sosList.size(); i++) {
                            sosList.get(i).setHave_cbox(false);
                            sosList.set(i, sosList.get(i));
                        }
                        adapter.changeData(sosList);
                    }
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Log.e("TAG", "测试联系人.................." + username + "  " + usernumber);

                if (null != sosList && sosList.size() > 0) {
                    for (int i = 0; i < sosList.size(); i++) {
                        if (sosList.get(i).getPhone_num().equals(usernumber)) {
                            MethodUtils.showToast(getApplicationContext(), "您已添加过此号码");
                            return;
                        }
                    }
                }
                CallNumInfo callNumInfo = new CallNumInfo();
                callNumInfo.setName(username);
                callNumInfo.setPhone_num(usernumber);
                sosList.add(callNumInfo);
                model.insertCallInfo(callNumInfo);
                adapter.changeData(sosList);
                MethodUtils.showToast(getApplicationContext(), "添加成功");
            }

        }
    }

    public void addDate() {
        sosList = model.SOSSearth();
        if (sosList.size() >= 30) {
            for (int i = 0; i < sosList.size() - 30; i++) {
                model.deleteSOSId(sosList.get(i).getId());
            }
            sosList = model.SOSSearth();
        }
    }

    public void onEventMainThread(EventPosition event) {

        if (null != sosList && sosList.size() >= (event.position + 1)) {
            model.deleteSOSId(sosList.get(event.position).getId());
            sosList.remove(event.position);
            adapter.changeData(sosList);
        }


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_num:
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
                if (title_right == R.string.back) {
                    title_right = R.string.delete;
                    titleView.setText(title_right);
                    if (null != sosList && sosList.size() > 0) {
                        for (int i = 0; i < sosList.size(); i++) {
                            sosList.get(i).setHave_cbox(false);
                            sosList.set(i, sosList.get(i));
                        }
                        adapter.changeData(sosList);
                    }
                }
                break;

        }
    }
}
*/
