package com.doex.demo.regex;

import java.util.HashMap;
import java.util.List;

import android.test.AndroidTestCase;
import android.text.TextUtils;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class RegexTest extends AndroidTestCase {

    public void testPattern7() {
        String t = "当前余额余额为300.00元";
        String template = "当前余额kyye.";
        Pattern p = Pattern.compile("[当前余额|总账户]余额为(?<kyye>[0-9\\.]+)元");
        Matcher matcherParse = p.matcher(t);

        List<String> list = p.groupNames();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        HashMap<String, String> valueMap = new HashMap<String, String>();
        while (matcherParse.find()) {
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                try {
                    String value = null;
                    if ((value = matcherParse.group(key)) != null) {
                        valueMap.put(key, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        p = Pattern.compile(TextUtils.join("|", list));
        matcherParse = p.matcher(template);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (matcherParse.find()) {
            matcherParse.appendReplacement(sb, valueMap.get(list.get(i)));
            i++;
        }
        matcherParse.appendTail(sb);
        System.out.println(sb);
        System.out.println(valueMap.toString());
        System.out.println("----------------------------");
    }

}
