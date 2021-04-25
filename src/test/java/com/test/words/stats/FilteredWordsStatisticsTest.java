package com.test.words.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
class FilteredWordsStatisticsTest {

    @Mock
    private WordsStatistics wordsStatisticsMock;

    @Mock
    private Predicate<String> wordsFilterMock;

    @InjectMocks
    private FilteredWordsStatistics filteredWordsStatistics;

    @Test
    public void testAddWord() {
        //given words filter will return true for "a" only
        Mockito.when(wordsFilterMock.test("a")).thenReturn(true);

        //when "a" and "b" words are added
        filteredWordsStatistics.addWord("a");
        filteredWordsStatistics.addWord("b");

        //then delegate is called only for "a"
        Mockito.verify(wordsStatisticsMock, Mockito.times(1)).addWord("a");
    }

    @Test
    public void testMerge() {
        //given another statistics
        final WordsStatistics anotherStatistics = Mockito.mock(WordsStatistics.class);

        //when another statistics is merged with filtered one
        filteredWordsStatistics.merge(anotherStatistics);

        //then merge is delegated to the underlying object
        Mockito.verify(wordsStatisticsMock, Mockito.times(1)).merge(anotherStatistics);
    }

    @Test
    public void testGetWordsCount() {
        //given underlying stats will return words counts
        final Map<String, Integer> wordsCount = Map.of("a", 10, "b", 20);
        Mockito.when(wordsStatisticsMock.getWordsCount()).thenReturn(wordsCount);

        //when filtered statistics is called for words count
        final Map<String, Integer> wordsCountActual = filteredWordsStatistics.getWordsCount();

        //then the call is delegated to the underlying object
        Assertions.assertSame(wordsCount, wordsCountActual);
    }

}