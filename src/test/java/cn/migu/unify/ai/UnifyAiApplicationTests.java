package cn.migu.unify.ai;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UnifyAiApplicationTests {

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 2; i++) {

            copyFile(new File("/Users/gus97/zz/old/old.txt"),
                    new File("/Users/gus97/zz/new/new.txt"));
        }
    }


    public static void copyFile(File fromFile, File toFile) throws IOException {
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }

        ins.close();
        out.close();
    }

}
