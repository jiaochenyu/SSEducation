package com.ss.education.entity;

import java.io.Serializable;

import io.rong.imkit.model.FileInfo;

/**
 * Created by JCY on 2017/10/30.
 * 说明： 本地文件 doc pptx docx ptf ......
 */

public class LocationFile implements Serializable {
    private String path = "";
    private String name = "";
    private int flag = 0; // 文件分类 1.xlsx  2.pptx， 3.docx
    private long fileSize;
    private boolean isChecked;
    private FileInfo mFileInfo;
    private String extension;

    public LocationFile() {
    }

    public LocationFile(String path, String name, int flag) {
        this.path = path;
        this.name = name;
        this.flag = flag;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FileInfo getFileInfo() {
        return mFileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        mFileInfo = fileInfo;
    }
}
