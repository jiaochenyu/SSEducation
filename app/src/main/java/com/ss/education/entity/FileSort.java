package com.ss.education.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCY on 2017/10/30.
 * 说明：
 */

public class FileSort implements Serializable {
    String name;
    int fileFlag; //文件分类 1.xlsx  2.pptx， 3.docx
    List<LocationFile> mLocationFiles;

    public FileSort(String name, int fileFlag, List<LocationFile> locationFiles) {
        this.name = name;
        this.fileFlag = fileFlag;
        mLocationFiles = locationFiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileFlag() {
        return fileFlag;
    }

    public void setFileFlag(int fileFlag) {
        this.fileFlag = fileFlag;
    }

    public List<LocationFile> getLocationFiles() {
        return mLocationFiles;
    }

    public void setLocationFiles(List<LocationFile> locationFiles) {
        mLocationFiles = locationFiles;
    }
}
