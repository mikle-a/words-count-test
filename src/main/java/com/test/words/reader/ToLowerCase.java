package com.test.words.reader;

import java.util.function.Function;

/**
 * Function to lower case strings
 */
public class ToLowerCase implements Function<String, String> {

    @Override
    public String apply(String word) {
        return word.toLowerCase();
    }

}
