package cn.migu.unify.ai.service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Mapp {

    public static void main(String[] args) throws IOException {

//        String str = "http://127.0.0.1:9999/pic/2.jpg";
//        System.out.println(str.substring(str.lastIndexOf("/")+1,str.length()));

//
//        for (int i = 1; i <= 5000; i++) {
//            Files.copy(new File("C:/gus/data/local/110/2.jpg").toPath(),
//                    new File("C:/gus/data/local/110/5000/" + i + ".jpg").toPath());
//
//        }

        FileOutputStream out = null;

        FileWriter fw = null;


        out = new FileOutputStream(new File("C:/gus/data/5000.txt"));

        for (int i = 1; i <= 5000; i++) {

            out.write(("http://172.18.111.7:9999/pic/" + i + ".jpg\n").getBytes());

        }
    }
}
