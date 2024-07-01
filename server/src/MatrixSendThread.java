import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MatrixSendThread extends Thread {
    private Socket socket;
    private int[][] matrix;;

    public MatrixSendThread(Socket socket_, int[][] matrix_) {
        this.socket = socket_;
        this.matrix = matrix_;
    }

    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            // Send the matrix to the client
            out.writeObject(matrix);
            out.flush();

            System.out.println("Matrix sent to client " + socket + ".");

            if (out != null)
                out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
