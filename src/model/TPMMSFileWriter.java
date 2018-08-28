package model;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


/**
 * This file is used to write tuples to output files
 *
 */
public class TPMMSFileWriter {
	
public static void writeListToFile(List<String> records, int counter, String filename, List<File> fileList, int Asize) throws IOException {
		Writer writer=null;
		try {
	    	String b = String.format("%05d", counter);
	    	File f=new File("SampleOutput"+b+".txt");
	        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
	        fileList.add(f);
	        ProcessedFilesTracker.processed+=Asize;
	        if(ProcessedFilesTracker.processed%250==0)
	        	System.out.println(ProcessedFilesTracker.processed+" records processed of file "+filename);
	        for (int i = 0; i < Asize; i++) {
	            writer.write(""+records.get(i));
	            writer.write("\r");
	        }
	        records.clear();
	        writer.flush();
	        writer.close();
	    } finally {
	    	//Nothing
	    }
	}
}
