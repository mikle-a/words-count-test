package com.test.words.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

class FileWordsReaderTest {

    @Test
    public void testReadWords() throws IOException {
        //given words reader configured to remove punctuation symbols and lower case all words
        final FileWordsReader fileWordsReader = new FileWordsReader(new RemovePunctuation().andThen(new ToLowerCase()));

                //and file with couple of words
        final File file = File.createTempFile("test-read", "");
        Files.write(file.toPath(), "Word1, woRd!s2\nword_3".getBytes());

        //when read file
        final ArrayList<String> wordsCollected = new ArrayList<>();
        fileWordsReader.read(file, wordsCollected::add);

        //then
        Assertions.assertEquals(3, wordsCollected.size());
        Assertions.assertEquals("word1", wordsCollected.get(0));
        Assertions.assertEquals("words2", wordsCollected.get(1));
        Assertions.assertEquals("word3", wordsCollected.get(2));

    }

}