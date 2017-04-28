package io.rverb.feedback.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

import io.rverb.feedback.model.Persistable;

public class DataUtils {
    public static final String EXTRA_RESULT_RECEIVER = "result_receiver";
    public static final String EXTRA_RESULT = "result";

    public static final String EXTRA_SCREENSHOT_FILE_NAME = "screenshot_file_name";
    public static final String EXTRA_SELF = "data";

    public static <T extends Persistable> T fromJson(String json, Class<T> type) {
        Gson gson = new Gson();
        T dataObject = gson.fromJson(json, type);

        return dataObject;
    }

    public static Persistable readObjectFromDisk(String fileName) {
        ObjectInputStream input;
        Persistable queuedObject = null;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object object = input.readObject();

            if (object instanceof Serializable) {
                queuedObject = (Persistable) object;
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queuedObject;
    }

    public static String writeObjectToDisk(Context context, Persistable object) {
        try {
            //create a temp file
            String fileName = "rv_" + object.getDataTypeDescriptor();

            File temp = File.createTempFile(fileName, ".rvb", context.getCacheDir());
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

    public static boolean validateTextEntryNotEmpty(@NonNull EditText field) {
        return !TextUtils.isEmpty(field.getText());
    }

    public static boolean validateTextEntryIsValid(@NonNull EditText field, @NonNull Pattern
            formatPattern) {
        return formatPattern.matcher(field.getText()).matches();
    }
}
