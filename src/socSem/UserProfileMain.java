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
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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


		//		try {
		//			BufferedReader br__ = new BufferedReader(new FileReader("training_set_users.txt"));
		//			String sl = "";
		//			List<String> al_ = new ArrayList<String>();
		//			while ((sl = br__.readLine()) != null)
		//				al_.add(sl);
		//
		//			br__.close();
		//			FileWriter fw_ = new FileWriter("training_set_users2.txt");
		//			for (int i = 0; i < al_.size(); i++) {
		//				fw_.write(al_.get(i).split("\\s+")[0] + " " + al_.get(i).split("\\s+")[0] + "\n");
		//			}
		//			fw_.close();
		//		} catch (FileNotFoundException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		} catch (IOException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}

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
			//			BufferedReader br = new BufferedReader(new FileReader(new File("C:\\Users\\asus\\Documents\\training_set_tweets.txt")));
			BufferedReader br = new BufferedReader(new FileReader(new File(traFile)));

			String line = "";
			int cnt = 0;



			//Tweet No + the whole tweet
			Map<Integer, String> twRowNo = new LinkedHashMap<Integer, String>();

			String wholeTw = "";
			int rowNo = 0;

			while ((line = br.readLine()) != null && cnt++ < tweetLimNo) {

				wholeTw = line;
				String rawTweet = "";
				//				System.out.println(line);
				line = line.toLowerCase(Locale.ENGLISH);
				//				Pattern p = Pattern.compile("([0-9]*[ ]*[0-9]*[ ]*)()([ ]*[0-9\\-]*)[ ]*[0-9:]*)( )?");
				line = line.replaceAll("([0-9]*\\s*[0-9]*\\s*)(.*)(\\s*[0-9\\-]*\\s*[0-9:]*)( )?", "$2");
				line = line.replaceAll("([0-9:\\-\\s]*)$", "");

				rawTweet = line;
				String userId = wholeTw.split("\\s+")[0];


				line = line.replaceAll(" http(s)?:([^ ])+", " ");
				line = line.replaceAll("([\\.\\,\\!\\?:;\\-\\_]{1,})", " $1 ");
				String[] strArr = line.split(" ");

				ArrayList<String> tmp =
						new ArrayList<String>();

				//				System.out.println(wholeTw);
				tmp.add(userId);



				ArrayList<String> nonHashes = new
						ArrayList<String>();

				ArrayList<String> hashes = new
						ArrayList<String>();


				boolean twNorm = false;
				for (int i = 0; i < strArr.length; i++) {
					if (strArr[i].length() > 1) {
						boolean isPl = false;
						if (wn.nouns.contains(strArr[i])) {
							tmp.add(strArr[i]);
							twWords.add(strArr[i]);

							twNorm = true;
							if (!nonHashes.contains(strArr[i]))
								nonHashes.add(strArr[i]);

							isPl = true;

						}
						else if (strArr[i].charAt(0) == '#') {
							tmp.add(strArr[i]);
							//							twRowNo.put(rowNo++, wholeTw);
							hashes.add(strArr[i]);
							twNorm = true;

							isPl = true;
						}
						int strLen = strArr[i].length();
						int indS = strArr[i].indexOf("s");
						int indMen = strArr[i].indexOf("men");
						if (isPl == false && 
								(indS == strLen - 1 || indMen == strLen - 3)){

							String wordStem = "";

							//							strArr[i].charAt(strArr[i].length() - 1)

							wordStem = stem(strArr[i]);
							if (wordStem.length() == 0)
								continue;

							if (wn.nouns.contains(wordStem)) {
								tmp.add(wordStem);
								twWords.add(wordStem);

								twNorm = true;
								if (!nonHashes.contains(wordStem))
									nonHashes.add(wordStem);

							}
						}
					}

				}

				if (twNorm == true) {
					twRowNo.put(rowNo++, wholeTw);

					ArrayList<String> userIdLine = (userIdTw.containsKey(userId)) ? userIdTw.get(userId):
						new ArrayList<String>();

					userIdLine.add(rawTweet);


					userIdTw.put(userId, userIdLine);
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

				//				System.out.println(tmp.toString());
				//				System.out.println(line);


			}
			//			System.out.println(twWords.size());
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
				//				Map<String, Integer> newHm = new LinkedHashMap<String, Integer>();


				//				ArrayList<String> alNew = new ArrayList<String>();

				//				for (Entry e: lst) {
				//					System.out.print("words: " + hashKeys.get(i) + ", key: " + e.getKey() + ", val: "
				//							+ e.getValue());
				//					alNew.add((String) e.getKey());
				//					//					newHm.put((String) e.getKey(), (Integer) e.getValue());
				//				}
				//				System.out.println();
				//				allHashes.put(hashKeys.get(i), alNew);


				hashMostCm.put(hashKeys.get(i), lst.get(0).getKey());


				hashHyp.put(hashKeys.get(i), wn.simpleHypSum(tmp).split(", ")[0]);


			}

			//			System.out.println("FACE: " + hashMostCm.get("#fb"));

			FileWriter fw = 
					new FileWriter("twitterWords.txt");
			//			
			//			
			//			String[] twWordsArr = twWords.toArray(new String[twWords.size()]);
			//			for (int i = 0; i < twWordsArr.length; i++)
			//				fw.write(twWordsArr[i] + "\n");


			List<List<String>> hypTw = new ArrayList<List<String>>();

			List<String> hypAll = new ArrayList<String>();


			//			System.out.println("TWEETS SIZE: " + tweets.size());



			int twNo = 0;


			//tweet - hypernyms (w/ # of occur.)
			Map<String, String> twHypNo = new LinkedHashMap<String, String>();

			for (int i = 0; i < tweets.size(); i++) {
				ArrayList<String> twAl = tweets.get(i);
				String userId = twAl.get(0);
				twAl.remove(0);
				String resHyp = "";

				if(twAl.size() == 0) {
					//					System.out.println("NOPE!!: " + twAl);
				}


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
					//					System.out.println(hashAft + " size: " + hashAft.get(0));
					resHyp = wn.simpleHypSum(hashAft);
				}
				else if (cntHasht == 0)
					resHyp = wn.simpleHypSum(twAl);
				else {

					nonHashAft.addAll(hashAft);
					//					System.out.println(nonHashAft);
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

				//				System.out.println(twAl.toString() + "//////" + res);


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








			//			System.out.println("hypAll: " + hypAll.size());
			//			System.out.println();

			FileWriter fwHyp = new FileWriter(new File("hypernames.txt"));

			Map<String, Double> preTfidf = new LinkedHashMap<String, Double>();

			line = "";
			for (int i = 0; i < hypAll.size(); i++) {
				if (ignoreIdf)
					preTfidf.put(hypAll.get(i), 1.);
				else 
					preTfidf.put(hypAll.get(i), 0.);
				line += hypAll.get(i) + ",";
				line = line.substring(0, line.length() - 1);
				fwHyp.write(line);
			}

			fwHyp.close();

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
						if (!ignoreIdf)
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
			for (Map.Entry ent: ls_) {

				if (elimMaxCnt++ < ls_.size() / 100 * 3) {
					preTfidf.put((String) ent.getKey(), 0.);
					sbStop.append((String) ent.getKey()).append(" ");
				}
			}
			sbStop.deleteCharAt(sbStop.length() - 1);

			fwSt.write(sbStop.toString());
			fwSt.close();



//			fw2.close();


			//			System.out.println("time_period: " + preTfidf.get("time_period"));

//			FileWriter fwN = new FileWriter(new File("matr.txt"));

			Map<String, Double> postTfidf = new LinkedHashMap<String, Double>();

			//			System.out.println("hypAll: " + hypAll.size());


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


					//					System.out.println("++++: " + (Double.valueOf(strSpl[j]) * Math.log(twWHL.size() / preTfidf.get(hypAll.get(j)))));

					val.append(String.valueOf(newV));
					val.append(",");
					postTfidf.put(hypAll.get(j), newV);
					//					System.out.println("log: " +Math.log(twWHL.size() / preTfidf.get(hypAll.get(j))));
				}
				val = val.deleteCharAt(val.length() - 1);
				//				fwN.write(val + "\n");
			}
//			fwN.close();


			//			System.out.println("For loop started..");
			for (int i = 0; i < twWHL.size(); i++) {
				String[] strSpl = twWHL.get(i).split("hypernyms: ")[1].split("__")[0].split(", ");
				String newVals = twWHL.get(i).split("nyms: ")[0] + "nyms: ";
				String last = twWHL.get(i).split("__")[1];
				last = "__" + last;
				for (int j = 0; j < strSpl.length; j++) {
					String word = strSpl[j].split(" ")[0];
					//					String freq = strSpl[j].split(" ")[1];
					double freq = Double.valueOf(strSpl[j].split(" ")[1]) * postTfidf.get(word);
					newVals += word + " " + freq + ", ";
				}
				newVals = newVals.substring(0, newVals.length());
				newVals += last;
				twWHL.set(i, newVals);
			}


			ArrayList<String> al = new ArrayList<String>(twRowNo.values());
			Map<Integer, String> twRowNoUpd = new LinkedHashMap<Integer, String>();
			for (int i = 0; i < al.size(); i++) {
				twRowNoUpd.put(i, al.get(i));
			}
			twRowNo = twRowNoUpd;


			/* CLUSTERING PART TO BE UNCOMMENTED IF TO BE PERFORMED

			//Below is clustering operation being performed(input produced by MATLAB)
			Map<Integer, List<Map.Entry<String, Integer>>> clusters = new LinkedHashMap<Integer, List<Map.Entry<String, Integer>>>();
			BufferedReader br3 = new BufferedReader(new FileReader(new File("clusterTrain.txt")));

			String lineCl = "";



			//			System.out.println("tw74: " + twHypNo.get("tw74"));

			while ((lineCl = br3.readLine()) != null) {

				//All the tweet numbers contained in clusters
				List<String> clList = Arrays.asList(lineCl.substring(lineCl.indexOf(']') + 1).split(","));


				//Below is the map containing the hypernyms of tweets that are in a
				//specific cluster
				Map<String, Integer> subHypCl = new LinkedHashMap<String, Integer>();

				for (int i = 0; i < clList.size(); i++) {


					//					System.out.println("clList: " + clList.get(i));
					//The hypernyms representing a tweet
					String[] hyps = twHypNo.get(clList.get(i)).split(", ");
					//All cluster hypernyms are getting collected, and get assigned to
					//the LinkedHashMap
					for (int j = 0; j < hyps.length; j++) {
						String hypWord = hyps[j].split(" ")[0];
						int valSm = Integer.parseInt(hyps[j].split(" ")[1]);
						int valNew = subHypCl.containsKey(hypWord) ?
								subHypCl.get(hypWord) + valSm: valSm;
						subHypCl.put(hypWord, valNew);
					}

				}

				//Clusters are represented through numbers, starting from 0
				List<Map.Entry<String, Integer>> ls =
						new LinkedList<Map.Entry<String, Integer>>(subHypCl.entrySet());
				Collections.sort(ls, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
						if (m1.getValue() < m2.getValue())
							return 1;
						else if (m1.getValue() > m2.getValue())
							return -1;
						return 0;
					}

				});



				clusters.put(Integer.parseInt(lineCl.substring(1, lineCl.indexOf(']'))), ls);
			}
			br3.close();


			//Below do we write the hypernyms, through which clusters are represented,
			//to a file
			FileWriter fw3 = new FileWriter(new File("clusterHypsFreq.txt"));

			//			List<Integer> l = new ArrayList<Integer>(clusters.keySet());
			for (int i = 0; i < clusters.size(); i++) {
				List<Map.Entry<String, Integer>> lSub = clusters.get(i);

				Map<String, Integer> hm = new LinkedHashMap<String, Integer>();

				for (Map.Entry<String, Integer> me: lSub) {
					hm.put(me.getKey(), me.getValue());
				}

				String line2 = "";
				for (int u = 0; u < hypAll.size(); u++) {
					if (hm.containsKey(hypAll.get(u)))
						line2 += hm.get(hypAll.get(u)) + ",";
					else
						line2 += "0,";
				}

				fw3.write(line2.substring(0, line2.length() - 1));
				fw3.write("\n");
			}
			fw3.close();

			 */


			//Loc - UserId mapping
			Map<String, String> UIdLoc = new LinkedHashMap<String, String>();

			BufferedReader br2 = new BufferedReader(new FileReader(new File("training_set_users2.txt")));
			line = "";

			System.out.println("UserId - Location mapping is being performed..");

			Map<String, LinkedHashSet<String>> locUIds = new LinkedHashMap<String, LinkedHashSet<String>>();

			int cnta = 0;
			while ((line = br2.readLine()) != null) {

				//				line = line.toLowerCase(Locale.ENGLISH);
				String[] arrSpl = line.split("[\\s]+");
				String uId = arrSpl[0];


				StringBuilder conc = new StringBuilder("");
				for (int q = 1; q < arrSpl.length; q++) {
					conc.append(" ").append(arrSpl[q]);
				}
				conc = new StringBuilder(new String(conc).trim());

				String concStr = conc.toString();


				//				List<String> li = new ArrayList<String>(UIdLoc.keySet());
				//				
				//				String tmpLoc = "";
				//				for (int i = 0; i < li.size(); i++) {
				//					String presLoc = li.get(i);
				//					if (presLoc.contains(conc) && presLoc.length() > conc.length()) {
				//						tmpLoc = presLoc;
				//						break;
				//					}
				//					
				//				}
				//				if (tmpLoc.length() > 0)
				//					UIdLoc.put(arrSpl[0], tmpLoc);
				//				else

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

				//				System.out.println("id: " + id + ", loc: " + loc);
				//				System.out.println(" ///: " + id + " - " + loc);


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


					//					System.out.println("line: " + twWHL.get(i));
					if (lHm.contains(word)) {
						hm2.put(word, (hm2.get(word) +
								Double.parseDouble(freq)));
					}
					else {
						hm2.put(word, Double.parseDouble(freq));
					}

					//					System.out.println("freq: " + freq);

				}
				locHyp.put(loc, hm2);


			}

			//			System.out.println("SIZE: " + locHyp.size());
			List<String> lox = new ArrayList<String>(locHyp.keySet());

			for (int j = 0; j < locHyp.size(); j++) {

				//				System.out.println("1: " + lox.get(j));
				//				System.out.println("2: " + locHyp.get(lox.get(j)).size());
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

				//				if (lox.get(j).substring(0, 3).equals("new")) {
				System.out.print(lox.get(j) + " --> \n");
				System.out.print("\tTOPICS: ");
				for (int i = 0; i < ls.size() && i < 10; i++)
					if (ls.get(i).getValue() != 0.)
						if (i < ls.size() - 1 && i < 9) {
							System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue() + ", ");
						}
						else
						{
							System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue());
						}
				System.out.println();

				ArrayList<String> locUsers = new ArrayList<String>(locUIds.get(lox.get(j)));
				for (int q = 0; q < locUsers.size(); q++) {
					List<String> twits = userIdTw.get(locUsers.get(q));

					for (int c = 0; c < twits.size(); c++) {
						System.out.println("\t\t\t\tId: " + locUsers.get(q) + ", tweet: " + twits.get(c));
					}
				}

				System.out.println("");

				//				}

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


//					List<Map.Entry<String, Double>> tmpHypList = new ArrayList<Map.Entry<String,Double>>(tmpHyp.entrySet());

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
				System.out.println("id: " + id);
				
				List<String> l_ = userIdTw.get(id);
				for (int i = 0; i < l_.size(); i++) {
					System.out.println("\t" + "tweet(" + i + "): " + l_.get(i));
				}
				System.out.print("\t\tCluster hypernyms: ");
				String str = "";
				for (Map.Entry<String, Double> e: clHyp.get(cl).entrySet()) {
					str += e.getKey() + " " + e.getValue() + ",";
				}
				str = str.substring(0, str.length() - 1);
				System.out.print(str + "\n");
				testCl.put(id, clHyp.get(cl));
			}
			bre.close();
			//Clustering operations ended here . .
			
			
			
			
			FileWriter htm = new FileWriter(new File("C:\\Users\\asus\\Desktop\\users2.htm"));
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
					"<h1>Concept Extraction from Tweets</h1>" +
					"<br><br></div>" +
					"<br><br><br>");
			htm.write("<div id = \"navSec\"><div id = \"nav\">");

			//			htm.write("Enter Table No:<br>");
			//			htm.write("<input type=\"text\" id = \"txt\" onfocus=\"func()\" width = \"6\"/>");
			htm.write("</div>");
			htm.write("<div id = \"section\">" +
					"<h2>Concepts extracted from the tweets on a time-basis</h2>" +
					"<p>" +
					"Below are the concepts extracted from the tweets concerned with"
					+ " the US elections (2012). These tweets grouped in accordance " +
					"</p>" +
					"</div>");

			htm.write("<div id = \"footer\">");
			htm.write("An Approach for Developing Concept Mining Methods in "
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
			int trSize = (int) Math.ceil((double) locHyp.size() / 10 * 7);
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
						htm.write("<td colspan = \"4\" style = \"clear:both; width:300px;\"><center>");
					
					

					if (setCnt != 0)
						htm.write("<a href = \"#table" + (tableNo - 1) +"\">&laquo;previous</a>");

					if (j >= trSize)
						htm.write("<b><i>&nbsp;&nbsp;&nbsp; Test table " + tableNo + "&nbsp;&nbsp;&nbsp;"
								+ "</i></b>");
					else
						htm.write("<b><i>&nbsp;&nbsp;&nbsp; Training table " + tableNo + "&nbsp;&nbsp;&nbsp;"
								+ "</i></b>"); 
					
					if (tableNo != locHyp.size() - 1)
						htm.write("<a href = \"#table" + (tableNo + 1) +"\">next&raquo;</a>");

					tableNo++;

					htm.write("<div id = \"blackDiv\">");
					htm.write("<bold><h1>" + times.get(j)+ "</h1></bold>");
					htm.write("</div>");


					htm.write("<div  style = \"background-color: rgba(88, 88, 88, 0.41);\">");

					htm.write("<br><b><u>TOP CONCEPTS</u></b><br><br><br>");

					List<Map.Entry<String, Double>> sortedTop = sortedParts.get(sortedPartsKeys.get(setCnt));
					int cntSort = 0;

					for (Map.Entry<String, Double> e: sortedTop) {
						htm.write((String) e.getKey() + "\t" + (Double) e.getValue() + "<br>");
						concList.add((String) e.getKey());
						if (cntSort++ == 15)
							break;
					}

					htm.write("<br></div></center></td></tr>");


				}
				setCnt++;

				htm.write("<tr>");
				htm.write("<td>");
				htm.write("<b><i>" + times.get(j) + "</i></b>");
				htm.write("</td>");
				htm.write("<td style = \"background-color: rgba(255, 255, 255, 0.72);\">");
				//Below, the concepts extracted are written into the file
				htm.write("<center><b><i><h4><u>CONCEPTS</u></h4></i></b></center><br>");

				List<String> hypsHighl = new ArrayList<String>();
				for (int i = 0; i < ls.size() && i < 10; i++) {



					if (ls.get(i).getValue() != 0.) {
						hypsHighl.add(ls.get(i).getKey());
						if (i < ls.size() - 1 && i < 9) {
							//							if (!concList.contains(ls.get(i).getKey()))
							//								concList.add(ls.get(i).getKey());
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
						if (Pattern.compile("[a-zA-Z]+").matcher(word).matches() && wn.nouns.contains(word)) {
							if (hypsHighl.contains(wn.hypernym(word))) {
								htmlLine += "<span style=\"background-color: " + colours[hypsHighl.indexOf(wn.hypernym(word))] + "\">" + word + "</span>" + rem + " ";
							}
						}
						else {
							htmlLine += word + " ";
						}
					}
					htm.write(htmlLine.replaceAll(" ([\\.\\,\\!\\?:;\\-\\_]{1,}) ", "$1 ") + "<br>");
				}

				htm.write("</td>");
				if (j >= trSize) {
					System.out.println("|||| | | |  " + times.get(j));
					htm.write("<td>");
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

			System.out.println("\n________________\n");
			
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
				//				fw_.write(times.get(j) + ",");
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



			


			//			System.out.println("_____");
			//			System.out.println("t.p.: " + postTfidf.get("time_period"));
			//			System.out.println("corn: " + postTfidf.get("corn"));


			//			System.out.println("======== -> " + twRowNo.size());


			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}//sheffield'i goster
//en frequent 10'unu ele


//4.1 times
//t.p.: 1.817672338223656
//corn: 7.427144133408616
//======== -> 1681