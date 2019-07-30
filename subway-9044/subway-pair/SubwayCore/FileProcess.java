package subwaycore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class FileProcess {
    public void printFile(String strContent, String strOutFile) {
        try {
            File file = new File(strOutFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), false);

            fileWriter.write(strContent);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFile(Vector<String> vcContent, String strOutFile) {
        String strContent = "";

        for (String strTmp: vcContent) {
            strContent += strTmp;
            strContent += "\r\n";
        }

        printFile(strContent, strOutFile);
    }
}
