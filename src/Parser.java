import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Parser {

    private static String jsonString = "";


    // Returns a hashmap with a Arraylist of Elements with the same name.
    // If there are more than one, with the same way, the content of the hashmap
    // equals true WORKS
    static HashMap<ArrayList<Element>, Boolean> isArray(Element[] elements) {
        HashMap<ArrayList<Element>, Boolean> occurring = new HashMap<>();
        HashMap<String, ArrayList<Element>> nameMap = new HashMap<>();

        for (Element element : elements) {
            String name = element.getName();
            if (nameMap.containsKey(name)) {

                ArrayList<Element> list = nameMap.get(name);
                list.add(element);

                nameMap.replace(name, list);
            } else {
                ArrayList<Element> objects = new ArrayList<>();
                objects.add(element);
                nameMap.put(name, objects);
            }
        }
        nameMap.forEach((s, elements1) -> {
            if (elements1.size() > 1)
                occurring.put(elements1, true);

            else
                occurring.put(elements1, false);
        });

        return occurring;
    }

    // WORKS
    static String getAttributes(Element element) {
        List<Attribute> attributes = element.getAttributes();
        final String[] returnString = {" "};

        attributes.forEach(attribute -> returnString[0] += "\"" + attribute.getName() + "\":\"" + attribute.getValue() + "\",");

        return returnString[0];
    }

    // WORKS
    static String attributesAndContent(Element element) {
        if (element.hasAttributes())
            return "{\"content\":\"" + element.getText() + "\"," + getAttributes(element) + "}";
        else
            return "\"" + element.getText() + "\"";
    }

    // displays a couple of Elements with the same names MAYBE WORKS
    static String displayArray(Element[] elements) {

        final String[] returnString = {""};

        returnString[0] += "\"" + elements[0].getName() + "\":[";

        for (Element element : elements) {
            List<Element> children = element.getChildren();
            if (children.size() > 0) {
                returnString[0] += mainWithoutName(element) + ",";
            } else {

                returnString[0] += attributesAndContent(element);

            }
        }
        returnString[0] = removeLastComma(returnString[0]);

        return returnString[0] + "],";
    }

    private static String removeLastComma(String s) {
        if (jsonString.substring(jsonString.length() - 1).equals(","))
            return s.substring(0, s.length() - 1);
        return s;
    }

    static String displaySingle(Element element) {
        return "\"" + element.getName() + "\":\"" + element.getText() + "\",";
    }

    // WORKS
    static String displayNormal(Element element) {

        if (element.getChildren().size() == 0)
            return displaySingle(element);

        ArrayList<Element> leftOver = new ArrayList<>();
        final String[] returnString = {""};
        String name = element.getName();
        returnString[0] = returnString[0] + "\"" + name + "\":{";
        returnString[0] += getAttributes(element);

        HashMap<String, String> stuff = new HashMap<>();

        List<Element> children = element.getChildren();

        children.forEach(e -> {
            if (e.getChildren().size() > 0) {
                leftOver.add(e);

            } else if (!e.hasAttributes())
                stuff.put(e.getName(), e.getText());
            else
                returnString[0] += "\"" + e.getName() + "\":" + attributesAndContent(e) + ",";
        });

        stuff.forEach((string3, string2) -> {
            // TODO:Make different datatypes usable
            returnString[0] = returnString[0] + "\"" + string3 + "\":\"" + string2 + "\",";
        });
        if (leftOver.size() > 0)
            returnString[0] += main(leftOver.toArray(new Element[leftOver.size()]));
        returnString[0] = removeLastComma(returnString[0]);
        returnString[0] += "},";
        return returnString[0];
    }

    private static String mainWithoutName(Element element) {

        String returnString = "{";

        returnString += getAttributes(element);
        returnString += main(element.getChildren().toArray(new Element[element.getChildren().size()]));
        if (returnString.substring(returnString.length() - 1).equals(","))
            returnString = removeLastComma(returnString);
        returnString += "}";
        return returnString;
    }

    private static String main(Element[] elements) {
        final String[] returnString = {""};

        HashMap<ArrayList<Element>, Boolean> isArrayHashMap = isArray(elements);

        isArrayHashMap.forEach(((arrayList, isArray) -> {

            if (isArray) {
                returnString[0] += displayArray(arrayList.toArray(new Element[arrayList.size()]));

            } else {
                returnString[0] += displayNormal(arrayList.get(0));
            }
        }));
        return returnString[0];
    }

    //Opens the XML file, calls the first function (main()) and saves the json file
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("XML location: ");
        File inputFile = new File(sc.nextLine().replace("\\", "\\\\"));


        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);

            Element root = document.getRootElement();
            Element[] elements = {root};

            jsonString += "{";

            jsonString += main(elements);

            jsonString = removeLastComma(jsonString);
            jsonString += "}";

            jsonString = jsonString.replace("\n", "     ");

            try {
                System.out.println("Saving location: ");
                FileWriter writer = new FileWriter(sc.nextLine());

                JSONObject jsonObject = new JSONObject(jsonString);
                writer.write(jsonObject.toString());
                writer.close();
            } catch (Exception e) {
                System.out.println("XML file is not convertible.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}