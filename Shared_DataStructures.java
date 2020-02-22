/**
 * @author Parthiban NS
 * 
 *  Java Multi-threaded URL scrape application
 *  */

package CO3090.coursework1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Shared_DataStructures {
    Set<String> set = new HashSet<String>();    				// Initial set into which all URLs are added. Set avoids duplicates. #Queue.
    Set<String> syn_set = Collections.synchronizedSet(set);     // Synchronized, thread-safe form of the above set.  #SynchronousQueue
    Set<String> visited_set = new HashSet<String>();			// Keep track of visited URLs in visited_set. This DS need not be synchronous.
    HashMap<String, Integer> results = new HashMap<String, Integer>();    // Final output of URL / word count is stored in results.

    String keyword;
    long start_time, end_time;
    
    /* Synchronized Method for each thread to pop first item from the queue and process */
	public synchronized String pop_queue_first(){       		
		String first_item;		
		if (!syn_set.isEmpty())									// process the queue only when non-empty
		{
			first_item = syn_set.iterator().next();
			visited_set.add(first_item); 
			
			String content=Helper.getContentFromURL(first_item);
			ArrayList<String> urls=Helper.getHyperlinksFromContent(first_item, content); 
			add_to_queue(urls);
			
			syn_set.remove(first_item);			
			return first_item;
		}
		else {
			return null;
		}
	}
	
	
	/* Synchronized Method to add the urls extracted from given url into the queue  */
	public synchronized void add_to_queue(ArrayList<String> urls) {  
		for (int i=0;i<urls.size();i++) {
			if(!visited_set.contains(urls.get(i)))			     // If the url is already present in visited_set it is duplicate - do not re-add to queue
			set.add(urls.get(i));
		}
	}
	
		
}
