package WasserkarteKml2Csv.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ExcelConverterTests
{
    @Test
    void convertToCsv() throws IOException, SAXException, ParserConfigurationException {

        String kml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\">\n" +
                        "<Document>\n" +
                        "    <Placemark>\n" +
                        "        <name> Baudenbach, Feuerwehrhaus (# 101)</name>\n" +
                        "        <description><![CDATA[Baudenbach, Feuerwehrhaus<br />Überflurhydrant<br />2x B<br /><a href=\"https://portal.wasserkarte.info/watermap/waterSource/123546\">Details</a>]]></description>\n" +
                        "        <Point>\n" +
                        "            <coordinates>10.5361927, 49.6234328, 0</coordinates>\n" +
                        "        </Point>\n" +
                        "        <Style>\n" +
                        "            <IconStyle>\n" +
                        "                <Icon>\n" +
                        "                    <href>https://portal.wasserkarte.info/m/123456_1.png</href>\n" +
                        "                </Icon>\n" +
                        "            </IconStyle>\n" +
                        "        </Style>\n" +
                        "    </Placemark>\n" +
                        "    <Placemark>\n" +
                        "        <name> Baudenbach, Feuerwehrhaus (# 122)</name>\n" +
                        "        <description><![CDATA[Baudenbach, Feuerwehrhaus<br />Unterflurhydrant<br /><br /><a href=\"https://portal.wasserkarte.info/watermap/waterSource/234567\">Details</a>]]></description>\n" +
                        "        <Point>\n" +
                        "            <coordinates>10.5361173, 49.6231482, 0</coordinates>\n" +
                        "        </Point>\n" +
                        "        <Style>\n" +
                        "            <IconStyle>\n" +
                        "                <Icon>\n" +
                        "                    <href>https://portal.wasserkarte.info/m/234567_1.png</href>\n" +
                        "                </Icon>\n" +
                        "            </IconStyle>\n" +
                        "        </Style>\n" +
                        "    </Placemark>\n" +
                        "</Document>\n" +
                        "</kml>";

        String csv = ExcelConverter.convert(kml);

        String expectedCsv =
                "Nr;Name;Typ\n" +
                        "101;Baudenbach, Feuerwehrhaus;Überflurhydrant\n" +
                        "122;Baudenbach, Feuerwehrhaus;Unterflurhydrant";

        Assertions.assertEquals(expectedCsv, csv);

    }
}
