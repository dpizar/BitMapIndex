package main;

import bimapindex.BICriteria;
import bimapindex.BIKey;
import bimapindex.BitmapIndex;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


	
/**
 * This is the main class of this project
 *
 */
public class MainClass {
	private static int Asize=250; //number of tuples per list.
	private static int counter;
	private static BufferedReader br;
	private static String filename;
	private static List<File> fileList=new ArrayList<File>();
	

	//variables to build Bitmap index.
	public static BitmapIndex indexT1;
	public static BitmapIndex indexT2;
	
	public static final int T1_Size=1000;
	public static final int T2_Size=1000;
	
	//Attributes constants
	public static int ID=1; //int(7)
	public static int NAME=2;//char(10)
	public static int DEPARTMENT=3;//int(3)
	public static int SIN=4;//int(9)
	public static int ADDRESS=5;//char(70)
	
	public static Integer LineCounter;
	
	//For querying teh bitmap
	public static ArrayList<String> bitMapKey;
	
	 
	
    public static void main(String[] args) 
    {	
    	long start = System.currentTimeMillis();
    	counter=-1;
    	LineCounter=0;
		filename="T1.txt";
		SortFiles sortFiles = new SortFiles();
		JoinTables joinTables = new JoinTables();
		ProcessedFilesTracker processedFilesTracker = new ProcessedFilesTracker();
		processedFilesTracker.setProcessedFilesNumber(0);
		//Start buffer reader
		try{
			 br=new BufferedReader(new FileReader(filename));
		} catch (IOException e) {
            //Do Nothing
        }
		//Get maximun number of lists.
		int max= (T1_Size/Asize);
		
		
        List<String> gainedArrFromFile;
        
        indexT1 = new BitmapIndex();
        for (int j = 0; j <max; j++) {
        	
        	counter ++;
   
           gainedArrFromFile = TPMMSFileReader.readFile(br,indexT1);
                                   
           sortFiles.sortFiles(gainedArrFromFile);
            
            try {
            	
            	TPMMSFileWriter.writeListToFile(gainedArrFromFile,counter, filename, fileList, Asize);
            } catch (IOException e) {
                e.printStackTrace();  
            }
        }
        
        //Get the size of the compressed bit map to display.
        HashMap<BIKey, EWAHCompressedBitmap> bitmapsIndex1=indexT1.getBiMaps();
        displaySizeHashMap(bitmapsIndex1);
        
        //bitmap.serializedSizeInBytes();  //Get the size of the bitmap.
        System.out.println("Maximun number of bits in indexmap for T1:"+indexT1.getMaxBitSize()+" number of records read: "+LineCounter);
       
        
        System.out.println("Merging files...");
        Merge.mergeFiles( T1_Size, "T1", Asize);
       
        fileList.clear();
        
        System.out.println("Finish process and combine 1st set processed into T1Sorted.txt");
        
        counter=-1;
        LineCounter=0;
        processedFilesTracker.setProcessedFilesNumber(0);
    	
    	filename="T2_100.txt";
    	try{
    		br.close();
    		br=new BufferedReader(new FileReader(filename));
    	} catch (IOException e) {
            //Do Nothing
        }
    	max=T2_Size/Asize;
    	
    	indexT2 = new BitmapIndex();
    	
        for (int j = 0; j < (max); j++) {
        	
        	counter ++;
  
        	gainedArrFromFile = TPMMSFileReader.readFile(br,indexT2);
        	
                 
            sortFiles.sortFiles(gainedArrFromFile);            	       
            

            try {
            	
            	TPMMSFileWriter.writeListToFile(gainedArrFromFile, counter, filename, fileList, Asize);
            } catch (IOException e) {
                //Do Nothing
            }
        }
        
      //Get the size of the compressed bit map to display.
        HashMap<BIKey, EWAHCompressedBitmap> bitmapsIndex2=indexT2.getBiMaps();
        displaySizeHashMap(bitmapsIndex2);
        
        System.out.println("Maximun number of bits in indexmap for T2:"+indexT2.getMaxBitSize()+" number of records read: "+LineCounter);
        
        System.out.println("Merging files...");
        Merge.mergeFiles( T2_Size, "T2_100", Asize);
		fileList.clear();		
        if(true)
        {	
            System.out.println("2nd set processed and combined into T2_100Sorted.txt");
        }
        
        Collections.sort(bitMapKey);//Sort keys.
        
        System.out.println();
        System.out.println();
        System.out.println("Starting to join the two sorted tuples.."); 
        
        joinTables.compareP();//Do the join merge.
        
        System.out.println("join is completed and stored into CommonRecords.txt");
        long end = System.currentTimeMillis();
        System.out.println("the join  took: "+(end - start) / 1000f + " seconds");
        
        System.out.println();
        start = System.currentTimeMillis();
        displayJoinResults();
        end = System.currentTimeMillis();
        System.out.println();
        System.out.println("Time to query including serialization took: "+(end - start) / 1000f + " seconds");
    }
    
    /**
     * Display the number of rows that are expected in the join result.
     */
    public static void displayJoinResults()
    {
    	for(int i=0;i<bitMapKey.size();i++)//Go thourgh every possible value for the attribute 'department'
    	{
    		//Query for every departments and get the number of records generated in index 1.
            BICriteria criteria = BICriteria.equals(new BIKey(MainClass.DEPARTMENT,bitMapKey.get(i)));
            EWAHCompressedBitmap result = indexT1.query(criteria);
            List<Integer> positionsForKey=result.getPositions();
            
            //Query for every departments and get the number of records generated in index 2.
            EWAHCompressedBitmap result2 = indexT2.query(criteria);
            List<Integer> positionsForKey2=result2.getPositions();
            
            System.out.println("Number of records in index1 for hash: "+bitMapKey.get(i)+" ==>"+positionsForKey.size());
            System.out.println("Number of records in index2 for hash: "+bitMapKey.get(i)+" ==>"+positionsForKey2.size());
            //Display the number of tuples there should be in the join result.
            System.out.println("Multiplication: "+positionsForKey.size()*positionsForKey2.size());
    	}
    	
    }
    

    
    /**
     * Prints out the total size of the bitmap sent as an argument.
     * @param bitmapsIndex1 bitmap to be displayed.
     * 
     */
    public static void displaySizeHashMap(HashMap<BIKey, EWAHCompressedBitmap> bitmapsIndex)
    {
    	bitMapKey= new ArrayList<String>();//put into heap.
    	
    	double totalSize=0;
    	String keyName="";
    	System.out.println();

    	//Go trough each index group in the indexmap.
    	for (HashMap.Entry<BIKey, EWAHCompressedBitmap> entry : bitmapsIndex.entrySet()) {
    		BIKey key = entry.getKey();
    		keyName=key.getKey();
    		EWAHCompressedBitmap value = entry.getValue();
    		 /*
             * serializedSizeInBytes()
             * Report the size required to serialize this bitmap with the current key.
             */
    		
            System.out.println("The size of the bitmap index for department: "+ keyName +" for T1 is: "+value.serializedSizeInBytes()+ " bytes.");  
            totalSize=totalSize+(double)value.serializedSizeInBytes();
            bitMapKey.add(keyName);//Store all the keys.
    	}
    	
    	System.out.println("The total size of the bitmap is: "+totalSize +" bytes.");
    	System.out.println();
    }

}
