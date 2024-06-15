package org.coltec.view;

import org.coltec.model.JournalEntry;
import org.coltec.model.JournalManager;
import org.coltec.util.DateConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static JournalManager journalManager = new JournalManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consumir a nova linha após nextInt()

            switch (choice) {
                case 1:
                    addEntry();
                    break;
                case 2:
                    try {
                        filterEntries();
                    } catch (ParseException e) {
                        System.out.println("Erro ao filtrar entradas: " + e.getMessage());
                    }
                    break;
                case 3:
                    exportEntries();
                    break;
                case 4:
                    importEntries();
                    break;
                case 5:
                    printAllEntries();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("Seja bem vindo ao Journaling.");
        System.out.println("Selecione uma das opções abaixo:");
        System.out.println("1. Nova entrada");
        System.out.println("2. Filtrar entradas");
        System.out.println("3. Exportar entradas");
        System.out.println("4. Importar entradas");
        System.out.println("5. Imprimir lista inteira");
        System.out.println("6. Sair");
    }

    private static void addEntry() {
        System.out.print("Texto: ");
        String text = scanner.nextLine();
        System.out.print("Deseja usar a data atual? (s/n): ");
        String useCurrentDate = scanner.nextLine().trim().toLowerCase();

        Date date;
        if (useCurrentDate.equals("s")) {
            date = new Date(); // Usa a data atual
        } else {
            System.out.print("Data (dd/MM/yyyy): ");
            String dateString = scanner.nextLine().trim();
            try {
                date = DateConverter.stringToDate(dateString);
            } catch (ParseException e) {
                System.out.println("Data inválida. Entrada não adicionada.");
                return;
            }
        }

        System.out.print("Categorias (separadas por vírgula): ");
        String categoriesString = scanner.nextLine().trim();
        List<String> categories = List.of(categoriesString.split(","));
        journalManager.addEntry(text, date, categories);
        System.out.println("Entrada adicionada com sucesso.");
    }

    private static void filterEntries() throws ParseException {
        System.out.print("Data inicial (dd/MM/yyyy) ou enter para pular: ");
        String startDateString = scanner.nextLine().trim();
        Date startDate = null;
        if (!startDateString.isEmpty()) {
            startDate = DateConverter.stringToDate(startDateString);
        }

        System.out.print("Data final (dd/MM/yyyy) ou enter para pular: ");
        String endDateString = scanner.nextLine().trim();
        Date endDate = null;
        if (!endDateString.isEmpty()) {
            endDate = DateConverter.stringToDate(endDateString);
        }

        System.out.print("Categorias (separadas por vírgula) ou enter para pular: ");
        String categoriesString = scanner.nextLine().trim();
        List<String> categories = null;
        if (!categoriesString.isEmpty()) {
            categories = List.of(categoriesString.split(","));
        }

        List<JournalEntry> filteredEntries = journalManager.filterEntries(startDateString, endDateString, categories);
        System.out.println("Entradas filtradas:");
        for (JournalEntry entry : filteredEntries) {
            System.out.println(entry.getText() + " - " + DateConverter.dateToString(entry.getDate()) + " - " + String.join(", ", entry.getCategories()));
        }
    }

    private static void importEntries() {
        System.out.print("Nome do Arquivo: ");
        String filePath = scanner.nextLine().trim();
        System.out.print("Formato (json/csv): ");
        String format = scanner.nextLine().trim();
        try {
            journalManager.importEntries(filePath, format);
        } catch (IOException e) {
            System.out.println("Erro ao importar entradas: " + e.getMessage());
        }
    }

    private static void printAllEntries() {
        journalManager.printAllEntries();
    }

    private static void exportEntries() {
        System.out.print("Nome do Arquivo: ");
        String filePath = scanner.nextLine().trim();
        System.out.print("Formato (json/csv): ");
        String format = scanner.nextLine().trim();
        try {
            journalManager.exportEntries(filePath, format);
            System.out.println("Entradas exportadas com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao exportar entradas: " + e.getMessage());
        }
    }
}
