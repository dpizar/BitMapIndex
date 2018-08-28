package model;

/**
 * This class is to keep track of the number of tracked processed tuples
 *
 */
public class ProcessedFilesTracker {
	
	public static int processed;
	
	public void setProcessedFilesNumber(int processedFiles){
		processed = processedFiles;
	}
	
	public int getProcessedFilesNumber(){
		 return processed;
	}	
}
