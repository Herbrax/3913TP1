package com.TP1;
public class testFileData {
    String filePath;
    String packageName;
    String className;
    int tloc;
    int tassert;
    double tcm;

    public testFileData() {
    }

    public testFileData(String filePath, String packageName, String className, int tloc, int tassert, double tcm) {
        this.filePath = filePath;
        this.packageName = packageName;
        this.className = className;
        this.tloc = tloc;
        this.tassert = tassert;
        this.tcm = tcm;
    }
}
