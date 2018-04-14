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
                nameMap.put(name, list);
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

        attributes.forEach(
                attribute -> returnString[0] += "\"" + attribute.getName() + "\":\"" + attribute.getValue() + "\",");

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
        // The substring because of a comma that would throw errors

        returnString[0] = returnString[0].substring(0, returnString[0].length() - 1);

        return returnString[0] + "],";
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
        returnString[0] = returnString[0].substring(0, returnString[0].length() - 1);
        returnString[0] += "},";
        return returnString[0];
    }

    private static String mainWithoutName(Element element) {

        String returnString = "{";

        returnString += getAttributes(element);
        returnString += main(element.getChildren().toArray(new Element[element.getChildren().size()]));
        if (returnString.substring(returnString.length() - 1).equals(","))
            returnString = returnString.substring(0, returnString.length() - 1);
        returnString += "}";
        return returnString;
    }

    private static String main(Element[] elements) {
        final String[] returnString = {""};
        HashMap<ArrayList<Element>, Boolean> isArrayHashMap = isArray(elements);

        isArrayHashMap.forEach(((arrayList, aBoolean) -> {
            if (aBoolean) {
                returnString[0] += displayArray(arrayList.toArray(new Element[arrayList.size()]));

            } else {
                returnString[0] += displayNormal(arrayList.get(0));
            }
        }));
        return returnString[0];
    }

    public static void main(String[] args) {
        File inputFile = new File("src\\xml.xml");
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document document = saxBuilder.build(inputFile);

            Element root = document.getRootElement();

            Element[] elements = {root};
            jsonString += "{" + main(elements);

            if(jsonString.substring(jsonString.length() - 1).equals(","))
                jsonString = jsonString.substring(0, jsonString.length() - 1);
            jsonString += "}";
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                FileWriter writer = new FileWriter("src\\json.json");

                writer.write(jsonObject.toString());
                writer.close();
            }
            catch (Exception e){
                System.out.println("XML file is not convertible.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}