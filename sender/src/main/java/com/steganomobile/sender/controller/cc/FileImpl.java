package com.steganomobile.sender.controller.cc;

import android.os.Environment;

import com.steganomobile.common.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public abstract class FileImpl extends CcImpl {
    public static final int BASE_SIZE = 128;
    private static final String TAG = FileImpl.class.getSimpleName();
    private RandomAccessFile accessFile;
    private File file;
    private FileChannel fileChannel;

    protected FileImpl() {
        file = new File(Environment.getExternalStorageDirectory(), Const.SHARED_FILE);
        try {
            accessFile = new RandomAccessFile(file, Const.READ_WRITE);
            fileChannel = accessFile.getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public File getFile() {
        return file;
    }

    public FileChannel getFileChannel() {
        return fileChannel;
    }

    public RandomAccessFile getAccessFile() {
        return accessFile;
    }

    @Override
    public void clearCc() {
        try {
            fileChannel.close();
            accessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
