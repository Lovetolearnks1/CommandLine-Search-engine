package tokens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;

public class TokenRead {
	private String path;
	private String path_1;
	String fileData;
	private String[] outfile;	
	
	
	public  TokenRead(String filein,String fileout){
		path = filein;
		path_1 = fileout;
		
	}
	
	
	public void tokanizer() throws IOException{
		//reading ... File
		FileReader filer = new FileReader(path) ;
		BufferedReader bufr = new BufferedReader(filer);
		
		File wfPath = new File(path);
		outfile = wfPath.getName().split("\\.");
		
		//writes contents into file out_ in output folder  
		
		File folderout = new File(path_1);
		if (!folderout.isDirectory()){
			folderout.mkdir();
			System.out.println("folder created");
		}
		
		File wf = new File(path_1 + "/out_" + outfile[0] + ".txt");
		BufferedWriter bwf = new BufferedWriter(new FileWriter(wf));
		
		//append all tokens to out_All file
		
		File outall = new File(path_1  + "/out_All.txt");
		BufferedWriter bwfAll = new BufferedWriter(new FileWriter(outall, true));
	
		
		while( (fileData= bufr.readLine()) != null ){
			
				String str = fileData.replaceAll("\\<.*?>"," ");
				str = str.replaceAll("\\<.*?$"," ");
				str = str.replaceAll("^.*?>"," "); 
				str = str.replaceAll("&.*?;","");
				str = str.replaceAll("[^\\w\\s\\-\\/\\.\\:]|--+|\\.\\.+|::++| //+"," "); // + with *
				str = str.replaceAll("\\n"," ");
							
				StringTokenizer tk = new StringTokenizer(str, " "); 
				while (tk.hasMoreTokens())
				{
					String tokens = tk.nextToken().trim();
					int len = tokens.length();
					
					while(tokens.startsWith("-") || tokens.startsWith(".") || tokens.startsWith(":") || tokens.startsWith("/"))
					{
						tokens = tokens.substring(1, len);
						len = tokens.length();
						if(len == 1)
							break;
						else
							continue;
						
					}
					while(tokens.endsWith(".") || tokens.endsWith(":") || tokens.endsWith("/") || tokens.endsWith("-") ) {
							
							tokens = tokens.substring(0, len-1);
							len = tokens.length();
							if(len == 1)
								break;
							else	
								continue;
					}
						
					bwf.write(tokens.toLowerCase().trim() + "\n");
					bwfAll.append(tokens.toLowerCase().trim() + "\n");
				}			
	
			}
		
		bufr.close();
		filer.close();
		bwf.close();
		bwfAll.close();

	}
	
	public void freqCal(String outFile) throws IOException{
		String word;
		List <String> wordArr = new ArrayList<String>();
		Integer frqCount = 0;
		String path1 = outFile;
		
		System.out.println("\nPath, where output files are stored:" +path1);
		int c = 0;
		FileReader frqF =  new FileReader(path1 + "/out_All.txt");
		BufferedReader frqbuf = new BufferedReader(frqF);
		Map<String, Integer> frequency = new HashMap<String, Integer>();
		
		while((word = frqbuf.readLine()) != null){ 
				wordArr.add(word);  //trim added 
				c++;
		}
		frqbuf.close();

		for (String token: wordArr){
			
			if(token.equals("-") || token.equals("") || token.equals("/") || token.equals(".") || token.equals(":"))
				continue;
			frqCount = frequency.get(token.trim()); //add
			frequency.put(token, (frqCount == null)? 1: frqCount+1);
			
			
		}
		
		//Sort tokens alphabetically (by keys) and store contents(key---value)
		// in .../sorted_ByToken.txt
	
		SortedMap<String,Integer> sortedMap = new TreeMap<String, Integer>(frequency);
		File sToken = new File(path1 + "/sorted_ByToken.txt");
		sortTokens(sToken,sortedMap);
		

		//Sort tokens by their frequencies (by values) and store contents(key---value)
		// in .../sorted_ByFrequency.txt
		@SuppressWarnings("unchecked")
		Map<String,Integer> sortedMapValue = new TreeMap<String, Integer>(new compareVal(frequency));
		sortedMapValue.putAll(frequency);
		File sFreq = new File(path1 + "/sorted_ByFrequency.txt");
		sortTokens(sFreq,sortedMapValue);
		System.out.println("Total number of tokens(unsorted):"+c);
		System.out.println("Total number of tokens(sorted):"+sortedMapValue.size());
		
	}
	
	
	public void sortTokens(File file, Map<String,Integer> map) throws IOException{
		
		BufferedWriter bwf = new BufferedWriter(new FileWriter(file));
		String  str = null;
		Iterator<String> itr1 = (Iterator<String>) map.keySet().iterator();
		Iterator<Integer> itr2 = (Iterator<Integer>) map.values().iterator();
		
		while(itr1.hasNext()){
			str = itr1.next().toString().trim() +","+ itr2.next().toString().trim() +"\n";
			bwf.write(str);
			
		}
			
		bwf.close();
		
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public class compareVal implements Comparator {
		 
		  Map<String,Integer> map;
		 
		  public compareVal(Map<String,Integer> map){
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
		
		
	
	public static void main(String[] args) throws IOException {
		
		// TODO Auto-generated method stub
		 String out=null;
		 String inFile,outFile;
		 int flag =1;
		 int c=0;
		 TokenRead fileobj;
		
		 Scanner in = new Scanner(System.in);
		 do{
		 System.out.println("Plese enter path to inputfiles:");
	     inFile = in.nextLine();
	     System.out.println("Path to InputFiles: "+inFile);
	     
	     File folderin1 = new File(inFile);
			
			if (!folderin1.isDirectory()){
				System.out.println("usage: ../../Documents/inputFiles");
				flag = 0;
			}
			else
				flag = 1;
		 }while(flag==0);
	     
		
	     do{
	    	 
	     
	     System.out.println("Plese enter path to outputfiles:");
	     outFile = in.nextLine();
	     System.out.println("Path to outputFiles: "+outFile);
	     
	     File f = new File(outFile);
			if(f.exists()){
				System.out.println("Folder already exist...Do yo want to delete?(YES:y/Y or No:n/N):");
				out = in.nextLine();
				
				if(out.equals("y") || out.equals("Y")){
					for (File fileName :f.listFiles()){
						String s= fileName.getName();
						File del= new File(outFile+"/"+s);
						del.delete();
					}
				System.out.println("\n Deleting ...files");
				}
				
			}
			else
				break;
	     }while(out.equals("n") || out.equals("N"));
	     
	     in.close();
	   
		File folderin = new File(inFile);
		
		//File folder = new File("/Users/../TokenRead/inputFiles");
		

		folderin = new File(inFile);
		
		long startTime = System.currentTimeMillis();
		for (File fileName :folderin.listFiles()){
			if(!(fileName.isDirectory() || fileName.getName().startsWith(".") || fileName.isHidden())){
				c = c+1;
				String abpath = inFile + "/" + fileName.getName();
				fileobj = new TokenRead(abpath,outFile);
				
				try{
					long start = System.currentTimeMillis();
					fileobj.tokanizer();
					long end = System.currentTimeMillis() - start;
					System.out.println("Time taken to tokenize:"+fileName.getName()+"," + end );
				}catch(IOException e)
				{
					System.out.println("Exception has occured" + e);
				}
			}			
			
		}	
		
		fileobj = new TokenRead("","");
		long startf = System.currentTimeMillis();
		fileobj.freqCal(outFile);
		long endf = System.currentTimeMillis() - startf;
		System.out.println("\nTime taken for frequency Calculation "+"," + endf + "milisecs");
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("\nProgram Time " +","+ estimatedTime + "milisecs");
		
	}

}


