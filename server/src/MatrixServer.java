import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MatrixServer {

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(1234);


        while(true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Client " + socket + " has connected.");

                final Socket finalSocket = socket;
                Thread clientHandlerThread = new Thread(() -> {
                    try {
                        prepareClient(finalSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                clientHandlerThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void prepareClient(Socket socket) throws IOException, InterruptedException {
        InputStreamReader streamIn = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter streamOut = new OutputStreamWriter(socket.getOutputStream());

        BufferedReader reader = new BufferedReader(streamIn);
        BufferedWriter writer = new BufferedWriter(streamOut);

        writer.write("Enter matrix size.");
        writer.newLine();
        writer.flush();

        String matrixSizeString = reader.readLine();
        System.out.println("Client " + socket +": " + matrixSizeString);

        writer.write("MSG Received.");
        writer.newLine();
        writer.flush();

        int matrixSize = Integer.parseInt(matrixSizeString);

        handleClient(matrixSize, streamOut, streamIn, writer, reader, socket);

    }

    private static void handleClient(int size, OutputStreamWriter out, InputStreamReader in, BufferedWriter writer, BufferedReader reader, Socket socket) throws IOException, InterruptedException {
        int[][] matrix = new int[0][];
        List<MatrixFillThread> threads = new ArrayList<MatrixFillThread>();

        try {
            // Receive the size of the square matrix from the client
            matrix = new int[size][size];
            System.out.println("Generated square matrix with size " + size + " for client " + socket + ".");

            writer.write("Generated square matrix with size " + size + ". Enter number #0.");
            writer.newLine();
            writer.flush();

            String columnNumberStringTemp = reader.readLine();
            int columnNumberTemp = Integer.parseInt(columnNumberStringTemp);
            MatrixFillThread threadTemp = new MatrixFillThread(String.valueOf(0) , matrix, 0, columnNumberTemp, writer);
            threads.add(threadTemp);

            for (int col = 1; col < size ; col++) {
                // Ask the client for the number to fill the current column
                writer.write("Enter number #" + col + ".");
                writer.newLine();
                writer.flush();

                String columnNumberString = reader.readLine();
                int columnNumber = Integer.parseInt(columnNumberString);

                MatrixFillThread thread = new MatrixFillThread(String.valueOf(col),matrix, col, columnNumber, writer);
                threads.add(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } //finally {
            long fastestThreadTime = Long.MAX_VALUE;
            String fastestThreadName = "";

            for (MatrixFillThread thread : threads) {
                thread.start();
            }

            for (MatrixFillThread thread : threads) {
                thread.join();
            }

            // Find the fastest thread
            for (MatrixFillThread thread : threads) {
                if (thread.getRunTime() < fastestThreadTime) {
                    fastestThreadName = thread.getThreadName();
                    fastestThreadTime = thread.getRunTime();
                }
            }

            // Send filled matrix to client as a string
            String matrixString = matrixToString(matrix);
            writer.write(matrixString);
            //writer.newLine();
            writer.flush();

            // Send thread data to the client
            writer.write("Fastest thread is " + fastestThreadName + " with time " + fastestThreadTime + " nanoseconds.");
            writer.newLine();
            writer.flush();

            for (MatrixFillThread thread : threads) {
                writer.write("Difference between thread " + thread.getThreadName() +
                        " time and fastest thread time is " + (thread.getRunTime() - fastestThreadTime) + ".");
                writer.newLine();
                writer.flush();
            }

            // Send the matrix to the client as an object
            MatrixSendThread sendThread = new MatrixSendThread(socket, matrix);
            sendThread.start();
            sendThread.join();

            // Close the client connection
            socket.close();
            System.out.println("Client " + socket + " has disconnected.");
            out.close();
            in.close();
            reader.close();
            writer.close();
        //}

    }

    private static String matrixToString(int[][] matrix) {
        StringBuilder result = new StringBuilder();

        for (int[] row : matrix) {
            for (int value : row) {
                result.append(value).append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }


}
