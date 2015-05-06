package edu.ncsu.csc.ase.dristi.test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by purejade on 2015/4/1.
 */
public class StopWords {

    private static  HashMap<String,Boolean> stopWords = new HashMap<String, Boolean>();

    public static  HashMap<String,Boolean> getStopWordsMap() {
        List<String> lines=new ArrayList<String>();
        String fileName = "G:\\FtpDir\\drishti\\src\\edu\\ncsu\\csc\\ase\\dristi\\test\\stopwords";
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if(line.trim() != "") {
                    stopWords.put(line,true);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(lines);
        return stopWords;
    }
    public static void main(String[] args) {
        HashMap<String,Boolean> stopwords = StopWords.getStopWordsMap();
        Set<String> keys = stopwords.keySet();
        System.out.println(keys.size());
        for(String key : keys){
            System.out.println(key + "--" + stopwords.get(key));
        }
    }
}
