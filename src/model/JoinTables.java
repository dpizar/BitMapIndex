package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import bimapindex.BICriteria;
import bimapindex.BIKey;

import com.googlecode.javaewah.EWAHCompressedBitmap;

import main.MainClass;

/**
 * This class compares tuples with the files *
 */
public class JoinTables{
	
	public int [] tupleCount;
	
	private ArrayList<String> dataForDepartment;//Data for a certain department from the smallest table.
	private int loopNumberOfTuples;
	private int departmentIndex;
	
	public JoinTables()
	{
		this.departmentIndex=0;//set to zero.
		this.dataForDepartment=new ArrayList<String>();
		this.loopNumberOfTuples=0;
	}
	
	/**
	 * Sets the number of tuples that belong to a certain department into the instance variable 'loopNumberOfTuples'.
	 * This is computed from the smallest file. After that populates an temporary array list with all these tuples.
	 * @param smallReader 
	 * @param departmentIndex
	 * @throws IOException 
	 */
	private void populateArray(BufferedReader smallReader, int departmentIndex) throws IOException
	{
		this.dataForDepartment.clear();
		String temp="";
		
		//Get the number of tuples that are in the smallest file, that correspond to a respective department.
		String currentDepartment=MainClass.bitMapKey.get(departmentIndex);
		
		//Get number of tuples in the small file for a certain department.
        BICriteria criteria = BICriteria.equals(new BIKey(MainClass.DEPARTMENT,currentDepartment));
        EWAHCompressedBitmap result = MainClass.indexT1.query(criteria);//Smallest file index is indexT1.
        List<Integer> positionsForKey=result.getPositions();
        
        this.loopNumberOfTuples= positionsForKey.size();
        
        for(int i=0;i< loopNumberOfTuples;i++)
        {
        	temp=smallReader.readLine();// Might throw IOException
        	this.dataForDepartment.add(temp);
        }
	}
	
	

	/**
	 * Join two tables.
	 */
	public  void compareP(){
		Writer writer=null;
		Writer sharedData=null;//Writer fo the data gathered.
		BufferedReader bigReader=null;
		BufferedReader smallReader=null;
		try { 
			bigReader=new BufferedReader(new FileReader("T2_100Sorted.txt"));
			smallReader=new BufferedReader(new FileReader("T1Sorted.txt"));
			//writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SharedRecords.txt")));
			
			sharedData= new BufferedWriter(new OutputStreamWriter(new FileOutputStream("sharedRecodsData.txt")));
			
			String s1="";
			String s2="";
			
			int departmentCounter=0;
			int counter=0;
			int indexT1=0;
			//int indexT2=0;
			
			s1=bigReader.readLine();
			// s2=smallReader.readLine();
			
			String hashKey= MainClass.bitMapKey.get(departmentCounter);
			populateArray(smallReader,departmentCounter);
			
			tupleCount= new int[MainClass.bitMapKey.size()];//Will store the number of tuples generated in the join for each department.
			
			while(true)
			{
				
				if( s1==null)
				{
					break;
				}
				
				
				for(int i=0;i<this.loopNumberOfTuples;i++)
				{
					if(s1.substring(17, 20).equals(dataForDepartment.get(i).substring(17, 20)))//If their department value matches add the tuple
					{
						//Don't write them just count how many of them exist.
						//writer.write(s1+" "+dataForDepartment.get(i));
						//writer.write("\r");
						counter++;
						indexT1++;
						tupleCount[departmentCounter]++;//Count tuples generated.
						
					}else
					{
						indexT1++;
					}
				}
				
				s1=bigReader.readLine();
				
				if( s1==null)
				{
					break;
				}
			
				if(!s1.substring(17, 20).equals(hashKey))
				{
					departmentCounter++;
					if(MainClass.bitMapKey.size()>departmentCounter)
					{
						populateArray(smallReader,departmentCounter);//Populate the array list with new parameters.
						hashKey= MainClass.bitMapKey.get(departmentCounter);//change hash key to the current one.
					}
				}
			}
			
			System.out.println("The two files share "+counter+" records");
			
			for(int i=0;i<this.tupleCount.length;i++)
			{
				String tmp="Number of tuples generated for: "+MainClass.bitMapKey.get(i)+" ==>"+tupleCount[i];
				System.out.println(tmp);
				sharedData.write(tmp);
				sharedData.write("\r");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 

		finally {
			try{
				if (bigReader != null)
					bigReader.close();
				if (smallReader!=null)
					smallReader.close();
				if (writer!=null)
					writer.close();
				if(sharedData!=null)
					sharedData.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
