package interpreter;

import java.io.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.*;

/**
 * A library of functions for manipulating files and their text
 */
public class FileOperations {

	/**
     * Copies the contents of srcFile to a file named dstFile
     * Creates dstFile if it doesn't exist; overwrites it if it does
     * Returns true if and only if the copy was successful
     *
     * @param srcFile the file whose contents will be copied
     * @param dstFile the file to copy to
     * @return true if and only if the copy was successful
     */
    public static boolean copy(String srcFile, String dstFile) {
        return writeFile(getFileText(srcFile), dstFile);
    }

    /**
     * Returns the contents of src
     *
     * @param src the file whose contents will be returned
     * @return the contents of src
     */
    public static String getFileText(String src) {
        String fileText = "";

        try {
            FileInputStream stream = new FileInputStream(new File(src));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fileText = Charset.defaultCharset().decode(mbb).toString();
        } catch (FileNotFoundException e) {
            System.out.println("File " + src + " not found");
        } catch (IOException e) {
            System.out.println("Unexpected IOException");
            e.printStackTrace();
        }
        return fileText;
    }

    /**
     * Copies the fileText to a file named dstFile
     * Creates dstFile if it doesn't exist; overwrites it if it does
     * Returns true if and only if the copy was successful
     *
     * @param filetext the text to copy into dstFile
     * @param dstFile the file to copy to
     * @return true if and only if the copy was successful
     */
    public static boolean writeFile(String fileText, String dstFile) {
        boolean written = true;
        try {
            FileWriter fstream = new FileWriter(dstFile);
    		BufferedWriter out = new BufferedWriter(fstream);
        	out.write(fileText);
        	out.close();
        } catch (IOException e) {
            written = false;
            System.out.println("Unexpected IOException");
            e.printStackTrace();
        }
        return written;
    }

    /**
     * Creates a temporary file in the working directory that should delete on
     * exit. Returns the filename if successful, "" otherwise.
     *
     * @return the filename if successful, "" otherwise
     */
    public static String createTempFile() {
        String name = "";
        try {
            File file = File.createTempFile("MRE", ".txt", new File(System.getProperty("user.dir")));
            file.deleteOnExit();
            name = file.getName();
        } catch (IOException e) {
            System.out.println("Unexpected IOException");
            e.printStackTrace();
        }
        return name;
    }
}
