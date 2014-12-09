package socSem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseTweets {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		WordNet wn = new WordNet();




		List<ArrayList<String>> tweets = new
				ArrayList<ArrayList<String>>();

		Set<String> twWords = new
				HashSet<String>();

		Map<String, ArrayList<String>> allHashes = new
				HashMap<String, ArrayList<String>>();

		Map<String, String> hashHyp = new HashMap<String, String>();


		Map<String, String> hashMostCm = new HashMap<String, String>();


		List<String> twWHL = new ArrayList<String>();

		Map<String, ArrayList<String>> userIdTw = new HashMap<String, ArrayList<String>>();

		try {
			//			BufferedReader br = new BufferedReader(new FileReader(new File("C:\\Users\\asus\\Documents\\training_set_tweets.txt")));
			BufferedReader br = new BufferedReader(new FileReader(new File("lim_tra_set.txt")));

			String line = "";
			int cnt = 0;



			//Tweet No + the whole tweet
			Map<Integer, String> twRowNo = new LinkedHashMap<Integer, String>();

			String wholeTw = "";
			int rowNo = 0;

			while ((line = br.readLine()) != null && cnt++ < 1800) {

				wholeTw = line;
				String rawTweet = "";
				//				System.out.println(line);
				line = line.toLowerCase();
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
						if (wn.nouns.contains(strArr[i])) {
							tmp.add(strArr[i]);
							twWords.add(strArr[i]);

							twNorm = true;
							if (!nonHashes.contains(strArr[i]))
								nonHashes.add(strArr[i]);



						}
						else if (strArr[i].charAt(0) == '#') {
							tmp.add(strArr[i]);
							//							twRowNo.put(rowNo++, wholeTw);
							hashes.add(strArr[i]);
							twNorm = true;
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
			Map<String, String> tmpTwHypNo = new HashMap<String, String>();
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

			Map<String, Double> preTfidf = new HashMap<String, Double>();

			line = "";
			for (int i = 0; i < hypAll.size(); i++) {
				preTfidf.put(hypAll.get(i), 0.);
				line += hypAll.get(i) + ",";
				line = line.substring(0, line.length() - 1);
				fwHyp.write(line);
			}

			fwHyp.close();

			List<String> aftMatr = new ArrayList<String>();

			FileWriter fw2 = new FileWriter(new File("matr.txt"));
			for (int y = 0; y < hypTw.size(); y++) {
				List<String> sub = hypTw.get(y);
				fw2.write("tw" + y +":");
				StringBuilder line2 = new StringBuilder("");

				//				List<String> subSpl = new ArrayList<String>();
				Map<String, String> hm = new HashMap<String, String>(); 
				for (int w = 0; w < sub.size(); w++) {
					//					subSpl.add(sub.get(w).split(" ")[0]);
					hm.put(sub.get(w).split(" ")[0], sub.get(w).split(" ")[1]);
				}

				for (int u = 0; u < hypAll.size(); u++) {
					if (hm.containsKey(hypAll.get(u))) {
						preTfidf.put(hypAll.get(u), (preTfidf.get(hypAll.get(u)) + 1));
						line2.append(hm.get(hypAll.get(u)));
						line2.append(",");
					}
					else
						line2.append("0,");
				}

				fw2.write(line2.substring(0, line2.length() - 1));
				fw2.write("\n");
				aftMatr.add(line2.substring(0, line2.length() - 1));
			}

			fw2.close();


//			System.out.println("time_period: " + preTfidf.get("time_period"));

			FileWriter fwN = new FileWriter(new File("matr.txt"));

			Map<String, Double> postTfidf = new HashMap<String, Double>();

//			System.out.println("hypAll: " + hypAll.size());


			for (int i = 0; i < aftMatr.size(); i++) {
				String[] strSpl = aftMatr.get(i).split(",");
				StringBuilder val = new StringBuilder("");
				for (int j = 0; j < strSpl.length; j++) {
					double newV = /*Double.valueOf(strSpl[j]) * */Math.log10(twWHL.size() / preTfidf.get(hypAll.get(j)));

					//					System.out.println("++++: " + (Double.valueOf(strSpl[j]) * Math.log(twWHL.size() / preTfidf.get(hypAll.get(j)))));

					val.append(String.valueOf(newV));
					val.append(",");
					postTfidf.put(hypAll.get(j), newV);
					//					System.out.println("log: " +Math.log(twWHL.size() / preTfidf.get(hypAll.get(j))));
				}
				val = val.deleteCharAt(val.length() - 1);
				fwN.write(val + "\n");
			}
			fwN.close();


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
			System.out.println("First loop finished..");


			ArrayList<String> al = new ArrayList<String>(twRowNo.values());
			Map<Integer, String> twRowNoUpd = new LinkedHashMap<Integer, String>();
			for (int i = 0; i < al.size(); i++) {
				twRowNoUpd.put(i, al.get(i));
			}
			twRowNo = twRowNoUpd;



			//Below is clustering operation being performed(input produced by MATLAB)
			Map<Integer, List<Map.Entry<String, Integer>>> clusters = new LinkedHashMap<Integer, List<Map.Entry<String, Integer>>>();
			BufferedReader br3 = new BufferedReader(new FileReader(new File("clusterNos.txt")));

			String lineCl = "";



			//			System.out.println("tw74: " + twHypNo.get("tw74"));

			while ((lineCl = br3.readLine()) != null) {

				//All the tweet numbers contained in clusters
				List<String> clList = Arrays.asList(lineCl.substring(lineCl.indexOf(']') + 1).split(","));


				//Below is the map containing the hypernyms of tweets that are in a
				//specific cluster
				Map<String, Integer> subHypCl = new HashMap<String, Integer>();

				for (int i = 0; i < clList.size(); i++) {


					//					System.out.println("clList: " + clList.get(i));
					//The hypernyms representing a tweet
					String[] hyps = twHypNo.get(clList.get(i)).split(", ");
					//All cluster hypernyms are getting collected, and get assigned to
					//the hashMap
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


			//Loc - UserId mapping
			Map<String, String> locUId = new HashMap<String, String>();

			BufferedReader br2 = new BufferedReader(new FileReader(new File("training_set_users.txt")));
			line = "";

			System.out.println("UserId - Location mapping is being performed..");

			Map<String, HashSet<String>> locUIdRev = new HashMap<String, HashSet<String>>();

			int cnta = 0;
			while ((line = br2.readLine()) != null) {

				//				line = line.toLowerCase();
				String[] arrSpl = line.split("[\\s]+");
				StringBuilder conc = new StringBuilder("");
				for (int q = 1; q < arrSpl.length; q++) {
					conc.append(" ").append(arrSpl[q]);
				}
				conc = new StringBuilder(new String(conc).trim());

				String concStr = conc.toString();

				//				List<String> li = new ArrayList<String>(locUId.keySet());
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
				//					locUId.put(arrSpl[0], tmpLoc);
				//				else

				String uId = arrSpl[0];

				if (userIdTw.containsKey(uId)) {
					HashSet<String> tmpHs = locUIdRev.containsKey(concStr) ? locUIdRev.get(concStr): 
						new HashSet<String>();
					tmpHs.add(uId);
					locUIdRev.put(concStr, tmpHs);	
				}


				locUId.put(arrSpl[0], concStr.toString());

			}

			br2.close();



			Map<String, Map<String, Double>> locHyp = new 
					HashMap<String, Map<String, Double>>();


			for (int i = 0; i < twWHL.size(); i++) {
				String id = twWHL.get(i).split("__")[1];
				String loc = locUId.get(id);

				//				System.out.println("id: " + id + ", loc: " + loc);

				Map<String, Double> hm2 = (locHyp.containsKey(loc)) ? locHyp.get(loc): 
					new HashMap<String, Double>();
				List<String> hm2L = new ArrayList<String>(hm2.keySet());


				Map<String, Double> hmNew = new HashMap<String, Double>();

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

				System.out.print(lox.get(j) + " --> \n");
				System.out.print("\tTOPICS: ");
				for (int i = 0; i < ls.size() && i < 10; i++)
					if (i < ls.size() - 1 && i < 9) {
						System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue() + ", ");
					}
					else
					{
						System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue());
					}

				ArrayList<String> locUsers = new ArrayList<String>(locUIdRev.get(lox.get(j)));
				for (int q = 0; q < locUsers.size(); q++) {
					List<String> twits = userIdTw.get(locUsers.get(q));

					for (int c = 0; c < twits.size(); c++) {
						System.out.println("\t\t\t\tId: " + locUsers.get(q) + ", tweet: " + twits.get(c));
					}
				}
				
				System.out.println("");
			}

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

}


//4.1 times
//t.p.: 1.817672338223656
//corn: 7.427144133408616
//======== -> 1681