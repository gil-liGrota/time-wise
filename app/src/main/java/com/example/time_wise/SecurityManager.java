package com.example.time_wise;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * מחלקה לניהול אבטחה ושמירת נתונים מקומית.
 * מיישמת את דרישות 9 (הצפנה) ו-10 (עבודה עם קבצים/SharedPreferences).
 */
public class SecurityManager {

    private static final String PREF_NAME = "TimeWise_Prefs";
    private static final String KEY_USER_ID = "logged_in_user_id";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // בנאי המקבל Context כדי לגשת לקבצי המערכת
    public SecurityManager(Context context) {
        // דרישה 10: שימוש ב-SharedPreferences לשמירת נתונים בקובץ XML מקומי
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    // --- דרישה 10: שמירת קבצים (SharedPreferences) ---

    /**
     * שמירת ה-ID של המשתמש לאחר התחברות מוצלחת
     */
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply(); // שמירה אסינכרונית ברקע
    }

    /**
     * שליפת ה-ID השמור (מחזיר null אם אף משתמש לא מחובר)
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * מחיקת הנתונים (התנתקות - Logout)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    // --- דרישה 9: הצפנת נתונים רגישים (Encryption) ---

    /**
     * פונקציה להצפנת סיסמה באמצעות אלגוריתם SHA-256.
     * הופכת טקסט רגיל ל"חתימה" דיגיטלית שלא ניתן להפוך חזרה.
     */
    public String hashPassword(String password) {
        try {
            // יצירת אובייקט הצפנה מסוג SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // הפיכת הסיסמה למערך של בייטים והרצת ההצפנה
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // הפיכת הבייטים למחרוזת הקסדצימלית (Hex) קריאה
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString(); // מחזיר את הסיסמה המוצפנת

        } catch (NoSuchAlgorithmException e) {
            Log.e("SecurityManager", "Error hashing password", e);
            return password; // הגנה: במקרה של תקלה נחזיר את המקור (לא אמור לקרות)
        }
    }
}