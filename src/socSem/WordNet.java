package socSem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WordNet {


	static Map<String, String> wordId = new LinkedHashMap<String, String>();
	static Map<String, String> hypernymyPre = new LinkedHashMap<String, String>();
	static Map<String, String> hypernymy = new LinkedHashMap<String, String>();



	static Map<String, Integer> nounFreqs = new LinkedHashMap<String, Integer>();

	static List<String> nouns = new ArrayList<String>();
	public static int lev = 4;

	static {
		try {
			BufferedReader br = new BufferedReader(new FileReader("data.noun"));
			String line;
			while ((line = br.readLine()) != null) {
				List<String> splLine = new ArrayList<>(Arrays.asList(line.toLowerCase().split(" ")));

				int ind = -1;
				if (splLine.contains("@"))
					ind = splLine.indexOf("@");

				if (splLine.contains("@i"))
					ind = splLine.indexOf("@i");

				if (ind > 0) {
					wordId.put(splLine.get(0), splLine.get(4));
					String tmp = hypernymyPre.containsKey(splLine.get(4)) ?
							hypernymyPre.get(splLine.get(4)) + "|" + splLine.get(ind + 1)
							:
								splLine.get(ind + 1)
								;
							hypernymyPre.put(splLine.get(4), tmp);
							nouns.add(splLine.get(4));
				}


			}

			br.close();

			List<String> l = new ArrayList<>(hypernymyPre.keySet());
			for (int i = 0; i < hypernymyPre.size(); i++) {
				String[] word = hypernymyPre.get(l.get(i)).split("\\|");
				String hyp = "";
				for (int j = 0; j < word.length; j++) {
					if (j != word.length - 1)
						hyp += wordId.get(word[j]) + " ";
					else
						hyp += wordId.get(word[j]);
				}
				hypernymy.put(l.get(i), hyp);
			}

			BufferedReader freq = new BufferedReader(new FileReader(new File("noun_freq.txt")));

			String freqLin = "";
			while ((freqLin = freq.readLine()) != null) {
				freqLin = freqLin.trim();
				nounFreqs.put(freqLin.split("\\s+")[0], Integer.parseInt(freqLin.split("\\s+")[1]));
			}
			freq.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String hypernym(String s) {
		String [] arr = hypernymy.get(s).split(" ");
		String word = arr[0];

		int maxNo = -1;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i].length() >= 10)
				if (arr[i].substring(0, 10).equals("president_")) {
					word = arr[i];
					break;
				}
			if (nounFreqs.containsKey(arr[i])) {
				if (nounFreqs.get(arr[i]) > maxNo) {
					maxNo = nounFreqs.get(arr[i]);
					word = arr[i];
				}
			}
		}
		return word;
	}
	public static Map<Integer, String> hypernymMultiLevel(String s, int i) {
		Map<Integer, String> m =
				new LinkedHashMap<Integer, String>();

		if (i < 0)
			System.exit(1);
		m.put(i, s);
		i -= 1;
		List<String> l = new ArrayList<String>(Arrays.asList(hypernym(s).split(" ")));
		Set<String> hs = new HashSet<>();
		while (i >= 0) {

			List<String> tmpL = new ArrayList<String>();

			for (int j = 0; j <l.size(); j++) {
				if (!hs.contains(l.get(j)))
				{
					hs.add(l.get(j));
					String tmpS = m.containsKey(i) ? 
							m.get(i) + " " + l.get(j):
								l.get(j);
							m.put(i, tmpS);
							if (hypernym(l.get(j)) != null)
								tmpL.addAll(Arrays.asList(hypernym(l.get(j)).split(" ")));	
				}

			}
			l = tmpL;
			i--;
		}
		return m;
	}
	public static String commHyper (String s1, String s2) {
		Map<Integer, String> m1 = hypernymMultiLevel(s1, lev);
		Map<Integer, String> m2 = hypernymMultiLevel(s2, lev);
		int min = Integer.MAX_VALUE;
		String tmp = "";
		outer:
			for (int i = lev; i >= 0; i--) {
				List<String> arr1 = Arrays.asList(m1.get(i).split(" "));
				for (int j = lev; j >= 0; j--) {
					List<String> arr2 = Arrays.asList(m2.get(j).split(" "));
					for (int k = 0; k < arr2.size(); k++)
						if (arr1.contains(arr2.get(k)))
						{
							tmp = arr2.get(k);
							int diff = Math.abs(i - j);
							tmp += "|" + String.valueOf(diff);

							break outer;


							//						if (diff < min) {
							//							min = diff;
							//							tmp = arr2.get(k);
							//						}
						}
				}
			}

		return tmp;
	}
	public static String commHyperAll (List<String> l) {



		Map<String, Double> m =
				new LinkedHashMap<>();
		for (int i = 0; i < l.size(); i++) {
			int min = Integer.MAX_VALUE;
			String word = "";
			int levDif = -1;
			if (l.size() == 1) {
				levDif = 0;
				word = hypernym(l.get(i)).split(" ")[0];
			}
			for (int j = 0; j < l.size(); j++) {

				if (i != j) {
					if (l.get(i).equals(l.get(j))) {
						word = hypernym(l.get(i));
						levDif = 0;
						break;
					}
					String s = commHyper(l.get(i), l.get(j));
					String com = s.split("\\|")[0];

					if (s.length() == 0)
						continue;
					int levDiff = Integer.parseInt(s.split("\\|")[1]);


					if (min > levDiff) {
						min = levDiff;
						word = com;
						levDif = levDiff;
					}
				}

			}

			int val = levDif;

			//			//System.out.println("vv: " + val);

			double res = m.containsKey(word) ?
					m.get(word) + 1 / (double) (1 + val)
					:
						1 / (double) (1 + val);
					m.put(word, res);	
		}



		List<Map.Entry<String, Double>> ls =
				new LinkedList<Map.Entry<String, Double>>(m.entrySet());
		Collections.sort(ls, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
				if (m1.getValue() < m2.getValue())
					return 1;
				else if (m1.getValue() > m2.getValue())
					return -1;
				return 0;
			}

		});

		//System.out.print("Hierarchical SUM: ");
		for (int i = 0; i < ls.size(); i++)
			if (i < ls.size() - 1) {
				//System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue() + ", ");
			}
			else
			{
				//System.out.print(ls.get(i).getKey() + " " + ls.get(i).getValue());
			}
		//System.out.println();
		return "";
	}
	public static String simpleHypSum(List<String> l) {
		Map<String, Integer> m =
				new LinkedHashMap<>();

		for (int i = 0; i < l.size(); i++) {
			String[] arr = hypernym(l.get(i)).split(" ");

			for (int j = 0; j < arr.length; j++) {
				String hyp = arr[j];
				int val = m.containsKey(hyp) ?
						m.get(hyp) + 1:
							1;
				m.put(hyp, val);
			}

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
		String result = "";
		for(Map.Entry<String, Integer> me: lst)
			result += me.getKey() + " " + me.getValue() + ", ";
		result = result.substring(0, result.length() - 2);
		//		System.out.println("Simple SUM: " + result);
		return result;
	}

	public static class Tree<T> {
		Node root;

		public Tree (T el) {
			Node root = new Node(el);
			this.root = root;
		}
		public void addNode (String s) {
			this.root.children.add(new Node(s));

		}

		public class Node<T> {
			T el;
			List<Node> children;
			public Node (T el) {
				this.el = el;
				this.children = null;
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		WordNet wn = new WordNet();

		//		//System.out.println(hypernym("cat"));
		//		//System.out.println(hypernym("dog"));
		//		//System.out.println(hypernym("earth"));
		//		//System.out.println(hypernym("country"));
		//		
		String w1 = "europe";
		String w2 = "asia";

		List<Integer> l = new ArrayList<>(hypernymMultiLevel(w1, lev).keySet());
		for (int i = l.size() - 1; i >= 0; i--);
		//System.out.println(l.get(i) + " " + hypernymMultiLevel(w1, lev).get(l.get(i)));
		List<Integer> l2 = new ArrayList<>(hypernymMultiLevel(w2, lev).keySet());
		//System.out.println("____");
		for (int i = l2.size() - 1; i >= 0; i--);
		//System.out.println(l2.get(i) + " " + hypernymMultiLevel(w2, lev).get(l2.get(i)));

		commHyperAll(Arrays.asList("painter", "portrait", "drawing", "sculpture", "music", "singer",
				"ballet", "dancing", "theater"
				));

		//System.out.println("||||||");
		simpleHypSum(Arrays.asList("europe", "asia", "brazil", "japan", "turkey", "germany", "world"));
		commHyperAll(Arrays.asList("europe", "asia", "brazil", "japan", "turkey", "germany", "world"));

		//System.out.println("|||||");
		simpleHypSum(Arrays.asList("electricity", "lamp", "light", "photon"));
		commHyperAll(Arrays.asList("electricity", "lamp", "light", "photon"));

		//System.out.println("***");

		commHyperAll(Arrays.asList("europe"));


		System.out.println("///");
		System.out.println(simpleHypSum(Arrays.asList("europe")));

		System.out.println(hypernym("europe") + " ___");

		System.out.println("jklhjl".split(" ")[0]);
		//		simpleHypSum(Arrays.asList("europe"));
		//		simpleHypSum(Arrays.asList("europe", "asia", "france", "brazil"));
		//System.out.println("||||");
		//System.out.println(hypernym("europe"));

		//System.out.println("*******");
		//System.out.println(simpleHypSum(Arrays.asList("back", "music")));
		//System.out.println(hypernym("back") + " _ " + "back");
		//System.out.println(hypernym("music") + " _ " + "music");
	}


}
