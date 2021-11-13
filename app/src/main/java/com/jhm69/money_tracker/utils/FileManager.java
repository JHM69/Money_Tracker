package com.jhm69.money_tracker.utils;

import android.os.Environment;

import com.jhm69.money_tracker.interfaces.FileGeneratorParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class FileManager {

    public static String getFileName() {
        return "Expense tracker "+(new Date().toString());
    }

    public static final File generateFile(FileGeneratorParser fileGeneratorParser) {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            File root = new File(Environment.getExternalStorageDirectory(), "ExpenseTracker");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, getFileName());
            try {
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(fileGeneratorParser.generateFileContent());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return gpxfile;
        }
        return null;
    }
}

