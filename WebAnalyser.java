/**
 * @author Parthiban NS
 * 
 *  Java Multi-threaded URL scrape application
 *  */

package CO3090.coursework1;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebAnalyser implements Runnable, WebStatistics{
	
	Shared_DataStructures sdb;   
	
	 String keyword;	

	 public static int MAX_THREAD_NUM=20; 
	 public static int MAX_PAGE_COUNT=500;     	
	 public static int MAX_WORDS_COUNT=80000;		
	 public static int TIME_OUT=1000000;

	 public WebAnalyser(Shared_DataStructures sd) {
		 sdb=sd;
	 }
	 
/*

Please see WebStastistics.java for more information

*/	 

	@Override
	public void find(String keyword, String url) {						//throws InterruptedException {
		if (url != null) { 												// proceed only when there are URLs embedded in the page.
			
			// 1. get content from url
			// 2. do word count with content and store URL/count in Map DS
			String content=Helper.getContentFromURL(url);
			int word_count = Helper.countNumberOfOccurrences(sdb.keyword,content);
			sdb.results.put(url, word_count);
			System.out.println();
		}
		System.out.println("Queue is: " + sdb.syn_set);
	}


	@Override
	public void printStatistics() {
			//throws KeywordNotProvidedException, IllegalURLException {
		System.out.println();System.out.println();
		System.out.println("**************************************************************************************************************************");
		System.out.println("**************************************************************************************************************************");
		System.out.println("FINAL OUTPUT:");System.out.println();
		System.out.println("***** Final list of visited URLs: " );
		Iterator<String> it = sdb.visited_set.iterator();
		while(it.hasNext())
		{
		System.out.println(it.next());
		}
		System.out.println("***** Count of keyword " + sdb.keyword +" occurence in each URL: ");
		for (Map.Entry<String, Integer> entry : sdb.results.entrySet()) {
			String k = entry.getKey();
			Integer v = entry.getValue();
			System.out.println(k + " : " + v);
		}
		System.exit(0);
	}


	@Override
	public void run() {
		System.out.println("Starting thread " + Thread.currentThread().getName());
				
		String url;
		url = sdb.pop_queue_first();    								// Each thread retrieve first url from queue to process
		find(sdb.keyword,url);
		
		
		/*--------------- Each thread to check these conditions before exiting:    -----------------------------*/
		sdb.end_time = sdb.start_time + TIME_OUT;
		Integer current_words_count = 0;
		for (Integer in : sdb.results.values()) {   current_words_count += in;    }
									
		if(System.currentTimeMillis() > sdb.end_time)
		{
			System.out.println("The timeout of " + TIME_OUT + " milliseconds reached. Exiting...!!");
			System.out.println("Start time: " + sdb.start_time);
			System.out.println("Current time: " + System.currentTimeMillis());
			printStatistics();
		}
		else if(current_words_count > MAX_WORDS_COUNT)								
		{
			System.out.println("The set max words count of " + MAX_WORDS_COUNT + " reached. Exiting...!!");
			printStatistics();
		}
		else if(sdb.visited_set.size() == MAX_PAGE_COUNT)								
		{
			System.out.println("The set max page count of " + MAX_PAGE_COUNT + " reached. Exiting...!!");
			printStatistics();
		}
		else{}	
		/*-------------------------------------------------------------------------------------------------------*/
		
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		long start_time = System.currentTimeMillis();
	    //System.out.println("Main thread starting at:" + start_time);
	         
		Shared_DataStructures sd_obj = new Shared_DataStructures();
		sd_obj.keyword="for";
		String websiteURL="http://www.comfortmosquitonet.com/shop/";
		sd_obj.set.add(websiteURL); System.out.println("Added seed url to queue.");
		sd_obj.start_time = start_time;
		
		WebAnalyser wa_obj = new WebAnalyser(sd_obj);
		ExecutorService pool = Executors.newFixedThreadPool(wa_obj.MAX_THREAD_NUM);
		
		while(!sd_obj.syn_set.isEmpty())
		{
			pool.execute(wa_obj);
			pool.awaitTermination(1, TimeUnit.SECONDS);					// This is important to keep the while loop spinning out of control.
		}		
		
		wa_obj.printStatistics();
		pool.shutdown();
	}
}
