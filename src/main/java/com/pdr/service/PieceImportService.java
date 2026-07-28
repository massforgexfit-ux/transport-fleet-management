package com.pdr.service;

import com.pdr.dao.PieceRechangeDao;
import com.pdr.model.PieceRechange;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PieceImportService {
    private final PieceRechangeDao pieceDao = new PieceRechangeDao();

    public int importFile(Path path) throws IOException, SQLException {
        List<PieceRechange> pieces;
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".csv")) {
            pieces = readCsv(path);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            pieces = readExcel(path);
        } else {
            throw new IllegalArgumentException("Format non supporte. Utiliser .xls, .xlsx ou .csv.");
        }
        for (PieceRechange piece : pieces) {
            pieceDao.save(piece);
        }
        return pieces.size();
    }

    private List<PieceRechange> readExcel(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            DataFormatter formatter = new DataFormatter(Locale.FRANCE);
            for (Sheet sheet : workbook) {
                List<PieceRechange> pieces = readSheet(sheet, formatter);
                if (!pieces.isEmpty()) {
                    return pieces;
                }
            }
            return List.of();
        } catch (Exception exception) {
            if (exception instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Lecture Excel impossible", exception);
        }
    }

    private List<PieceRechange> readSheet(Sheet sheet, DataFormatter formatter) {
        for (Row row : sheet) {
            Map<String, Integer> header = headerFromExcel(row, formatter);
            if (!header.containsKey("code article")) {
                continue;
            }
            List<PieceRechange> pieces = new ArrayList<>();
            for (int rowIndex = row.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);
                if (dataRow == null) {
                    continue;
                }
                PieceRechange piece = pieceFromExcel(dataRow, header, formatter);
                if (piece != null) {
                    pieces.add(piece);
                }
            }
            return pieces;
        }
        return List.of();
    }

    private Map<String, Integer> headerFromExcel(Row row, DataFormatter formatter) {
        Map<String, Integer> header = new HashMap<>();
        for (Cell cell : row) {
            String value = normalize(formatter.formatCellValue(cell));
            if (!value.isBlank()) {
                header.put(value, cell.getColumnIndex());
            }
        }
        return header;
    }

    private PieceRechange pieceFromExcel(Row row, Map<String, Integer> header, DataFormatter formatter) {
        String code = value(row, header, formatter, "code article");
        if (code.isBlank()) {
            return null;
        }
        PieceRechange piece = new PieceRechange();
        piece.setCodeArticle(code);
        piece.setDescriptionPiece(value(row, header, formatter, "description de la piece et outil", "description de la piece"));
        piece.setUnite(value(row, header, formatter, "unite"));
        piece.setGroupeArticles(value(row, header, formatter, "groupe d articles"));
        piece.setQuantiteConsommeeHistorique(parseInt(value(row, header, formatter, "qte consommee historique", "quantite consommee historique")));
        piece.setSousEnsemble(value(row, header, formatter, "sous ensemble ref plan", "sous ensemble"));
        piece.setReferenceConstructeur(value(row, header, formatter, "ref constructeur", "reference constructeur"));
        piece.setPieceUsure(isYes(value(row, header, formatter, "piece d usure")));
        piece.setStockActuel(0);
        piece.setStockMinimum(0);
        return piece;
    }

    private String value(Row row, Map<String, Integer> header, DataFormatter formatter, String... keys) {
        for (String key : keys) {
            Integer index = header.get(key);
            if (index != null) {
                Cell cell = row.getCell(index);
                return cell == null ? "" : formatter.formatCellValue(cell).trim();
            }
        }
        return "";
    }

    private List<PieceRechange> readCsv(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            Map<String, Integer> header = null;
            List<PieceRechange> pieces = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                List<String> columns = parseCsvLine(line);
                if (header == null) {
                    Map<String, Integer> candidate = headerFromCsv(columns);
                    if (candidate.containsKey("code article")) {
                        header = candidate;
                    }
                    continue;
                }
                PieceRechange piece = pieceFromCsv(columns, header);
                if (piece != null) {
                    pieces.add(piece);
                }
            }
            return pieces;
        }
    }

    private Map<String, Integer> headerFromCsv(List<String> columns) {
        Map<String, Integer> header = new HashMap<>();
        for (int index = 0; index < columns.size(); index++) {
            String value = normalize(columns.get(index));
            if (!value.isBlank()) {
                header.put(value, index);
            }
        }
        return header;
    }

    private PieceRechange pieceFromCsv(List<String> columns, Map<String, Integer> header) {
        String code = value(columns, header, "code article");
        if (code.isBlank()) {
            return null;
        }
        PieceRechange piece = new PieceRechange();
        piece.setCodeArticle(code);
        piece.setDescriptionPiece(value(columns, header, "description de la piece et outil", "description de la piece"));
        piece.setUnite(value(columns, header, "unite"));
        piece.setGroupeArticles(value(columns, header, "groupe d articles"));
        piece.setQuantiteConsommeeHistorique(parseInt(value(columns, header, "qte consommee historique", "quantite consommee historique")));
        piece.setSousEnsemble(value(columns, header, "sous ensemble ref plan", "sous ensemble"));
        piece.setReferenceConstructeur(value(columns, header, "ref constructeur", "reference constructeur"));
        piece.setPieceUsure(isYes(value(columns, header, "piece d usure")));
        return piece;
    }

    private String value(List<String> columns, Map<String, Integer> header, String... keys) {
        for (String key : keys) {
            Integer index = header.get(key);
            if (index != null && index < columns.size()) {
                return columns.get(index).trim();
            }
        }
        return "";
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ';' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    private String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT)
                .replace('’', ' ')
                .replace('\'', ' ')
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private int parseInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value.replace(',', '.').split("\\.")[0].trim());
    }

    private boolean isYes(String value) {
        return value != null && normalize(value).equals("oui");
    }
}
