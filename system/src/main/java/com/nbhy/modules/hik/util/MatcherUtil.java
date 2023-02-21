package com.nbhy.modules.hik.util;

import cn.hutool.core.bean.BeanUtil;
import com.nbhy.modules.hik.constant.HikDeptConstant;
import com.nbhy.modules.hik.constant.HikPersonConstant;
import com.nbhy.modules.hik.domain.dto.HikUser;
import com.nbhy.modules.hik.domain.entity.HikPerson;
import com.nbhy.modules.hik.exception.HikException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class
MatcherUtil {

    /**
     *新增海康人员的用户名的正则
     * @param personName 海康人员名称
     */
    public static boolean getpersonNameIsMatcher(String personName) {
        String[] ss = new String[]{"'","/","\\","/",":","*","?","\"","<",">"};
        boolean isMatch = false;
        for (String s : ss) {
            if(personName.indexOf(s) >= 0){
                isMatch = true;
                break;
            }
        }
//        Pattern p= Pattern.compile("[/\\?*:‘\"<>]{1,32}");
//        String str = "/\\?*:‘\"<>";
//        Matcher m = p.matcher(str);
//        isMatch = m.matches();
//        System.out.println(m.matches());
//        Pattern p= Pattern.compile("[’/\\:*?\"<>]{1,35}");
//        Matcher m = p.matcher(personName);
//        System.out.println(m.matches());
        return isMatch;
    }

    public static void main(String[] args) {
        String s = "？'";
        boolean b = MatcherUtil.getpersonNameIsMatcher(s);
//        System.out.println(b);
    }
}
