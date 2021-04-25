package com.test.words.stats;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Passes words allowed by the specified word filter to the delegate {@link WordsStatistics}.
 */
public class FilteredWordsStatistics implements WordsStatistics {

    private final WordsStatistics delegate;
    private final Predicate<String> wordFilter;

    /**
     * Create instance
     * @param delegate delegate {@link WordsStatistics}
     * @param wordFilter filter which controls words to be passed to the delegate
     */
    public FilteredWordsStatistics(WordsStatistics delegate, Predicate<String> wordFilter) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.wordFilter = Objects.requireNonNull(wordFilter, "word filter must not be null");
    }

    @Override
    public void addWord(String word) {
        if (wordFilter.test(word)) {
            delegate.addWord(word);
        }
    }

    @Override
    public Map<String, Integer> getWordsCount() {
        return delegate.getWordsCount();
    }

    @Override
    public void merge(WordsStatistics wordsStatistics) {
        delegate.merge(wordsStatistics);
    }
}
