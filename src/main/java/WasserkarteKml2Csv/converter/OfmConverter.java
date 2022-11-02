package WasserkarteKml2Csv.converter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfmConverter
{
    public static String INVALID_TYPE = "INVALID";
    public static String OFM_CSV_HEADER = "emergency;ref;longitude;latitude;fire_hydrant:type;fire_hydrant:pressure";

    public static String convertToOpenFireMapCsv(String kml) throws ParserConfigurationException, IOException, SAXException
    {
        // Pattern to extract
        final String regex_ref = ".*\\(#\\s(\\d+)\\)";
        final Pattern pattern_ref = Pattern.compile(regex_ref);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        ByteArrayInputStream input = new ByteArrayInputStream(kml.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(input);

        List<String> hydranten = new ArrayList<>();

        Element root = doc.getDocumentElement();
        NodeList placemarks = root.getElementsByTagName("Placemark");
        for (int i = 0; i < placemarks.getLength(); i++) {
            Node placemark = placemarks.item(i);

            String name = "", typ = "", x = "", y = "", pressure = "yes", ref = "";

            NodeList placemarkChildNodes = placemark.getChildNodes();
            for (int j = 0; j < placemarkChildNodes.getLength(); j++) {
                Node element = placemarkChildNodes.item(j);

                if ("name".equalsIgnoreCase(element.getNodeName())) {
                    name = element.getTextContent().trim();
                    if (name.contains(";")) {
                        throw new RuntimeException("ERROR: Found semicolon in hydrant name: " + name);
                    }

                    // Extract id from string like "Baudenbach, Feuerwehrhaus (# 101)"
                    Matcher matcher = pattern_ref.matcher(name);
                    if (matcher.find()) {
                        ref = String.valueOf(Integer.parseInt(matcher.group(1))); // Ensure it's an id
                    }
                    else {
                        System.out.println("WARN: No ref for " + name);
                        throw new RuntimeException("ERROR: Unable to extract id from name: " + name);
                    }
                }
                else if ("description".equalsIgnoreCase(element.getNodeName())) {
                    String desc = element.getTextContent();
                    if (desc.contains("Unterflurhydrant")) {
                        typ = "underground";
                    }
                    else if (desc.contains("Überflurhydrant")) {
                        typ = "pillar";
                    }
                    else if (desc.contains(("Löschwasserteich"))) {
                        typ = "pipe";
                        pressure = "suction";
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

            String csvLine = String.join(";", Arrays.asList("fire_hydrant", ref, y, x, typ, pressure));

            if (!csvLine.contains(INVALID_TYPE)) {
                hydranten.add(csvLine);
            }
        }

        return OFM_CSV_HEADER + System.lineSeparator()
                + String.join(System.lineSeparator(), hydranten).trim();
    }
}
