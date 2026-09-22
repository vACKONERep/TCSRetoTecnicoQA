package com.demoblaze.automation.data;

import com.demoblaze.automation.models.PurchaseCase;
import com.demoblaze.automation.models.PurchaseData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads Scenario Outline data from external files so the feature only names a case id.
 * <p>
 * {@code data/purchases.csv} supplies the two products. {@code data/buyers.json} supplies
 * the Place Order form, linked by {@code buyerId}.
 */
public final class PurchaseCatalog {

    private static final String CASES_RESOURCE = "data/purchases.csv";
    private static final String BUYERS_RESOURCE = "data/buyers.json";
    private static final Pattern JSON_OBJECT = Pattern.compile("\\{(.*?)}", Pattern.DOTALL);
    private static final Pattern JSON_FIELD = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"\\\\]*)\"");

    private static Map<String, PurchaseCase> cached;

    private PurchaseCatalog() {
    }

    public static PurchaseCase find(String caseId) {
        PurchaseCase found = cases().get(caseId);
        if (found == null) {
            throw new IllegalArgumentException(
                    "No purchase case '" + caseId + "' in " + CASES_RESOURCE
                            + ". Known cases: " + cases().keySet()
            );
        }
        return found;
    }

    private static Map<String, PurchaseCase> cases() {
        if (cached == null) {
            cached = Collections.unmodifiableMap(load());
        }
        return cached;
    }

    private static Map<String, PurchaseCase> load() {
        Map<String, PurchaseData> buyers = loadBuyers();
        Map<String, PurchaseCase> cases = new LinkedHashMap<>();

        for (Map<String, String> row : readCsv(CASES_RESOURCE)) {
            String caseId = required(row, "caseId", CASES_RESOURCE);
            String buyerId = required(row, "buyerId", CASES_RESOURCE);
            PurchaseData buyer = buyers.get(buyerId);
            if (buyer == null) {
                throw new IllegalStateException(
                        "Case '" + caseId + "' points to buyerId '" + buyerId
                                + "' which is not in " + BUYERS_RESOURCE
                                + ". Known buyers: " + buyers.keySet()
                );
            }
            if (cases.containsKey(caseId)) {
                throw new IllegalStateException("Duplicated caseId '" + caseId + "' in " + CASES_RESOURCE);
            }
            cases.put(caseId, new PurchaseCase(
                    caseId,
                    required(row, "description", CASES_RESOURCE),
                    required(row, "firstProduct", CASES_RESOURCE),
                    required(row, "secondProduct", CASES_RESOURCE),
                    buyer
            ));
        }

        if (cases.isEmpty()) {
            throw new IllegalStateException(CASES_RESOURCE + " has no data rows");
        }
        return cases;
    }

    private static Map<String, PurchaseData> loadBuyers() {
        String json = readText(BUYERS_RESOURCE);
        Map<String, PurchaseData> buyers = new LinkedHashMap<>();
        Matcher objects = JSON_OBJECT.matcher(json);
        while (objects.find()) {
            Map<String, String> fields = new LinkedHashMap<>();
            Matcher field = JSON_FIELD.matcher(objects.group(1));
            while (field.find()) {
                fields.put(field.group(1), field.group(2));
            }
            String buyerId = required(fields, "buyerId", BUYERS_RESOURCE);
            buyers.put(buyerId, new PurchaseData(
                    required(fields, "name", BUYERS_RESOURCE),
                    required(fields, "country", BUYERS_RESOURCE),
                    required(fields, "city", BUYERS_RESOURCE),
                    required(fields, "creditCard", BUYERS_RESOURCE),
                    required(fields, "month", BUYERS_RESOURCE),
                    required(fields, "year", BUYERS_RESOURCE)
            ));
        }
        if (buyers.isEmpty()) {
            throw new IllegalStateException(BUYERS_RESOURCE + " has no buyer objects");
        }
        return buyers;
    }

    private static List<Map<String, String>> readCsv(String resource) {
        List<Map<String, String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(open(resource), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalStateException(resource + " is empty");
            }
            List<String> headers = splitCsv(stripBom(headerLine));
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                List<String> values = splitCsv(line);
                if (values.size() != headers.size()) {
                    throw new IllegalStateException(
                            resource + " line " + lineNumber + " has " + values.size()
                                    + " columns, expected " + headers.size()
                    );
                }
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    row.put(headers.get(i), values.get(i));
                }
                rows.add(row);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read " + resource, ex);
        }
        return rows;
    }

    private static String readText(String resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(open(resource), StandardCharsets.UTF_8))) {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append('\n');
            }
            return stripBom(text.toString());
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read " + resource, ex);
        }
    }

    private static InputStream open(String resource) {
        InputStream stream = PurchaseCatalog.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException("Classpath resource not found: " + resource);
        }
        return stream;
    }

    private static String required(Map<String, String> row, String key, String resource) {
        String value = row.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing '" + key + "' in " + resource);
        }
        return value.trim();
    }

    private static String stripBom(String value) {
        if (value != null && !value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }

    private static List<String> splitCsv(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        String source = line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
        for (int i = 0; i < source.length(); i++) {
            char character = source.charAt(i);
            if (character == '"') {
                inQuotes = !inQuotes;
            } else if (character == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }
}
