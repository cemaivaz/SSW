package socSem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class UserProfileMain {

	/**
	 * @param args
	 */

	public static String traFile = "lim_tra_set.txt";
	public static List<String> plSuffOne = Arrays.asList("s");
	public static List<String> plSuffThr = Arrays.asList("zes", "xes", "ies", "men", "ses");
	public static List<String> plSuffFour = Arrays.asList("shes", "ches");

	public static boolean ignoreIdf = false;

	public static int tweetLimNo = 3450;

	public static String [] colours = {"#909092", "#FF0000", "#00FF00", "#0000FF", "#FFF700", "#074F88", "#FF00FF", "#00FFFF", "#6F1F04", "#127682"};

	public static String stem (String s) {


		int strLen = s.length();
		if (strLen >= 4)
			if (plSuffFour.contains(s.substring(strLen - 2))) {
				return s.substring(0, strLen - 2);
			}
		if (strLen >= 3)
			if (plSuffThr.contains(s.substring(strLen - 2))) {
				return s.substring(0, strLen - 2);
			}
		if (strLen >= 2)
			if (plSuffOne.contains(s.substring(strLen - 1))) {
				return s.substring(0, strLen - 1);
			}
		return "";
	}

	public static void txtForm() {
		try {
			BufferedReader br2 = new BufferedReader(new FileReader(new File("training_set_users.txt")));
			String line = "";

			List<String> li = new ArrayList<String>();
			while ((line = br2.readLine()) != null) {

				line = line.toLowerCase(Locale.ENGLISH);
				li.add(line);
			}
			br2.close();

			FileWriter fw = new FileWriter(new File("training_set_users.txt"));
			for (int i = 0; i < li.size(); i++)
				fw.write(li.get(i) + "\n");
			fw.close();

			String tmpFile = traFile;

			br2 = new BufferedReader(new FileReader(new File(tmpFile)));
			line = "";

			li = new ArrayList<String>();
			while ((line = br2.readLine()) != null) {

				line = line.toLowerCase(Locale.ENGLISH);
				li.add(line);
			}
			br2.close();

			fw = new FileWriter(new File(tmpFile));
			for (int i = 0; i < li.size(); i++)
				fw.write(li.get(i) + "\n");
			fw.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Do you want the program to take account of the"
				+ " tf-idf score?\n"
				+ "If yes, press y\n"
				+ "If no, press n\n");

		Scanner sc = new Scanner(System.in);
		String tfIdfDec = sc.nextLine().trim();
		if (!(tfIdfDec.equals("y") || tfIdfDec.equals("n"))) {
			System.err.println("Wrong input format! Try again..");
			System.exit(1);
		}

		System.out.println("Enter the full path name of the file"
				+ " where do you want to "
				+ "see the output (Its extension should be htm or html): ");

		sc = new Scanner(System.in);

		String fileName = sc.nextLine().trim();
		if (fileName.equals("")) {
			System.out.println("You should have specified the name of the file!");
			System.exit(1);
		}
		else if(fileName.length() <= 4) {
			System.out.println("Invalid file name! (File name should "
					+ "have had at least five characters)");
			System.exit(1);
		}
		else if (!(fileName.substring(fileName.length()- 3, fileName.length()).equals("htm") ||
				fileName.substring(fileName.length()- 4, fileName.length()).equals("html"))) {
			System.err.println("File extension should "
					+ "have been htm or html!");
			System.exit(1);
		}




		if (tfIdfDec.equals("n"))
			ignoreIdf = true;
		else
			ignoreIdf = false;
		WordNet wn = new WordNet();

		//		txtForm();


		List<ArrayList<String>> tweets = new
				ArrayList<ArrayList<String>>();

		Set<String> twWords = new
				LinkedHashSet<String>();

		Map<String, ArrayList<String>> allHashes = new
				LinkedHashMap<String, ArrayList<String>>();

		Map<String, String> hashHyp = new LinkedHashMap<String, String>();


		Map<String, String> hashMostCm = new LinkedHashMap<String, String>();


		List<String> twWHL = new ArrayList<String>();

		Map<String, ArrayList<String>> userIdTw = new LinkedHashMap<String, ArrayList<String>>();

		try {
			String line = "";
			//			Map<String, ArrayList<String>> norm = new HashMap<String, ArrayList<String>>();
			//			
			//			BufferedReader brNorm = new BufferedReader(new FileReader(traFile.substring(0, traFile.indexOf(".txt")) + "_norm.txt"));
			//			
			//			Map<String, Map<String, ArrayList<Boolean>>> liNorm = new 
			//					HashMap<String, Map<String, ArrayList<Boolean>>>();
			//			while ((line = brNorm.readLine()) != null) {
			//				String tmpLine = line.split("\\s+")[1];
			//				String normLineId = tmpLine.substring(0, tmpLine.indexOf("_"));
			//				String normLineTmp = line.substring(line.indexOf(" ") + 1);
			//				String normLine = normLineTmp.substring(normLineTmp.indexOf(" ") + 1);
			//				String [] spl = normLine.split(" ");
			//								
			//				Map<String, ArrayList<Boolean>> boolCh = new 
			//						HashMap<String, ArrayList<Boolean>>();
			//				for (int i = 0; i < spl.length; i++){
			//					ArrayList<Boolean> bools = (boolCh.containsKey(spl[i])) ? boolCh.get(spl[i]): 
			//						new ArrayList<Boolean>();
			////					if (!boolCh.containsKey(spl[i])) {
			////						boolCh.put(spl[i], false);
			////					}
			//					String word = "";
			//					if (spl[i].indexOf("_") > -1) {
			//						word = spl[i].substring(0, spl[i].lastIndexOf("_"));
			//						
			//					} else
			//						continue;
			//						
			//					boolean fnd = false;
			//					if (spl[i].indexOf("_NN") > -1) {
			//						fnd = true;
			//					} 
			//					bools.add(fnd);
			//					boolCh.put(word, bools);
			//				}
			//
			//				Map<String, ArrayList<Boolean>> boolChNew = new 
			//						HashMap<String, ArrayList<Boolean>>();
			//				for (Map.Entry<String, ArrayList<Boolean>> e: boolCh.entrySet()) {
			//					if (e.getValue().contains(true)) {
			//						boolChNew.put(e.getKey(), e.getValue());
			//						System.out.println(e.getKey());
			//					}
			//				}
			//				System.out.println("\n\n");
			//				liNorm.put(normLineId, boolChNew);
			//			}
			//			brNorm.close();
			//			System.exit(1);


			BufferedReader br = new BufferedReader(new FileReader(new File(traFile.substring(0, traFile.indexOf(".txt")) + "_norm.txt")));


			//			int cnt = 0;



			//Tweet No + the whole tweet
			Map<Integer, String> twRowNo = new LinkedHashMap<Integer, String>();

			String wholeTw = "";
			int rowNo = 0;
			System.out.println("Tweets are being processed..");

			while ((line = br.readLine()) != null /*&& cnt++ < tweetLimNo*/) {

				wholeTw = line.replaceAll("([^ ]+)_[^ _]+[\\s]*", "$1 ").replaceAll("([^ ]+)_[[^ ] && [^_]]+", "$1 ");;
				String rawTweet = "";
				//				System.out.println(line);
				line = line.toLowerCase(Locale.ENGLISH);
				//				Pattern p = Pattern.compile("([0-9]*[ ]*[0-9]*[ ]*)()([ ]*[0-9\\-]*)[ ]*[0-9:]*)( )?");
				line = line.replaceAll("([0-9]*_cd\\s*[0-9]*_cd\\s*)(.*)(\\s*[0-9\\-]*_cd\\s*[0-9:]*_cd)( )?", "$2");
				line = line.replaceAll("([0-9:\\-\\s]*)$", "");

				rawTweet = line.replaceAll("([^ ]+)_[^ _]+ ", "$1 ").replaceAll("([^ ]+)_[[^ ] && [^_]]+", "$1 ");;

				String userId = wholeTw.split("\\s+")[0];


				line = line.replaceAll(" http(s)?:([^ ])+[\\s]*", " ");
				//				line = line.replaceAll("([\\.\\,\\!\\?:;\\-]{1,})", " $1 ");
				String[] strArr = line.split(" ");

				ArrayList<String> tmp =
						new ArrayList<String>();
				tmp.add(userId);



				ArrayList<String> nonHashes = new
						ArrayList<String>();

				ArrayList<String> hashes = new
						ArrayList<String>();


				boolean twNorm = false;
				for (int i = 0; i < strArr.length; i++) {

					String strWord = strArr[i];
					boolean isNoun = false;
					if (strWord.contains("_nn") || strWord.contains("_NN"))
						isNoun = true;
					if (strWord.indexOf("_") > -1)
						strWord = strWord.substring(0, strWord.lastIndexOf("_"));
					
					if (strWord.length() <= 1)
						continue;
					if (strWord.length() > 1) {
						boolean isPl = false;
						if (wn.nouns.contains(strWord) && isNoun) {
							tmp.add(strWord);
							twWords.add(strWord);

							
							twNorm = true;
							if (!nonHashes.contains(strWord))
								nonHashes.add(strWord);

							isPl = true;

						}
						else if (strWord.charAt(0) == '#') {
							tmp.add(strWord);
							//							twRowNo.put(rowNo++, wholeTw);
							hashes.add(strWord);
							twNorm = true;

							isPl = true;
						}
						int strLen = strWord.length();
						int indS = strWord.indexOf("s");
						int indMen = strWord.indexOf("men");
						if (isPl == false && 
								(indS == strLen - 1 || indMen == strLen - 3)){

							String wordStem = "";

							//							strWord.charAt(strWord.length() - 1)

							wordStem = stem(strWord);
							if (wordStem.length() == 0)
								continue;

							if (wn.nouns.contains(wordStem) && isNoun) {
								tmp.add(wordStem);
								twWords.add(wordStem);

								twNorm = true;
								if (!nonHashes.contains(wordStem))
									nonHashes.add(wordStem);

							}
						}
					}

				}
				ArrayList<String> userIdLine = (userIdTw.containsKey(userId)) ? userIdTw.get(userId):
					new ArrayList<String>();

				userIdLine.add(rawTweet);
				userIdTw.put(userId, userIdLine);
				if (twNorm == true) {
					twRowNo.put(rowNo++, wholeTw);


				}
				if (tmp.size() > 1) //degisti: if (tmp.size() > 0)
					tweets.add(tmp);

				if (hashes.size() > 0 && nonHashes.size() > 0) {
					for (int i = 0; i < hashes.size(); i++) {
						for (int j = 0; j < nonHashes.size(); j++) {
							ArrayList<String> tmpHsh = (allHashes.containsKey(hashes.get(i))) ? 
									allHashes.get(hashes.get(i)): new ArrayList<String>();
									tmpHsh.add(nonHashes.get(j));
									allHashes.put(hashes.get(i), tmpHsh);
						}

					}
				}

			}
			br.close();


			FileWriter usIds = new FileWriter("training_set_users2.txt");

			List<String> keys_ = new ArrayList<String>(userIdTw.keySet());
			for (int i = 0; i < keys_.size(); i++)
				usIds.write(keys_.get(i) + " " + keys_.get(i) + "\n");
			usIds.close();

			ArrayList<String> hashKeys = new ArrayList<String>(allHashes.keySet());
			for (int i = 0; i < hashKeys.size(); i++) {
				ArrayList<String> tmp = allHashes.get(hashKeys.get(i));


				hashHyp.put(hashKeys.get(i), wn.simpleHypSum(tmp).split(", ")[0]);

				Map<String, Integer> m = new LinkedHashMap<String, Integer>();
				for (int j = 0; j < tmp.size(); j++) {
					int cntHm = (m.containsKey(tmp.get(j))) ?
							m.get(tmp.get(j)) + 1: 1;
					m.put(tmp.get(j), cntHm);

				}

				List<Map.Entry<String, Integer>> lst =
						new LinkedList<Map.Entry<String, Integer>>(m.entrySet());
				Collections.sort(lst, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
						if (m1.getValue() < m2.getValue())
							return 1;
						else if (m1.getValue() > m2.getValue())
							return -1;
						return 0;
					}
				});


				hashMostCm.put(hashKeys.get(i), lst.get(0).getKey());


				hashHyp.put(hashKeys.get(i), wn.simpleHypSum(tmp).split(", ")[0]);


			}

			FileWriter fw = 
					new FileWriter("twitterWords.txt");


			List<List<String>> hypTw = new ArrayList<List<String>>();

			List<String> hypAll = new ArrayList<String>();

			int twNo = 0;


			//tweet - hypernyms (w/ # of occur.)
			Map<String, String> twHypNo = new LinkedHashMap<String, String>();

			for (int i = 0; i < tweets.size(); i++) {
				ArrayList<String> twAl = tweets.get(i);
				String userId = twAl.get(0);
				//				if (userId.equals("15724930")) {
				//					System.out.println("found 1 ! " + twAl);
				//				}
				//				if (userId.equalsIgnoreCase("10483052"))
				//					System.out.println("found this as well ! ! ! " + twAl);
				twAl.remove(0);
				String resHyp = "";
				



				List<String> hypTwSub = new ArrayList<String>();

				int cntHasht = 0;

				ArrayList<String> hashAft = new ArrayList<String>();
				ArrayList<String> nonHashAft = new ArrayList<String>();
				for (int j = 0; j < twAl.size(); j++) {
					if (twAl.get(j).charAt(0) == '#') {
						hashAft.add(hashMostCm.get(twAl.get(j)));
						cntHasht++;
					}
					else
						nonHashAft.add(twAl.get(j));
				}

				String line2 = "";


				ArrayList<String> tmpHashAft = new ArrayList<String>();
				for (int m = 0; m < hashAft.size(); m++)
					if (hashAft.get(m) != null && !hashAft.get(m).isEmpty())
						tmpHashAft.add(hashAft.get(m));
				hashAft = tmpHashAft;


				if (cntHasht == twAl.size()) {
					if (hashAft.size() == 0 || hashAft.get(0) == null){
						twRowNo.remove(i);
						continue;

					}
					resHyp = wn.simpleHypSum(hashAft);
				}
				else if (cntHasht == 0)
					resHyp = wn.simpleHypSum(twAl);
				else {

					nonHashAft.addAll(hashAft);
					resHyp = wn.simpleHypSum(nonHashAft);
				}

				twNo++;


				hypTwSub = Arrays.asList(resHyp.split(", "));
				hypTw.add(hypTwSub);

				for (int k = 0; k < hypTwSub.size(); k++) {
					if (!hypAll.contains(hypTwSub.get(k).split(" ")[0]))
						hypAll.add(hypTwSub.get(k).split(" ")[0]);
				}

				StringBuilder allLine = new StringBuilder("");
				for (int j = 0; j < twAl.size(); j++) {
					if ( j == twAl.size() - 1) {
						fw.write(twAl.get(j));
						allLine.append(twAl.get(j));

					}
					else {
						fw.write(twAl.get(j) + ",");
						allLine.append(twAl.get(j));
						allLine.append(",");
					}
				}


				twHypNo.put("tw" + String.valueOf(i), resHyp);

				fw.write(" | hypernyms: " + resHyp + "__" + userId);


				allLine.append(" | hypernyms: ").append(resHyp).append("__").append(userId);
				twWHL.add(allLine.toString());
				fw.write("\n");
				
			}




			fw.close();


			//Some of the line numbers for the strings including the hypernyms are 
			//removed, if these return NULL
			Map<String, String> tmpTwHypNo = new LinkedHashMap<String, String>();
			List<String> tmpList = new ArrayList<String>(twHypNo.values());
			for (int i = 0; i < twHypNo.size(); i++) {
				tmpTwHypNo.put("tw" + new Integer(i).toString(), tmpList.get(i));
			}

			twHypNo = tmpTwHypNo;



			FileWriter fwTw = new FileWriter(new File("tweetsNo.txt"));



			for (int i = 0; i < twNo; i++) {
				if (i != twNo - 1)
					fwTw.write("tw" + String.valueOf(i) + ",");
				else
					fwTw.write("tw" + String.valueOf(i));
			}

			fwTw.close();





			Map<String, Double> preTfidf = new LinkedHashMap<String, Double>();

			line = "";
			for (int i = 0; i < hypAll.size(); i++) {
				//				if (ignoreIdf)
				//					preTfidf.put(hypAll.get(i), 1.);
				//				else 
				preTfidf.put(hypAll.get(i), 0.);
				//				line += hypAll.get(i) + ",";
				//				line = line.substring(0, line.length() - 1);
			}


			List<String> aftMatr = new ArrayList<String>();

			//			FileWriter fw2 = new FileWriter(new File("matr.txt"));
			for (int y = 0; y < hypTw.size(); y++) {
				List<String> sub = hypTw.get(y);
				//				fw2.write("tw" + y +":");
				StringBuilder line2 = new StringBuilder("");

				//				List<String> subSpl = new ArrayList<String>();
				Map<String, String> hm = new LinkedHashMap<String, String>(); 
				for (int w = 0; w < sub.size(); w++) {
					//					subSpl.add(sub.get(w).split(" ")[0]);
					hm.put(sub.get(w).split(" ")[0], sub.get(w).split(" ")[1]);
				}

				for (int u = 0; u < hypAll.size(); u++) {
					if (hm.containsKey(hypAll.get(u))) {
						//						if (!ignoreIdf)
						preTfidf.put(hypAll.get(u), (preTfidf.get(hypAll.get(u)) + 1));
						line2.append(hm.get(hypAll.get(u)));
						line2.append(",");
					}
					else
						line2.append("0,");
				}

				//				fw2.write(line2.substring(0, line2.length() - 1));
				//				fw2.write("\n");
				aftMatr.add(line2.substring(0, line2.length() - 1));
			}

			List<Map.Entry<String, Double>> ls_ =
					new LinkedList<Map.Entry<String, Double>>(preTfidf.entrySet());
			Collections.sort(ls_, new Comparator<Map.Entry<String, Double>>() {
				public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
					if (m1.getValue() < m2.getValue())
						return 1;
					else if (m1.getValue() > m2.getValue())
						return -1;
					return 0;
				}

			});


			//Elimination of top 3% words
			int elimMaxCnt = 0;
			FileWriter fwSt = new FileWriter(new File("stopWords.txt"));

			StringBuilder sbStop = new StringBuilder("");
			Map<String, Double> tmpPretf = new LinkedHashMap<String, Double>();
			
			for (Map.Entry ent: ls_) {

				if (elimMaxCnt++ > ls_.size() / 100 * 3) {


					tmpPretf.put((String) ent.getKey(), 1.);
				}
				else {
//					if (elMult >= 1)
//						elMult = 1;
					tmpPretf.put((String) ent.getKey(), 0.);
					preTfidf.put((String) ent.getKey(), 0.);
					sbStop.append((String) ent.getKey()).append(" ");
				}
			}
			sbStop.deleteCharAt(sbStop.length() - 1);

			fwSt.write(sbStop.toString());
			fwSt.close();

			if (ignoreIdf == true) {
				preTfidf = tmpPretf;
			}



			//			fw2.close();

			Map<String, Double> postTfidf = new LinkedHashMap<String, Double>();

			//			word: aid, postt: 0.0
			//			word: relative_quantity, postt: 0.0
			//			word: state, postt: 0.0
			//			0 thanks,nothing,order | hypernyms:

			System.out.println();


			for (int i = 0; i < aftMatr.size(); i++) {
				String[] strSpl = aftMatr.get(i).split(",");
				StringBuilder val = new StringBuilder("");
				for (int j = 0; j < strSpl.length; j++) {
					double newV = 0.;
					
					if (preTfidf.get(hypAll.get(j)) != 0)
						if (!ignoreIdf)
							newV = /*Double.valueOf(strSpl[j]) * */Math.log10(twWHL.size() / preTfidf.get(hypAll.get(j)));
						else
							newV = preTfidf.get(hypAll.get(j));

					val.append(String.valueOf(newV));
					val.append(",");
					postTfidf.put(hypAll.get(j), newV);
				}
				val = val.deleteCharAt(val.length() - 1);
			}

			//			System.out.println("////// " + twWHL.get(0));
			//			System.out.println("////// " + twWHL.get(1));


			for (int i = 0; i < twWHL.size(); i++) {
				String[] strSpl = twWHL.get(i).split("hypernyms: ")[1].split("__")[0].split(", ");
				String newVals = twWHL.get(i).split("nyms: ")[0] + "nyms: ";
				String last = twWHL.get(i).split("__")[1];
				
				
				last = "__" + last;
				
				for (int j = 0; j < strSpl.length; j++) {
					
					
					String word = strSpl[j].split(" ")[0];
					double freq = Double.valueOf(strSpl[j].split(" ")[1]) * postTfidf.get(word);
					//					if (i < 5)
					//						System.out.println("word: " + word + ", postt: " + postTfidf.get(word));
					newVals += word + " " + freq + ", ";
				}

				newVals = newVals.substring(0, newVals.length());
				newVals += last;
				twWHL.set(i, newVals);
//				if (newVals.contains("30405521"))
//					System.out.println("30405521: " + newVals);
//				if (newVals.contains("75493854"))
//					System.out.println("75493854: " + newVals);
				//				if (i < 55)
				//					System.out.println(i + " " + newVals);
			}


			ArrayList<String> al = new ArrayList<String>(twRowNo.values());
			Map<Integer, String> twRowNoUpd = new LinkedHashMap<Integer, String>();
			for (int i = 0; i < al.size(); i++) {
				twRowNoUpd.put(i, al.get(i));
			}
			twRowNo = twRowNoUpd;



			//Loc - UserId mapping
			Map<String, String> UIdLoc = new LinkedHashMap<String, String>();

			BufferedReader br2 = new BufferedReader(new FileReader(new File("training_set_users2.txt")));
			line = "";

			//			System.out.println("Tweets of users are being processed..");

			Map<String, LinkedHashSet<String>> locUIds = new LinkedHashMap<String, LinkedHashSet<String>>();

			int cnta = 0;
			while ((line = br2.readLine()) != null) {

				String[] arrSpl = line.split("[\\s]+");
				String uId = arrSpl[0];


				StringBuilder conc = new StringBuilder("");
				for (int q = 1; q < arrSpl.length; q++) {
					conc.append(" ").append(arrSpl[q]);
				}
				conc = new StringBuilder(new String(conc).trim());

				String concStr = conc.toString();

				UIdLoc.put(arrSpl[0], concStr.toString());


				if (userIdTw.containsKey(uId)) {
					LinkedHashSet<String> tmpHs = locUIds.containsKey(concStr) ? locUIds.get(concStr): 
						new LinkedHashSet<String>();
					tmpHs.add(uId);
					locUIds.put(concStr, tmpHs);	
				}




			}

			br2.close();



			Map<String, Map<String, Double>> locHyp = new 
					LinkedHashMap<String, Map<String, Double>>();


			for (int i = 0; i < twWHL.size(); i++) {
				String id = twWHL.get(i).split("__")[1];
				String loc = UIdLoc.get(id);

				Map<String, Double> hm2 = (locHyp.containsKey(loc)) ? locHyp.get(loc): 
					new LinkedHashMap<String, Double>();
				List<String> hm2L = new ArrayList<String>(hm2.keySet());


				Map<String, Double> hmNew = new LinkedHashMap<String, Double>();

				for (int r = 0; r < hm2.size(); r++) {
					String hypWord = hm2L.get(r);
					double valSm = hm2.get(hm2L.get(r));


					double valNew = hmNew.containsKey(hypWord) ?
							hmNew.get(hypWord) + valSm: valSm;


					hmNew.put(hypWord, valNew);
				}

				String spl[] = twWHL.get(i).split("hypernyms: ")[1].trim().split("__")[0].split(", ");

				List<String> lHm = new ArrayList<String>(hmNew.keySet());

				for (int u = 0; u < spl.length; u++) {
					String word = spl[u].split(" ")[0];
					String freq = spl[u].split(" ")[1];
					if (lHm.contains(word)) {
						hm2.put(word, (hm2.get(word) +
								Double.parseDouble(freq)));
					}
					else {
						hm2.put(word, Double.parseDouble(freq));
					}

				}
				locHyp.put(loc, hm2);


			}
			List<String> lox = new ArrayList<String>(locHyp.keySet());

			for (int j = 0; j < locHyp.size(); j++) {
				List<Map.Entry<String, Double>> ls =
						new LinkedList<Map.Entry<String, Double>>(locHyp.get(lox.get(j)).entrySet());
				Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
						if (m1.getValue() < m2.getValue())
							return 1;
						else if (m1.getValue() > m2.getValue())
							return -1;
						return 0;
					}

				});


			}

			Map<String, List<Map.Entry<String, Double>>> sortedParts = new
					LinkedHashMap<String, List<Map.Entry<String, Double>>>();
			for (int j = 0; j < locHyp.size(); j++) {

				List<Map.Entry<String, Double>> ls =
						new LinkedList<Map.Entry<String, Double>>(locHyp.get(lox.get(j)).entrySet());

				Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {

						return -Double.compare(m1.getValue(), m2.getValue());
					}

				});

				ls = ls.subList(0, ls.size() >= 10 ? 10 : ls.size());

				sortedParts.put(lox.get(j), ls);

			}
			List<String> sortedPartsKeys = new ArrayList<String>(sortedParts.keySet());

			List<String> times = new ArrayList<String>(locHyp.keySet());






			//Clustering operations . .
			Map<Integer, LinkedHashMap<String, Double>> clHyp = new
					LinkedHashMap<Integer,LinkedHashMap<String, Double>>();
			BufferedReader br__ = new BufferedReader(new FileReader("clusterTrain.txt"));
			String ll = "";
			while ((ll = br__.readLine()) != null) {
				int clNo = Integer.parseInt(ll.substring(ll.indexOf("[") + 1, ll.indexOf("]")));
				Map<String, Double> tmp = clHyp.containsKey(clNo) ? clHyp.get(clNo): 
					new LinkedHashMap<String, Double>();

				String[] strArr = ll.substring(ll.indexOf("]") + 1).split(",");
				for (int i = 0; i < strArr.length; i++) {
					Map<String, Double> tmpHyp = locHyp.get(strArr[i]);

					for (Map.Entry<String, Double> e: tmpHyp.entrySet()) {
						double val = tmp.containsKey((String)e.getKey()) ? (double) tmp.get((String)e.getKey())
								: 0.;
						val += (double) e.getValue();
						tmp.put((String) e.getKey(), (double) val);
					}
				}


				List<Map.Entry<String, Double>> ls =
						new LinkedList<Map.Entry<String, Double>>(tmp.entrySet());
				Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {

						return -Double.compare(m1.getValue(), m2.getValue());
					}

				});
				ls = ls.subList(0, ls.size() >= 10 ? 10: ls.size());
				Map<String, Double> lsMap = new LinkedHashMap<String, Double>();
				for (Map.Entry<String, Double> e: ls) {
					lsMap.put(e.getKey(), e.getValue());
				}
				clHyp.put(clNo, (LinkedHashMap<String, Double>)lsMap);
			}
			br__.close();
			BufferedReader bre = new BufferedReader(new FileReader("clusterTest.txt"));
			String lStr = "";
			Map<String, LinkedHashMap<String, Double>> testCl = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
			while((lStr = bre.readLine()) != null) {
				lStr = lStr.replaceAll("[\\s\\']+", "");
				String[] spl = lStr.split(":");
				String id = spl[0];
				int cl = Integer.valueOf(spl[1]);

				List<String> l_ = userIdTw.get(id);
				String str = "";
				for (Map.Entry<String, Double> e: clHyp.get(cl).entrySet()) {
					str += e.getKey() + " " + e.getValue() + ",";
				}
				str = str.substring(0, str.length() - 1);
				testCl.put(id, clHyp.get(cl));
			}
			bre.close();
			//Clustering operations ended here . .

			int trSize = (int) Math.ceil((double) locHyp.size() / 10 * 7);


			FileWriter htm = new FileWriter(fileName);


			htm.write("<html>"
					+ "<body background=\"twitter-pic2.jpg\">");
			htm.write("<head>");
			htm.write("<style>");
			htm.write("body {background-attachment:fixed;}");
			htm.write("#header {" +
					"background-color:black;" +
					"color:white;" +
					"text-align:center;" +
					"padding:5px;" +
					"width:922px;" +
					"}");
			htm.write("#nav {" +
					"line-height:30px;" +
					"background-color:#bfbfbf;" +
					"height:300px;" +
					"width:100px;" +
					"float:left;" +
					"background-color: rgba(188, 188, 188, 0.92);"
					+ "}");
			htm.write("#section {" +
					"line-height:30px;" +
					"position:static;" +
					"width:822px;" +
					"float:right;" +
					"height:300px;" +

					"background-color: rgba(255, 255, 255, 0.88);" +
					"}");
			htm.write("#footer {" +
					"background-color:black;" +
					"color:white;" +
					"clear:both;" +
					"text-align:center;" +
					"width:922px;" +
					"padding:5px;" +
					"}");

			htm.write("#navSec {" +
					"background-color: rgba(222, 222, 222, 0.16);" +
					"}");

			htm.write("#cont {" +
					"background-color: rgba(222, 222, 222, 0.36);" +
					"}");
			htm.write("table, tr, td {" +
					"background-color: rgba(222, 222, 222, 0.36);"
					+ "}");

			htm.write("#blackDiv {"
					+ "background-color: rgba(5, 5, 5, 0.74);"
					+ "height:200px;"
					+ "color:white;"
					+ "clear:both;"
					+ "height:100px;"

					+ "}");

			htm.write("</style>");
			htm.write("</head>");



			htm.write("<center><div style = \"width:922px;\"><div id = \"header\">" +
					"<h1>Generating Semantic User Profile in Twitter "
					+ "through Concept Mining</h1>" +
					"<br><br></div>" +
					"<br><br><br>");
			htm.write("<div id = \"navSec\"><div id = \"nav\">");

			htm.write("<a href = \"#table" + (trSize + 1) +"\">Test tables</a><br>");

			//			htm.write("Enter Table No:<br>");
			//			htm.write("<input type=\"text\" id = \"txt\" onfocus=\"func()\" width = \"6\"/>");
			htm.write("</div>");
			htm.write("<div id = \"section\">" +
					"<h2>Extracting concepts from tweets for generating user profile</h2>" +
					"<p>" +
					"In this page, the first 117 tables represent "
					+ "the concepts extracted from each Twitter user "
					+ "based on their tweets."
					+ "</p><p>"
					+ "The last 49 tables represent the test data, which are "
					+ "the remaining Twitter users' tweets, as well as the concepts extracted "
					+ "from them. The clustering concept section on the right of "
					+ "each of these tables are those extracted through clustering "
					+ "the training data that are listed atop the page." +
					"</p>" +
					"</div>");

			htm.write("<div id = \"footer\">");
			htm.write("An Approach for Extracting User Profiles in "
					+ "Twitter");

			htm.write("</div></div></center>");
			htm.write("<br><br>");
			htm.write("<br><br><br>");

			htm.write("<div id = \"cont\"><center>");

			List<String> setTw = new ArrayList<String>(locHyp.keySet());
			int setCnt = 0;



			//matr.txt

			//end

			int tableNo = 1;

			Set<String> concList = new LinkedHashSet<String>();

			List<String> timeCollList = new ArrayList<String>();
			for (int j = 0; j < locHyp.size(); j++) {

				List<Map.Entry<String, Double>> ls =
						new LinkedList<Map.Entry<String, Double>>(locHyp.get(times.get(j)).entrySet());
				Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {

						return -Double.compare(m1.getValue(), m2.getValue());
					}

				});

				if (setCnt % 1 == 0) {
					//					if (setCnt >= secs * partsAm)
					//						break;
					if (setCnt > 0)
						htm.write("</table><br><br>");

					htm.write("<table id = \"table" + (tableNo) + "\">");



					htm.write("<table border = \"1\">");

					htm.write("<tr>");

					if (j >= trSize)
						htm.write("<td colspan = \"3\" style = \"clear:both; width:300px;\"><center>");
					else
						htm.write("<td colspan = \"2\" style = \"clear:both; width:300px;\"><center>");



					if (setCnt != 0)
						htm.write("<a href = \"#table" + (tableNo - 1) +"\">&laquo;previous</a>");

					if (j >= trSize)
						htm.write("<b><i>&nbsp;&nbsp;&nbsp; Test table " + (tableNo - trSize) + "&nbsp;&nbsp;&nbsp;"
								+ "</i></b>");
					else
						htm.write("<b><i>&nbsp;&nbsp;&nbsp; Training table " + tableNo + "&nbsp;&nbsp;&nbsp;"
								+ "</i></b>"); 

					if (tableNo != locHyp.size() - 1)
						htm.write("<a href = \"#table" + (tableNo + 1) +"\">next&raquo;</a>");

					tableNo++;
					htm.write("<div id = \"blackDiv\">");
					htm.write("<bold><h1>User ID: " + times.get(j)+ "</h1></bold>");
					htm.write("</div>");

					htm.write("</tr>");


				}
				setCnt++;

				htm.write("<tr>");

				htm.write("<td style = \"background-color: rgba(255, 255, 255, 0.72);\">");
				//Below, the concepts extracted are written into the file
				htm.write("<center><b><i><h4><u>CONCEPTS</u></h4></i></b></center><br>");

				List<String> hypsHighl = new ArrayList<String>();
				for (int i = 0; i < ls.size() && i < 10; i++) {



					if (ls.get(i).getValue() != 0.) {
						hypsHighl.add(ls.get(i).getKey());
						if (i < ls.size() - 1 && i < 9) {
							if (!concList.contains(ls.get(i).getKey()))
								concList.add(ls.get(i).getKey());
							htm.write("<span style=\"background-color: " + colours[i] + "\">" + ls.get(i).getKey() + "</span> " + ls.get(i).getValue() + "<br>");
						}
						else
						{
							htm.write("<span style=\"background-color: " + colours[i] + "\">" + ls.get(i).getKey() + "</span> " + ls.get(i).getValue() + "<br>");
						}
					}
				}

				htm.write("</div></td>");

				htm.write("<td>");

				List<String> userTweets = userIdTw.get(times.get(j));

				//Raw tweets are written into the HTML file through the below
				//code
				htm.write("<center><b><u>Tweets</u></b></center><br><br><br>");
				for (int b = 0; b < userTweets.size(); b++) {
					htm.write("<i>Tweet</i> (" +b + "): ");
					String tokLine = userTweets.get(b).replaceAll("([\\.\\,\\!\\?:;\\-\\_]{1,})", " $1 ");
					//					htm.write("<br>" + userTweets.get(b) + "<br>");
					String[] spl = tokLine.split(" ");

					String htmlLine = "";
					for (int q = 0; q < spl.length; q++) {
						String word = spl[q];


						String rem = "";
						String wordStem = stem(word);
						if (!wn.nouns.contains(word))
							if (wordStem.length() != 0)
								if (wn.nouns.contains(wordStem)) {
									rem = word.substring(wordStem.length());
									word = wordStem;
								}
						if (word.length() == 1)
							htmlLine += word + " ";
						else if (Pattern.compile("[a-zA-Z]+").matcher(word).matches() && wn.nouns.contains(word)) {

							if (hypsHighl.contains(wn.hypernym(word))) {

								htmlLine += "<span style=\"background-color: " + colours[hypsHighl.indexOf(wn.hypernym(word))] + "\">" + word + "</span>" + rem + " ";
							} 
							else
								htmlLine += word + rem + " ";
						}
						else {
							htmlLine += word + " ";
						}
					}
					htm.write(htmlLine.replaceAll("[ ]+([\\.\\,\\!\\?:;\\-\\_]{1,})[ ]+", "$1 ") + "<br>");
				}

				htm.write("</td>");
				if (j >= trSize) {
					htm.write("<td style = \"background-color: rgba(122, 122, 122, 0.72);\">");
					htm.write("<center><b><i><h4><u>CLUSTER CONCEPTS</u></h4></i></b></center><br>");
					LinkedHashMap<String, Double> hm_ = testCl.get(times.get(j));
					//					List<Map.Entry<String, Double>> ll_ = new ArrayList<Map.Entry<String,Double>>(hm_.entrySet());
					htm.write("<center>");
					for (Map.Entry<String, Double> e: hm_.entrySet())
						htm.write(e.getKey() + " " + e.getValue() + "<br>");
					htm.write("</center>");
					htm.write("</td>");
				}
				htm.write("</tr>");
				if (setCnt == setTw.size() - 1) {
					htm.write("</table><br><br>");
				}
			}
			htm.write("</center>");
			htm.write("</div>");


			htm.write("</body>");

			htm.write("</html>");
			htm.close();


			FileWriter fw__ = new FileWriter("usersId.txt");

			for (int i = 0; i < times.size(); i++)
				if (i == times.size() - 1)
					fw__.write(times.get(i));
				else
					fw__.write(times.get(i) + ",");
			fw__.close();
			setCnt = 0;
			FileWriter fw_ = new FileWriter("matr.txt");
			List<String> concList_ = new ArrayList<String>(concList);

			for (int j = 0; j < locHyp.size(); j++) {

				//				List<Map.Entry<String, Double>> ls =
				//						new LinkedList<Map.Entry<String, Double>>(locHyp.get(times.get(j)).entrySet());
				//				Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
				//					public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
				//
				//						return -Double.compare(m1.getValue(), m2.getValue());
				//					}
				//

				//				});

				List<Map.Entry<String, Double>> sortedTop = sortedParts.get(sortedPartsKeys.get(setCnt));
				//				int cntSort = 0;
				String sb = times.get(j) + ",";
				fw_.write(times.get(j) + ",");
				for (int r = 0; r < concList_.size(); r++) {

					String conc = concList_.get(r);
					boolean fnd = false;

					for (Map.Entry<String, Double> e: sortedTop) {
						//						htm.write((String) e.getKey() + "\t" + (Double) e.getValue() + "<br>");
						//						if (cntSort++ == 10)
						//							break;
						if (conc.equals((String) e.getKey())) {
							DecimalFormat formatter = new DecimalFormat("#0.00");
							double d = (Double)e.getValue();
							String val_ = formatter.format(d);
							sb += val_ + ",";
							fnd = true;
							break;
						}

					}
					if (fnd == false)
						sb += "0.,";
				}
				sb = sb.substring(0, sb.length() - 1);
				fw_.write(sb + "\n");
				setCnt++;
			}
			fw_.close();



		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}