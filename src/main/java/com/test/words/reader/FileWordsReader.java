package com.test.words.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helps to read words from file.
 */
public class FileWordsReader {

    private Function<String, String> wordNormalizer;

    /**
     * Create new instance
     * @param wordNormalizer words normalizer function which will be applied to each word before passing
     *                       it further to the word handler
     */
    public FileWordsReader(Function<String, String> wordNormalizer) {
        this.wordNormalizer = Objects.requireNonNull(wordNormalizer, "word normalizer must not be null");
    }

    /**
     * Read words from the specified file. Uses space as word delimiter.
     * @param file file to read
     * @param wordsConsumer consumer to handle each found word.
     * @throws FileNotFoundException when specified file is not found
     */
    public void read(File file, Consumer<String> wordsConsumer) throws FileNotFoundException {
        Objects.requireNonNull(file, "file must not be null");
        Objects.requireNonNull(wordsConsumer, "words consumer must not be null");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                final String word = scanner.next();
                final String normalizedWord = wordNormalizer.apply(word);
                wordsConsumer.accept(normalizedWord);
            }
        }
    }

}
