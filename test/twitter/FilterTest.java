package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-09-15T00:00:00Z");
    private static final Instant d4 = Instant.parse("2016-10-15T00:00:00Z");
    private static final Instant d5 = Instant.parse("2016-12-15T00:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "kevinsmith", "Kyle, @johnsmith is my name", d3);
    private static final Tweet tweet8 = new Tweet(8, "playboy", "hi my name is @playboy!", d3);    
    private static final Tweet tweet4 = new Tweet(3, "kevinsmith", "fyi: @johnsmith @JohnSmith are the same name", d4);
    private static final Tweet tweet5 = new Tweet(3, "KevinSmith", "mentioned-names: @usa123, @canada-1, @usa_456", d5);
    private static final Tweet tweet6 = new Tweet(6, "HelloKitty", "one or two or no name? @johnsmith@mit?", d2);
    private static final Tweet tweet7 = new Tweet(7, "snowwhite", "this username @1234567890123456789 is too long!", d2);
    private static final Tweet tweet9 = new Tweet(9, "batman", "my email address: batman@gmail.com", d2);
    private static final Tweet tweet10 = new Tweet(10, "steve", "This is a legit name-mention: @@dupatsign", d2);
        
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing strategy for writtenBy()
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     * 
     * Partition the inputs as follows:
     * tweetList.size() = 0, 1, > 1 (we will use 3 or more)
     * tweets with matched author username = 0, 1, 2
     * tweets with same author name but in different case is tested.
     * returned matching tweets order is also tested.
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");
        
        assertTrue("expected empty set", writtenBy.isEmpty());
    }
    
    @Test
    public void testWrittenByMultipleTweetsNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet4, tweet2, tweet6), "alyssa");
        
        /* test
        for (int i = 0; i < writtenBy.size(); ++i) {
           System.out.println("Written by:" + (writtenBy.get(i)).getAuthor());
        }
        */
        
        assertEquals("expected no match", 0, writtenBy.size());
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        /* test
        for (int i = 0; i < writtenBy.size(); ++i) {
           System.out.println("Written by:" + (writtenBy.get(i)).getAuthor());
        }
        */
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testWrittenByMultipleTweetsMultipleResult1() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3, tweet8, tweet4), "kevinsmith");
        
        /* test
        for (int i = 0; i < writtenBy.size(); ++i) {
           System.out.println("Written by:" + (writtenBy.get(i)).getAuthor());
        }
        */
        
        assertEquals("expected two lists for one author", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet3));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet4));
    }
    
    @Test
    public void testWrittenByMultipleTweetsMultipleResult2() {
        // test author name is case-insensitive.
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet5, tweet3, tweet2, tweet4), "kevinsmith");
        
        /* test
        for (int i = 0; i < writtenBy.size(); ++i) {
           System.out.println("Written by:" + (writtenBy.get(i)).getAuthor());
        }
        */
        
        assertEquals("expected three lists for one author", 3, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet3));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet4));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet5));
    }
    
    @Test
    public void testWrittenByMultipleTweetsMultipleResult3() {
        // test the returned tweets order is same as input's order
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet5, tweet3, tweet2, tweet4), "kevinsmith");
        
        /* test
        for (int i = 0; i < writtenBy.size(); ++i) {
           System.out.println("Written by:" + (writtenBy.get(i)).getAuthor());
        }
        */
        
        assertEquals("expected three matches", 3, writtenBy.size());
        assertTrue("expected the 1st tweet on list", writtenBy.get(0).getTimestamp() == (tweet5.getTimestamp()));
        assertTrue("expected the 2nd tweet on list", writtenBy.get(1).getTimestamp() == (tweet3.getTimestamp()));
        assertTrue("expected the 3rd tweet on list", writtenBy.get(2).getTimestamp() == (tweet4.getTimestamp()));
    }
    
    /*
     * Testing strategy for inTimeSpan()
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     * 
     * Partition the inputs as follows:
     * tweetList.size() = 0, 1, > 1 (we will use 2 or more)
     * tweets with matched required timespan = 0, 1, 2
     * returned matching tweets order is tested.
     * timestamp boundary condition is tested.
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testInTimespanEmptyList() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty set", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanSingleTweetNoResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet3), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanSingleTweetOneResult() {
        Instant testStart = Instant.parse("2016-09-01T09:00:00Z");
        Instant testEnd = Instant.parse("2016-09-30T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet3), new Timespan(testStart, testEnd));
        
        assertTrue("expected one on list", inTimespan.size() == 1);
    }
    
    @Test
    public void testInTimespanMultipleTweetsNoResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet3, tweet4, tweet5), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));
        
        assertTrue("expected list has two matches", inTimespan.size() == 2);
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    
    @Test
    public void testInTimespanMultipleTweetsTimestampBoundaryCheck() {
        Instant testStart = Instant.parse("2016-02-17T10:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected list has two matches", inTimespan.size() == 2);
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    /*
     * Testing strategy for containing()
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
     * 
     * Partition the inputs as follows:
     * tweet list size = 0, 1, > 1 (we will use 2 or more)
     * word list size = 0, 1, > 1 (we will use 2 or more)
     * same tweets on tweet list is tested.
     * returned matching tweets order is tested.
     * word case-sensitivity is tested.
     *          
     * Cover each part testing coverage.
     */

    @Test
    public void testContainingEmptyList() {
        
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList());
        
        assertTrue("expected empty set", containing.isEmpty());
    }
    
    @Test
    public void testContainingNoneTweetZeroMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("HELLO"));
        
        assertTrue("expected empty set", containing.isEmpty());
    }
    
    
    @Test
    public void testContainingSingleTweetOneMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk"));
        
        assertTrue("expected one element on list", containing.size() == 1);
    }
    
    @Test
    public void testContainingSameTweetOneMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet1), Arrays.asList("talk"));
        
        assertTrue("expected one element on list", containing.size() == 1);
    }
    
        
    @Test
    public void testContainingMultipleTweetsOneMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("reasonable"));
        
        assertTrue("expected one tweet on list", containing.size() == 1);

        assertEquals("expected correct tweet on list", 0, containing.indexOf(tweet1));
    }
    
    @Test
    public void testContainingMultipleTweetsMultipleMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("It", "talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    @Test
    public void testContainingCaseSensitivityCheck() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("TALK"));
        
        assertTrue("expected list size 2", containing.size() == 2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
