package WasserkarteKml2Csv;

import WasserkarteKml2Csv.converter.AlamosConverter;
import WasserkarteKml2Csv.converter.ExcelConverter;
import WasserkarteKml2Csv.converter.FireBoardConverter;
import WasserkarteKml2Csv.converter.OfmConverter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length != 1) {
            System.out.println("ERROR: Usage: WasserkarteKml2Csv.jar inputFile.kms");
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

            System.out.println("Generating Alamos CSV...");
            String content = AlamosConverter.convertToAlamosCsv(kml);
            String fileName = inputFile.replaceAll("\\.kml", "_alamos.csv");
            writeFile(fileName, content, StandardCharsets.US_ASCII);

            System.out.println("Generating Excel CSV...");
            content = ExcelConverter.convert(kml);
            fileName = inputFile.replaceAll("\\.kml", "_excel.csv");
            writeFile(fileName, content, StandardCharsets.UTF_8);

            System.out.println("Generating OFM CSV...");
            content = OfmConverter.convertToOpenFireMapCsv(kml);
            fileName = inputFile.replaceAll("\\.kml", "_ofm.csv");
            writeFile(fileName, content, StandardCharsets.UTF_8);

            System.out.println("Generating FireBoard CSV...");
            content = FireBoardConverter.convertToFireBoardCsv(kml);
            fileName = inputFile.replaceAll("\\.kml", "_fb.csv");
            writeFile(fileName, content, StandardCharsets.UTF_8);

            System.out.println("DONE!");
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw);
        }
    }

    private static void writeFile(final String csvFileName, final String content, final Charset charset) throws IOException
    {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get(csvFileName)), charset))) {
            writer.write(content);
        }
    }
}
