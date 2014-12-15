package twitFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class TwFileMain {



	public static void tweets(String fileName, int lmt) {

//		fileName = "lim_tra_set.txt";
//		lmt = 1700;
		try {

			File f = new File("test");
			f.mkdir();
			Map<String, ArrayList<String>> tw = new LinkedHashMap<String, ArrayList<String>>();
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

			String line = "";
			String wholeTw;
			while ((line = br.readLine()) != null) {

				wholeTw = line;

				line = line.toLowerCase(Locale.ENGLISH);
				//				Pattern p = Pattern.compile("([0-9]*[ ]*[0-9]*[ ]*)()([ ]*[0-9\\-]*)[ ]*[0-9:]*)( )?");
				line = line.replaceAll("([0-9]*\\s*[0-9]*\\s*)(.*)(\\s*[0-9\\-]*\\s*[0-9:]*)( )?", "$2");
				line = line.replaceAll("([0-9:\\-\\s]*)$", "");
				String userId = wholeTw.split("\\s+")[0];


				line = line.replaceAll(" http(s)?:([^ ])+", " ");
				line = line.replaceAll("([\\.\\,\\!\\?:;\\-\\_]{1,})", " $1 ");


				ArrayList<String> userIdLine = (tw.containsKey(userId)) ? tw.get(userId):
					new ArrayList<String>();

				userIdLine.add(line);


				tw.put(userId, userIdLine);

			}
			br.close();


			int cnt = 0;

			List<Map.Entry<String, ArrayList<String>>> lst = new ArrayList<Map.Entry<String,ArrayList<String>>>(tw.entrySet());

			for (Entry e: lst) {

				if (cnt++ > lmt)
					break;
				FileWriter fw = new FileWriter(new File(f, (String) e.getKey() + ".txt"));

				ArrayList<String> al = (ArrayList<String>) e.getValue();
				for (int i = 0; i < al.size(); i++) {
					fw.write(al.get(i) + "\n");
				}

				fw.close();
			}

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
		tweets("lim_tra_set.txt", 1700);
	}

}
