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
    // equals true
    private static HashMap<ArrayList<Element>, Boolean> ifElementNameOccursMultipleTimes(Element[] elements) {
        HashMap<ArrayList<Element>, Boolean> result = new HashMap<>();
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

        nameMap.forEach((name, elements1) -> {
            if (elements1.size() > 1)
                result.put(elements1, true);

            else
                result.put(elements1, false);
        });

        return result;
    }

    private static String getAttributes(Element element) {
        List<Attribute> attributes = element.getAttributes();
        final String[] returnString = {" "};

        attributes.forEach(attribute -> returnString[0] += "\"" + attribute.getName() + "\":\"" + attribute.getValue() + "\",");

        return returnString[0];
    }

    private static String displayAttributesAndContent(Element element) {
        if (element.hasAttributes())
            return "{\"content\":\"" + element.getText() + "\"," + getAttributes(element) + "}";
        else
            return "\"" + element.getText() + "\"";
    }

    // displays a couple of Elements with the same names
    private static String displayElementsWithTheSameName(Element[] elements) {

        final String[] returnString = {"\"" + elements[0].getName() + "\":["};

        for (Element element : elements) {
            List<Element> children = element.getChildren();
            if (children.size() > 0) {
                returnString[0] += displayChildrenAndAttributes(element) + ",";
            } else {
                returnString[0] += displayAttributesAndContent(element);
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

    private static String displayElementWithoutChildren(Element element) {
        return "\"" + element.getName() + "\":\"" + element.getText() + "\",";
    }

    private static String displayNormalElement(Element element) {

        if (element.getChildren().size() == 0)
            return displayElementWithoutChildren(element);

        final String[] returnString = {""};
        String name = element.getName();
        List<Element> children = element.getChildren();

        returnString[0] = returnString[0] + "\"" + name + "\":{";
        returnString[0] += getAttributes(element);
        returnString[0] += displayChildren(children);
        returnString[0] = removeLastComma(returnString[0]);
        returnString[0] += "},";
        return returnString[0];
    }


    public static String displayChildren(List<Element> children) {

        ArrayList<Element> elementsWithChildren = new ArrayList<>();
        ArrayList<Element> nameAndTextArrayList = new ArrayList<>();
        String[] returnString = {""};

        children.forEach(e -> {
            if (e.getChildren().size() > 0) {
                elementsWithChildren.add(e);

            } else if (!e.hasAttributes())
                nameAndTextArrayList.add(e);
            else
                returnString[0] += "\"" + e.getName() + "\":" + displayAttributesAndContent(e) + ",";
        });

        returnString[0] += displayNameAndTexts(nameAndTextArrayList);
        returnString[0] += main(elementsWithChildren.toArray(new Element[elementsWithChildren.size()]));

        return returnString[0];
    }

    private static String displayNameAndTexts(ArrayList<Element> elements) {

        String[] returnString = {""};
        elements.forEach(element -> {
            // TODO:Make different datatypes usable
            returnString[0] += displayElementWithoutChildren(element);
        });

        return returnString[0];
    }

    private static String displayChildrenAndAttributes(Element element) {

        String returnString = "{";

        returnString += getAttributes(element);

        returnString += main(element.getChildren().toArray(new Element[element.getChildren().size()]));

        returnString = removeLastComma(returnString);

        returnString += "}";

        return returnString;
    }

    private static String main(Element[] elements) {
        final String[] returnString = {""};

        HashMap<ArrayList<Element>, Boolean> isArrayHashMap = ifElementNameOccursMultipleTimes(elements);

        isArrayHashMap.forEach(((arrayList, isArray) -> {
            if (isArray) {
                returnString[0] += displayElementsWithTheSameName(arrayList.toArray(new Element[arrayList.size()]));

            } else {
                returnString[0] += displayNormalElement(arrayList.get(0));
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