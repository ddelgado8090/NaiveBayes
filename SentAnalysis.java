/*
 * Please see submission instructions for what to write here. 
 */

 import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
 
 public class SentAnalysis {
 
	 final static File TRAINFOLDER = new File("newtrain/train");
 
	 public static File[] listOfFiles = TRAINFOLDER.listFiles();
	 // for (int i = 0; i < listOfFiles.length; i++){
	 // 	System.out.println(listOfFiles[i]);
	 // }
 
	 //Create hashtable for all of the words in the positive documents
	 public static HashMap<String, Integer> positiveReviewWord = new HashMap <String, Integer>();
	 //Create hashtable for all of the words in the negative documents
	 public static HashMap<String, Integer> negativeReviewWord = new HashMap <String, Integer>();
 
	 public static double negReviewCount = 0;
	 public static double posReviewCount = 0;
	 public static double probabilityNegReview = 0.0;
	 public static double probabilityPosReview = 0.0;
 
	 public static double smoothingPosWords = 0.0;
	 public static double smoothingNegWords = 0.0;
	 
		 
	 public static void main(String[] args) throws IOException
	 {
		 System.out.println(TRAINFOLDER);
 
		 ArrayList<String> files = readFiles(TRAINFOLDER);
 
		 train(files);
		 //if command line argument is "evaluate", runs evaluation mode
		 if (args.length==1 && args[0].equals("evaluate")){
			 evaluate();
		 }
		 else{//otherwise, runs interactive mode
			 @SuppressWarnings("resource")
			 Scanner scan = new Scanner(System.in);
			 System.out.print("Text to classify>> ");
			 String textToClassify = scan.nextLine();
			 System.out.println("Result: "+classify(textToClassify));
		 }
		 
	 }
	 
 
	 
	 /*
	  * Takes as parameter the name of a folder and returns a list of filenames (Strings) 
	  * in the folder.
	  */
	 public static ArrayList<String> readFiles(File folder){
		 
		 System.out.println("Populating list of files");
		 
		 //List to store filenames in folder
		 ArrayList<String> filelist = new ArrayList<String>();
		 
		 for (File fileEntry : folder.listFiles()) {
			 String filename = fileEntry.getName();
			 filelist.add(filename);
		 }
		 
		 
		 return filelist;
	 }
	 
	 
	 
	 /*
	  * TO DO
	  * Trainer: Reads text from data files in folder datafolder and stores counts 
	  * to be used to compute probabilities for the Bayesian formula.
	  * You may modify the method header (return type, parameters) as you see fit.
	  */
	 public static void train(ArrayList<String> files) throws FileNotFoundException, IOException
	 {
		 for (String rate : files) {
			 FileReader fr = new FileReader(new File(TRAINFOLDER, rate));
			 BufferedReader br = new BufferedReader(fr);
			 String xf;
			 
			 String substringRate = rate.substring(0, rate.indexOf("-"));
			 int sizeSubstring = substringRate.length();
			 
			 if (rate.charAt(sizeSubstring + 1) == '1') { // If the number is 1, then it's a negative review
				 while ((xf = br.readLine()) != null) {
					 String[] wordsInFile = xf.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
					 for (String word : wordsInFile) {
						 if (negativeReviewWord.containsKey(word)) {
							 int tempFreq = negativeReviewWord.get(word);
							 int updatedFreq = tempFreq + 1; // Increment the frequency
							 negativeReviewWord.put(word, updatedFreq);
						 } else {
							 negativeReviewWord.put(word, 1); // Initialize frequency to 1
						 }
					 }
				 }
				 negReviewCount++;
			 } else {
				 while ((xf = br.readLine()) != null) {
					 String[] wordsInFile = xf.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
					 for (String word : wordsInFile) {
						 if (positiveReviewWord.containsKey(word)) {
							 int tempFreq = positiveReviewWord.get(word);
							 int updatedFreq = tempFreq + 1; // Increment the frequency
							 positiveReviewWord.put(word, updatedFreq);
						 } else {
							 positiveReviewWord.put(word, 1); // Initialize frequency to 1
						 }
					 }
				 }
				 posReviewCount++;
			 }
			 br.close();
		 }
		 
		 // System.out.println("Negative reviews: " + negReviewCount);
		 // System.out.println("Positive reviews: " + posReviewCount);
 
		 double totalReviews = negReviewCount + posReviewCount;
		 probabilityNegReview = negReviewCount / totalReviews;
		 probabilityPosReview = posReviewCount / totalReviews;
	 
		 // Calculate the total number of unique words for smoothing
		 smoothingPosWords = positiveReviewWord.size();
		 smoothingNegWords = negativeReviewWord.size();
	 }
 
	 /*
	  * TO DO
	  * Classifier: Classifies the input text (type: String) as positive or negative
	  */
	 public static String classify(String text)
	 {
		 String result="";
 
		 String[] words = text.replaceAll("[^a-zA-Z ]","").toLowerCase().split("\\s+");
 
		 double probSentencePos = log2(probabilityPosReview); //this is the probability that the word is positive P(positive)
		 double probSentenceNeg = log2(probabilityNegReview); //this is the probability that the word is negative P(negative)
 
		 double smoothingPos = .0001/(smoothingPosWords + (smoothingNegWords + smoothingPosWords)*.0001);
		 smoothingPos = log2(smoothingPos);
		 double smoothingNeg = .0001/(smoothingNegWords + (smoothingPosWords + smoothingNegWords)*.0001);
		 smoothingNeg = log2(smoothingNeg);
 
		 for(String word : words){
			 if(positiveReviewWord.containsKey(word)){//if the word is in the positive dictionary, calculate the regular log2 base of the word
				 double probWordPos = log2(positiveReviewWord.get(word)/smoothingPosWords);
				 probSentencePos += probWordPos;
			 } else {//if the word isn't in the positive dictionary, apply smoothing
				 probSentencePos += smoothingPos;
			 }
			 if(negativeReviewWord.containsKey(word)){//if the word is in the negative dictionary, calculate the regular log2 base of the word
				 double probWordNeg = log2(negativeReviewWord.get(word)/smoothingNegWords);
				 probSentenceNeg += probWordNeg;
			 } else {//if the word isn't in the negative dictionary, apply smoothing
				 probSentenceNeg += smoothingNeg;
			 }
		 }
		 if(probSentencePos > probSentenceNeg){
			 result = "positive";
		 } else {
			 result = "negative";
		 }
		 
		 return result;
		 
	 }
 
	 /*
	  * Does the log base 2 calculation
	  */
	 public static double log2(double n){
		 return Math.log(n) / Math.log(2.0);
	 }
	 
	 
	 /*
	  * TO DO
	  * Classifier: Classifies all of the files in the input folder (type: File) as positive or negative
	  * You may modify the method header (return type, parameters) as you like.
	  */
	 public static void evaluate() throws FileNotFoundException, IOException
	 {
		 @SuppressWarnings("resource")
		 Scanner scan = new Scanner(System.in);
 
		 double totalFiles = 0.0;
		 double totalPositiveFiles = 0.0;
		 double totalNegativeFiles = 0.0;
 
		 double correctPositiveFiles = 0.0; //# of positive files that are classified correctly
		 double correctNegativeFiles = 0.0; //# of negative files that are classified correctly
		 
		 System.out.print("Enter folder name of files to classify: ");
		 String foldername = scan.nextLine();
		 File folder = new File(foldername);
 
		 ArrayList<String> filesToClassify = readFiles(folder);
		 for(String file : filesToClassify){
			 BufferedReader br = new BufferedReader(new FileReader(String.format("newtrain/train/%s", file)));
			 String xf;
 
			 String substringRate = file.substring(0, file.indexOf("-"));
			 int sizeSubstring = substringRate.length();
 
			 if (file.charAt(sizeSubstring + 1) == '1') { // If the number is 1, then it's a negative review
				 while((xf = br.readLine()) != null){
					 String result = classify(xf);
					 totalNegativeFiles++;
					 if(result.equals("negative")){
						 correctNegativeFiles++;
					 }
				 }
				 // newtrain\train\actors-1-10011.txt
			 } else {
				 while((xf = br.readLine()) != null){
					 String result = classify(xf);
					 totalPositiveFiles++;
					 if(result.equals("positive")){
						 correctPositiveFiles++;
					 }
				 }
			 }
			 br.close();
			 totalFiles++;
		 }
 
		 double accuracyOfFiles = ((correctPositiveFiles + correctNegativeFiles) / totalFiles) * 100;
		 double precisePositiveFiles = (correctPositiveFiles/totalPositiveFiles) * 100;
		 double preciseNegativeFiles = (correctNegativeFiles/totalNegativeFiles) * 100;
 
		 System.out.println("Accuracy: " + accuracyOfFiles + "%");
		 System.out.println("Positive precision: " + precisePositiveFiles + "%");
		 System.out.println("Negative precision: " + preciseNegativeFiles + "%");
		 
	 }
	 
	 
	 
 }