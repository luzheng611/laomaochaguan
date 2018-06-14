package com.laomachaguan.SideListview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.PhoneSMSManager;
import com.laomachaguan.Utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Contact extends AppCompatActivity implements View.OnClickListener{
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	
	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<SortModel> selectedList;
	private String sms;
	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;
	private ImageView back;
	private TextView fasong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
		setContentView(R.layout.sort);
		sms=getIntent().getStringExtra("sms");//�������Ķ�������
		initViews();
	}

	private void initViews() {
		//ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		back= (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		fasong= (TextView) findViewById(R.id.fasong);
		fasong.setOnClickListener(this);
		sideBar.setTextView(dialog);
		
		//�����Ҳഥ������
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//����ĸ�״γ��ֵ�λ��
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SortModel s=(SortModel)adapter.getItem(position);
				if(selectedList==null)selectedList=new ArrayList<SortModel>();
				if(s.isSelected()){
					s.setSelected(false);
					if(selectedList.contains(s))selectedList.remove(s);
					view.findViewById(R.id.checkbox).setSelected(false);
				}else{
					s.setSelected(true);
					if(!selectedList.contains(s))selectedList.add(s);
					view.findViewById(R.id.checkbox).setSelected(true);
				}

					fasong.setText("邀请"+(selectedList.size())+"人");


				//����Ҫ����adapter.getItem(position)����ȡ��ǰposition����Ӧ�Ķ���
//				PhoneUtils.sendSms(((SortModel)adapter.getItem(position)).getNumber(),sms);
//				Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getNumber()+"      "+((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
			}
		});
		//��ϵ������

		SourceDateList = filledData(PhoneSMSManager.queryContactPhoneNumber(this));
		
		// ����a-z��������Դ����
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//�������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	/**
	 * ΪListView�������
	 * @param mSortList
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> mSortList){
//		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<mSortList.size(); i++){
			SortModel sortModel = mSortList.get(i);
			//����ת����ƴ��
			String pinyin = characterParser.getSelling(mSortList.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
		}
		return mSortList;
		
	}
	
	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.back:
				finish();
				break;
			case R.id.fasong:
				if (Build.VERSION.SDK_INT >= 23) {

					//�ж���û�в���绰Ȩ��
					if (PermissionChecker.checkSelfPermission(Contact.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
							|| PermissionChecker.checkSelfPermission(Contact.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
						//���󲦴�绰Ȩ��
						ActivityCompat.requestPermissions(Contact.this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, 222);
						return;
					}

				}
				if(selectedList==null||selectedList.size()==0){
					Toast.makeText(this,"请选择联系人",Toast.LENGTH_SHORT).show();
					return;
				}
				String num="";
				for (SortModel s:selectedList){
					String number=s.getNumber();
					num+=(number+";");
				}
				Uri uri=Uri.parse("smsto:"+num);
				LogUtil.e(num.substring(0,num.length()-2));
				Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
				intent.putExtra("sms_body", sms);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
		}
	}
}
