/**
* Program to build an index for all documents in document collection
* @author  Vani Koduru Shankara Murthy
* @version 8.0
*/

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.*;

public class Index {
	
	
	// Function which creates the posting file
	public void postingFileCreation(Map<String, Map<Integer,Double>> TDM, String path) throws IOException{
		
		File out = new File(path+"/PostingFile.txt");
		BufferedWriter bwf = new BufferedWriter(new FileWriter(out));
		
		for(Entry<String, Map<Integer,Double>> entry:TDM.entrySet()){
			String key = entry.getKey();
			Map<Integer,Double> values = entry.getValue();
			
			for(Entry<Integer,Double> subkey:values.entrySet()){
				if(subkey.getValue() != 0.0){
					
					bwf.write(subkey.getKey() +","+subkey.getValue()+"\n");
				}
			}
			
		}
		bwf.close();
		
		System.out.println("Posting File has been generated\n");
	}
	
	// Function which creates the dictionary file
	public void dictionaryFileCreation(Map<String, Map<Integer,Double>> TDM, String path,int len) throws IOException{
		
		int locOfFirstRec = 0;
		int totalPrevTokens = 1;
		int prevCount = 0;
		int totalDoc_ = len;        
		File outFile = new File(path+"/DictionaryFile.txt");
		BufferedWriter bwfd = new BufferedWriter(new FileWriter(outFile));
		int count = 0;
		for(Entry<String, Map<Integer,Double>> entry:TDM.entrySet()){
			String token = entry.getKey();
			Map<Integer,Double> values = entry.getValue();
			
			
			for(Entry<Integer,Double> subkey:values.entrySet()){
				if(subkey.getValue() != 0.0){         
					count++;				 
					
				}				
					
			}			
				
			if((totalDoc_ - count) == 0){
				locOfFirstRec= prevCount+1;	      // token present in all files 
				
			}else if(count == 1){               // if one token 
				locOfFirstRec = prevCount + 1;
			} else{
				
				locOfFirstRec= prevCount+count- 1;   // more than one files
			}
			
			
			prevCount = prevCount + count;
			bwfd.write(token+"\n"+count+"\n"+locOfFirstRec+"\n");
			count =0;
			
		}
		bwfd.close();
		System.out.println("Dictonary File has been generated\n");
	}
	
	public void displayMatrix(Map<String, Map<Integer,Double>> TDM){
		for (Entry<String, Map<Integer, Double>> entry :TDM.entrySet()) {
			 String key =  entry.getKey();
			 //HashMap<Integer, Double> check = entry.getValue();
			 Map<Integer, Double> check = entry.getValue();
			// System.out.println( check);
			 for(Entry<Integer, Double> subkeys :check.entrySet()){
				 Integer doc = subkeys.getKey();
				// if(subkeys.getKey()==1){
				 Double weight = subkeys.getValue();
				 System.out.println( "Token-"+ key + ":"+ "Doc-"+doc +":"+"weight-"+weight); 
				// }
			 }
			 
			}
		System.out.println("TDM size"+TDM.size());
	}

 @SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		
		String token;
		Index indexObj;
		System.out.println("Input Folder: "+args[0]);
		System.out.println("Output Folder: "+args[1]);
		
		File in = new File(args[0]);
		File out = new File(args[1]);
		
		
		if(!out.isDirectory()){
			out.mkdir();
			System.out.println("Output Folder does not exists....creating...");
		}
	
		
		int totalDoc =0;  
		Map<String, Map<Integer,Double>> matrix = new TreeMap<String, Map<Integer,Double>>();
		
		if(in.isDirectory()){
			int i = 1;
			for(File file: in.listFiles()){
				
				BufferedReader infile = null;
				if(!(file.isDirectory() || file.getName().startsWith(".") || file.isHidden())){
				String inFolder = in + "/"+file.getName();
				totalDoc++;
				infile = new BufferedReader(new FileReader(inFolder));
				
				while((token = infile.readLine()) != null){
					
					token = token .trim();
					String[] tokenwt = token.split("\\|");
					if(tokenwt[0].length()>60){
						continue;
					}
					
					// Creation of TDM
					Map<Integer, Double> check = matrix.get(tokenwt[0].trim());
					if(check != null){	
						matrix.get(tokenwt[0].trim()).put(i,Double.parseDouble(tokenwt[1].trim()));
						
					}else{
						
						matrix.put(tokenwt[0].trim(), new HashMap());
						matrix.get(tokenwt[0].trim()).put(i,Double.parseDouble(tokenwt[1].trim()));
					}
						
					}
					i++;
					infile.close();
				}
				
			}
			
		}
	
		indexObj = new Index();
		Map<String, Map<Integer,Double>> TDM = new TreeMap<String, Map<Integer,Double>>();
		TDM.putAll(matrix);	
		indexObj.postingFileCreation(TDM,args[1]);
		indexObj.dictionaryFileCreation(TDM,args[1],totalDoc);
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("\nTime taken to execute Phase3" +", "+ estimatedTime + "milisecs");
	}
	
}
