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
 
 public class SentAnalysisBest {
 
     final static File TRAINFOLDER = new File("newtrain/train");
 
     public static File[] listOfFiles = TRAINFOLDER.listFiles();
 
     //Create hashtable for all of the words in the positive documents
     public static HashMap<String, Integer> positiveReviewWord = new HashMap <String, Integer>();
     //Create hashtable for all of the words in the negative documents
     public static HashMap<String, Integer> negativeReviewWord = new HashMap <String, Integer>();
 
     //Create a hashtable for all bigram words in the positive documents
     public static HashMap<String, Integer> positiveReviewBigram = new HashMap <String, Integer>();
     //Create a hashtable for all bigram words in the negative documents
     public static HashMap<String, Integer> negativeReviewBigram = new HashMap <String, Integer>();
 
 
     public static double negReviewCount = 0;
     public static double posReviewCount = 0;
     public static double probabilityNegReview = 0.0;
     public static double probabilityPosReview = 0.0;
 
     public static double smoothingPosWords = 0.0;
     public static double smoothingNegWords = 0.0;
 
     public static double smoothingPosBigram = 0.0;
     public static double smoothingNegBigram = 0.0;
     
         
     public static void main(String[] args) throws IOException
     {
 
         ArrayList<String> files = readFiles(TRAINFOLDER);
 
         train(files);
         //if command line argument is "evaluate", runs evaluation mode
         if (args.length==1 && args[0].equals("evaluate")){
             Scanner scanner = new Scanner(System.in);
             System.out.print("Enter folder path of files to classify: ");
             String folderPath = scanner.nextLine();
             evaluate(folderPath);
             scanner.close();
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
                     //for-loop for unigram
                     for (String word : wordsInFile) {
 
                         if (negativeReviewWord.containsKey(word)) {
                             int tempFreq = negativeReviewWord.get(word);
                             int updatedFreq = tempFreq + 1; // Increment the frequency
                             negativeReviewWord.put(word, updatedFreq);
                         } else {
                             negativeReviewWord.put(word, 1); // Initialize frequency to 1
                         }
 
                     }
                     
                     //for-loop for bigram
                     for (int i = 0; i < wordsInFile.length - 1; i++) {
 
                         String bigram = wordsInFile[i] + wordsInFile[i + 1];
 
                         if (negativeReviewBigram.containsKey(bigram)) {
                             int tempFreq = negativeReviewBigram.get(bigram);
                             int updatedFreq = tempFreq + 1;
                             negativeReviewBigram.put(bigram, updatedFreq);
                         } else {
                             negativeReviewBigram.put(bigram, 1);
                         }
 
                     }
 
                 }
                 negReviewCount++;
             } else {
                 while ((xf = br.readLine()) != null) {
                     String[] wordsInFile = xf.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
 
                     //for-loop for unigram
                     for (String word : wordsInFile) {
                         if (positiveReviewWord.containsKey(word)) {
                             int tempFreq = positiveReviewWord.get(word);
                             int updatedFreq = tempFreq + 1; // Increment the frequency
                             positiveReviewWord.put(word, updatedFreq);
                         } else {
                             positiveReviewWord.put(word, 1); // Initialize frequency to 1
                         }
                     }
 
                     //for-loop for bigram
                     for (int i = 0; i < wordsInFile.length - 1; i++) {
                         String bigram = wordsInFile[i] + wordsInFile[i + 1];
 
                         if (positiveReviewBigram.containsKey(bigram)) {
                             int tempFreq = positiveReviewBigram.get(bigram);
                             int updatedFreq = tempFreq + 1;
                             positiveReviewBigram.put(bigram, updatedFreq);
                         } else {
                             positiveReviewBigram.put(bigram, 1);
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
 
         smoothingPosBigram = positiveReviewBigram.size(); //smoothing for bigram positive phrases
         smoothingNegBigram = negativeReviewBigram.size(); //smoothing for bigram negative phrases
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
         smoothingPos = log2(smoothingPos);//smoothing for positive unigrams
         double smoothingNeg = .0001/(smoothingNegWords + (smoothingPosWords + smoothingNegWords)*.0001);
         smoothingNeg = log2(smoothingNeg);//smoothing for negative unigrams
 
         double smoothingPosBi = .0001/(smoothingPosBigram + (smoothingNegBigram + smoothingPosBigram)*.0001);
         smoothingPosBi = log2(smoothingPosBi);//smoothing for positive bigrams
         double smoothingNegBi = .0001/(smoothingNegBigram + (smoothingNegBigram + smoothingPosBigram)*.0001);
         smoothingNegBi = log2(smoothingNegBi);//smoothing for negative bigrams
 
         //unigram calculation
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
 
         //bigram calculation
         for (int i = 0; i < words.length - 1; i++) {
             String bigram = words[i] + words[i + 1];
 
             if (positiveReviewBigram.containsKey(bigram)) {
                 double probBigramPos = log2(positiveReviewBigram.get(bigram) / smoothingPosBigram);
                 probSentencePos += probBigramPos;
             } else {
                 probSentencePos += smoothingPosBi;
             }
 
             if (negativeReviewBigram.containsKey(bigram)) {
                 double probBigramNeg = log2(negativeReviewBigram.get(bigram) / smoothingNegBigram);
                 probSentenceNeg += probBigramNeg;
             } else {
                 probSentenceNeg += smoothingNegBi;
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
     public static void evaluate(String folderPath) throws FileNotFoundException, IOException {
         double totalFiles = 0.0;
         double totalPositiveFiles = 0.0;
         double totalNegativeFiles = 0.0;
     
         double correctPositiveFiles = 0.0; //# of positive files that are classified correctly
         double correctNegativeFiles = 0.0; //# of negative files that are classified correctly
     
         File folder = new File(folderPath);
     
         ArrayList<String> filesToClassify = readFiles(folder);
         for (String file : filesToClassify) {
     
             BufferedReader br = new BufferedReader(new FileReader(new File(folder, file)));
             String xf;
     
             String substringRate = file.substring(0, file.indexOf("-"));
             int sizeSubstring = substringRate.length();
     
             if (file.charAt(sizeSubstring + 1) == '1') { // If the number is 1, then it's a negative review
                 totalNegativeFiles++;
                 while ((xf = br.readLine()) != null) {
                     String result = classify(xf);
                     if (result.equals("negative")) {
                         correctNegativeFiles++;
                     }
                 }
             } else {
                 totalPositiveFiles++;
                 while ((xf = br.readLine()) != null) {
                     String result = classify(xf);
                     if (result.equals("positive")) {
                         correctPositiveFiles++;
                     }
                 }
             }
             br.close();
             totalFiles++;
         }
     
         double accuracyOfFiles = ((correctPositiveFiles + correctNegativeFiles) / totalFiles) * 100;
         double precisePositiveFiles = (correctPositiveFiles / totalPositiveFiles) * 100;
         double preciseNegativeFiles = (correctNegativeFiles / totalNegativeFiles) * 100;
     
         System.out.println("Accuracy: " + accuracyOfFiles + "%");
         System.out.println("Positive precision: " + precisePositiveFiles + "%");
         System.out.println("Negative precision: " + preciseNegativeFiles + "%");
     }
     
         
 }
