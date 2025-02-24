// REPOBEE-SANITIZER-SHRED
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for the {@link FileTextAnalyzer} class
 * @author Gabriel Skoglund
 */
public class FileTextAnalyzerTest extends TextFileTest {
    private final FileTextAnalyzer hamletAnalyzer = new FileTextAnalyzer(HAMLET_FILEPATH.toString());
    private final FileTextAnalyzer emptyFileAnalyzer = new FileTextAnalyzer(EMPTY_FILEPATH.toString());

    private final String[] HAMLET_EXCERPT_WORDS = HAMLET_EXCERPT.split("\\s+");

    @Test
    public void wordCountIsCorrectForNonEmptyFile() {
        assertEquals(HAMLET_EXCERPT_WORD_COUNT, hamletAnalyzer.wordCount());
    }

    @Test
    public void wordCountIsCorrectForEmptyFile() {
        assertEquals(0, emptyFileAnalyzer.wordCount());
    }

    /**
     * Test that the {@link FileTextAnalyzer#wordCount} method does not read any file data (this should be
     * handled in the constructor).  This is done by adding a word to a file and ensuing that the word count
     * remains unchanged
     */
    @Test
    public void wordCountIsPerformedInConstructor() throws IOException {
        Files.writeString(EMPTY_FILEPATH, "Test");
        assertEquals(0, emptyFileAnalyzer.wordCount());
        // Reset the file to empty
        Files.writeString(EMPTY_FILEPATH, "", StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Test
    public void occurrencesOfGivesExpectedResultForFirstWordInText() {
        String firstWord = HAMLET_EXCERPT_WORDS[0].toLowerCase();
        long firstWordCount = Arrays.stream(HAMLET_EXCERPT_WORDS)
                .filter(s -> s.toLowerCase().equals(firstWord))
                .count();
        assertEquals(firstWordCount, hamletAnalyzer.occurrencesOf(firstWord));
    }

    @Test
    public void occurrencesOfGivesExpectedResultForLastWordInText() {
        String lastWord = HAMLET_EXCERPT_WORDS[HAMLET_EXCERPT_WORDS.length - 1].toLowerCase();
        long lastWordCount = Arrays.stream(HAMLET_EXCERPT_WORDS)
                .filter(s -> s.toLowerCase().equals(lastWord))
                .count();
        assertEquals(lastWordCount, hamletAnalyzer.occurrencesOf(lastWord));
    }

    @Test
    public void occurrencesOfGivesExpectedResultForWordWithMultipleOccurrences() {
        String word = "of";
        long wordCount = Arrays.stream(HAMLET_EXCERPT_WORDS)
                .filter(s -> s.toLowerCase().equals(word))
                .count();
        assertEquals(wordCount, hamletAnalyzer.occurrencesOf(word));
    }

    @Test
    public void occurrencesOfGivesExpectedResultForWordNotInText() {
        assertEquals(0, hamletAnalyzer.occurrencesOf("foobar"));
    }

    @Test
    public void occurrencesOfIsCaseInsensitive() {
        String word = HAMLET_EXCERPT_WORDS[0];
        assertEquals(hamletAnalyzer.occurrencesOf(word), hamletAnalyzer.occurrencesOf(word.toUpperCase()));
    }

    /**
     * Test that no file reading is handled in the {@link FileTextAnalyzer#occurrencesOf} method.
     * This is done by adding a word to a file and ensuing that the count remains unchanged.
     */
    @Test
    public void occurrencesOfDoesNotReadTheFileAgain() throws IOException {
        Files.writeString(EMPTY_FILEPATH, "Test");
        assertEquals(0, emptyFileAnalyzer.occurrencesOf("test"));
        // Reset the file to empty
        Files.writeString(EMPTY_FILEPATH, "", StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Test
    public void frequencyOfGivesExpectedResultForWordInText() {
        String word = "of";
        long wordCount = Arrays.stream(HAMLET_EXCERPT_WORDS)
                .filter(s -> s.toLowerCase().equals(word))
                .count();
        double expected = wordCount / (double) HAMLET_EXCERPT_WORD_COUNT;
        assertEquals(expected, hamletAnalyzer.frequencyOf(word), 1e-14);
    }

    @Test
    public void frequencyOfGivesExpectedResultForWordNotInText() {
        assertEquals(0, hamletAnalyzer.frequencyOf("foobar"), 0);
    }

    @Test
    public void frequencyOfIsCaseInsensitive() {
        String word = HAMLET_EXCERPT_WORDS[0];
        assertEquals(hamletAnalyzer.frequencyOf(word), hamletAnalyzer.frequencyOf(word.toUpperCase()), 0);
    }

    @Test
    public void uniqueWordCountGivesCorrectResultForNonEmptyFile() {
        assertEquals(HAMLET_EXCERPT_UNIQUE_WORD_COUNT, hamletAnalyzer.uniqueWordCount());
    }

    @Test
    public void uniqueWordCountGivesCorrectResultForEmptyFile() {
        assertEquals(0, emptyFileAnalyzer.uniqueWordCount());
    }
}