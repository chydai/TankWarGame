import java.io.*;

public class file {
    public static void main(String[] args) throws IOException {
        try {
            /*byte bWrite[] = { 11, 21, 3, 40, 5 };
            OutputStream os = new FileOutputStream("a.txt");
            for (int x = 0; x < bWrite.length; x++) {
                os.write(bWrite[x]); // writes the bytes
            }
            os.close();*/

            InputStream is = new FileInputStream("frame.txt");
            char size = (char) is.available();

            for (int i = 0; i < size; i++) {
                System.out.print((char) is.read() + "  ");
            }
            is.close();
        } catch (IOException e) {
            System.out.print("Exception");
        }
    }
}