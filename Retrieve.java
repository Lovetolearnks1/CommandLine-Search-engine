
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;




public class Retrieve {
	
	//Function to calculate Document Score
	public Map<Integer,Double> documentScore(Map<String,Double> queryMatrix, Map<String, Map<Integer,Double>> TDM){
		Map<Integer,Double> docScore = new TreeMap<Integer,Double>();
		double dotProduct = 0.0;  
		double queryMag = 0.0;
		final Integer NUM = 0, DEN1 = 1;
		
		// array which contains document id as key and (dotproduct , doc magnitude) as value
		Map<Integer, ArrayList<Double>> magnitude = new HashMap<Integer, ArrayList<Double>>();
		
		//calculate query magnitude
		for(Entry<String,Double>entry : queryMatrix.entrySet()){
			queryMag += Math.pow(entry.getValue(), 2);
		}
		queryMag = Math.sqrt(queryMag);
		ArrayList<Double> temp = null; 
		Integer key = 0;
		Double value = 0.0;
		
		for(Entry<String,Double>entry : queryMatrix.entrySet()){
			String queryWord = entry.getKey();
			if(TDM.containsKey(queryWord)){

				Map<Integer, Double> values = TDM.get(queryWord);
				
				for(Entry<Integer, Double> subkeys :values.entrySet()) {
					key = subkeys.getKey();
					value = subkeys.getValue();
			
					
					dotProduct = value * entry.getValue();
					
					if(magnitude.get(key) == null){
						temp = new ArrayList<Double>();
						temp.add(dotProduct);  
						temp.add(Math.pow(value, 2));
						magnitude.put(key, temp);
					} else {
						dotProduct += magnitude.get(key).get(NUM);
						magnitude.get(key).set(NUM, dotProduct);
						magnitude.get(key).set(DEN1, magnitude.get(key).get(DEN1) + Math.pow(value, 2));
					}
				
				}
				
			}
			
		
			dotProduct = 0;
		}
		
		for(Entry<Integer,ArrayList<Double>> magnitude_:magnitude.entrySet()){
			Integer key1 = magnitude_.getKey();
			Double num = magnitude.get(key1).get(NUM);
			Double den = magnitude.get(key1).get(DEN1);
			docScore.put(key1, num/(Math.sqrt(den) * queryMag));		
			
		}	
		return 	docScore;
		
	}
	
	//Function to calculate query weight
	public Map<String,Double> queryweight(ArrayList<String> query_,Map<String, Map<Integer,Integer>> dictmatrix){
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		Double weight = 0.0;
		double idf = 0.0;
		Integer docCount = null;
		for (String temp : query_) {
			Integer count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}
		
		int len = query_.size();      		
		int noofDoc = 503;    	
		Map<String,Double> queryMatrix = new TreeMap<String,Double>();
		
		for(String s:query_){
			if(queryMatrix.get(s) == null)
				queryMatrix.put(s, 0.0);
			
				int freq = map.get(s);
				double tf = (float) freq/(float)len;
				
				if(dictmatrix.containsKey(s)){
					Map<Integer, Integer> values = dictmatrix.get(s);
					
					for(Entry<Integer, Integer> subkeys :values.entrySet()){
						docCount = subkeys.getKey();	
						
						break;
					}
					
				}				
				
				if(!dictmatrix.containsKey(s)){
					idf = 0.0;
				
				}
				else  {     //check for formula  if it occurs in all documents?(becomes 0)				                      
					idf = Math.log10((float) noofDoc/(float)docCount);
				}	
				weight = tf*idf;
				queryMatrix.put(s, weight);			
			}
					
		return queryMatrix;
	}

	
	@SuppressWarnings("rawtypes")
	public  static class compareVal implements Comparator {
		 
		  Map<Integer,Double> map;
		 
		  public compareVal(Map<Integer,Double> map){
		    this.map = map;
		  }
		  public int compare(Object keyA, Object keyB){
		    
		    if (map.get(keyA) >= map.get(keyB)) {
		    	return -1;
		    } else {
		    	return 1;
		    }
		    
		  }
		}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		
		if(args.length == 0){
			System.out.println("Query not specified");
			System.exit(1);
		} 
		String Displayquery="";
		for(int i = 0;i<args.length;i++){
		Displayquery = Displayquery+args[i]+ " ";
		}
		System.out.println("Query is: "+ Displayquery);
		
		File slist = new File(new File(".").getAbsolutePath());
		String path = slist.getCanonicalPath();
		File stopList = new File(path + "/stoplist.txt");
		BufferedReader bf = new BufferedReader(new FileReader(stopList));
		String word;
		
		ArrayList<String> stopWords = new ArrayList<String>();
		
		
		while((word = bf.readLine()) != null){
			stopWords.add(word.trim().toLowerCase());
		}
		bf.close();
		
		ArrayList<String> query = new ArrayList<String>();
		
		for(int i = 0;i<args.length;i++){
			boolean found = false;
			String check = args[i].toLowerCase().trim();
			
			int len = 0;
			len = check.length();
			check = check.replaceAll("[^\\w\\s\\-\\/\\.\\:]|--+|\\.\\.+|::++| //+"," ");
			while(check.startsWith("-") || check.startsWith(".") || check.startsWith(":") || check.startsWith("/"))
			{
				check = check.substring(1, len);
				len = check.length();
				if(len == 1)
					break;
				else
					continue;
				
			}
			while(check.endsWith(".") || check.endsWith(":") || check.endsWith("/") || check.endsWith("-") ) {
				 check = check.substring(0, len-1);
					len = check.length();
					if(len == 1)
						break;
					else	
						continue;
			}
			
			
			if(check != null){
				for(int j=0; j<stopWords.size();j++){
					if(stopWords.get(j).equals(check)){
						found = true;
						break;
					}											
				}			
				if(!found)
					query.add(check);
				}				
		}
		//System.out.println(query);
		
		Collections.sort(query);
		
		//File file = new File("/Users/vaniks/Documents/workspace/Indexing/OutputDir/DictionaryFile.txt");
		
	    File file = new File("Index/Phase3_output/DictionaryFile.txt");
	    File file1 = new File("Index/Phase3_output/PostingFile.txt");
	    
	    if(!file.exists() || !file1.exists()){
	    	System.out.println("files doesnot exist");
	    	System.exit(0);
	    }
	    
	    
		BufferedReader bwf = new BufferedReader(new FileReader(file));
		Map<String, Map<Integer,Integer>> matrix = new TreeMap<String, Map<Integer,Integer>>();
		long count = 0;
		
		
		while((word =bwf.readLine()) != null){
			count = count + 1;
			if(query.contains(word.toLowerCase().trim())){
				count = count + 2;
				matrix.put(word, new TreeMap());
				matrix.get(word).put(Integer.parseInt(bwf.readLine()),Integer.parseInt(bwf.readLine()));
			} else {
				
				bwf.readLine();
				bwf.readLine();
				count = count + 2;
			}
			
		}
		bwf.close();	
		
		Retrieve obj = new Retrieve();
		//File file1 = new File("/Users/vaniks/Documents/workspace/Indexing/OutputDir/PostingFile.txt");
		
		Map<String, Map<Integer,Double>> TDM = new TreeMap<String, Map<Integer,Double>>();
	
		
		for (Entry<String, Map<Integer, Integer>> entry :matrix.entrySet()) {
			String key = entry.getKey();
			TDM.put(key, new TreeMap<Integer, Double>());
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file1));
			Map<Integer,Integer> values = entry.getValue();
			for(Entry<Integer, Integer> subkeys : values.entrySet()){
		
				int doccount = subkeys.getKey();
				int  pos = subkeys.getValue();
				int oldpos = pos;
				

				while((word = lineNumberReader.readLine()) != null) {
					int lineno = lineNumberReader.getLineNumber();
					if(lineno == pos){
						String[] str = word.split(",");
						TDM.get(key).put(Integer.parseInt(str[0]), Double.parseDouble(str[1]));						
						
						if(lineno == oldpos + doccount-1){
							lineNumberReader.close();
							break;
						}
						else{
							pos += 1;
						}
						
					}
				}
			}
		}	
		
		Map<Integer,Double> score = new HashMap<Integer,Double>();
		Map<String,Double> queryVector = obj.queryweight(query, matrix);
		score = obj.documentScore(queryVector, TDM);
		Map<Integer,Double> sortedScore= new TreeMap<Integer,Double>(new compareVal(score));
		sortedScore.putAll(score);
		if(sortedScore.size() >=1)
		{ int count_ = 0;
			System.out.println("Top-ranking documents containing the query words");
			for(Entry<Integer,Double> topScore:sortedScore.entrySet()){
				if(count_ == 10){
					break;
				}
				
				DecimalFormat df = new DecimalFormat("#.#####");
				if(topScore.getKey() < 10)
					System.out.println("00"+topScore.getKey()+".html"+" " +df.format(topScore.getValue()));
				else if (topScore.getKey() >= 10 && topScore.getKey() < 100)
					System.out.println("0"+topScore.getKey()+".html"+" "+df.format(topScore.getValue()));
				else
					System.out.println(topScore.getKey()+".html"+" " +df.format(topScore.getValue()));
			
				 count_ ++;
			}
		}
		else
		{
			System.out.println("No documents contain the query words");
		}
		
		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Time taken by program in milisec: "+endTime);
			
				
	}
	

}
