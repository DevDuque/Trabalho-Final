package org.coltec.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.coltec.Model.JournalEntry;
import org.coltec.Util.DateConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JournalManager {
    private List<JournalEntry> entries;

    public JournalManager() {
        this.entries = new ArrayList<>();
    }

    public List<JournalEntry> getEntries() {
        return entries;
    }

    public void clearEntries() {
        entries.clear();
    }

    public void addEntry(String text, Date date, List<String> categories) {
        JournalEntry entry = new JournalEntry(text, date, categories);
        entries.add(entry);
    }

    public List<JournalEntry> filterEntries(String startDateString, String endDateString, List<String> categories) throws ParseException {
        Date startDate = DateConverter.stringToDate(startDateString);
        Date endDate = DateConverter.stringToDate(endDateString);

        return entries.stream()
                .filter(entry -> (startDate == null || DateConverter.isDateEqualOrAfter(entry.getDate(), startDate)) &&
                        (endDate == null || DateConverter.isDateEqualOrBefore(entry.getDate(), endDate)) &&
                        (categories == null || categories.isEmpty() || hasCommonCategories(entry, categories)))
                .collect(Collectors.toList());
    }

    private boolean hasCommonCategories(JournalEntry entry, List<String> categories) {
        List<String> entryCategories = entry.getCategories().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> lowerCaseCategories = categories.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return entryCategories.stream().anyMatch(lowerCaseCategories::contains);
    }

    public void exportEntries(String filePath, String format) throws IOException {
        if (format.equalsIgnoreCase("json")) {
            exportToJson(filePath);
        } else if (format.equalsIgnoreCase("csv")) {
            exportToCsv(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private void exportToJson(String fileName) throws IOException {
        String projectDir = System.getProperty("user.dir");
        String resourcesDir = projectDir + "/src/main/java/org/coltec/exports/";
        String filePath = resourcesDir + fileName + ".json";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(entries, writer);
        }
    }

    private void exportToCsv(String fileName) throws IOException {
        String projectDir = System.getProperty("user.dir");
        String resourcesDir = projectDir + "/src/main/java/org/coltec/exports/";
        String filePath = resourcesDir + fileName + ".csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Text,Date,Categories\n");
            for (JournalEntry entry : entries) {
                writer.write(String.format("%s,%s,%s\n", entry.getText(), DateConverter.dateToString(entry.getDate()), String.join(";", entry.getCategories())));
            }
        }
    }

    public void importEntries(String fileName, String format) throws IOException {
        String resourcesDir = "./src/main/java/org/coltec/exports/";
        String filePath = resourcesDir + fileName + "." + format.toLowerCase();

        if (format.equalsIgnoreCase("json")) {
            importFromJson(filePath);
        } else if (format.equalsIgnoreCase("csv")) {
            importFromCsv(filePath);
        } else {
            throw new IllegalArgumentException("Formato não suportado: " + format);
        }
    }

    private void importFromJson(String filePath) throws IOException {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Type listType = new TypeToken<List<JournalEntry>>(){}.getType();
            List<JournalEntry> importedEntries = gson.fromJson(reader, listType);
            entries.addAll(importedEntries);
        }
        System.out.println("Entradas importadas com sucesso.");
    }

    private void importFromCsv(String filePath) throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 3) {
                    String text = nextLine[0].trim();
                    String dateString = nextLine[1].trim();
                    String categoriesString = nextLine[2].trim();

                    Date date;
                    try {
                        date = DateConverter.stringToDate(dateString);
                    } catch (ParseException e) {
                        System.out.println("Erro ao converter data: " + dateString);
                        continue;
                    }

                    List<String> categories = Arrays.asList(categoriesString.split(";"));
                    addEntry(text, date, categories);
                } else {
                    System.out.println("Linha inválida no CSV: " + Arrays.toString(nextLine));
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Entradas importadas com sucesso.");
    }

    public void printAllEntries() {
        entries.stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .forEach(entry -> {
                    String formattedDate = DateConverter.dateToString(entry.getDate());
                    System.out.println(entry.getText() + " - " + formattedDate + " - " + String.join(", ", entry.getCategories()) + "\n");
                });
    }
}
