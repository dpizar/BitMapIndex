package model;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;


/**
 * This class is to sort the files
 *
 */
public class SortFiles {
	

	
public void sortFiles(List<String> list){
	//Collections.sort(list);	//sort by Id number.
	
	//Sort by department attribute.
	Collections.sort(list,
            new Comparator<String>()
            {
		 		@Override
                public int compare(String f1, String f2)
                {
                	f1=f1.substring(17,20);
                	f2=f2.substring(17,20);
                	int f1Int=Integer.parseInt(f1);
                	int f2Int=Integer.parseInt(f2);;
                    //return f1.compareTo(f2);
                    
                    if(f1Int == f2Int)
                    	return 0;
                    if(f1Int > f2Int)
                    	return 1;
                    if(f1Int < f2Int)
                    	return -1;
                    return 0;
                }        
            });
	}	
}
