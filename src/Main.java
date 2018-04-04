import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class Main {

    public static void main(String[] args) {

        File file = new File("C:\\Users\\Martin Goetze\\workspace\\XML to JSON\\res\\xml.xml");
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";

            while (br.ready()) {
                line += br.readLine();
            }


            JSONObject jsonObject = XML.toJSONObject(line);


            FileWriter writer = new FileWriter("C:\\Users\\Martin Goetze\\workspace\\XML to JSON\\res\\json.json");
            writer.write(jsonObject.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
