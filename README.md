# WasserkarteKml2Fe2Csv

###General information
Convert a KML file, exported from Wasserkarte.info to a CSV File which is compatible to Alamos FE2.  
The benefit is, that FE2 could store them offline, so that the new aMobile Pro application could show all hydrants alongside the ones with pretty picture from Wasserkarte.info.  
It will transform the coordinates, the type and the name.  
In addition it ensures, that only valid ASCII signs are used. It will replace common german symbols like ä,ö,ü,ß.

###Example
```xml
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2">
<Document>
    <Placemark>
        <name> Baudenbach, Feuerwehrhaus (# 101)</name>
        <description><![CDATA[Baudenbach, Feuerwehrhaus<br />Überflurhydrant<br />2x B<br /><a href=\https://portal.wasserkarte.info/watermap/waterSource/123546\>Details</a>]]></description>
        <Point>
            <coordinates>10.5361927, 49.6234328, 0</coordinates>
        </Point>
        <Style>
            <IconStyle>
                <Icon>
                    <href>https://portal.wasserkarte.info/m/123456_1.png</href>
                </Icon>
            </IconStyle>
        </Style>
    </Placemark>
    <Placemark>
        <name> Baudenbach, Feuerwehrhaus (# 122)</name>
        <description><![CDATA[Baudenbach, Feuerwehrhaus<br />Unterflurhydrant<br /><br /><a href=\https://portal.wasserkarte.info/watermap/waterSource/234567\>Details</a>]]></description>
        <Point>
            <coordinates>10.5361173, 49.6231482, 0</coordinates>
        </Point>
        <Style>
            <IconStyle>
                <Icon>
                    <href>https://portal.wasserkarte.info/m/234567_1.png</href>
                </Icon>
            </IconStyle>
        </Style>
    </Placemark>
</Document>
</kml>
```
will be transformed to
```csv
Y-Koordinate;X-Koordinaten;Anzeigetext;Typ;Durchfluss;Kategorie
10.5361927;49.6234328;Baudenbach, Feuerwehrhaus (# 101);O;0;96
10.5361173;49.6231482;Baudenbach, Feuerwehrhaus (# 122);U;0;96
```
###Usage
* Download *WasserkarteKml2Fe2Csv.jar* from this Repository
* Ensure you have a running Java 8 installation (or higher)  
  ```java --version```
* Download KML file from https://www.wasserkarte.info and place into the same folder as the tool.  
  Name it for example *hydranten.kml*
* Execute following command  
  ```java -jar WasserkarteKml2Fe2Csv.jar hydranten.kml```
* Look into the directory, there you should have a new file being *hydranten.csv*

This file you can in the next step upload as *Ebene* in FE2 (Webinterface > Objekte > Ebenen)

###Restrictions

* The kml file does not include *Durchfluss* (Default: 0)
* Thereby the field *Kategorie* cannot be filled meaningful (Default: 96 = GREEN)
* Alamos only supports above- and below ground hydrants. Others will be excluded and just logged
* If there are other non-ascii symbols than äöüß, program will terminate
* The tool itself is developed quick-and-dirty :)