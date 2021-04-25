package com.test.words.top;

import com.test.words.exec.RawThreadExecutor;
import com.test.words.reader.FileWordsReader;
import com.test.words.reader.RemovePunctuation;
import com.test.words.reader.ToLowerCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class TopWordsResolverTestIntegrationTest {

    @Test
    public void integrationTest() throws IOException, InterruptedException {
        //given configured top words resolver
        final FileWordsReader fileWordsReader = new FileWordsReader(new RemovePunctuation().andThen(new ToLowerCase()));
        final TopWordsResolver topWordsFinder = new TopWordsResolver(
                new RawThreadExecutor(),
                Runtime.getRuntime().availableProcessors(),
                fileWordsReader);

        //when top words resolver is requested to find top words in files from the test directory
        final List<WordCount> topWords = topWordsFinder.getTopWords(
                Path.of("./src/test/resources/test-dir"),
                word -> word.length() >= 3,
                10);

        //then
        Assertions.assertEquals(10, topWords.size());
        Assertions.assertEquals("the", topWords.get(0).getWord());
        Assertions.assertEquals(244, topWords.get(0).getCount());
        Assertions.assertEquals("and", topWords.get(1).getWord());
        Assertions.assertEquals(185, topWords.get(1).getCount());
        Assertions.assertEquals("her", topWords.get(2).getWord());
        Assertions.assertEquals(92, topWords.get(2).getCount());
        Assertions.assertEquals("she", topWords.get(3).getWord());
        Assertions.assertEquals(63, topWords.get(3).getCount());
        Assertions.assertEquals("was", topWords.get(4).getWord());
        Assertions.assertEquals(61, topWords.get(4).getCount());
        Assertions.assertEquals("that", topWords.get(5).getWord());
        Assertions.assertEquals(52, topWords.get(5).getCount());
        Assertions.assertEquals("with", topWords.get(6).getWord());
        Assertions.assertEquals(50, topWords.get(6).getCount());
        Assertions.assertEquals("his", topWords.get(7).getWord());
        Assertions.assertEquals(47, topWords.get(7).getCount());
        Assertions.assertEquals("anna", topWords.get(8).getWord());
        Assertions.assertEquals(42, topWords.get(8).getCount());
        Assertions.assertEquals("said", topWords.get(9).getWord());
        Assertions.assertEquals(41, topWords.get(9).getCount());
    }

}