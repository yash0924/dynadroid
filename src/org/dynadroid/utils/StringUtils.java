package org.dynadroid.utils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brianknorr
 * Date: May 14, 2010
 * Time: 12:22:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static String join(List list, String delimiter) {
        if (list.isEmpty()) return "";

        StringBuilder joinedString = new StringBuilder();

        for (int i = 0; i<list.size(); i++) {
            String string = list.get(i).toString();
            joinedString.append(string + delimiter);
        }

        return joinedString.delete(joinedString.length() - delimiter.length(), joinedString.length()).toString();
    }
}
