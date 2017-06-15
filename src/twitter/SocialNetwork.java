package twitter;

import java.util.*;


/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
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
     */
    public static Map<String, Set<String>> guessFollowsGraph(final List<Tweet> tweets) {

        String validAuthor;
        Set<String> mentionedNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        // use TreeMap as it allows case insensitive keys.
        final Map<String, Set<String>> followsMap = 
                new TreeMap<String, Set<String>>(String.CASE_INSENSITIVE_ORDER);
        
        
        // *** test
        // System.out.println("*** guessFollowsGraph ***");
               
        // check if the list is empty
        if (tweets.isEmpty()) {
            // System.out.println("Empty tweets list!");
            return followsMap;
        }
 
        // scan the text from each tweet on the list, to establish the follows-relationship

        for (Tweet twt : tweets) {
            Set<String> followList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

            mentionedNames = Extract.getMentionedNamesFromOneTweet(twt);
            
            // check if any name matches author, remove that name from list if so.
            validAuthor = twt.getAuthor();
            // System.out.print("Author: " + validAuthor + " follows: "); // test ***

            for (String name : mentionedNames)
            {
                // do not allow one follows himself! 
                if (name.compareToIgnoreCase(validAuthor) == 0) { // name is same as author
                    mentionedNames.remove(name);
                    // System.out.println("* Name removed: " + name); // test ***
                }
                else {
                    // add the @-mentions to the graph with an empty follow set
                    // if it has not been added previously.
                    
                    if (!followsMap.containsKey(name.toLowerCase())) {
                        followsMap.put(name.toLowerCase(), new TreeSet<>());
                    }
                }
            } // end of for (name : mentionedNames)

            // System.out.println(); // test ***
          
            // Add the <author, <follows>> to map:
            //
            // Check if the author (K) is already in the map. If so then
            //    we need to get the V (set of follow-name) and merge these two sets,
            // then put the new (K, V) back into the map.
            // If the author (K) does not exist in the map then just put the (K, V)
            //    directly into the map.

            if (followsMap.containsKey(validAuthor)) {
                followList = followsMap.get(validAuthor.toLowerCase());
            }
                   
            followList.addAll(mentionedNames);
            followsMap.put(validAuthor.toLowerCase(), followList);
        }
        
        // System.out.println("guessFollowsGraph map size = " + followsMap.size()); // ** test
        return followsMap;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers (note: NOT just the count of @-names
     * we should consider. For example:
     * a guessFollowsGraph contains (author, followSet) :
     * (A, {}),      // A has no follow
     * (B, {}),      // B has no follow
     * (C, {A, D}),  // C follows A and D
     * (D, {A, C})   // D follows A and C
     * 
     * the influence graph should look like:
     * A has two followers: C, D
     * B has zero follower
     * C has one follower: D
     * D has one follower: C
     * 
     * the returned list should be like: A (2), C (1), D (1), B (0)
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(final Map<String, Set<String>> followsGraph) {
        String key, followedName;
        Set<String> follows;
        int followsCount = 0;

        final TreeMap<String, Integer> influencerList = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        final LinkedHashMap<String, Integer> sortedList;
        final List<String> namesList = new ArrayList<String>();
        
        // System.out.println("*** influencers ***");
        
        // if followsGraph is empty, return an empty name list.
        if (followsGraph.isEmpty()) 
            return namesList;
        
        // first we will prepare the influencer list with all possible names -
        // from the tweet's author and the name in the follow list.
        // the follow count is initialized to be 0, and
        // we will also convert all names to lower case while preparing the list.
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            key = entry.getKey();
            influencerList.put(key.toLowerCase(), 0);
            
            for (String s: entry.getValue()) {
                influencerList.put(s.toLowerCase(), 0);
            }
        }
        
        // iterate through the whole follows graph: get the follow list from each map entry,
        // parse the follow list of each entry.
        // for each name on the follow list:
        // increment the follow count of the corresponding entry on the influencer list.
        // 
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            key = entry.getKey();
            follows = entry.getValue();
                                 
            Iterator<String> iter = follows.iterator();
            
            while (iter.hasNext()) {
                followedName = (String )iter.next().toLowerCase();
                
                // retrieve the follow count of the name on the influence list,
                // increment the count and then save it back.
                followsCount = influencerList.get(followedName);
                influencerList.put(followedName, ++followsCount);
                
            } // end of while iterator
                      
        } // end of for - graph map
        
        // sort the map into the descending order
        //
        sortedList = (LinkedHashMap<String, Integer>) sortByValue(influencerList);
        
        for (Map.Entry<String, Integer> entry : sortedList.entrySet()) {
            key = entry.getKey();
            namesList.add(key);
            
            // ** test \/
            /*
               System.out.print("<influencerList> ");
               System.out.println("key: " + key +
                       " follows count: " + entry.getValue());
            */
               // ** test /\
        }
        
        // ** test \/
        /*
        System.out.print("<influencerList - nameList> ");
        namesList.forEach((temp) -> {
            System.out.print(temp + ", ");
        });
        System.out.println();
        */
        // ** test /\
        
        return namesList;
    }
    
    /*
     * sortByValue will sort according to the value in * DESCENDING * order.
     * 
     * @param map: the map to be sorted according to the value of its entries.
     *  the map will not be mutated.
     * 
     * @return a sorted map.
     * 
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue (final Map<K, V> map)
{
    // make a linked list according to the entries in the map
    //
    List<Map.Entry<K, V>> list = new LinkedList<> (map.entrySet());
    
    // Sorts the list according to the order induced by the specified comparator below
    //
    Collections.sort (list, new Comparator<Map.Entry<K, V>>()
    {
        @Override
        public int compare (Map.Entry<K, V> entity1, Map.Entry<K, V> entity2)
        {
            return (entity2.getValue()).compareTo (entity1.getValue());
        }
    } );

    // return the sorted map according to sorted list. 
    // Note that we can not use a Tree map as it will re-order the entries 
    // as the tree map is created.
    //
    Map<K, V> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list)
    {
        sortedMap.put (entry.getKey(), entry.getValue());
    }
    return sortedMap;
}

    /*
     * compare a string s to all elements in the list l, to see if l contains s
     * 
     * @param s string to be compared to the list of strings.
     *        l list of strings to be compared.
     * @return true if s is found in the list l (case insensitivity)
     * 
     */
    public static boolean containsCaseInsensitive (String s, final List<String> l) {
        for (String string : l) {
           if (string.equalsIgnoreCase(s)) {
               return true;
           }
        }
       return false;
     }
    
    
    /*
     * Influencers contains the userName (author name) from the follows graph and
     * the followsCount (how many follows the userName is following).
     */
    //
    static class Influencers implements Comparable<Influencers> {
        private String userName; 
        private int followsCount;  
        // Rep invariant: 
        //   userName is Twitter username (nonempty strings of letters, digits, underscores)
        //   followsCount >= 0
        //        
        // Abstraction Function:
        //   represents the influencer name and the follow count of his/her
        //   follow list.
        //
        // Safety from rep exposure:
        //   All fields are private;
        //   userName is String and followsCount is int, so are guaranteed immutable;
        //  
 
        // Creator:
        Influencers (String userName, int followsCount) {  
            if (userName == null) {
                throw new NullPointerException("userName string is null");
            }
            if (followsCount < 0) {
                throw new IllegalArgumentException("followsCount is a negative number");
            }
            this.userName = userName;  
            this.followsCount = followsCount;   
        }  
          
        // Observer:
        public int compareTo(Influencers name){  
        if(followsCount == name.followsCount)  
            return 0;  
        else if(followsCount > name.followsCount)  
            return 1;  
        else  
            return -1;  
        }  
        
        // Observer:
        public boolean find (String name){
            if (userName.equalsIgnoreCase(name))
                return true;
            else
                return false;
        }
    }  
    //
    
    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
