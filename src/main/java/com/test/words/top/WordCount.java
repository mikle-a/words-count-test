package com.test.words.top;

/**
 * Represents words and its occurrences count
 */
public class WordCount implements Comparable<WordCount> {
    private final String word;
    private final int count;

    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(WordCount that) {
        return Integer.compare(this.count, that.count);
    }

    @Override
    public String toString() {
        return "Word '" + word + "' occurred " + count + " times";
    }
}
