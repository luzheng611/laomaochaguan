package com.laomachaguan.Utils;

import android.text.TextUtils;

import com.laomachaguan.SideListview.CharacterParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/1/12.
 */
public class LocalGroupSearch {

    /**
     * 按群号-群名拼音搜索
     *
     * @param str
     */
    public static ArrayList<HashMap<String,String>> searchGroup(CharSequence str,
                                                 ArrayList<HashMap<String ,String  >> allContacts) {
        ArrayList<HashMap<String,String>> groupList = new ArrayList<HashMap<String,String>>();
        // 如果搜索条件以0 1 +开头则按号码搜索
//        if (str.toString().startsWith("0") || str.toString().startsWith("1")
//                || str.toString().startsWith("+")) {
//            for (Group group : allContacts) {
//                if (getGroupName(group) != null
//                        && group.getSecond_name() != null) {
//                    if ((group.getRoomid() + "").contains(str)
//                            || group.getNaturalName().contains(str)) {
//                        groupList.add(group);
//                    }
//                }
//            }
//            return groupList;
//        }// TODO: 2017/1/12 暂时不用数字搜索
        CharacterParser finder = CharacterParser.getInstance();

        String result = "";
        for (HashMap<String ,String > map : allContacts) {
            // 先将输入的字符串转换为拼音
            finder.setResource(str.toString());
            result = finder.getSpelling();
            if (contains(map, result)) {
                groupList.add(map);
            } else if ((map.get("pet_name")).contains(str)) {
                groupList.add(map);
            }
        }
        return groupList;
    }

    /**
     * 根据拼音搜索
     *
     *
     *
     *
     *
     *
     *            搜索条件是否大于6个字符
     * @return
     */
    private static boolean contains(HashMap<String  ,String > map, String search) {
        if (TextUtils.isEmpty(map.get("pet_name"))
                &&TextUtils.isEmpty(map.get("job"))) {
            return false;
        }

        boolean flag = false;

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            String firstLetters = FirstLetterUtil
                    .getFirstLetter(getGroupName(map));
            // 不区分大小写
            Pattern firstLetterMatcher = Pattern.compile(search,
                    Pattern.CASE_INSENSITIVE);
            flag = firstLetterMatcher.matcher(firstLetters).find();
        }

        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 全拼匹配
            CharacterParser finder = CharacterParser.getInstance();
            finder.setResource(getGroupName(map));
            // 不区分大小写
            Pattern pattern2 = Pattern
                    .compile(search, Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(finder.getSpelling());
            flag = matcher2.find();
        }

        return flag;
    }

    private static String getGroupName(HashMap<String  ,String  > map) {
        String strName = null;
        if (!TextUtils.isEmpty(map.get("pet_name"))) {
            strName = map.get("pet_name");
        }  else if(!TextUtils.isEmpty(map.get("job"))){
            strName = map.get("pet_name");
        }else {
            strName="";
        }

        return strName;
    }


    public static  class Group{
        private String pet_name;
        private String second_name;
        private String job;

        public String getJob() {
            return job;
        }

        public String getPet_name() {
            return pet_name;
        }

        public String getSecond_name() {
            return second_name;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public void setPet_name(String pet_name) {
            this.pet_name = pet_name;
        }

        public void setSecond_name(String second_name) {
            this.second_name = second_name;
        }
    }
}
