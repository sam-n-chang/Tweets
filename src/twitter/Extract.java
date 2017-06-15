package twitter;

import java.util.List;
import java.util.Set;
import java.time.Instant;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.TreeSet;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(final List<Tweet> tweets) {
        
        // for every tweet in the list, retrieve the timespan and
        // save the earliest and latest time stamp for return.
        Instant timestamp;
        Instant start = Instant.parse("1970-01-01T00:00:00Z");
        Instant end = Instant.parse("1970-01-01T00:00:00Z");

        Tweet twt;
        
        // the tweets list must not be null
        if (tweets == null) {
            throw new NullPointerException("tweet list is null");
        }
           
        // check if the list is empty
        if (tweets.isEmpty()) {
            // System.out.println("getTimespan - Empty tweets list!"); // ** test
            return new Timespan(start, end);
        }
        // take the timestamp of the first element to begin with.
        twt = tweets.get(0);
        timestamp = twt.getTimestamp();    // instant of this tweet
        start = end = timestamp;
        
        for (int i = 1; i < tweets.size(); ++i) {
            twt = tweets.get(i);
            timestamp = twt.getTimestamp();
            if (timestamp.isBefore(start)) {
                start = timestamp;
            }
            if (timestamp.isAfter(end)) {
                end = timestamp;
            }
        }
       
        return new Timespan(start, end);
    }
    
    /**
     * Get usernames mentioned in a tweet.
     * 
     * @param tweet
     *         A tweet to be parsed.
     * @return the set of unique usernames who are mentioned in the text of this tweet.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedNamesFromOneTweet(final Tweet tweet) {
        String twtText, validAuthor;
        Matcher matcher;
        boolean duplicate = false;
        final Set<String> mentionedUsers = new TreeSet<String>();
        
        // the tweet must not be null
        if (tweet == null) {
            throw new NullPointerException("tweet is null");
        }
        
        // A username is just the username-mention without the @. Or rather, 
        // the username-mention is @ plus a username.
        // 
        // "(?<! [\w\-])" use negative lookbehind to make sure there is no valid 
        //    letter (including "-") in front of @, then
        // "([\w\-]){1, 15}" there are at least 1 but no more than 15 valid
        //    letters.
        // "(?=$|\\s|\\,|\\.])" only "end of string" or "white space" or "," or "." 
        //    allowed after the legal name.
        
  //      String regex = "(?<![\\w\\-])@(([\\w\\-]){1,15})(?=$|\\s|\\,|\\.])";
        String regex = "(?<![\\w\\-])@(([\\w\\-]){1,15})(?![\\w\\-])";
        final Pattern pattern = Pattern.compile(regex);
                
        // parse the text of the tweet according to the pattern.
        twtText = tweet.getText();
        // System.out.println("input text: " + twtText); // *** test
        matcher = pattern.matcher(twtText);
          
        // find all matches in the string. Only the username is needed, not including @
        while (matcher.find()) {
            validAuthor = matcher.group(1);
            // System.out.println("valid name: " + validAuthor); // *** test
            
            // find out if the name already exists in the set.
            for (String name : mentionedUsers)
            {
                if (name.compareToIgnoreCase(validAuthor) == 0) {
                    duplicate = true;
                }
            }

            if(!duplicate) { // name is not in the set yet
                mentionedUsers.add(validAuthor);
            }
            else {
                duplicate = false; // reset the flag
            }
        };
        return mentionedUsers;
    }


    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(final List<Tweet> tweets) {
        final Set<String> mentionedUsers = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        
        // the tweets list must not be null
        if (tweets == null) {
            throw new NullPointerException("tweet list is null");
        }
        
        // check if the list is empty
        if (tweets.isEmpty()) {
            // System.out.println("Empty tweets list!"); // ** test
            return Collections.emptySet();
        }
        
        // find out all mentioned names in the tweet list.
        for (Tweet t : tweets) {
            // add all names found to the set if they are not alrady present.
            mentionedUsers.addAll(getMentionedNamesFromOneTweet(t));
        }
        return mentionedUsers;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
