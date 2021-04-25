package com.test.words.stats;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Simple {@link WordsStatistics} implementation. Not thread safe.
 */
@NotThreadSafe
public class DefaultWordsStatistics implements WordsStatistics {

    private final Map<String, Integer> wordsCount;

    public DefaultWordsStatistics() {
        this.wordsCount = new HashMap<>();
    }

    @Override
    public void addWord(String word) {
        wordsCount.compute(word, (k, v) -> v == null ? 1 : ++v);
    }

    @Override
    public Map<String, Integer> getWordsCount() {
        return Collections.unmodifiableMap(wordsCount);
    }

    @Override
    public void merge(WordsStatistics that) {
        final HashSet<String> allWords = new HashSet<>();
        allWords.addAll(this.getWordsCount().keySet());
        allWords.addAll(that.getWordsCount().keySet());

        allWords.forEach(word -> wordsCount.compute(word, (k, v) -> {
            final Integer thatWordCount = that.getWordsCount().getOrDefault(word, 0);
            return v == null ? thatWordCount : v + thatWordCount;
        }));
    }
}
