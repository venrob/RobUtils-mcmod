package com.venrob.robutils.util;

import java.io.*;

public class FileLogger {
    private PrintWriter pw;
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FileLogger(String path) throws IOException{
        File f = new File(path);
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
    }
    public void close() {
        pw.close();
    }
    public void write(String line) {
        pw.write(line + "\r\n");
        pw.flush();
    }
}
