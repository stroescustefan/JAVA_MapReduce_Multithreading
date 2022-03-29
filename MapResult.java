import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Return result for MapTask.
 */
public class MapResult {
    private String file;
    private List<String> maximalWords;
    private Map<Integer, Integer> words;

    public MapResult(String file, List<String> maximalWords, Map<Integer, Integer> words) {
        this.file = file;
        this.maximalWords = maximalWords;
        this.words = words;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getMaximalWords() {
        return maximalWords;
    }

    public void setMaximalWords(List<String> maximalWords) {
        this.maximalWords = maximalWords;
    }

    public Map<Integer, Integer> getWords() {
        return words;
    }

    public void setWords(Map<Integer, Integer> words) {
        this.words = words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapResult mapResult = (MapResult) o;
        return Objects.equals(file, mapResult.file) && Objects.equals(maximalWords, mapResult.maximalWords) &&
                Objects.equals(words, mapResult.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, maximalWords, words);
    }

    @Override
    public String toString() {
        return "MapResult{" +
                "file='" + file + '\'' +
                ", maximalWords=" + maximalWords +
                ", words=" + words +
                '}';
    }
}
