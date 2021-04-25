package com.test.words;

import com.test.words.exec.RawThreadExecutor;
import com.test.words.reader.FileWordsReader;
import com.test.words.reader.RemovePunctuation;
import com.test.words.reader.ToLowerCase;
import com.test.words.top.TopWordsResolver;
import com.test.words.top.WordCount;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Console line interface to run {@link TopWordsResolver}
 */
public class Cli {

    private static final int DEFAULT_TOPN = 10;
    private static final int DEFAULT_MIN_LENGTH = 3;

    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private static final Options options = new Options();

    static {
        options.addOption(Option.builder("d")
                .argName("files directory")
                .required()
                .longOpt("directory")
                .hasArg()
                .desc("directory containing files to be analyzed")
                .build());

        options.addOption(Option.builder("t")
                .argName("top number")
                .required(false)
                .longOpt("top")
                .hasArg()
                .desc("number of top words, 10 by default")
                .build());

        options.addOption(Option.builder("l")
                .argName("words min length")
                .required(false)
                .longOpt("length")
                .hasArg()
                .desc("min words length, 3 by default")
                .build());

        options.addOption(Option.builder("th")
                .argName("threads")
                .required(false)
                .longOpt("threads")
                .hasArg()
                .desc("count of threads to be used, equals to available processors by default")
                .build());
    }


    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        //resolve params
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);

        final Path directory = Path.of(cmd.getOptionValue("d"));
        final int topN = cmd.hasOption("t") ? Integer.parseInt(cmd.getOptionValue("t")) : DEFAULT_TOPN;
        final int wordMinLength = cmd.hasOption("l") ? Integer.parseInt(cmd.getOptionValue("l")) : DEFAULT_MIN_LENGTH;
        final int threadsCount = cmd.hasOption("th")
                ? Integer.parseInt(cmd.getOptionValue("th"))
                : Runtime.getRuntime().availableProcessors();

        logger.info("Resolved params: directory='{}', topN='{}', wordMinLength='{}', threadsCount='{}'",
                directory, topN, wordMinLength, threadsCount);

        //configure resolver and dependencies
        final FileWordsReader fileWordsReader = new FileWordsReader(new RemovePunctuation().andThen(new ToLowerCase()));
        final TopWordsResolver topWordsResolver = new TopWordsResolver(new RawThreadExecutor(), threadsCount, fileWordsReader);

        //resolve top words and print the report
        final Predicate<String> wordsFilter = word -> word.length() >= wordMinLength;
        final List<WordCount> topWords = topWordsResolver.getTopWords(directory, wordsFilter, topN);
        final String resultReport = topWords.stream().map(WordCount::toString).collect(Collectors.joining("\n"));
        logger.info("Result:\n{}", resultReport);
    }
}
