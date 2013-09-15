
package com.doex.demo;

import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoexTest extends AndroidTestCase {

    public void testGet() {
        String orig2 = "";
        assertEquals("+15810403964|1\t", getNewArray(orig2, "+15810403964"));

        String orig1 = "15810403964|22\t+3345|4\t123|1\t";

        String newvalue = getNewArray(orig1, "1581040099");
        Log.i("hello", "newvalue:" + newvalue);
        assertEquals("15810403964|22\t+3345|4\t123|1\t1581040099|1\t", newvalue);

        String newValue2 = getNewArray(orig1, "15810403964");
        assertEquals("15810403964|23\t+3345|4\t123|1\t", newValue2);

        String newValue3 = getNewArray(orig1, "1581040");
        assertEquals("15810403964|22\t+3345|4\t123|1\t1581040|1\t", newValue3);

        String origs = "+8615810403964|22\t15810403964|1\t+3345|4\t123|1\t";
        String newValue4 = getNewArray(origs, "403964");
        assertEquals("+8615810403964|22\t15810403964|1\t+3345|4\t123|1\t403964|1\t", newValue4);

        newValue4 = getNewArray("+8615810403964|22\t15810403964|1\t+3345|4\t123|1\t", "15810403964");
        assertEquals("+8615810403964|22\t15810403964|2\t+3345|4\t123|1\t", newValue4);

        newValue4 = getNewArray("+8615810403964|22\t15810403964|1\t+3345|4\t123|1\t",
                "+8615810403964");
        assertEquals("+8615810403964|23\t15810403964|1\t+3345|4\t123|1\t", newValue4);

    }

    public static String getNewArray(String originValue, String value) {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(originValue)) {
            sb.append(value);
            sb.append("|");
            sb.append(1);
            sb.append("\t");
        } else {
            if (originValue.contains(value)) {
                String[] numberArr = originValue.split("\t");
                StringBuilder newSb = new StringBuilder();
                int length = numberArr.length;
                boolean isExist = false;
                for (int i = 0; i < length; i++) {
                    String numberInfo = numberArr[i];
                    String[] array = TextUtils.split(numberInfo, "\\|");
                    String number = array[0];
                    if (number.equals(value)) {
                        int count = Integer.parseInt(array[1]) + 1;
                        String newNumberInfo = value + "|" + count;
                        newSb.append(newNumberInfo);
                        isExist = true;
                    } else {
                        newSb.append(numberInfo);
                    }
                    newSb.append("\t");
                }
                if (!isExist) {
                    newSb.append(getNewArray("", value));
                }
                return newSb.toString();
            } else {
                sb.append(originValue);
                sb.append(getNewArray("", value));
            }
        }
        return sb.toString();
    }

    public void testString() {
        String progress = getContext().getString(R.string.progress, 10);
        Log.i("hello", progress);
    }

    public void testSplit() {
        String text = "+8615810403964|23\t15810403964|1\t+3345|4\t123|1\t";
        assertEquals(4, text.split("\t").length);

        assertEquals(5, TextUtils.split(text, "\t").length);
    }

}
