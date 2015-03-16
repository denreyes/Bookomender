package com.example.dj.bookomender;

/**
 * Created by DJ on 3/11/2015.
 */
public class StringPretifier {

    public String pretifyCategory(String category){
        StringBuffer stringBuffer = new StringBuffer(category.replace(' ','_'));
        stringBuffer.delete(0,2);
        stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length());
        return stringBuffer.toString();
    }
}
