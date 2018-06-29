# CommandLine-Search-engine1.	
The project involvs building the command line search engine using JAVA. The project has 5 class files.

1. TokenRead.java: Tokenizing 500 document,calculating the frequency removing  special characters  except . / : - to retain the special meaning

2. TokenWeight.java: calculating the term weight : tf*idf
  tf: how of the particular term is found is a document and idf (in entire document)
  The term weight is the product of tf and idf, and a term will have higher weight when it is found many times within a single document,     and lowest when it is found in all documents.
  
3. Index.java: Building the index to find a search term by calculating Term Document Matrix( term, document no.. on which the term occurs and weight), a Posting file(documentno , term weight) and a dictionary file(term, count of doc in which term occurs, location at which it starts in the posting file )

4.	Retrieve.java: Displaying ten top-ranking documents to the user by taking command line query words. 

5.	Cluster.java: The program uses Cosine Similarity measure to perform clustering using cosine similarity matrix.
