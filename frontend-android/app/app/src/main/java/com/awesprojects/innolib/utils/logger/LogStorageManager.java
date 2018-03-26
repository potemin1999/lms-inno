package com.awesprojects.innolib.utils.logger;

import com.awesprojects.innolib.InnolibApplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Ilya on 3/22/2018.
 */

public class LogStorageManager {

    private static LogStorageManager mInstance;

    public static LogStorageManager getInstance() {
        if (mInstance == null)
            mInstance = new LogStorageManager();
        return mInstance;
    }

    public File mBaseLog;
    public File mDebugLog;
    public PrintStream mBasePrintStream;
    public PrintStream mDebugPrintStream;


    LogStorageManager() {
        mBaseLog = new File(InnolibApplication.getInstance().getFilesDir(), "application.log");
        mBaseLog.setWritable(true);
        mDebugLog = new File(InnolibApplication.getInstance().getFilesDir(), "debug.log");
        try {
            mBasePrintStream = new PrintStream(new FileOutputStream(mBaseLog, true));
            mDebugPrintStream = new PrintStream(new FileOutputStream(mDebugLog));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public PrintStream getBasePrintStream() {
        return mBasePrintStream;
    }

    public PrintStream getDebugPrintStream() {
        return mDebugPrintStream;
    }

    public String loadBaseLogFile(){
        try {
            FileInputStream fis = new FileInputStream(mBaseLog);
            byte[] array = new byte[(int)mBaseLog.length()];
            fis.read(array);
            fis.close();
            return new String(array);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
