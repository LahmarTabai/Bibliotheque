package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    public static boolean validerMotDePasse(String motDePasse) {
        return motDePasse.length() >= 8 &&
               motDePasse.matches(".*[A-Z].*") &&  // Au moins une majuscule
               motDePasse.matches(".*[a-z].*") &&  // Au moins une minuscule
               motDePasse.matches(".*\\d.*") &&   // Au moins un chiffre
               motDePasse.matches(".*[@#$%^&+=].*"); // Au moins un caractère spécial
    }

}
