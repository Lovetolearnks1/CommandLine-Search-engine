import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Cluster {    //new

	public Map<ArrayList<Integer>, Map<ArrayList<Integer>,Double>> doCosineSimMatrix
					(Map<ArrayList<Integer>, Map<String,Double>> matrix){
		
		DecimalFormat df = new DecimalFormat("#.00000");
		//finding magnitude
		Double magWeight = 0.0;
		ArrayList<Integer> magDocno;
			
		Map<ArrayList<Integer>, Double> magnitude = new HashMap<ArrayList<Integer>, Double>();
		for(Entry<ArrayList<Integer>, Map<String,Double>> entry:matrix.entrySet()){
			magDocno = entry.getKey();
			Map<String,Double> values = entry.getValue();
			for(Entry<String,Double> tokenandWeight:values.entrySet()){
				magWeight += Math.pow(tokenandWeight.getValue(), 2);
			}		
			
			magnitude.put(magDocno, magWeight);
			magWeight = 0.0;
		}
		
		//System.out.println("magnitude: "+magnitude);
				
		Map<ArrayList<Integer>, Map<ArrayList<Integer>,Double>> cosineSimMatrix = 
				new HashMap<ArrayList<Integer>, Map<ArrayList<Integer>,Double>>();
		
		Double DotProduct = 0.0;
		for(Entry<ArrayList<Integer>, Map<String,Double>> entryRow:matrix.entrySet()){
			ArrayList<Integer> RowdocNo = entryRow.getKey();
			Map<String,Double> values = entryRow.getValue();
			
			for(Entry<ArrayList<Integer>, Map<String,Double>> entryCol:matrix.entrySet()){
				ArrayList<Integer> ColdocNo = entryCol.getKey();
									
				for(Entry<String,Double> tokenandWeight:values.entrySet()){
					String token = tokenandWeight.getKey();
					Double weight = tokenandWeight.getValue();
					
					if(matrix.get(ColdocNo).containsKey(token)){
						Map<String, Double> nextDoc = matrix.get(ColdocNo);
						Double nextWeight = nextDoc.get(token);				
						DotProduct += weight * nextWeight;
					}											
				}
				
				Double cosineSim = 
						DotProduct / (Math.sqrt(magnitude.get(RowdocNo)) * Math.sqrt(magnitude.get(ColdocNo)));
				
				cosineSim = Double.parseDouble(df.format(cosineSim)); //fix decimal points to 5 decimal places
								
				if(cosineSimMatrix.get(RowdocNo) != null){						
					cosineSimMatrix.get(RowdocNo).put(ColdocNo, (RowdocNo==ColdocNo)? 1.0: cosineSim); //diagonal should be 1
				} else{	
					cosineSimMatrix.put(RowdocNo, new HashMap<>());
					cosineSimMatrix.get(RowdocNo).put(ColdocNo, (RowdocNo==ColdocNo)? 1.0: cosineSim);
				}	
				DotProduct = 0.0;								
			}					
		}		
		return cosineSimMatrix;			
	}
	
	private void MergeWithCentroid(Map<ArrayList<Integer>, Map<String,Double>> documentVector, ArrayList<Integer> ci, ArrayList<Integer> cj) {

		Map<String, Double> term_list_ci = documentVector.get(ci);
		Map<String, Double> term_list_cj = documentVector.get(cj);
		Map<String, Double> term_list_new = new HashMap<String, Double>();
		ArrayList<Integer> new_index = new ArrayList<Integer>();
		
		new_index.addAll(ci);
		new_index.addAll(cj);

		term_list_new.putAll(term_list_ci);
		
		for (Entry<String, Double> item :term_list_new.entrySet()) {
			if(term_list_cj.containsKey(item.getKey())) {
				Double average = (term_list_cj.get(item.getKey()) + item.getValue())/2;
				item.setValue(average);
			} else {
				item.setValue(item.getValue()/2);
			}
		}
		
		for (Entry<String, Double> item :term_list_cj.entrySet()) {
			if(! term_list_new.containsKey(item.getKey())) {
				term_list_new.put(item.getKey(), item.getValue()/2);
			}
		}
		
		documentVector.remove(ci);
		documentVector.remove(cj);
		ci.addAll(cj);
		documentVector.put(ci, term_list_new);	
	}
	
	private static Double CalculateCorpusCentroid(Map<ArrayList<Integer>, Map<String,Double>> documentVector) {
		Map<String,Double> corpus = new HashMap<String,Double>();
		Integer noOfDocuments = 0;
		Map<ArrayList<Integer>, Double> docCentroids = new HashMap<ArrayList<Integer>, Double>();
		
		for (Entry<ArrayList<Integer>, Map<String, Double>> entry :documentVector.entrySet()) {
			noOfDocuments++;
			Map<String, Double> docTermWeights = entry.getValue();
			Double docTotal = 0.0;
			
			for (Entry<String, Double> term :docTermWeights.entrySet()) {
				docTotal += term.getValue();
				if (corpus.containsKey(term.getKey())) {
					corpus.put(term.getKey(), term.getValue()+corpus.get(term.getKey()));
				} else {
					corpus.put(term.getKey(), term.getValue());
				}
			}
			docCentroids.put(entry.getKey(), docTotal/docTermWeights.size());
			//System.out.println("doc="+entry.getKey()+" centroid="+docCentroids.get(entry.getKey()));
		}
		
		//System.out.println("noOfDocuments="+noOfDocuments);
		
		Double CorpusCentroid = 0.0;
		for (Entry<String, Double> term :corpus.entrySet()) {
			CorpusCentroid += (term.getValue() / noOfDocuments);
		}
		
		CorpusCentroid = CorpusCentroid/corpus.size();
		
		Double distance = Double.MAX_VALUE;
		ArrayList<Integer> nearestDoc = new ArrayList<Integer>();
		
		for (Entry<ArrayList<Integer>, Double> doc :docCentroids.entrySet()) {
			Double temp = Math.abs(CorpusCentroid - doc.getValue());
			if (temp < distance) {
				distance = temp;
				nearestDoc = doc.getKey();
			}		
		}
		
		System.out.println("CorpusCentroid="+CorpusCentroid+
					" nearest document is: "+nearestDoc+" wt="+docCentroids.get(nearestDoc));
		
		return CorpusCentroid;
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {

		File in = new File("/Users/vaniks/Desktop/Project/Phase2_output"); // change the path
		
		String token;
		int totalDoc =0;  
		Map<ArrayList<Integer>, Map<String,Double>> matrix = 
				new HashMap<ArrayList<Integer>, Map<String,Double>>();
		
		if(in.isDirectory()){
			Integer i = 1;
			
			for(File file: in.listFiles()){
				
				BufferedReader infile = null;
				if(!(file.isDirectory() || file.getName().startsWith(".") || file.isHidden())){
					
					String inFolder = in + "/"+file.getName();
					totalDoc++;
					infile = new BufferedReader(new FileReader(inFolder));
					ArrayList<Integer> ai = new ArrayList<Integer>();
					ai.add(i);
					
					while((token = infile.readLine()) != null){
						
						token = token.trim();
						String[] tokenwt = token.split("\\|");
						
						if(tokenwt[0].length()>60){
							continue;
						}
						
						// Creation of TDM
						Map<String, Double> check = matrix.get(ai);
						if(check != null){
							matrix.get(ai).put(tokenwt[0].trim(),Double.parseDouble(tokenwt[1].trim()));					
						}else{
							Map<String,Double> tempVal = new HashMap<String,Double>();
							tempVal.put(tokenwt[0].trim(),Double.parseDouble(tokenwt[1].trim()));
							matrix.put(ai, tempVal);
						}
							
					}
					i++;
					infile.close();
				}
				
			}
			
		}		
		//System.out.println("Initial Matrix:"+matrix);
		//System.out.println("totalDoc="+totalDoc);
		
		Double corpusCentroid = CalculateCorpusCentroid(matrix);
		
		Cluster obj = new Cluster();
		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;
		Integer stg = 0;
		ArrayList<Integer> ci = new ArrayList<Integer>();
		ArrayList<Integer> cj = new ArrayList<Integer>();
		Map<ArrayList<Integer>, Map<ArrayList<Integer>,Double>> cosineSimMatrix;
		
		ArrayList<Integer> c1i = new ArrayList<Integer>();
		ArrayList<Integer> c1j = new ArrayList<Integer>();
		
		do {
			stg++;
			max = Double.MIN_VALUE; 			
			min = Double.MAX_VALUE; //added
					
			cosineSimMatrix = obj.doCosineSimMatrix(matrix);
			
			for (Entry<ArrayList<Integer>, Map<ArrayList<Integer>, Double>> entry :cosineSimMatrix.entrySet()) {
				ArrayList<Integer> Row =  entry.getKey();
				Map<ArrayList<Integer>, Double> Cols = entry.getValue();
				
				for(Entry<ArrayList<Integer>, Double> subkey :Cols.entrySet()){
					ArrayList<Integer> Col = subkey.getKey();
					if (Row == Col) continue;
					 
					Double weight = subkey.getValue();
					 
					if(weight >= max){
						 max = weight;
						 ci = Row;
						 cj = Col;
					}	
					
					if(stg == 1 && weight > 0 && weight <= min){  // added
						 min = weight;
						 c1i = Row;
						 c1j = Col;
					}	
				}				 
			}
			
			if( stg == 1 ) {
				System.out.println("Pair of documents most similar are: "+ci+" and "+cj+" the weight is="+ max);
				System.out.println("Pair of documents most dissimilar are: "+c1i+" and "+c1j+" the weight is="+ min);
				System.out.println("Merging:"+ci+" and "+cj+" the weight is="+max);
			} else {
				System.out.println("Merging:"+ci+" and "+cj+" the weight is="+max);
			}
			
			obj.MergeWithCentroid(matrix, ci, cj);
			
		} while(max > 0.4 && matrix.size() > 1);
		
		System.out.println("Clusturing done !!");	
	}

}
