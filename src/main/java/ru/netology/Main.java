package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.writeString;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        //CSV2Json

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        System.out.println("columnMapping:  " + columnMapping);
        String fileName = "data.csv";
        System.out.println("fileName:  " + fileName);
        List<Employee> list = parseCSV(columnMapping, fileName);
        System.out.println("list:  " + list);
        String json = listToJson(list);
        System.out.println("json:  " + json);
        String jsonFilename = "data.json";
        System.out.println("jsonFilename:  " + jsonFilename);
        writeString(json, jsonFilename);


        //XML2Json
        System.out.println("==================");
        System.out.println("json from XML");
        String xmlFileName = "data.xml";
        List<Employee> listXML = parseXML(xmlFileName);
        System.out.println(listXML);
        String jsonFromXML = listToJson(listXML);
        System.out.println(jsonFromXML);
        String jsonFromXMLFileName = "data2.json";
        writeString(jsonFromXML, jsonFromXMLFileName);




    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Employee> parseXML(String xmlFileName) throws ParserConfigurationException, IOException, SAXException {
        List<String> elements = new ArrayList<>();
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new File(xmlFileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i=0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                    NodeList nodeList1 = node.getChildNodes();
                    for (int j=0; j < nodeList1.getLength(); j++) {
                        Node node_ = nodeList1.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            elements.add(node_.getTextContent());
                        }
                    }
                    list.add(new Employee(
                            Long.parseLong(elements.get(0)),
                            elements.get(1),
                            elements.get(2),
                            elements.get(3),
                            Integer.parseInt(elements.get(4))));

            }
        }
        return list;
   }
    private static void writeString(String json, String jsonFilename) {
        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}