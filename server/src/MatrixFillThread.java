import java.io.BufferedWriter;
import java.io.IOException;

public class MatrixFillThread extends Thread {
    private String name;
    private int[][] matrix;
    private int col;
    private int columNumber;
    private long runTime;
    private BufferedWriter writer;

    public MatrixFillThread(String name_, int[][] matrix_, int col_, int columNumber_, BufferedWriter writer_) {
        this.name = name_;
        this.matrix = matrix_;
        this.col = col_;
        this.columNumber = columNumber_;
        this.writer = writer_;

        this.runTime = 0;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        for (int row = 0; row < matrix.length; row++) {
            matrix[row][col] = this.columNumber;
            /*try {
                Thread.sleep(25); // Simulating some work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
        long elapsedTime = System.nanoTime() - startTime;
        this.runTime = elapsedTime;

        try {
            writer.write("Thread " + this.name + " has filled column number " + this.col
                    + " with the number " + this.columNumber + ". Execution time: " + elapsedTime + " nanoseconds.");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public long getRunTime() {
        return runTime;
    }

    public String getThreadName() {
        return name;
    }

}
