import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyOwn {
    static ArrayList<Element> leftOver = new ArrayList<>();
    static ArrayList<Element> normal = new ArrayList<>();
    private static String jsonString = "{";
    private static String string;

    public static void main(String[] args) {

        String endBraces = "}";


        File inputFile = new File("src\\xml.xml");

        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document document = saxBuilder.build(inputFile);

            Element root = document.getRootElement();
            jsonString += "\"" + root.getName() + "\":";

            jsonString += "{";
            endBraces = "}" + endBraces;

            Element[] elements = new Element[root.getChildren().size()];
            elements = root.getChildren().toArray(elements);


            jsonString += showLastElements(elements);

            jsonString += endBraces;
            System.out.println(jsonString);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String showLastElements(Element[] elements) {
        HashMap<ArrayList<Element>, Boolean> isArray = SecTry.isArray(elements);
        final String[] string = {""};

        leftOver.clear();
        final String[] string1 = {""};

        isArray.forEach((element, array) -> {

            if (array) {
                String s = SecTry.displayArray(element.toArray(new Element[element.size()]));
                System.out.println(s);
                leftOver.clear();
                String name = element.get(0).getName();
                string[0] = string[0] + "\"" + name + "\":[";

                element.forEach(element1 -> {
                    HashMap<String, String> stuff = new HashMap<>();

                    List<Element> children = element1.getChildren();

                    children.forEach(e -> {
                        if (e.getChildren().size() > 0) {
                            leftOver.add(e);
                        } else
                            stuff.put(e.getName(), e.getText());
                    });


                    string1[0] += "{";


                    stuff.forEach((key, text) -> {
                        //TODO:Make different datatypes usable
                        string1[0] += "\"" + key + "\":" + text + ",";
                    });


                    if (leftOver.size() > 0) {
                        string1[0] += showLastElements(leftOver.toArray(new Element[leftOver.size()]));
                    }
                    if (string1[0].substring(string1[0].length() - 1).equals(","))
                        string1[0] = string1[0].substring(0, string1[0].length() - 1);
                    string1[0] += "},";
                });
                string1[0] = string1[0].substring(0, string1[0].length() - 1);
                string[0] += string1[0] + "],";

            } else {

                String name = element.get(0).getName();
                string[0] = string[0] + "\"" + name + "\":{";

                HashMap<String, String> stuff = new HashMap<>();

                List<Element> children = element.get(0).getChildren();

                children.forEach(e -> {
                    if (e.getChildren().size() > 0) {
                        leftOver.add(e);

                    } else
                        stuff.put(e.getName(), e.getText());
                });

                stuff.forEach((string3, string2) -> {
                    //TODO:Make different datatypes usable
                    string[0] = string[0] + "\"" + string3 + "\":" + string2 + ",";
                });
                if (leftOver.size() > 0)
                    string[0] += showLastElements(leftOver.toArray(new Element[leftOver.size()]));
                string[0] = string[0].substring(0, string[0].length() - 1);
                string[0] += "},";

            }
        });

        if (string[0].substring(string[0].length() - 1).equals(","))
            return string[0].substring(0, string[0].length() - 1);
        else
            return string[0];
    }

}
