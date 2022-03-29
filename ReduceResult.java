import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Return result for ReduceTask.
 */
public class ReduceResult {
    private String filename;
    private Map<Integer, Integer> allWords;
    private List<String> maximalWords;
    private float rang;

    public ReduceResult(String filename, Map<Integer, Integer> allWords, List<String> maximalWords, float rang) {
        this.filename = filename;
        this.allWords = allWords;
        this.maximalWords = maximalWords;
        this.rang = rang;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Map<Integer, Integer> getAllWords() {
        return allWords;
    }

    public void setAllWords(Map<Integer, Integer> allWords) {
        this.allWords = allWords;
    }

    public List<String> getMaximalWords() {
        return maximalWords;
    }

    public void setMaximalWords(List<String> maximalWords) {
        this.maximalWords = maximalWords;
    }

    public float getRang() {
        return rang;
    }

    public void setRang(float rang) {
        this.rang = rang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReduceResult that = (ReduceResult) o;
        return Float.compare(that.rang, rang) == 0 && Objects.equals(filename, that.filename) &&
                Objects.equals(allWords, that.allWords) && Objects.equals(maximalWords, that.maximalWords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, allWords, maximalWords, rang);
    }

    @Override
    public String toString() {
        return "ReduceResult{" +
                "filename='" + filename + '\'' +
                ", allWords=" + allWords +
                ", maximalWords=" + maximalWords +
                ", rang=" + rang +
                '}';
    }
}
