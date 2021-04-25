package com.test.words.stats;

import java.util.Map;

/**
 * Statistics collected from files. Provides only words count for the moment, but could be expanded with any required
 * information collected from files.
 */
public interface WordsStatistics {

    /**
     * Add new word occurrence
     * @param word word from file
     */
    void addWord(String word);

    /**
     * Provides words count statistics
     * @return map where key is a word and value represents occurrences count
     */
    Map<String, Integer> getWordsCount();

    /**
     * Merge external statistics
     * @param wordsStatistics external statistics to merge into current one
     */
    void merge(WordsStatistics wordsStatistics);
}
