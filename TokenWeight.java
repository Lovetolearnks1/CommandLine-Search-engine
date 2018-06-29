/**
* Program to calculate the term weight of the tokens that occur in each documents in collection
* @author  Vani Koduru Shankara Murthy
* @version 8.0
*/


import java.io.*;
import  java.util.*;

public class TokenWeight {
	
	/* tfCal(): Function which calculates the tf value of the tokeni
	 * (frequency of tokeni in documentj /total tokens in documentj)
	 * arguments: String, double, Map
	 * Return : tf value of type double
	 */
	public double tfCal(String str, double doclen, Map<String, Integer> map1){ 
		double tf;
		tf =(float) map1.get(str) / doclen;
		//tf = 1 + Math.log10(tf);
		return tf;
		
	}
	
	
	/* idfCal: Function which calculates the idf value of the tokeni
	 * (how often the term is found within the entire collection- total documents/no of documents containing tokeni)
	 * arguments: String, integer, Map
	 * Return : idf value of type double
	 */
	public double idfCal(String str, int docCount, Map<String, Integer> map1) {
		double idf;
		double div;
		div = (float) docCount / (float)map1.get(str.trim()) ;
		idf = Math.log10(div);
		return idf;
	}
	
	/*FreqCal - Function to calculate frequency of tokens 
	 * (returns Temp map for calculating tf)
	 */
	public Map<String, Integer> freqCal(String path) throws FileNotFoundException{
		Integer frqTemp = 0;
		String str;
		BufferedReader inf1 = new BufferedReader(new FileReader(path));
		Map<String, Integer> temMap = new HashMap<String, Integer>();
		
		try {
			while((str = inf1.readLine())!= null){
				frqTemp = temMap.get(str.trim());
				temMap.put(str.trim(), (frqTemp == null)? 1: frqTemp+1);
			}
			inf1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temMap;
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String word, contents;
		ArrayList<String> stopwords = new ArrayList<String>();
		TokenWeight objWeight;
		
		if(args.length < 2){
			System.out.println("\nPlese enter path to inputfiles as the commandline argument");
			System.out.println("usage: inputFiles, outputFiles");
			System.exit(0);
		}
		
		
		File inFolder = new File(args[0]); //input dir
		if (!inFolder.isDirectory()){
			System.out.println("\nFolder does not exist, Plese enter path to inputfiles as the commandline argument..");;
			System.exit(0);
		}
		
		File outFolder = new File(args[1]); //out dir
		if (!outFolder.isDirectory()){
			System.out.println("Folder doesnot exist..crating the folder" +args[1]);
			outFolder.mkdir();
			//System.exit(0);
		}
		
		
		
		File currentDir = new File(new File(".").getAbsolutePath()); //stoplist 
		String wpath = currentDir.getCanonicalPath();
		File stoplist = new File(wpath + "/stoplist.txt");   // should be in current working dir
		
		BufferedReader stopword = new BufferedReader(new FileReader(stoplist));
		int c = 0; //no need
		
		while ((word = stopword.readLine()) != null) {
			//System.out.println(word);
			stopwords.add(word.trim());
			c = c + 1;
		}
		
		stopword.close();
		Integer frqCount = 0;         // frequency count of words in entire corpus
		int totalDoc = 0;
		Map<String, Integer> frequency = new HashMap<String, Integer>();
		Map<String, Integer> occurance = new HashMap<String, Integer>();
		for (File file:inFolder.listFiles()){
			BufferedReader infile1;
			Map<String, Integer> temp_ = new HashMap<String, Integer>();
			if(!(file.isDirectory() || file.getName().startsWith(".") || file.isHidden())) {
				totalDoc = totalDoc + 1;
				String path = inFolder + "/" + file.getName();	
				infile1 = new BufferedReader(new FileReader(path));
				while((contents = infile1.readLine())!= null){
					frqCount = temp_.get(contents.trim());
					temp_.put(contents, (frqCount == null)? 1: frqCount+1);
				}
			}
			
			for (Map.Entry<String, Integer> entry : temp_.entrySet()) {
	            String key = entry.getKey();
	            frqCount = frequency.get(entry.getKey());
	            frequency.put(key, (frqCount == null)? entry.getValue(): frqCount+entry.getValue());
	            Integer occCount = occurance.get(key.trim());
	            occurance.put(key, (occCount == null)? 1: occCount + 1);
	        }
		}
		
		Map<String, Integer> fileLength = new HashMap<String, Integer>();
		for (File filename:inFolder.listFiles()){
			File out1;
			BufferedWriter bwfout;
			BufferedReader infile;
			
			if(!(filename.isDirectory() || filename.getName().startsWith(".") || filename.isHidden())) {
			String path = inFolder + "/" + filename.getName();	
			String[] out = filename.getName().split("\\.");
			out1 = new File(outFolder + "/" + out[0] + ".wts");
			long start = System.currentTimeMillis();
			
			bwfout = new BufferedWriter(new FileWriter(out1));
			Set<String> infilewords1 = new HashSet<String>();
			infile = new BufferedReader(new FileReader(path));
			int filelen = 0;
			
			while((contents = infile.readLine())!= null){
				//filelen = filelen + 1;
				int count = 0;
				for (int i=0;i<stopwords.size();i++) {
					if (stopwords.get(i).equals(contents.trim())){
						count = count + 1;
						break;
					}		
				}
					
				// contents with no stopwords, length >1  and occurs more than once in corpus
				// filelen--> contains the length of doc which survives the processing
				if(count == 0 && contents.length() > 1 && frequency.get(contents) > 1){
					filelen = filelen + 1;
					infilewords1.add(contents);       
					}
				
				}
			
				// stores file name and no of tokens
				fileLength.put(filename.getName(),filelen);
				objWeight = new TokenWeight();	
				Map<String, Integer> temMap = new HashMap<String, Integer>();
				temMap = objWeight.freqCal(path); //temp map to hold info about term frequencies
				
				 for (Iterator<String> it = infilewords1.iterator(); it.hasNext(); ) {
				        String item = it.next();
				        double tf = objWeight.tfCal(item, fileLength.get(filename.getName()),temMap);
				        double idf = objWeight.idfCal(item, totalDoc, occurance);
						double tokenWeight = tf * idf;
						String s_output = String.format("%20s | %.6f\n", item, tokenWeight);
						bwfout.write(s_output);
				 }
				
				bwfout.close();
				infile.close();
				
				
				long endf = System.currentTimeMillis() - start;
				System.out.println("Time taken for term weight Calculation(in Milisecs) "+ filename.getName()+"," + endf);
			}			
		}
		///Users/Sunil/Documents/workspace/TermWeight/InputFiles 
		///Users/Sunil/Documents/workspace/TermWeight/outputFiles
		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("\nTotal Time Taken "+ endTime + "milisecs");		
		
	 }
	

}
