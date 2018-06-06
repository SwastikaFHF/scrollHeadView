package com.aitangba.testproject.util;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class PropertiesUtil {

    public static String getProperties(Context context, String key){
        Properties props = new Properties();
        try {
            InputStream in = context.getAssets().open("appConfig.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props.getProperty(key);
    }
}
