package model;




import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This class is to merge the sorted lists into one list.
 *
 */
public class Merge {
	
	public static  void mergeFiles(int maxL,String newFilename,int Asize){
		
		Map<String, BufferedReader> bufferreaders = new HashMap<>();
		int Generalsize=(maxL/Asize);	
		Writer writer=null;
		try{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFilename+"Sorted.txt")));
			//initialize the name for a buffer reader for each file there is to read(Number of sublists).
			//'%05d' means padding zeros to the left with size 5. so 1 would be 00001.
			for(int i=0;i<Generalsize ;i++)
			{
				String b = String.format("%05d", i);
				bufferreaders.put("br"+b, new BufferedReader(new FileReader("SampleOutput"+b+".txt")));
			}		
			//Create an array of strings the same size as the total number of sublists.
			String []inputTA=new String[(Generalsize)];
			int counter=0;
			
			for(int i=0;i<inputTA.length;i++)
			{
				String b = String.format("%05d", i);
				String temp=bufferreaders.get("br"+b).readLine();//Get the next line from all the sublists..
				inputTA[i]=temp.substring(17, 20)+"br"+b+temp;
			}			
			counter=inputTA.length;
			//Merge all the records(500000 and later 1000000) by getting the smallest one,from the sublists, and writing it to a main file.
			for(int i=0;i<maxL;i++)
			{
				int min=1000000000;
				int index=-1;
				for(int j=0;j<inputTA.length ;j++){
					//Here we get the data in inputTA[], not from temp which holds the value from the tuple.
			        if(Integer.parseInt(inputTA[j].substring(0, 3)) < min){	
			        	min=Integer.parseInt(inputTA[j].substring(0, 3));
			            index = j;
			        }
				}
				String temp=inputTA[index].substring(10);//Get from 10 since the first 3 characters are the dept, +2 for 'br' + 5 for the number padded 0's length 5.
				writer.write(temp);
	            writer.write("\r"); 
	            
				temp=inputTA[index].substring(3, 10);
				String record="";
			        
				record=bufferreaders.get(temp).readLine();
	    		if(record==null)
	    		{
	    			inputTA[index]="99999999";
	    		}
	    		else{
	    			inputTA[index]=record.substring(17, 20)+temp+record;
	    			counter++;
	    		}
			    min=1000000000;
			}
			System.out.println(counter+" records compined together in "+newFilename+"Sorted.txt");
			
		}catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try{
				for(int i=0;i<(Generalsize);i++)
				{
					String b = String.format("%05d", i);
					if(bufferreaders.get("br"+b)!=null)
						bufferreaders.get("br"+b).close();
				}				
				writer.close();				
			} catch (IOException ex) {
			//Swallow exception
			}
		}
	}
}
