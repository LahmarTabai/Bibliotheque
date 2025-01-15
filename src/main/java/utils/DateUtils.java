package utils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Convertit une date au format "JJ/MM/AAAA" en "YYYY-MM-DD HH:mm:ss".
     * Si elle est déjà "yyyy-MM-dd...", on la renvoie telle quelle.
     * @param dateVal Chaîne représentant la date.
     * @return La date convertie au format MySQL (yyyy-MM-dd HH:mm:ss).
     * @throws SQLException si la date est invalide
     */
    public static String convertDateFormat(String dateVal) throws SQLException {
        if (dateVal == null || dateVal.isEmpty()) {
            // Date vide => on choisit par exemple "1970-01-01 00:00:00"
            return "1970-01-01 00:00:00";
        }
        // Si déjà au format "yyyy-MM-dd..."
        if (dateVal.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return dateVal;
        }
        // Sinon, on suppose "dd/MM/yyyy"
        if (!dateVal.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new SQLException("Date invalide : " + dateVal);
        }

        try {
            DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ld = LocalDate.parse(dateVal, inFmt);
            LocalDateTime ldt = ld.atTime(0, 0, 0);
            DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return ldt.format(dbFmt);

        } catch (Exception e) {
            throw new SQLException("Impossible de convertir la date : " + dateVal, e);
        }
    }

}
