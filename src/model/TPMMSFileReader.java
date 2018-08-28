package model;

import java.util.List;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;

import main.MainClass;
import bimapindex.BIKey;
import bimapindex.BitmapIndex;

/**
 * This class is used to read the files and return their contents
 *
 */
public class TPMMSFileReader {
	BufferedReader br;
	private static int Asize=250;
	
	public TPMMSFileReader (BufferedReader bufferReader){
		br = bufferReader;
	}
	
	public static List<String> readFile(BufferedReader br,BitmapIndex index) {	
		
	    List<String> returnedArray = new ArrayList<String>();    
	    try 
	    {
	    	for (int i = 0 ; i < Asize; i++) {
	            String temp=br.readLine();
	            if(temp==null){
	            	break;
	            }
	            index.set(new BIKey(MainClass.DEPARTMENT, temp.substring(17, 20)), MainClass.LineCounter);//Add to the index.
	            returnedArray.add(temp.trim());
	            MainClass.LineCounter++;
	        }	    	
		} catch (IOException e) {
		       //Do nothing
		}
	    return returnedArray;
	}
}
