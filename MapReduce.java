import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class MapReduce {

    public static void main(String[] args) {
        int threadNumber;
        String inputFile, outputFile;


        /**
         * Parse arguments.
         */
        if (args.length < 3) {
            System.err.println("Usage: MapReduce <workers> <in_file> <out_file>");
        }
        threadNumber = Integer.parseInt(args[0]);
        inputFile = args[1];
        outputFile = args[2];

        /**
         * Map operation.
         */
        List<String> fileNames = new ArrayList<>();
        ExecutorService executorMap = Executors.newFixedThreadPool(threadNumber);
        List<Future<MapResult>> mapResults = new ArrayList<>();
        processMap(inputFile, executorMap, mapResults, fileNames);

        /**
         * Reduce operation.
         */
        ExecutorService executorReduce = Executors.newFixedThreadPool(threadNumber);
        List<Future<ReduceResult>> reduceResults = new ArrayList<>();
        processReduce(fileNames, mapResults, executorReduce, reduceResults);
        List<ReduceResult> output = new ArrayList<>();
        /**
         * Create a list of ReduceResult from a list of Future<ReduceResult>
         */
        for (Future<ReduceResult> reduceResult : reduceResults) {
            ReduceResult result = null;
            try {
                result = reduceResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            output.add(result);
        }
        /**
         * Sort the results by rang.
         */
        Collections.sort(output, new Comparator<ReduceResult>() {
            @Override
            public int compare(ReduceResult o1, ReduceResult o2) {
                if (o1.getRang() > o2.getRang()) {
                    return -1;
                } else if (o1.getRang() < o2.getRang()) {
                    return 1;
                }

                return 0;
            }
        });

        /**
         * Write result to output file.
         */
        try {
            writeToFile(outputFile, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function that writes the Reduce result to the output file.
     * @param outputFile - name of the output file
     * @param output - list of results produced after Reduce stage.
     * @throws IOException
     */
    public static void writeToFile(String outputFile, List<ReduceResult> output) throws IOException {
        File fout = new File(outputFile);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (ReduceResult value : output) {
            String stringSplitter[] = value.getFilename().split("/");
            int wordMaxLength = value.getMaximalWords().get(0).length();

            String toWrite = stringSplitter[stringSplitter.length - 1] + "," + String.format("%.2f", value.getRang()) +
                    "," + wordMaxLength + "," + value.getAllWords().get(wordMaxLength);
            bw.write(toWrite);
            bw.newLine();
        }

        bw.close();
    }

    /**
     * Function that reads the input file, starts the tasks and store the Map operation results and names of files
     * in 2 lists.
     * @param inputFile - name of the file from where we read fragment size, number of files and name of the files to
     * be processed.
     * @param executorMap - a reference to an executor service.
     * @param mapResults -  list in which results of each Map operation are stored.
     * @param fileNames - list to add file names to be processed.
     */
    private static void processMap(String inputFile, ExecutorService executorMap,
                                   List<Future<MapResult>> mapResults, List<String> fileNames) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            int fragmentLength = Integer.parseInt(br.readLine());
            int numberOfLines = Integer.parseInt(br.readLine());
            for (int index = 0; index < numberOfLines; index++) {
                String line = br.readLine();

                fileNames.add(line);
                int fileSize = (int) new File(line).length();
                int tasksPerFile = 0;
                if (fileSize % fragmentLength == 0) {
                    tasksPerFile = fileSize / fragmentLength;
                } else {
                    tasksPerFile = fileSize / fragmentLength + 1;
                }

                for (int i = 0; i < tasksPerFile; i++) {
                    int startOffset = i * fragmentLength;
                    Future<MapResult> future = executorMap.submit(new MapTask(line, startOffset, fragmentLength, fileSize));
                    mapResults.add(future);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorMap.shutdown();
        try {
            executorMap.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that computes the Reduce operation and stores the results in a list. Each reduce tasks receives the
     * file for which we perform Reduce operations and list of MapResults.
     * @param fileNames - list of file names.
     * @param mapResults - list of results of Map operations.
     * @param executorReduce - reference to executor
     * @param reduceResults - list in which we store the results of Reduce operations.
     */
    private static void processReduce(List<String> fileNames, List<Future<MapResult>> mapResults,
                                      ExecutorService executorReduce, List<Future<ReduceResult>> reduceResults) {
        List<MapResult> mapResultList = new ArrayList<>();
        for (Future<MapResult> mapResult : mapResults) {
            MapResult result = null;
            try {
                result = mapResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            mapResultList.add(result);
        }
        for (String file : fileNames) {
            Future<ReduceResult> future = executorReduce.submit(new ReduceTask(file, mapResultList));
            reduceResults.add(future);
        }

        executorReduce.shutdown();
        try {
            executorReduce.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
