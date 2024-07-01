import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class MatrixClient {

    public static void main(String[] args) {
        Socket socket = null;
        InputStreamReader streamIn = null;
        OutputStreamWriter streamOut = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        ObjectInputStream objIn = null;

        try {socket = new Socket( "localhost", 1234);
            streamIn = new InputStreamReader(socket.getInputStream());
            streamOut = new OutputStreamWriter(socket.getOutputStream());

            reader = new BufferedReader(streamIn);
            writer = new BufferedWriter(streamOut);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Server: " + reader.readLine());

            //
            String matrixSizeString = scanner.nextLine();
            writer.write(matrixSizeString);
            writer.newLine();
            writer.flush();
            System.out.println("Server: " + reader.readLine());
            System.out.println("Server: " + reader.readLine());

            int matrixSize = Integer.parseInt(matrixSizeString);
            // Client enters numbers for the matrix
            // In the inner for loop the client receives the thread times of each thread
            for (int i = 1; i <= matrixSize ; i++) {
                String msgToSend = scanner.nextLine();

                writer.write(msgToSend);
                writer.newLine();
                writer.flush();

                if (i != matrixSize) {
                    System.out.println("Server: " + reader.readLine());
                } else {
                    System.out.println();//
                    for (int j = 0; j < matrixSize; j++) {
                        System.out.println("Server: " + reader.readLine());
                    }
                }

                if(msgToSend.equalsIgnoreCase("exit"))
                    break;
            }

            // The system prints the received matrix
            System.out.println();
            for (int i = 0; i < matrixSize ; i++) {
                System.out.println("Server: " + reader.readLine());
            }

            // The system prints the time difference between each of the threads and the fastest thread
            System.out.println();
            for (int i = 0; i <= matrixSize ; i++) {
                System.out.println("Server: " + reader.readLine());
            }

            objIn = new ObjectInputStream(socket.getInputStream());

            System.out.println();
            System.out.println("Printing the matrix received from the server: ");
            // Receive the matrix from the server as an object
            int[][] receivedMatrix = (int[][]) objIn.readObject();

            // Print the received matrix
            for (int[] row : receivedMatrix) {
                for (int value : row) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null)
                    socket.close();
                if ( reader != null)
                    reader.close();
                if ( writer != null)
                    writer.close();
                if ( streamIn != null)
                    streamIn.close();
                if ( streamOut != null)
                    streamOut.close();
                if ( objIn != null)
                    objIn.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }


    }
}
