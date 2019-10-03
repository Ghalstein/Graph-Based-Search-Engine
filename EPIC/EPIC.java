/*
Open-ended questions:

1. Strategies for exploring the web other than DFS and BFS: There are different implementations
of BFS, such as Dijkstra's shortest path algorithm. This particularly finds the path with the least 
amount of nodes from the root connecting to the desired node. There is also another implementation known as
the A* algorithm, which improves on Dijkstra's by implementing elements of best-first search. The best-frist
search algorithm explores a graph by exapnding on the most significant node, which is chosen by a set of rules.
This way it follows down a single path that it beleives has the best solutions in it and ignores neighboring paths.

2. Determining relevance: My approach in determining relevance involved initially searching for the exact 
phrase of the query either in a title or in the text of the website. If it appeared in both, then I would mark
this as the most relevant page and add first to my linked list of relevant pages. After searching with this method I decided to 
normalize the text I was searching and stemmed the query so that only non-stop words are accounted for in the search. Until the max amount of 
relvant pages were finally added to the list or the limit on how many pages I set the program to look through until it breaks out of the loop.
Then the search algorithm keeps searching through the list of already collected relevant pages by looking at the adjacent URL's on the relevant pages.
So, to me the order of significance is the exact phrase appearing on the direct page, then searching through relevant pages adjacent to 
pages already found. Then are the stemmed queries and the pages adjacent to them until the max relevant pages found is added to the 
list or the iteration limit is hit.(I set a limit for iteration of searches so that the program can time out if it is taking too long, 
this number can bee changed in the code)

3. Relevance can be measured as a point system. This can be done by adding the relevant pages to a list such as an array. Once all of 
the relevant pages are found the points for each page on the list can be compared. One tally of points is measured by appearance of the queries in the text,
another is whether the queries appeared in the title, another is dependent on how many stemmed query words were found. All of these 
different points can then be multiplied by each other and the totals determine the order of relevancy. 

Compile: 
	Windows:
javac -cp .;jsoup.jar EPIC.java

Execution:
	Windows:
java  -cp .;jsoup.jar EPIC -uURL -qQuery -mN
	Mac:
java -cp :./jsoup.jar EPIC -uURL -qQuery -mN
*/

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EPIC{

	private static String stopWords = "‘ourselves’, ‘hers’, ‘between’, ‘yourself’, ‘but’, ‘again’, ‘there’, ‘about’, ‘once’, ‘during’, ‘out’, ‘very’, ‘having’, ‘with’, ‘they’, ‘own’, ‘an’," + 
	" ‘be’, ‘some’, ‘for’, ‘do’, ‘its’, ‘yours’, ‘such’, ‘into’, ‘of’, ‘most’, ‘itself’, ‘other’, ‘off’, ‘is’, ‘s’, ‘am’, ‘or’, ‘who’, ‘as’, ‘from’, ‘him’, ‘each’, ‘the’, ‘themselves’, ‘until’, " +
	"‘below’, ‘are’, ‘we’, ‘these’, ‘your’, ‘his’, ‘through’, ‘don’, ‘nor’, ‘me’, ‘were’, ‘her’, ‘more’, ‘himself’, ‘this’, ‘down’, ‘should’, ‘our’, ‘their’, ‘while’, ‘above’, ‘both’, ‘up’, ‘to’, " +
	"‘ours’, ‘had’, ‘she’, ‘all’, ‘no’, ‘when’, ‘at’, ‘any’, ‘before’, ‘them’, ‘same’, ‘and’, ‘been’, ‘have’, ‘in’, ‘will’, ‘on’, ‘does’, ‘yourselves’, ‘then’, ‘that’, ‘because’, ‘what’, ‘over’, ‘why’," + 
	" ‘so’, ‘can’, ‘did’, ‘not’, ‘now’, ‘under’, ‘he’, ‘you’, ‘herself’, ‘has’, ‘just’, ‘where’, ‘too’, ‘only’, ‘myself’, ‘which’, ‘those’, " + 
	" ‘i’, ‘after’, ‘few’, ‘whom’, ‘t’, ‘being’, ‘if’, ‘theirs’, ‘my’, ‘against’, ‘a’, ‘by’, ‘doing’, ‘it’, ‘how’, ‘further’, ‘was’, ‘here’, ‘than’";
	private static int itLim = 250; // limit on how many iterations you want to wait for (captures up to about 50 rel pages on average before giving up)
	private static int iterations = 0; // keeps track of the iterations in searching pages
	private static int unretievable = 0; // keeps track of the unretrievable errors that came across with pages 
	private static int argTracker = 0; // tracks the argument being processed
	private static boolean u = false; // flag for url
	private static boolean q = false; // flag for query
	private static boolean m = false; // flag for maximum number of relevant pages
	private static String uStr = ""; // url input
	private static String qStr = ""; // value of query input
	private static String mStr = ""; // value of max input
	private static int max = 0; // default max
	private static LinkedList<Page> relPages = new LinkedList<Page>(); // list of the relevant pages
	private static boolean rel = false;

	// searches pages based on the given inital page supplied to it, the list to linked list to keep track of relevant pages, and the query provided
	public static void searchPage(Page p, LinkedList<Page> list, String origQ) throws IOException {
		bigLoop:
		for (String v: p.adjacentURL()) {
		  	if(iterations > itLim){
				break bigLoop;	
			}
	    	iterations++;
	  		if(list.getN() == max){ // stops searching if max amount of relevant pages are found
	  			break;
	  		}
	  		try{
			    Page n = new Page(v);
			    for(Page test : list){
			    	// precaution for duplicates by making sure title is not identical
			    	if(test.getTitle().equals(n.getTitle())){
			    		continue bigLoop;
			    	}
			    	//precaution for duplicates by making sure body text is not identical
			    	if(test.getText().length() == n.getTitle().length()){
			    		continue bigLoop;
			    	}
			    }
			    if(n.getTitle().toLowerCase().contains(origQ.toLowerCase()) && n.getText().toLowerCase().contains(origQ.toLowerCase())){
			    	n.setSnip(origQ);
			    	for(Page test : list){
			    		if(test.getSnip().equals(n.getSnip())){
			    			continue bigLoop;
			    		}
			    	}
			    	if(!rel){
				    	list.addFirst(n);
				    }
				    else{
				    	list.addLast(n);
			    	}
			    } 
			    else if(!n.getTitle().toLowerCase().contains(origQ.toLowerCase()) && n.getText().toLowerCase().contains(origQ.toLowerCase())){
			    	n.setSnip(origQ);
			    	list.addLast(n);
			    }
			} catch (IOException | IllegalArgumentException e) {
				    unretievable++;
				    continue;
			}
			catch (Exception e) {
				System.out.println("Sorry, your input could not be parsed as is.");
				System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
				System.exit(0);
			}	
		}
	}

	public static void main(String[] args) throws IOException {
		// error handling for args less than 3
		if(args.length < 3){
			System.out.println("Sorry, you did not enter 3 parameters.");
			System.exit(0);
		}
		// parsing and error handling the arguments
		try{
			for(String flagTest: args){
				char[] testArr = flagTest.toCharArray();
				if(testArr[0] != '-' && flagTest.equals(args[0])){
					System.out.println("Sorry, your input does not follow the proper usage for EPIC.java on Windows.");
					System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
					System.exit(0);
				}
				if(!(testArr[0] == '-')){
					argTracker++;
					continue;
				}
				if(testArr[1] == 'u'){ // tests the URL flag and saves the URL if it passes
					if(u){
						System.out.println("Sorry, you entered the falg, \"-u\", more than once.");
						System.exit(0);
					}else {
						for(int i = 2; i < testArr.length; i++){
							uStr += testArr[i]; 
						}
						u = true;
						argTracker++;
						continue;
					}
				}
				if(testArr[1] == 'q'){ // tests the query flag and saves the query if it passes. If query is greater than one word it adds to it until another flag is reached
					if(q){
						System.out.println("Sorry, you entered the falg, \"-q\", more than once.");
						System.exit(0);
					}else {
						for(int i = 2; i < testArr.length; i++){
							qStr += testArr[i]; 
						}
						int qTrack = argTracker + 1; // tracks the argument place during the query read
						for(int i = qTrack; i < args.length; i++){
							char[] qTest = args[i].toCharArray();
							if(qTest[0] == '-' && (qTest[1] == 'u' || qTest[1] == 'm' || qTest[1] == 'q')){
								break;
							}
							qStr += " " + args[i];
						}
						q = true;
						argTracker++;
						continue;
					}
				}
				if(testArr[1] == 'm'){ // tests the max flag and sets the max if it passes
					if(m){
						System.out.println("Sorry, you entered the falg, \"-m\", more than once.");
						System.exit(0);
					}else {
						for(int i = 2; i < testArr.length; i++){
							mStr += testArr[i]; 
						}
						m = true;
						argTracker++;
						continue;
					}
					argTracker++;
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("Sorry, your input does not follow the proper usage for EPIC.java on Windows.");
			System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
			System.exit(0);
		}
		catch (RuntimeException re) {
			System.out.println("Sorry, your input could not be parsed as is.");
			System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
			System.exit(0);
		}

		if (qStr.equals("")){
			System.out.println("Sorry, your query cannot be blank.");
			System.exit(0);
		}

		String[] wordsInQ = qStr.split(" ");
		
		// parsing the max value to int
		// makes sure max is a number
		try{
			max = Integer.parseInt(mStr);
		}catch(NumberFormatException nfe){
			System.out.println("Sorry, it appears that the value, \"" + mStr + 
				"\", which is supposed to represent \nthe maximum number of relevant pages that can be displayed is not an integer.");
			System.exit(0);
		}
		catch (Exception e) {
			System.out.println("Sorry, your input could not be parsed as is.");
			System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
			System.exit(0);
		}

		// parsing the URL and searches it if no error is found
		System.out.print("Fetching you results...");
		// first searches the direct string inputted
  		Page p = new Page(uStr);
  		searchPage(p, relPages, qStr);
  		rel = true; // most relvant pages found so any others found will be added to the list last

  		// if max has not been reached yet for the size of the list then it will search the links from the relevant pages found 
		if(relPages.getN() > 0 && relPages.getN() < max){
			biggerLoop:
			for (Page pg : relPages) {
				for (String v: pg.adjacentURL()) {
				if(iterations > itLim){
		  			break biggerLoop;
		  			}
			  		if(relPages.getN() == max){
			  			break biggerLoop;
			  		}
			  		searchPage(pg, relPages, qStr); 
				}
			}
		}
		
		// if max still has not been reached then it will search through stemmed version of ever
		if(relPages.getN() < max){
			biggerLoop:
			for(String str : wordsInQ){
				Stemmer stem = new Stemmer();
				str = stem.stem(str);
				if(stopWords.contains(str)){
					continue;
				}
				Page pg = new Page(uStr);
				for (String v: pg.adjacentURL()) {
			  		if(relPages.getN() == max){
			  			break biggerLoop;
			  		}
		  			if(iterations > itLim){
	  					break biggerLoop;
		  			}
			  		searchPage(pg, relPages, str); 
				}
			}
		}
		System.out.println(); // just for formative purposes
		if(unretievable > 0) {
			System.out.println();
			System.out.println(unretievable + " page(s) were unretievable.");
		}
		System.out.println();
		System.out.println(relPages.getN() + " relevant page(s) have been found.");
		System.out.println();
		if(relPages.getN() > 0){	
			System.out.println("Here are your search results for the key phrase \"" + 
				qStr + "\" on the website \"" + uStr + "\":");
			System.out.println();
			int i = 1; // number of each page
			for (Page pg : relPages) {
				System.out.println("Page " + i + ": " + pg.getTitle());
				System.out.println("URL: " + pg.getURL());
				System.out.println("Snippet: " + pg.getSnip());
				System.out.println();
				i++;
			}
		}
	}
}