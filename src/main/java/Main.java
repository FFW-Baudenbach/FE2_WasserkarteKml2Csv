import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static String DURCHFLUSSMENGE = "0";
    public static String KATEGORIE_RED = "24";
    public static String KATEGORIE_YELLOW = "48";
    public static String KATEGORIE_GREEN = "96";
    public static String INVALID_TYPE = "INVALID";
    public static String CSV_HEADER = "Y-Koordinate;X-Koordinaten;Anzeigetext;Typ;Durchfluss;Kategorie";

    public static void main(String[] args)
    {
        if (args.length != 1) {
            System.out.println("ERROR: Usage: WasserkarteKml2Fe2Csv.jar inputFile.kms");
            return;
        }

        String inputFile = args[0];
        if (!inputFile.endsWith(".kml")) {
            System.out.println("ERROR: You need to specify a kml file");
            return;
        }

        Path inputFilePath = Paths.get(inputFile);
        if (!inputFilePath.toFile().exists()) {
            System.out.println("ERROR: Input file does not exist");
            return;
        }

        try {
            String kml = new String(Files.readAllBytes(inputFilePath));

            String result = convertToCsv(kml);

            String csvFileName = inputFile.replaceAll("\\.kml", ".csv");

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFileName), StandardCharsets.US_ASCII))) {
                writer.write(result);
            }

            System.out.println("DONE! Check " + csvFileName + " for result");
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
    }

    public static String convertToCsv(String kml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        ByteArrayInputStream input = new ByteArrayInputStream(kml.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(input);

        List<String> hydranten = new ArrayList<>();

        Element root = doc.getDocumentElement();
        NodeList placemarks = root.getElementsByTagName("Placemark");
        for (int i = 0; i < placemarks.getLength(); i++) {
            Node placemark = placemarks.item(i);

            String name = "", typ = "", x = "", y = "";

            NodeList placemarkChildNodes = placemark.getChildNodes();
            for (int j = 0; j < placemarkChildNodes.getLength(); j++) {
                Node element = placemarkChildNodes.item(j);

                if ("name".equalsIgnoreCase(element.getNodeName())) {
                    name = element.getTextContent().trim();
                    if (name.contains(";")) {
                        throw new RuntimeException("ERROR: Found semicolon in hydrant name: " + name);
                    }
                }
                else if ("description".equalsIgnoreCase(element.getNodeName())) {
                    String desc = element.getTextContent();
                    if (desc.contains("Unterflurhydrant")) {
                        typ = "U";
                    }
                    else if (desc.contains("Überflurhydrant")) {
                        typ = "O";
                    }
                    else {
                        typ = INVALID_TYPE;
                        System.out.println("WARN: Ignoring " + desc);
                    }
                }
                else if ("Point".equalsIgnoreCase(element.getNodeName())) {
                    NodeList pointChildNodes = element.getChildNodes();
                    for (int k = 0; k < pointChildNodes.getLength(); k++) {

                        Node coordinate = pointChildNodes.item(k);
                        if ("coordinates".equalsIgnoreCase(coordinate.getNodeName())) {
                            String fullString = coordinate.getTextContent();
                            y = fullString.substring(0, fullString.indexOf(",")).trim();
                            x = fullString.substring(fullString.indexOf(",") + 1, fullString.lastIndexOf(",")).trim();
                        }

                    }
                }
            }

            String csvLine = String.join(";", Arrays.asList(y, x, name, typ, DURCHFLUSSMENGE, KATEGORIE_GREEN));

            csvLine = csvLine.replaceAll("ä", "ae");
            csvLine = csvLine.replaceAll("ö", "oe");
            csvLine = csvLine.replaceAll("ü", "ue");
            csvLine = csvLine.replaceAll("ß", "ss");

            if (!isPureAscii(csvLine)) {
                throw new RuntimeException("ERROR: Found non-ascii in: " + csvLine);
            }

            if (!csvLine.contains(INVALID_TYPE)) {
                hydranten.add(csvLine);
            }
        }

        String finalCsvContent =
                CSV_HEADER + System.lineSeparator()
                        + String.join(System.lineSeparator(), hydranten).trim();

        return finalCsvContent;
    }

    public static boolean isPureAscii(String v) {
        return Charset.forName(StandardCharsets.US_ASCII.name()).newEncoder().canEncode(v);
    }
}
