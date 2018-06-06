package com.aitangba.testproject.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserPersistenceUtil {

    private static final String FILENAME = "option";

    public static void writerObject(Context context, Object userInfo) {
        ObjectOutputStream objectOutputStream = null;
        try {
            FileOutputStream fileOutputStream = context.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(userInfo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readerObject(Context context) {
        ObjectInputStream objectInputStream = null;
        Object userInfo = null;
        try {
            FileInputStream fileInputStream = context.getApplicationContext().openFileInput(FILENAME);
            objectInputStream = new ObjectInputStream(fileInputStream);
            userInfo = objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return userInfo;
    }

}
