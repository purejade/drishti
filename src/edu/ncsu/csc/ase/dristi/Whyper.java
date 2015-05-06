package edu.ncsu.csc.ase.dristi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.didion.jwnl.data.Exc;
import org.omg.CORBA.PERSIST_STORE;
import org.slf4j.Logger;

import edu.ncsu.csc.ase.dristi.datastructure.ITuple;
import edu.ncsu.csc.ase.dristi.datastructure.Tuple;
import edu.ncsu.csc.ase.dristi.heuristics.semantic.WordNetSimilarity;
import edu.ncsu.csc.ase.dristi.knowledge.Knowledge;
import edu.ncsu.csc.ase.dristi.knowledge.KnowledgeAtom;
import edu.ncsu.csc.ase.dristi.logging.MyLoggerFactory;
import edu.ncsu.csc.ase.dristi.util.ConsoleUtil;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class Whyper {

	public enum Permission {

		Read_Contact, Read_Calendar, Record_Audio
	}

	private Logger logger = MyLoggerFactory.getLogger(Whyper.class);
	
	// Change the assignment to refer to different permission
	// Current permissions supported are Read_Contacts, Read_Calendar,
	// Record_Audio
	 static  Permission permission = Permission.Read_Contact;

	public static void main(String[] args) {
        Whyper instance = new Whyper();
        BufferedWriter finalout = null;
        try {
            File finalresult = new File("C:\\Users\\purejade\\PycharmProjects\\BYSJ\\LAB1-2\\final.txt");
            finalresult.createNewFile();
            finalout = new BufferedWriter(new FileWriter(finalresult));
        }catch (Exception e) {
            e.printStackTrace();
        }
//        String[] permissions = {"READ_SMS", "READ_CONTACTS", "READ_CALENDAR"};
        String[] permissions = {"READ_SMS"};
        for (int cnt = 0; cnt < permissions.length; cnt++) {
//            String des_dir = "C:\\Users\\purejade\\PycharmProjects\\BYSJ\\"+permissions[cnt];
            String des_dir = "G:\\FtpDir\\RESULT\\PLAY_APP\\MODDES-3";
            System.out.println(des_dir);
            File desFile = new File(des_dir);
            File[] files = desFile.listFiles();
            BufferedWriter out = null;
            BufferedWriter out2 = null;
            try {
                String tmpfile = "";
                if(permissions[cnt] == "READ_SMS") {
                    tmpfile = "sms";
                    permission = Permission.Read_Contact;
                }
                else if(permissions[cnt] == "READ_CONTACTS") {
                    tmpfile = "contact";
                    permission = Permission.Read_Contact;
                }
                else if(permissions[cnt] == "READ_CALENDAR") {
                    tmpfile = "calendar";
                    permission = Permission.Read_Calendar;
                }
                File writename = new File("C:\\Users\\purejade\\PycharmProjects\\BYSJ\\LAB1-2\\"+tmpfile+".txt");
                File timefile = new File("C:\\Users\\purejade\\PycharmProjects\\BYSJ\\LAB1-2\\"+tmpfile+"_time.txt");
                timefile.createNewFile();
                out2 = new BufferedWriter(new FileWriter(timefile));
                writename.createNewFile(); // 创建新文件
                out = new BufferedWriter(new FileWriter(writename));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 6337; i < files.length; i++) {
                long startTime = System.currentTimeMillis();   //获取开始时间
                File filename = files[i];
                ArrayList<String> lines = new ArrayList<String>();
                try {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream((filename)));
                    BufferedReader br = new BufferedReader(reader);
                    String line = br.readLine();
                    while (line != null) {
                        lines.add(new String(line));
                        line = br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int size = lines.size();
                boolean flag = false;
                for (int j = 0; j < size; j++) {
                    String line = lines.get(j);
                    if (line.length() > 0) {
                        try {
                            String[] items = line.split(",");
                            if (items.length > 5) {
                                line = items[0];
                                for (int k = 1; k < items.length; k++) {
                                    if (items[k].length() > 10)
                                        lines.add(items[k]);
                                }
                                size = lines.size();
                            }
                            if (line.length() < 10) continue;
                            String newline = new String(line);
                            if (instance.isPermissionSentnce(line)) {
                                System.out.println(i + "| Describes Permission!");
                                out.write(i + "| " + filename.getName() + "| Describes Permission!\n");
                                finalout.write(permission + "|" + filename.getName() + "|" + newline+"\n");
                                finalout.flush();
                                out.flush();
                                flag = true;
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("error" + i);
                            //                    out.write(i + " | error");
                        }
                    }
                }
                if (flag == false) {
                    try {
                        System.out.println(i + " Does Not Describe Permission!");
                        out.write(i + "| " + filename.getName() + "| Does Not Describe Permission!\n");
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    long endTime = System.currentTimeMillis(); //获取结束时间
                    out2.write(filename.getName() + "|" + (endTime - startTime)+"\n");
                    out2.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                out.flush();
                out.close();
                out2.flush();
                out2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        try {
            finalout.flush();
            finalout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//			String message = "Enter The Sentnce =>";
//			String s = ConsoleUtil.readConsole(message);
//        String message = "Enter the sentnce";

    }
	public boolean isPermissionSentnce(String sentnce) {

		// Only the first Sentence is considered so provide input sentence by
		// sentence
        try {
            SemanticGraph semdep = NLPParser.getInstance().getStanfordDependencies(sentnce).get(0);
            Tuple t = TextAnalysisEngine.parse(semdep);
            return isDescribingPermission(t);
        } catch(Exception e) {
            return false;
        }
	}

	private boolean isDescribingPermission(Tuple t) {
		if (t == null)
			return false;
		Knowledge k = Knowledge.getInstance();
		KnowledgeAtom a;
		List<ITuple> tList;
		switch (permission) {
		case Read_Contact:
//			a = k.fetchAtom("contact");
//			tList = searchAtom(t, a.getSynonyms(),new ArrayList<ITuple>());
//			for (ITuple t1 : tList) {
//				if ((t1 != null)
//						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
//						|| isAugumentedAction(a.getActions(), t, t1)) {
//					return true;
//				}
//			}

			a = k.fetchAtom("sms");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
						|| isAction(a.getActions(), t, t1))
					return true;
			}
			
//			a = k.fetchAtom("email");
//			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
//			for (ITuple t1 : tList) {
//				if ((t1 != null)
//						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
//						|| isAction(a.getActions(), t, t1))
//					return true;
//			}
			break;

		case Read_Calendar: {

			a = k.fetchAtom("calendar");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1))
						|| isAugumentedAction(a.getActions(), t, t1)) {
					return true;
				}
			}

			a = k.fetchAtom("event");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1)) || isAction(a.getActions(), t, t1))
					return true;
			}
		}
			break;

		case Record_Audio:
			a = k.fetchAtom("mic");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1))
						|| isAugumentedAction(a.getActions(), t, t1)) {
					return true;
				}
			}

			a = k.fetchAtom("audio");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1)) || isAction(a.getActions(), t, t1))
					return true;
			}
			break;

		default:
			logger.error("Unknown Permission!");
			break;
		}

		return false;
	}

	private static boolean isAugumentedAction(ArrayList<String> actions,
			Tuple t, ITuple t1) {
		String s = "";
		if (t1.isTerminal())
			s = t1.getEntity().getName();
		else
			s = t1.getRelation().getName();
		for (String action : actions) {
			if (s.contains(action)) {
				return true;
			}

		}

		return false;
	}

	private static boolean isUserOwned(Tuple t1, ITuple t2) {
		ITuple t_parent = t1.findParent(t2);
		ITuple t_Sibling = t1.findSibling(t2);
		String comp_Value = "";
		if ((t_parent != null) && !t_parent.same(t2)) {
			comp_Value = t_parent.getRelation().getName();

			try {
				if (WordNetSimilarity.getInstance()
						.isSimilar(comp_Value, "and")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "or")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "in")) {
					return isUserOwned(t1, t_parent);
				}

				else if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
						"owned")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "is")) {
					if (t_Sibling.isTerminal()) {
						if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "user"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "you"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "their"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "her"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "his"))
							return true;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean isAction(ArrayList<String> actions, ITuple t1,
			ITuple t2) {
		ITuple t_parent = t1.findParent(t2);
		ITuple t_Sibling = t1.findSibling(t2);
		String comp_Value = "";
		if ((t_parent != null) && !t_parent.same(t2)) {
			comp_Value = t_parent.getRelation().getName();
			for (String action : actions) {
				try {
					if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
							action)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (t_Sibling != null) {
				if (t_Sibling.isTerminal())
					comp_Value = t_Sibling.toString();
				else
					comp_Value = t_Sibling.getRelation().getName();
				for (String action : actions) {
					try {
						if (WordNetSimilarity.getInstance().isSimilar(
								comp_Value, action)) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			return isAction(actions, t1, t_parent);
		} else if (t_parent.same(t2)) {
			if (t_parent.isTerminal())
				comp_Value = t_parent.toString();
			else
				comp_Value = t_parent.getRelation().getName();
			for (String action : actions) {
				try {
					if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
							action)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static ArrayList<ITuple> searchAtom(ITuple t,
			ArrayList<String> atom, ArrayList<ITuple> tupleList) {

		if (t == null)
			return tupleList;
		if (t.isTerminal()) {
			for (String res : atom) {
				if (t.getEntity().getName().toString().toLowerCase()
						.contains(res)) {
					if (!tupleList.contains(t))
						tupleList.add(t);

				}
			}

		} else {

			for (String res : atom) {
				if (t.getRelation().getName().toLowerCase().contains(res)) {
					if (!tupleList.contains(t))
						tupleList.add(t);
				}

			}

			tupleList = searchAtom(t.getLeft(), atom, tupleList);

			tupleList = searchAtom(t.getRight(), atom, tupleList);
		}
		return tupleList;
	}
}
