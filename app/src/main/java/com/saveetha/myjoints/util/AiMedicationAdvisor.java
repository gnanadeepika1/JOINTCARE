package com.saveetha.myjoints.util;

public class AiMedicationAdvisor {

    public static String getSuggestion(double sdai, double das28) {

        if (sdai <= 3.3 && das28 < 2.6)
            return "Remission: Maintain current therapy / consider taper";

        if (sdai <= 11 || das28 <= 3.2)
            return "Low activity: Mild DMARD adjustment suggested";

        if (sdai <= 26 || das28 <= 5.1)
            return "Moderate activity: Combination DMARD recommended";

        return "High activity: Consider biologic or aggressive treatment";
    }
}