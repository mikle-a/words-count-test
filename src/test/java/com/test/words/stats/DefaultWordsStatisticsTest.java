package com.test.words.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultWordsStatisticsTest {

    @Test
    public void testAddWords() {
        //given new empty words statistics
        final DefaultWordsStatistics wordsStats = new DefaultWordsStatistics();

        //when add words
        wordsStats.addWord("a");
        wordsStats.addWord("a");
        wordsStats.addWord("b");

        //then stats provides proper counters
        Assertions.assertEquals(2, wordsStats.getWordsCount().get("a"));
        Assertions.assertEquals(1, wordsStats.getWordsCount().get("b"));
    }

    @Test
    public void testMergeStatistics() {
        //given two words stats
        final DefaultWordsStatistics wordsStats1 = new DefaultWordsStatistics();
        wordsStats1.addWord("a");
        wordsStats1.addWord("a");
        wordsStats1.addWord("b");

        final DefaultWordsStatistics wordsStats2 = new DefaultWordsStatistics();
        wordsStats2.addWord("b");
        wordsStats2.addWord("c");

        //when two words stats are merged
        wordsStats1.merge(wordsStats2);

        //then counters are merged properly
        Assertions.assertEquals(2, wordsStats1.getWordsCount().get("a"));
        Assertions.assertEquals(2, wordsStats1.getWordsCount().get("b"));
        Assertions.assertEquals(1, wordsStats1.getWordsCount().get("c"));

    }

}