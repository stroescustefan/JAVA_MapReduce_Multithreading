import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Callable;

class MapTask implements Callable<MapResult> {
    private String filename;
    private int startPos;
    private int fragmentLength;
    private int fileSize;

    public MapTask(String filename, int startPos, int fragmentLength, int fileSize) {
        this.filename = filename;
        this.startPos = startPos;
        this.fragmentLength = fragmentLength;
        this.fileSize = fileSize;
    }

    /**
     * Function that is being runned by a worker.
     * @return a MapResult object which contains computed map, file name and a list of maximal words.
     * @throws Exception
     */
    @Override
    public MapResult call() throws Exception {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        String[] wordSplitter;
        int endPos;

        endPos = Math.min(startPos + fragmentLength, fileSize);
        updateStartPos(file);
        endPos = updateEndPos(endPos, file);
        wordSplitter = getWords(file, endPos);

        file.close();
        return createMapResult(wordSplitter);
    }

    /**
     * Function that creates an object of MapResult
     * @param wordSplitter - list of words
     * @return - a MapResult object
     */
    private MapResult createMapResult(String[] wordSplitter) {
        Map<Integer, Integer> words = new HashMap<>();
        List<String> maximalWords = new ArrayList<>();
        int maxWordLength;

        /**
         * Create the map and compute the maximum length.
         */
        maxWordLength = Integer.MIN_VALUE;
        for (String s : wordSplitter) {
            if (!s.equalsIgnoreCase("")) {
                if (s.length() > maxWordLength) {
                    maxWordLength = s.length();
                }

                if (!words.containsKey(s.length())) {
                    words.put(s.length(), 1);
                } else {
                    words.put(s.length(), words.get(s.length()) + 1);
                }
            }
        }

        /**
         * Add all words that have length equal to maxWordLength.
         */
        for (String value : wordSplitter) {
            if (value.length() == maxWordLength) {
                maximalWords.add(value);
            }
        }

        return new MapResult(filename, maximalWords, words);
    }

    /**
     * Function that returns a list of words.
     * @param file - file from which it is read.
     * @param endPos - upper bound
     * @return an array of strings which represents the words.
     * @throws IOException
     */
    private String[] getWords(RandomAccessFile file, int endPos) throws IOException {
        StringBuilder parseText = new StringBuilder();

        file.seek(startPos);
        for (int i = startPos; i < endPos; i++) {
            parseText.append((char) file.read());
        }
        String result = parseText.toString();
        String wordSplitter[] = result.split("[^A-Za-z0-9]");
        return wordSplitter;
    }

    /**
     * Function that returns the correct end position. Checks if the character on endPos + 1 index is a special character
     * or not. In case it's a special character or if it's the last character from the file we return the same endPos,
     * otherwise we loop to the right till we find a special character. On each iteration we update the endPos (it gets
     * incremented). If endPos is in the middle of a word we will take the whole word.
     * @param endPos - initial end position
     * @param file - file from where we read.
     * @return - correct end position.
     * @throws IOException
     */
    private int updateEndPos(int endPos, RandomAccessFile file) throws IOException {
        if (endPos != fileSize) {
            file.seek(endPos - 1);
            if (!Utils.isSpecialCharacter((char) file.read())) {
                file.seek(endPos);
                char c = (char) file.read();
                if (!Utils.isSpecialCharacter(c)) {
                    endPos++;
                    int value = file.read();
                    while (value != -1 && !Utils.isSpecialCharacter((char) value)) {
                        endPos++;
                        value = file.read();
                    }
                }
            }
        }
        return endPos;
    }

    /**
     * Function that updates the startPos with the correct one. We check if the caracter on startPos - 1 index is a
     * special character or not. In case it's a special character or if it's the first character from the file we return
     * the same startPos, otherwise we loop to the right till we find a special character. On each iteration we update
     * the startPos (it gets incremented). If startPos is in the middle of a word we will skip the whole word.
     * @param file - file from where we read.
     * @throws IOException
     */
    private void updateStartPos(RandomAccessFile file) throws IOException {
        if (startPos != 0) {
            file.seek(startPos);
            if (!Utils.isSpecialCharacter((char) file.read())) {
                int prev = startPos - 1;
                file.seek(prev);
                char c = (char) file.read();
                if (!Utils.isSpecialCharacter(c)) {
                    int value = file.read();
                    while (value != -1 && !Utils.isSpecialCharacter((char) value)) {
                        startPos++;
                        value = file.read();
                    }
                }
            }
        }
    }
}
