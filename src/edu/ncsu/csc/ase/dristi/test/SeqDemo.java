package edu.ncsu.csc.ase.dristi.test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Created by purejade on 2015/4/1.
 */
public class SeqDemo {
    private static final String basedir = System.getProperty("SegDemo", "G:\\FtpDir\\stanford-segmenter-2015-01-29\\stanford-segmenter-2015-01-30\\data\\");

    private static String readFile(String utf8File) {
        File f1 = new File(utf8File);
        byte buffer[] = new byte[(int)f1.length()];
        String text = null;
        try {
            FileInputStream fileinput = new FileInputStream(f1);
            fileinput.read(buffer);
            text = new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
    private static  void writeFile(List<String> strings,String outfile) {
        BufferedWriter finalout = null;
        try {
            File finalresult = new File(outfile);
            finalresult.createNewFile();
            finalout = new BufferedWriter(new FileWriter(finalresult));
        }catch (Exception e) {
            e.printStackTrace();
        }
        for(String str : strings) {
            try {
                finalout.write(str+" ");
                finalout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            finalout.flush();
            finalout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "utf-8"));

        Properties props = new Properties();
        props.setProperty("sighanCorporaDict", basedir);
        // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
        // props.setProperty("normTableEncoding", "UTF-8");
        // below is needed because CTBSegDocumentIteratorFactory accesses it
        props.setProperty("serDictionary", basedir + "dict-chris6.ser.gz");
//        if (args.length > 0) {
//            props.setProperty("testFile", args[0]);
//        }
//        props.setProperty("testFile","G:\\FtpDir\\stanford-segmenter-2015-01-29\\stanford-segmenter-2015-01-30\\test.simp.utf8");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");

        CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
        segmenter.loadClassifierNoExceptions(basedir + "ctb.gz", props);
        HashMap<String,Boolean> stopwords = StopWords.getStopWordsMap();
//        segmenter.classifyAndWriteAnswers("G:\\FtpDir\\stanford-segmenter-2015-01-29\\stanford-segmenter-2015-01-30\\test.simp.utf8");

//        for (String filename : args) {
//            segmenter.classifyAndWriteAnswers(filename);
//        }
//        String testFile = "G:\\FtpDir\\stanford-segmenter-2015-01-29\\stanford-segmenter-2015-01-30\\test.simp.utf8";
        String des_dir = "G:\\FtpDir\\NEW_XIAO_APPS\\TMP\\";
        String out_dir = "G:\\FtpDir\\NEW_XIAO_APPS\\TMP-OUT\\";
        File desFile = new File(des_dir);
        File[] files = desFile.listFiles();
        for(File file : files) {
//            String testFile = "G:\\FtpDir\\NEW_XIAO_APPS\\DETAILS\\49832";
            String testFile = file.getAbsolutePath();
            System.out.println(testFile);
            String filename = file.getName();
            String sample = readFile(testFile);
//        String sample = "我住在美国。";
            sample = sample.replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "");  // 去掉标点
            sample = sample.replaceAll("[0-9]*", "");  // 去掉数字
//            System.out.println(sample);
            List<String> segmented = segmenter.segmentString(sample);
            List<String> new_segmented = new ArrayList<String>();
            for (String str : segmented) {
                str = str.trim();
                if (str.length() == 0 || str == "") continue;
                if (stopwords.containsKey(str) == false) {
                    new_segmented.add(str);
                }
            }
            writeFile(new_segmented,out_dir+filename);
//            System.out.println(segmented);
        }
    }
}
