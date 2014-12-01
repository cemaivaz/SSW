package socSem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

		try {
			//			BufferedReader br = new BufferedReader(new FileReader(new File("C:\\Users\\asus\\Documents\\training_set_tweets.txt")));
			BufferedReader br = new BufferedReader(new FileReader(new File("lim_tra_set.txt")));

			String line = "";
			int cnt = 0;
			while ((line = br.readLine()) != null && cnt++ < 1800) {
				//				System.out.println(line);
				line = line.toLowerCase();
				//				Pattern p = Pattern.compile("([0-9]*[ ]*[0-9]*[ ]*)()([ ]*[0-9\\-]*)[ ]*[0-9:]*)( )?");
				line = line.replaceAll("([0-9]*\\s*[0-9]*\\s*)(.*)(\\s*[0-9\\-]*\\s*[0-9:]*)( )?", "$2");
				line = line.replaceAll("([0-9:\\-\\s]*)$", "");
				line = line.replaceAll(" http(s)?:([^ ])+", " ");
				line = line.replaceAll("([\\.\\,\\!\\?:;\\-\\_]{1,})", " $1 ");
				String[] strArr = line.split(" ");

				ArrayList<String> tmp =
						new ArrayList<String>();

				ArrayList<String> nonHashes = new
						ArrayList<String>();

				ArrayList<String> hashes = new
						ArrayList<String>();


				for (int i = 0; i < strArr.length; i++) {
					if (strArr[i].length() > 1) {
						if (wn.nouns.contains(strArr[i])) {
							tmp.add(strArr[i]);
							twWords.add(strArr[i]);

							if (!nonHashes.contains(strArr[i]))
								nonHashes.add(strArr[i]);
						}
						else if (strArr[i].charAt(0) == '#') {
							tmp.add(strArr[i]);
							hashes.add(strArr[i]);
						}
					}

				}
				if (tmp.size() > 0)
					tweets.add(tmp);

				if (hashes.size() > 1 && nonHashes.size() > 1) {
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
			System.out.println(twWords.size());
			br.close();

			
			ArrayList<String> hashKeys = new ArrayList<String>(allHashes.keySet());
			for (int i = 0; i < allHashes.size(); i++) {
				
			}


			FileWriter fw = 
					new FileWriter("twitterWords.txt");
			//			
			//			
			//			String[] twWordsArr = twWords.toArray(new String[twWords.size()]);
			//			for (int i = 0; i < twWordsArr.length; i++)
			//				fw.write(twWordsArr[i] + "\n");

			for (int i = 0; i < tweets.size(); i++) {
				ArrayList<String> twAl = tweets.get(i);
				String res = "";


				int cntHasht = 0;
				for (int j = 0; j < twAl.size(); j++) {
					if (twAl.get(j).charAt(0) == '#')
						cntHasht++;
					if ( j == twAl.size() - 1)
						fw.write(twAl.get(j));
					else
						fw.write(twAl.get(j) + ",");
				}

				if (cntHasht == twAl.size())
					System.out.println();
				else if (cntHasht == 0)
					res = wn.simpleHypSum(twAl);
				else
					System.out.println();


				System.out.println(twAl.toString() + "//////" + res);


				fw.write(" | hypernyms: ");



				fw.write("\n");
			}


			//			System.out.println("|||||____");
			//			System.out.println(wn.hypernym("france"));
			//			System.out.println("***");
			//			System.out.println(wn.hypernymy.get("france"));



			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
