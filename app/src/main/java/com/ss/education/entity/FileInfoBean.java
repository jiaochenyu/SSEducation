package com.ss.education.entity;

import java.io.Serializable;

/**
 * Created by JCY on 2017/10/27.
 * 说明：
 */

public class FileInfoBean  implements Serializable{


    /**
     * file : upload/9a4db50c80d04010a2d089681ea2f92f.png
     * filename : 9a4db50c80d04010a2d089681ea2f92f.png
     * keyname : file
     * oldname : icon_file.png
     */

    private String file;
    private String filename;
    private String keyname;
    private String oldname;

    public FileInfoBean() {
    }

    public FileInfoBean(String file, String oldname) {
        this.file = file;
        this.oldname = oldname;
    }

    public FileInfoBean(String file) {
        this.file = file;
    }

    public FileInfoBean(String file, String oldname, String keyname) {
        this.file = file;
        this.oldname = oldname;
        this.keyname = keyname;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getOldname() {
        return oldname;
    }

    public void setOldname(String oldname) {
        this.oldname = oldname;
    }
}
