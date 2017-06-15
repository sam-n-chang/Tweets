package twitter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.time.Instant;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(final List<Tweet> tweets, final String username) {

        final List<Tweet> matchedTweets = new ArrayList<Tweet>();
        
        // find the tweets with a matching username.
        for (Tweet t : tweets) {
            if (t.getAuthor().equalsIgnoreCase(username)) {
                matchedTweets.add(t);
            }
        }
        return matchedTweets;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(final List<Tweet> tweets, final Timespan timespan) {

        final List<Tweet> matchedTweets = new ArrayList<Tweet>();
        final Instant start, end;
        Instant timestamp;
        
        // Set the start and end timestamp of the duration.
        // System.out.println("Timespan : " + timespan.toString()); // *** test
        start = timespan.getStart();
        end = timespan.getEnd();
        
        for (Tweet t : tweets) {
            timestamp = t.getTimestamp();
            if ((!timestamp.isBefore(start)) && (!timestamp.isAfter(end))) {
                // System.out.println("Matched timestamp: " + timestamp.toString()); // ** test
                matchedTweets.add(t);
            }
        }
        return matchedTweets;
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(final List<Tweet> tweets, final List<String> words) {
        
        final List<Tweet> matchedTweets = new ArrayList<Tweet>();
        String textString, regex;
        Matcher matcher;
        
        // first make sure either the words or tweets list is not empty, do nothing if so.
        if ((words.size() == 0) || (tweets.size() == 0))
                return matchedTweets;
        
        // prepare regex based on the words list: on word boundary
        // 
        regex = "\\b(";
        regex = regex.concat(words.get(0));
        for (int i = 1; i < words.size(); ++i) {
            regex = regex.concat("|");
            regex = regex.concat(words.get(i));
        }
        regex = regex.concat(")\\b");
        // System.out.println("regex = " + regex); // ** test
        
        // compile the regex to be used in matcher()
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        
        // loop thru the whole tweet list to check if its text string contain any
        // word from the words list.
        for (Tweet t : tweets) {
            textString = t.getText();
            
            // System.out.println("Text string to be matched: " + textString); // ** test
            
            matcher = pattern.matcher(textString);

            // Find if matched.
            if (matcher.find()) {
                // System.out.println("match found : " + matcher.group(1)); // ** test
                // check if this tweet is already on our list?
                if (!matchedTweets.contains(t))
                    matchedTweets.add(t);
            }
        }
        return matchedTweets;
    }
    
    /*
    private static List<Tweet> containing (List<Tweet> tweets, List<String> words) {
        List<Tweet> rt = new ArrayList<Tweet>();
        for (Tweet t: tweets) {
            for (String w: words) {
                if (Arrays.asList(t.getText().toLowerCase().split(" ")).contains(w.toLowerCase()) && !rt.contains(t)) {
                    rt.add(t);
                }
            }
        }
        return rt;
    }
    */

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
