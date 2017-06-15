package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy for getTimespan()
     * 
     * tweetList - list of tweets.
     * 
     * Partition the inputs as follows:
     * tweetList.size() = 0, 1, > 1
     * timestamp = EPOCH, // Constant for the 1970-01-01T00:00:00Z epoch instant.
     *             MAX,   // The maximum supported Instant, '1000000000-12-31T23:59:59.999999999Z'.
     *             MIN    // The minimum supported Instant, '-1000000000-01-01T00:00Z'.
     *             myEarliestTime // The earliest timestamp in the tweet list.
     *             myLatestTime   // The latest timestamp in the tweet list.
     *             
     * Cover each part testing coverage.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-12-01T00:00:00Z");
    private static final Instant d4 = Instant.parse("2016-12-31T00:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "smith", "Kyle, @johnsmith is my name", d3);
    private static final Tweet tweet8 = new Tweet(8, "playboy", "hi my name is @playboy!", d3);    
    private static final Tweet tweet4 = new Tweet(4, "blueblue", "fyi: @johnsmith @JohnSmith are the same name", d2);
    private static final Tweet tweet5 = new Tweet(5, "Samuel", "@usa123, @canada-1, @usa_456", d2);
    private static final Tweet tweet6 = new Tweet(6, "HelloKitty", "one or two or no name? @johnsmith@mit?", d2);
    private static final Tweet tweet7 = new Tweet(7, "snowwhite", "this username @A1234567890123456789 is too long!", d2);
    private static final Tweet tweet9 = new Tweet(9, "batman", "my email address: batman@gmail.com", d2);
    private static final Tweet tweet10 = new Tweet(10, "steve", "This is a legit name-mention: @@dupatsign", d2);
    private static final Tweet tweet11 = new Tweet(11, "playgirl", "@JohnSmith, let's have dinner together", d1);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing strategy for getTimespan()
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     * 
     * 
     * Partition the inputs as follows:
     * tweetList - list of tweets.
     * tweetList null ptr tested.
     * tweetList.size() = 0, 1, > 1 (we will use 2 and 3 along with timestamps testing below)
     *
     * timestamps of 3 tweets A > B > C:
     *    A B C
     *    A C B
     *    B A C
     *    B C A
     *    C A B
     *    C B A
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testGetTimespanNullList() {
        boolean thrown = false;
        
        try {
            Extract.getTimespan(null);
          } catch (NullPointerException e) {
            thrown = true;
          }

          assertTrue(thrown);
    }
    

    
    @Test
    public void testGetTimespanEmptyList() {
        Instant startTime, endTime;
        
        Timespan timespan = Extract.getTimespan(Arrays.asList());
        
        // there is no definition when the tweet list is empty, but it must not be
        // null return.
        assertNotNull(timespan);
        
        // as a boundary condition, check if the timespan returned is legit
        //
        startTime = timespan.getStart();
        endTime = timespan.getEnd();
        assertTrue (startTime.equals(endTime));
        
    }
    
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("List start time should equal to end time!", timespan.getStart(), timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsABC() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsACB() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsBAC() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet1, tweet3));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsBCA() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet3, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsCAB() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanThreeTweetsCBA() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    /*
     * Testing strategy for getMentionedUsers()
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
     * 
     * 
     * Partition the inputs as follows:
     * tweetList - list of tweets.
     * tweetList null ptr tested.
     * tweetList.size() = 0, 1, > 1 (we will use 2)
     * tweet with valid username-mention = 0, 1, 2
     * Valid username-mention: [A-Z][a-z][0-9][-][_] and 1 <= length <= 15
     *     @@valid-name
     * 
     * invalid username-mention tests:
     *     @johnsmith@mit has one valid name: @johnsmith
     *     name length too long (more than 15 legit characters)
     *     email address such as: xxx@gmail.com
     * 
     * duplicate username-mention (case insensitivity) test
     * username-mention in the beginning/end of text string
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void getMentionedUsersNullList() {
        boolean thrown = false;
        
        try {
            Extract.getMentionedUsers(null);
          } catch (NullPointerException e) {
            thrown = true;
          }

          assertTrue(thrown);
    }

    
    @Test
    public void testGetMentionedUsersListNull() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList());
        
        assertNotNull("Null list returned!", mentionedUsers);
    }
    
    @Test
    public void testGetMentionedUsersEmptyList() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList());
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
      
    @Test
    public void testGetMentionedUsersOneTweetNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    @Test
    public void testGetMentionedUsersOneTweetOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("OneTweetOneMention: " + name));

        assertTrue("testGetMentionedUsersOneTweetOneMention:expected one name in set", mentionedUsers.size() == 1);
    }
    
    @Test
    public void testGetMentionedUsersOneTweetThreeMentionsBeginEnd() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("OneTweetThreeMentionsBeginEnd: " + name));

        assertTrue("expected three names in set", mentionedUsers.size() == 3);
    }
    
    @Test
    public void testGetMentionedUsersTwoTweetsTwoMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet8));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("TwoTweetsTwoMentions: " + name));

        assertTrue("expected two names in set", mentionedUsers.size() == 2);
    }
       
    @Test
    public void testGetMentionedUsersDupName() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("DupName: " + name));

        assertTrue("expected no dup name in set", mentionedUsers.size() == 1);
    }
    
    @Test
    public void testGetMentionedUsersMultipleTweetsOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet11));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("MultipleTweetsOneMention: " + name));

        assertTrue("testGetMentionedUsersMultipleTweetsOneMention:expected one name in set", mentionedUsers.size() == 1);
    }
       
    
    @Test
    public void testGetMentionedUsersNoSeparator() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet6));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("NoSeparator: " + name));

        assertTrue("expected only one name!", mentionedUsers.size() == 1);
    }


    @Test
    public void testGetMentionedUsersNameTooLong() {        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet7));
        
        // ** test
        // System.out.println("* testGetMentionedUsersNameTooLong");
        
        assertNotNull ("returned null!", mentionedUsers);
        
        // ** test - Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("NameTooLong: " + name));

        // the length of a legit name is not defined in the param
        // assertTrue("expected no name in set, name too long!", mentionedUsers.isEmpty());
    }

    
    @Test
    public void testGetMentionedUsersEmailAddress() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet9));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("EmailAddress: " + name));

        assertTrue("expected no name in set!", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersAtSignTooMany() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet10));
        
        // Use stream to print out the valid names.
        // mentionedUsers.forEach(name -> System.out.println("AtSignTooMany: " + name));

        assertTrue("testGetMentionedUsersAtSignTooMany:expected one name in set!", mentionedUsers.size() == 1);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
