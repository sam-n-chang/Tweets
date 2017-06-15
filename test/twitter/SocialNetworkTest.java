package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-09-15T00:00:00Z");
    private static final Instant d4 = Instant.parse("2016-10-15T00:00:00Z");
    private static final Instant d5 = Instant.parse("2016-12-15T00:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "hellokitty", "@peter, have lunch together?", d1);
    private static final Tweet tweet3 = new Tweet(2, "hellokitty", "@peter and @mary, where is paul?", d1);
    private static final Tweet tweet8 = new Tweet(8, "playboy", "hi my name is @playboy!", d3);    
    private static final Tweet tweet4 = new Tweet(3, "kevinsmith", "fyi: @johnsmith @JohnSmith are the same name", d4);
    private static final Tweet tweet5 = new Tweet(3, "KevinSmith", "mentioned-names: @usa123, @canada-1, @usa_456", d5);
    private static final Tweet tweet6 = new Tweet(6, "HelloKitty", "one or two or no name? @johnsmith@mit?", d2);
    private static final Tweet tweet7 = new Tweet(7, "snowwhite", "this username @1234567890123456789 is too long!", d2);
    private static final Tweet tweet9 = new Tweet(9, "batman", "my email address: batman@gmail.com", d2);
    private static final Tweet tweet10 = new Tweet(10, "steve", "This is a legit name-mention: @@dupatsign", d2);
    private static final Tweet tweet11 = new Tweet(8, "PlayBoy", "@mary, care for a movie?", d2);
    private static final Tweet tweet12 = new Tweet(8, "playboy", "@mary, @alyssa will be joining us!", d2);
   
    private static final Tweet tweet21 = new Tweet(0, "a_lyssp_", "no friends :(", Instant.now());
    private static final Tweet tweet22 = new Tweet(1, "a_lyssp_", "RT @a_lyssp_: no friends :(", Instant.now());
    private static final Tweet tweet23 = new Tweet(2, "bbitdiddle", "RT @a_lyssp_: no friends :(", Instant.now());
    private static final Tweet tweet24 = new Tweet(3, "evalu_", "@a_lyssp_ how are you??", Instant.now());
    private static final Tweet tweet25 = new Tweet(4, "_reAsOnEr", "@evalu_ @a_lyssp_: great talk yesterday!", Instant.now());
    private static final Tweet tweet26 = new Tweet(5, "a_lyssp_", "@evalu_ doing great! you?", Instant.now());
    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing strategy for guessFollowsGraph()
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     * 
     * Partition the inputs as follows:
     * returned tweet list must not be null
     * tweetList.size() = 0, 1, > 1 (we will use 2 or more)
     * one tweet with zero @-mention
     * one tweet with one @-mention
     * one tweet with two different @-mentions
     * two tweets with only one @-mention
     * two tweets with one @-mention each
     * two tweets with two different @-mentions each
     * two tweets with duplicate @-mentions
     * case-sensitivity is checked in both author and @-mentions.
     *    three tweets from same author (but name's case sensitivity different) with
     *    duplicate @-mentions in different tweets.
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testGuessFollowsGraphNotNull() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertNotNull("expected no null return", followsGraph);
    }
    
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testGuessFollowsOneElementNoFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        Set<String> follows;
        int followsCount = 0;
        
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            follows = entry.getValue();
            followsCount = follows.size();
        }
        
        // since the follow graph is not defined if the author should be included when
        // there is no mentioned name in his tweet text. So we may allow the return size
        // to be 0 (the author does not count) or 1
        assertTrue("expected one author in graph", (followsGraph.size() == 1) || (followsGraph.size() == 0));
        assertEquals("expected no follows from this author", followsCount, 0);
    }
    
    @Test
    public void testGuessFollowsOneElementSingleFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        Set<String> follows;
        int followsCount = 0;
        
        // ** test
        // System.out.println("* testGuessFollowsOneElementSingleFollow graph size = " + followsGraph.size());
        
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
              
        // System.out.println("** follows count = " + followsCount); //** test
        
        assertTrue("expected 1 or 2 authors in graph", (followsGraph.size() == 1) || (followsGraph.size() == 2));
        assertEquals("expected one follow from this author", followsCount, 1);
    }
    
    @Test
    public void testGuessFollowsOneElementMultipleFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3));
        Set<String> follows;
        int followsCount = 0;
             
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
                 
        assertTrue("expected 1 or 2 authors in graph", (followsGraph.size() == 1) || (followsGraph.size() == 3));
        assertEquals("expected 2 follow from this author", followsCount, 2);
    }
    
    @Test
    public void testGuessFollowsTwoElementsSingleFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet1));
        Set<String> follows;
        int followsCount = 0;
              
        assertTrue("expected 2 or 3 authors in graph", (followsGraph.size() == 2) || (followsGraph.size() == 3));
        
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();

        assertEquals("expected only one follow from this author", followsCount, 1);
    }
    
    @Test
    public void testGuessFollowsMultipleElementsSingleFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet11));
        Set<String> follows;
        int followsCount = 0;
        
        assertTrue("expected 2 or 4 authors in graph", (followsGraph.size() == 2) || (followsGraph.size() == 4));
        
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
        
        assertEquals("expected only one follow from this author", followsCount, 1);
    }
    
    @Test
    public void testGuessFollowsMultipleElementsMultipleFollows() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet12));
        Set<String> follows;
        int followsCount = 0;
        
        assertTrue("expected 2 or 5 authors in graph", (followsGraph.size() == 2) || (followsGraph.size() == 5));
        
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
        
        assertEquals("expected 2 follows from this author", followsCount, 2);
    }
       
    @Test
    public void testGuessFollowsMultipleElementsDuplicateFollow() {
        Set<String> follows;
        int followsCount = 0;
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet3));
        
        follows = followsGraph.get("hellokitty");
        if (follows == null)
            follows = followsGraph.get("hellokitty".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
                
        assertTrue("expected 1 or 3 authors in graph", (followsGraph.size() == 1) || (followsGraph.size() == 3));
        assertEquals("expected two follows in graph", followsCount, 2);
    }
    
    @Test
    public void testGuessFollowsSingleFollowsCaseSensitivityCheck() {
        Set<String> follows;
        int followsCount = 0;
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4));
        
        follows = followsGraph.get("kevinsmith");
        if (follows == null)
            follows = followsGraph.get("kevinsmith".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
              
        assertEquals("expected 1 follows in graph", followsCount, 1);
    }
    
    @Test
    public void testGuessFollowsMultipleElementsAuthorSensitivityCheck() {
        Set<String> follows;
        int followsCount = 0;
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet8, tweet11, tweet12));
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            follows = entry.getValue();
            followsCount = follows.size();
        }
                    
        follows = followsGraph.get("playboy");
        if (follows == null)
            follows = followsGraph.get("playboy".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
        
        assertEquals("expected 2 follows from this author", followsCount, 2);
        assertTrue("expected 1 or 3 authors in graph", (followsGraph.size() == 1) || (followsGraph.size() == 3));
    }
    
    @Test
    public void testGuessFollowsLargeNetwork() {
        Set<String> follows;
        int followsCount = 0;
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet21, tweet22, tweet23, tweet24, tweet25, tweet26));
    
        /* testing
        System.out.println("*** testGuessFollowsLargeNetwork *** ");
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            follows = entry.getValue();
            followsCount = follows.size();
            System.out.println("graph entry : "+entry);
        }
        */
              
        follows = followsGraph.get("a_lyssp_");
        if (follows == null)
            follows = followsGraph.get("a_lyssp_".toUpperCase());
        if (follows != null)
           followsCount = follows.size();
        
        assertEquals("expected 1 follows from this author", followsCount, 1);
        assertEquals("expected 4 authors in graph", followsGraph.size(), 4);
    }
    
    /*
     * Testing strategy for influencers()
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     * 
     * Partition the inputs as follows:
     * map.size() = 0, 1, > 1 (we will use 2, 3 or more)
     * one map entry with zero follow: A -> ""
     * one map entry with one follow: A -> {B}
     * two map entries with only one follow: A -> {B}, B -> ""
     * two map entries with one follow each: A -> {C}, B -> {D}
     * two map entries with the same follows: A -> {C}, B -> {C}
     * three map entries with follows of: A -> {C}, B -> {C}, D -> {B}
     * four map entries with follows of: A -> "", B -> "", C -> {A, D}, D -> {A, C}
     * 
     *    
     * Cover each part testing coverage.
     */
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    @Test
    public void testInfluencersOneEntryNoFollow() {
        Set<String> follows = new HashSet<String>();
        Map<String, Set<String>> followsGraph = new HashMap<>();
        
        followsGraph.put("peter", follows);
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 1 entry on list", influencers.size(), 1);
    }
    
    @Test
    public void testInfluencersOneEntrySingleFollow() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList("cathy"));
        String entity1 = "playboy";
        
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity1, follows1);
        }};
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 2 entries on list", influencers.size(), 2);
        assertEquals(influencers.get(0), "cathy");
    }
    
    @Test
    public void testInfluencersTwoEntriesOneFollow() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList("cathy"));
        Set<String> follows2 = new HashSet<String>(Arrays.asList());
        String entity1 = "aceboy";
        String entity2 = "cathy";
  
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity1, follows1);
             put(entity2, follows2);
        }};
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("testInfluencersTwoEntriesOneFollow: expected 2 entries on list", influencers.size(), 2);
        
        assertTrue("first entity on list", influencers.get(0).equals(entity2));
    }
    
    @Test
    public void testInfluencersTwoEntriesTwoFollows() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList("cathy"));
        Set<String> follows2 = new HashSet<String>(Arrays.asList("fish"));
        String entity1 = "playboy";
        String entity2 = "sam";
  
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity1, follows1);
             put(entity2, follows2);
        }};
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("testInfluencersTwoEntriesTwoFollows: expected 4 entries on list", influencers.size(), 4);
    }
    
    @Test
    public void testInfluencersTwoEntriesSameFollows() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList("betty6352"));
        Set<String> follows2 = new HashSet<String>(Arrays.asList("BETTY6352"));
        String entity1 = "paul";
        String entity2 = "sam";
  
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity1, follows1);
             put(entity2, follows2);
        }};
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("testInfluencersTwoEntriesTwoFollows: expected 3 entries on list", influencers.size(), 3);
        assertTrue("first entity on list", influencers.get(0).equals("betty6352"));
    }


    @Test
    public void testInfluencersThreeEntriesDupFollows() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList("cathy"));
        Set<String> follows2 = new HashSet<String>(Arrays.asList("cathy"));
        Set<String> follows3 = new HashSet<String>(Arrays.asList("peter"));
        String entity1 = "playboy";
        String entity2 = "peter";
        String entity3 = "mary";
        
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity1, follows1);
             put(entity2, follows2);
             put(entity3, follows3);
        }};

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 9 entries on list", influencers.size(), 4);
        
        assertTrue("first entity on list", influencers.get(0).equals("cathy"));
    }
    
    @Test
    public void testInfluencersFourEntriesDupFollows() {
        Set<String> follows1 = new HashSet<String>(Arrays.asList());
        Set<String> follows2 = new HashSet<String>(Arrays.asList());
        Set<String> follows3 = new HashSet<String>(Arrays.asList("cathy", "fish"));
        Set<String> follows4 = new HashSet<String>(Arrays.asList("sam", "cathy"));
        String entity1 = "cathy";
        String entity2 = "paul";
        String entity3 = "sam";
        String entity4 = "fish";
        
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>()
        {{
             put(entity4, follows4);
             put(entity3, follows3);
             put(entity2, follows2);
             put(entity1, follows1);
        }};

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected 4 entries on list", influencers.size(), 4);
        
        //make sure the list is in ascending order:0 0 0 0 0 0 1 2 3
        assertTrue("first entity on list", influencers.get(0).equals(entity1));
    }
      
    
    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
