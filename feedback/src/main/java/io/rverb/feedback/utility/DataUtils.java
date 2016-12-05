package io.rverb.feedback.utility;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.rverb.feedback.model.Cacheable;

public class DataUtils {
    public static <T extends Cacheable> T fromJson(String json, Class<T> type) {
        Gson gson = new Gson();
        T dataObject = gson.fromJson(json, type);

        return dataObject;
    }

    public static Cacheable readObjectFromDisk(String fileName) {
        ObjectInputStream input;
        Cacheable queuedObject = null;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object object = input.readObject();

            if (object instanceof Serializable) {
                queuedObject = (Cacheable) object;
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queuedObject;
    }

    public static String writeObjectToDisk(Context context, Cacheable object) {
        try {
            //create a temp file
            String fileName = "rv_" + object.getDataTypeDescriptor();

            File temp = File.createTempFile(fileName, ".tmp", context.getCacheDir());
            FileOutputStream fos = getFileOutputStream(temp);

            if (fos != null) {
                ObjectOutputStream os = getObjectOutputStream(fos);

                if (os != null) {
                    os.writeObject(object);
                    os.close();
                    fos.close();

                    return temp.getAbsolutePath();
                }
            }

            return null;
        } catch (IOException e) {
            // If this doesn't write, I think it's alright for now,
            // this is just a file to be checked on app start,
            // in case the initial API call failed.

            e.printStackTrace();
            return null;
        }
    }

    protected static FileOutputStream getFileOutputStream(File temp) {
        try {
            return new FileOutputStream(temp.getAbsolutePath());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected static ObjectOutputStream getObjectOutputStream(FileOutputStream fos) {
        try {
            return new ObjectOutputStream(fos);
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) {
        if (!RverbioUtils.isNullOrWhiteSpace(fileName)) {
            File file = new File(fileName);
            file.delete();
        }
    }
}
