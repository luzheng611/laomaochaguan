package com.laomachaguan.Utils;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.laomachaguan.SideListview.CharacterParser;

import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by Administrator on 2017/5/12.
 */

public class JXl {

    public interface ExcelLoadedListener {
        void onDataLoaded(ArrayList<CountryModel> list);
    }

    public JXl(ExcelLoadedListener listener) {
        super();
        loader=new ExcelDataLoader(listener);
    }

    public ExcelDataLoader loader;
    private static final String TAG = "JXl";
    public static class CountryModel{
        private String chinaName;
        private String areaNumber;
        private String itemType;
        private String pinYin;

        public String getPinYin() {
            return pinYin;
        }

        public void setPinYin(String pinYin) {
            this.pinYin = pinYin;
        }

        public String getChinaName() {
            return chinaName;
        }

        public void setChinaName(String chinaName) {
            this.chinaName = chinaName;
        }

        public String getAreaNumber() {
            return areaNumber;
        }

        public void setAreaNumber(String areaNumber) {
            this.areaNumber = areaNumber;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        @Override
        public String toString() {
            return "国家名："+chinaName+"   代码： "+areaNumber+"  大写首字母： "+pinYin;
        }
    }
    /**
     * 获取 excel 表格中的数据,不能在主线程中调用
     *
     * @param xlsName excel 表格的名称
     * @param index   第几张表格中的数据
     */
    private static ArrayList<CountryModel> getXlsData(String xlsName, int index) {
        ArrayList<CountryModel> countryList = new ArrayList<CountryModel>();
        AssetManager assetManager = mApplication.getInstance().getAssets();

        try {
            Workbook workbook = Workbook.getWorkbook(assetManager.open(xlsName));
            Sheet sheet = workbook.getSheet(index);

            int sheetNum = workbook.getNumberOfSheets();
            int sheetRows = sheet.getRows();
            int sheetColumns = sheet.getColumns();

            Log.e(TAG, "the num of sheets is " + sheetNum);
            Log.e(TAG, "the name of sheet is  " + sheet.getName());
            Log.e(TAG, "total rows is 行=" + sheetRows);
            Log.e(TAG, "total cols is 列=" + sheetColumns);
            CountryModel c=new CountryModel();
            c.setChinaName("中国大陆");
            c.setAreaNumber("86");
            c.setPinYin("@");
            countryList.add(c);
            for (int i = 0; i < sheetRows; i++) {
                if(i==0){
                    continue;
                }
                CountryModel countryModel = new CountryModel();
                countryModel.setChinaName(sheet.getCell(1,i).getContents());
                countryModel.setAreaNumber(sheet.getCell(3, i).getContents());
                if(sheet.getCell(1,i).getContents().contains("中国")){
                    countryModel.setPinYin("@");
                }else{
                    countryModel.setPinYin(CharacterParser.getInstance().getSelling(sheet.getCell(1,i).getContents()).toUpperCase().substring(0,1));
                }
                countryList.add(countryModel);
                LogUtil.e(countryModel.toString());
            }

            workbook.close();

        } catch (Exception e) {
            Log.e(TAG, "read error=" + e, e);
        }

        return countryList;
    }


    //在异步方法中 调用
    public static class ExcelDataLoader extends AsyncTask<String, Void, ArrayList<CountryModel>> {
        private ExcelLoadedListener listener;
        public ExcelDataLoader(ExcelLoadedListener listener) {
            super();
            this.listener=listener;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<CountryModel> doInBackground(String... params) {
            return getXlsData(params[0], 0);
        }

        @Override
        protected void onPostExecute(ArrayList<CountryModel> countryModels) {


            if(countryModels != null && countryModels.size()>0){
                //存在数据
//                sortByName(countryModels);
//                setupData(countryModels);
//                LogUtil.e("最终数据："+countryModels);

                listener.onDataLoaded(countryModels);
            }else {
                //加载失败


            }

        }
    }
}
