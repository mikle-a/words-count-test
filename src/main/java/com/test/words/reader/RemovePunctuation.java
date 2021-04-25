package com.test.words.reader;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Function to remove punctuation symbols from strings
 */
public class RemovePunctuation implements Function<String, String> {

    private static final Pattern PUNCTUATION_PATTER = Pattern.compile("\\p{Punct}");

    @Override
    public String apply(String word) {
        return PUNCTUATION_PATTER.matcher(word).replaceAll("");
    }
}
