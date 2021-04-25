package com.test.words.top;

import com.test.words.reader.FileWordsReader;
import com.test.words.stats.DefaultWordsStatistics;
import com.test.words.stats.FilteredWordsStatistics;
import com.test.words.stats.WordsStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Encapsulates high-level logic required to find top words in files.
 */
public class TopWordsResolver {

    private static final Logger log = LoggerFactory.getLogger(TopWordsResolver.class);

    private final Executor executor;
    private final int parallelism;
    private final FileWordsReader wordsReader;

    /**
     * Construct new instance
     *
     * @param executor    executor to be used for concurrent tasks
     * @param parallelism defines the concurrency level
     */
    public TopWordsResolver(Executor executor, int parallelism, FileWordsReader wordsReader) {
        if (parallelism <= 0) throw new IllegalArgumentException("parallelism must be positive");
        this.executor = Objects.requireNonNull(executor);
        this.wordsReader = Objects.requireNonNull(wordsReader);
        this.parallelism = parallelism;
    }

    public List<WordCount> getTopWords(Path path, Predicate<String> wordsFilter, int topN)
            throws InterruptedException, IOException {
        Objects.requireNonNull(path, "path must not be empty");
        Objects.requireNonNull(wordsFilter, "words filter must not be empty");
        if (topN <= 0) throw new IllegalArgumentException("words filter must not be empty");
        if (!path.toFile().isDirectory() || !path.toFile().exists())
            throw new IllegalArgumentException("path must point to the existing directory");

        log.info("Get words statistics for directory '{}'", path);

        //resolve list of all files
        final BlockingQueue<File> filesQueue = Files.walk(path)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toCollection(LinkedBlockingQueue::new));
        log.info("Found {} files in '{}'", filesQueue.size(), path);

        //create and submit tasks to consume all files from the queue - each task provides its own result
        log.info("Start analyzing files");
        final List<Future<WordsStatistics>> pendingTasks = IntStream.range(0, parallelism)
                .mapToObj($ -> new FutureTask<>(() -> analyzeFiles(filesQueue, wordsReader, wordsFilter)))
                .peek(executor::execute)
                .collect(Collectors.toList());

        //wait all tasks to complete and merge results
        final WordsStatistics wordsStatistics = getAndMergeResults(pendingTasks);
        log.info("Finished files analysis");

        //resolve top n words
        return resolveTopWords(wordsStatistics, topN);
    }

    private static WordsStatistics analyzeFiles(BlockingQueue<File> files,
                                                FileWordsReader wordsReader,
                                                Predicate<String> wordsFilter) {
        //each thread aggregates results locally
        final WordsStatistics localResults = new FilteredWordsStatistics(new DefaultWordsStatistics(), wordsFilter);

        while (true) {
            try {
                final File file = files.poll(1L, TimeUnit.MILLISECONDS);
                if (file != null) {
                    log.info("Read file '{}'", file.getName());
                    wordsReader.read(file, localResults::addWord);
                } else {
                    return localResults;
                }
            } catch (InterruptedException e) {
                log.info("Interruption request received, return current result");
                Thread.currentThread().interrupt();
                return localResults;
            } catch (FileNotFoundException e) {
                log.error("Unexpected error: {}", e.toString(), e);
            }
        }
    }


    /**
     * Get and merge results from all futures from the list. Since actions taken by the asynchronous computation
     *  <a href="package-summary.html#MemoryVisibility"> <i>happen-before</i></a> actions following the corresponding
     *  {@code Future.get()}, it is threadsafe to do it in this way.
     * @param pendingTasks list of futures
     * @return merge result
     * @throws InterruptedException just rethrows underlying interrupted exceptions
     */
    private static WordsStatistics getAndMergeResults(Collection<Future<WordsStatistics>> pendingTasks)
            throws InterruptedException {
        final DefaultWordsStatistics overallResult = new DefaultWordsStatistics();
        for (Future<WordsStatistics> pendingTask : pendingTasks) {
            try {
                final WordsStatistics wordsStatistics = pendingTask.get();
                overallResult.merge(wordsStatistics);
            } catch (ExecutionException e) {
                log.error("Unexpected execution error: {}", e.toString(), e);
            }
        }
        return overallResult;
    }

    private static List<WordCount> resolveTopWords(WordsStatistics wordsStatistics, int topN) {
        return wordsStatistics.getWordsCount().entrySet().stream()
                .map(e -> new WordCount(e.getKey(), e.getValue()))
                .sorted(Comparator.reverseOrder())
                .limit(topN)
                .collect(Collectors.toList());
    }

}
