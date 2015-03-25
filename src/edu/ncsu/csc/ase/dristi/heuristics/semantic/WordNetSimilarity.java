package edu.ncsu.csc.ase.dristi.heuristics.semantic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;


import net.didion.jwnl.JWNLException;

//import edu.illinois.cs.cogcomp.wnsim.WNSim;
//import edu.illinois.cs.cogcomp.sim.WNSim;

import edu.ncsu.csc.ase.dristi.configuration.Config;


import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

/**
 * This Class is responsible for calculating the similarity between two
 * words/phrases. The Class utilizes the WORD-NET similarity project by UIUC
 * Cognitive Computing Group :
 * <http://cogcomp.cs.illinois.edu/page/software_view/Illinois%20WNSim>
 * 
 * @author rahul_pandita
 * 
 */
public class WordNetSimilarity {

	/**
	 * variable to hold the singleton instance
	 */
	private static WordNetSimilarity instance;

    private static ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator[] rcs = {
            new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
            new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
    };

	/**
	 * HashMap to store additional synonyms
	 */
	private static HashMap<String, ArrayList<String>> synonymList;

	/**
	 * Instance variable holding instance of WNSim Class
	 */
//	private static WNSim wnsim;

	/**
	 * Static method to get instance of WordNetSimilarity singleton object
	 * 
	 * @return
	 */
	public static synchronized WordNetSimilarity getInstance() {
		if (instance == null) {
			try {
				instance = new WordNetSimilarity();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * Private Constructor for Singleton Pattern
	 * 
	 * @throws FileNotFoundException
	 * @throws JWNLException
	 */
	private WordNetSimilarity() throws FileNotFoundException, JWNLException {
//		wnsim = WNSim.getInstance(Config.WORD_NET_PATH,
//				Config.WORD_NET_CONFIG_XML_PATH);

		synonymList = new HashMap<String, ArrayList<String>>();
		ArrayList<String> synonyms = new ArrayList<String>();
		synonyms.add("USER");
		synonyms.add("YOUR");
		synonyms.add("YOU");
		synonymList.put("YOUR", synonyms);
		synonymList.put("USER", synonyms);
		synonymList.put("YOU", synonyms);
	}

	/**
	 * Method to Compute similarity between words in a phrase and the provided
	 * word. If any of the word in phrase is synonym of word the method returns
	 * true else false.
	 * 
	 * @param phrase
	 *            phrase that needs to matched
	 * @param word
	 *            word with which the phrase is matched
	 * @return true if any of the word in phrase is synonym of word, else false
	 * @throws Exception
	 */
	public boolean isSimilar(String phrase, String word) throws Exception {
        boolean similarity = false;
        String[] wordlist = phrase.split("\\s+");
        for (String wrd : wordlist) {
            if (wrd.equalsIgnoreCase(word))
                return true;
            if ((synonymList.containsKey(wrd.toUpperCase()))
                    && (synonymList.get(wrd.toUpperCase()).contains(word
                    .toUpperCase())))
                return true;

//			double sim = wnsim.getWupSimilarity(wrd, word);
//            double sim = wnsim.score(wrd, word);
            double sim = run(wrd,word);
//            System.out.println("the score is " + sim);
//            double sim = 0.9;
//            System.out.println(wrd + "-" + word + "(" + sim + ")");
            if (sim > Config.WORD_NET_MIN_THRESHOLD) {
                return true;

            }
        }

        return similarity;
    }
    private static double run( String word1, String word2 ) {
        int count = 0;
        double scores = 0.0;
        double result = 0.0;

        WS4JConfiguration.getInstance().setMFS(true);
        for ( RelatednessCalculator rc : rcs ) {
            double s = rc.calcRelatednessOfWords(word1, word2);
            result = Math.max(result,s);
            if (s != 0) {
                count += 1;
                scores += s;
            }
//            System.out.println( rc.getClass().getName()+"\t"+s );
        }
//        System.out.println(count);
//        System.out.println("the average score is " + scores/count);
//        return scores/count;
        return result;
    }

	public static void main(String[] args) throws Exception {
		WordNetSimilarity wn = WordNetSimilarity.getInstance();
		System.out.println(wn.isSimilar("you", "man"));
	}
}
