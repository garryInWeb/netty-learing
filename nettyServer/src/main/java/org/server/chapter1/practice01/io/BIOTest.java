package org.server.chapter1.practice01.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhengtengfei on 2018/12/27.
 */
public class BIOTest {

    private List<String> userIdList = new ArrayList<>();
    private void userIdList() throws FileNotFoundException {
        File resource = new File("C:\\Users\\zhengtengfei\\Desktop\\userIdList.txt");
        BufferedReader br = null;
        FileInputStream fileInputStream = new FileInputStream(resource);
        long x = System.currentTimeMillis();

        try {
            String s1;
            br = new BufferedReader(new InputStreamReader(fileInputStream,"utf-8"));
            while ((s1 = br.readLine()) != null) {
                userIdList.add(s1);
            }
            System.out.println(System.currentTimeMillis() - x);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getSignRandom() throws FileNotFoundException {
        if (userIdList == null || userIdList.isEmpty()){
            userIdList();
        }
        Random rand = new Random();
        int line = rand.nextInt(userIdList.size());
        return userIdList.get(line);
    }

    public static void main(String[] args) throws FileNotFoundException {
        BIOTest bioTest = new BIOTest();

        bioTest.getSignRandom();
    }
}
