import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ReduceTask implements Callable<ReduceResult> {
    private String filename;
    private List<MapResult> mapResults;

    public ReduceTask(String filename, List<MapResult> mapResults) {
        this.filename = filename;
        this.mapResults = mapResults;
    }

    /**
     * Function that is being runned by a worker.
     * @return a ReduceResult object which contains final computed map, file name, final list of maximal words and
     * computed rang.
     * @throws Exception
     */
    @Override
    public ReduceResult call() throws Exception {
        Map<Integer, Integer> allWords = new HashMap<>();
        List<String> maximalWords;
        int numberOfWords;
        float rang = 0;

        numberOfWords = createGlobalMap(allWords);
        maximalWords = createGLobalMaximalWords();
        rang = computeRang(allWords, numberOfWords);

        return new ReduceResult(filename, allWords, maximalWords, rang);
    }

    /**
     * Function that computes the rang of a file.
     * @param allWords - a map of words
     * @param numberOfWords - number of words.
     * @return
     */
    private float computeRang(Map<Integer, Integer> allWords, int numberOfWords) {
        float rang = 0;

        for (Map.Entry<Integer, Integer> entry : allWords.entrySet()) {
            rang += Utils.fibo(entry.getKey() + 1) * entry.getValue();
        }
        rang /= numberOfWords;

        return rang;
    }

    /**
     * Function that creates and returns the list of maximal words for current file.
     * @return - a list of maximal words
     */
    private List<String> createGLobalMaximalWords() {

        /**
         * Creates a list of maximal words from a list of lists of maximal words. We select the lists of maximal
         * words only for current file results(filter). We sort the computed list by words length.
         */
        List<String> allMaximalWords = mapResults.stream()
                .filter(value -> value.getFile().equals(filename))
                .map(MapResult::getMaximalWords)
                .collect(Collectors.toList()).stream()
                .flatMap(List::stream)
                .sorted(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        if (o1.length() < o2.length()) {
                            return 1;
                        } else if (o1.length() > o2.length()) {
                            return -1;
                        }

                        return 0;
                    }
                }).collect(Collectors.toList());

        /**
         * Create final maximal words list. We only add those words which have maximum length.
         */
        List<String> maximalWords = new ArrayList<>();
        if (allMaximalWords.size() != 0) {
            int maxLengthWord;
            int i;

            maxLengthWord  = allMaximalWords.get(0).length();
            i = 0;
            while (i < allMaximalWords.size() && allMaximalWords.get(i).length() == maxLengthWord) {
                maximalWords.add(allMaximalWords.get(i));
                i++;
            }
        }

        return maximalWords;
    }

    /**
     * Function which computes the final map for current file and the number of words
     * @param allWords - final created map.
     * @return - count of all file words.
     */
    private int createGlobalMap(Map<Integer, Integer> allWords) {
        int numberOfWords;

        numberOfWords = 0;
        for (MapResult mapTask : mapResults) {
            if (mapTask.getFile().equals(filename)) {
                for (Map.Entry<Integer, Integer> entry : mapTask.getWords().entrySet()) {
                    numberOfWords += entry.getValue();
                    if (!allWords.containsKey(entry.getKey())) {
                        allWords.put(entry.getKey(), entry.getValue());
                    } else {
                        allWords.put(entry.getKey(), allWords.get(entry.getKey()) + entry.getValue());
                    }
                }
            }
        }

        return numberOfWords;
    }

}
