package com.example.time_wise;

import java.util.Random;

public class EncouragementSystem {
    private static final String[] ENCOURAGING_QUOTES = {
            "Keep going, you're doing great!",
            "Small steps lead to big results.",
            "You got this! Stay focused.",
            "Every effort counts. Don't give up!"
    };

    private static final String[] CONGRATULATION_QUOTES = {
            "Amazing! You did it!",
            "You're a superstar! Goal achieved.",
            "Outstanding work! Keep that momentum.",
            "Success is yours! Well done."
    };

    public static String getEncouragement() {
        return ENCOURAGING_QUOTES[new Random().nextInt(ENCOURAGING_QUOTES.length)];
    }

    public static String getCongratulation() {
        return CONGRATULATION_QUOTES[new Random().nextInt(CONGRATULATION_QUOTES.length)];
    }
}