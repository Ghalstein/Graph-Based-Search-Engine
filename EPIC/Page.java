/******************************************************************************
 *  Compilation:  javac Page.java
 *  Execution:    java -cp :./jsoup.jar Page http://www.nytimes.com
 *  Dependencies: LinkedList.java jsoup.jar
 *  
 *  Models a "Web Page", consisting of a URL, a title, the text of the body,
 *  and the list of links within. The program uses jsoup for accessing and
 *  manipulating web pages. In particular, part of the code for accessing
 *  and extracting links are from this example:
 *   https://jsoup.org/cookbook/extracting-data/example-list-links 
 *
 ******************************************************************************/

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
    private static Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN
	= "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,15}";
    private static Pattern patrn = Pattern.compile(DOMAIN_NAME_PATTERN);

    private String URL;
    private String theTitle;    
    private Document doc; 
    private String theText;
    private String snip;

    public Page(String url) throws IOException {

	try{
		URL = url;
		doc = Jsoup
		    .connect(URL)
		    .userAgent("Jsoup client")
		    .timeout(3000).get();

		theText = doc.select("body").text();
		theTitle = doc.title();
		snip = "";
	}
	catch (java.net.UnknownHostException uhe) {
		System.out.println("Sorry the URL that you provided cannot be found.");
		System.exit(1);
	}
	catch (IllegalArgumentException iae) {
		System.out.println("Sorry, you did not provide a URL to query.");
		System.exit(1);
	}
	catch (NoClassDefFoundError ncdfe) {
		System.out.println("Sorry the URL that you provided cannot be found.");
		System.exit(1);
	}
	catch (Exception e) {
		System.out.println("Sorry, your input could not be parsed as is.");
		System.out.println("Correct Usage: java -cp .;jsoup.jar EPIC -u<URL> -q<Query> -m<N>");
		System.exit(0);
	}

	// System.out.println("opened " + theTitle + "\n" + theText.substring(0, Math.min(1000,theText.length())));
    }

    public void setSnip(String key){
    	if(theText.toLowerCase().contains(key.toLowerCase())){
    		int index = theText.toLowerCase().indexOf(key.toLowerCase());
    		int front = index - 1;
    		String frontStr = "";
    		int back = index;
    		String backStr = "";
  			char[] testArr = theText.toCharArray();
  			// finding beginning of the snippet
  			for(int i = front; i > front - 40; i--){
  				if(i <= 0){
  					break;
  				}
  				frontStr += testArr[i];
  				if(i - 1 == 0){
  					break;
  				}
  				if(i == front - 39){
  					frontStr += "...";
  				}
  			}
  			StringBuilder sb = new StringBuilder(frontStr);
  			frontStr = sb.reverse().toString();

  			// finding the end of the snippet
  			for(int i = back; i < back + 40; i++){
  				if(i <= 0){
  					break;
  				}
  				backStr += testArr[i];
  				if(i + 1 == testArr.length - 1){
  					break;
  				}
  				if(i == back + 39){
  					backStr += "...";
  				}
  			}
  			snip = frontStr + backStr;
    	}
    }

    public String getTitle() { return theTitle; }
    public String getText() { return theText; }
    public String getURL() { return URL; }
    public String getSnip() { return snip;}

    public Iterable<String> adjacentURL() {
	LinkedList<String> domains = new LinkedList<String>();
	Elements links = doc.select("a[href]");
	
	for (Element link : links) {
	    String attr = link.attr("href");
	    String domainName;
	    // System.out.println("trying " + attr);
	    if (attr.startsWith("http") || attr.startsWith("https"))
		domainName = attr;
	    else
		if (attr.startsWith("/"))
		    domainName = URL + attr;
		else
		    domainName = URL + "/" + attr;
	    if (domains.exists(domainName) || domainName.equals(URL))
		continue;
	    domains.addLast(domainName);
	}
	return domains;
    }

    public Iterable<Page> adjacentTo() {
	// This retrieves all the pages that can be accessed
	// through the links of the current Page.
	LinkedList<Page> theLinks = new LinkedList<Page>();
	LinkedList<String> domains = new LinkedList<String>();
	Elements links = doc.select("a[href]");
	
	for (Element link : links) {
	    String attr = link.attr("href");
	    String domainName;
	    // System.out.println("trying " + attr);
	    if (attr.startsWith("http") || attr.startsWith("https"))
		domainName = attr;
	    else
		if (attr.startsWith("/"))
		    domainName = URL + attr;
		else
		    domainName = URL + "/" + attr;
	    try {
		if (domains.exists(domainName) || domainName.equals(URL))
		    continue;
		domains.addLast(domainName);
		theLinks.addLast(new Page(domainName));
	    }
	    catch (IOException | IllegalArgumentException e) {
		System.out.println("unretievable: " + domainName + "; " + e.getMessage());
	    }
	}
	return theLinks;
    }
    
    public static String getDomainName(String url) {
	String domainName = "";
	matcher = patrn.matcher(url);
	if (matcher.find()) 
	    domainName = matcher.group(0).toLowerCase().trim();
	return domainName;
    }

    public static void main(String[] args) throws IOException {
		boolean original = true;
		if (original) { // this is what was in the original program
		    Page p = new Page(args[0]);
		    // An illustration of adjacentTo()
		    System.out.println(p.getText());
		    for (Page v: p.adjacentTo())
			System.out.println(v.getURL());
		}
		else { // the new iterable adjacentURL() may be a better design 
		    Page p = new Page(args[0]);
		    // An illustration of adjacentURL()
		    System.out.println(p.getText());
		    for (String v: p.adjacentURL()) {
				try {
				    Page n = new Page(v);
				    System.out.println(n.getURL());
				}
				catch (IOException | IllegalArgumentException e) {
				    System.out.println("unretievable: " + v + "; " + e.getMessage());
				    continue;
				}
		    }

		} 
    }
}