package com.bodyweight.fitness.utils;

public class ApplicationStoreUtils {
    private static final String androidInstallerPackage = "com.android.vending";
    private static final String amazonInstallerPackage = "com.amazon.venezia";

    public enum ApplicationStore {
        GooglePlay("https://play.google.com/store/apps/details?id=com.bodyweight.fitness.pro"),
        AmazonStore("amzn://apps/android?p=io.mazur.fit.pro");

        private String mApplicationStoreAppUrl;

        ApplicationStore(String applicationStoreAppUrl) {
            mApplicationStoreAppUrl = applicationStoreAppUrl;
        }

        public String getApplicationStoreAppUrl() {
            return mApplicationStoreAppUrl;
        }
    }

    public static ApplicationStore getApplicationStoreUrl(String installerPackageName) {
        if (installerPackageName != null) {
            if (installerPackageName.equals(amazonInstallerPackage)) {
                return ApplicationStore.AmazonStore;
            } else if (installerPackageName.equals(androidInstallerPackage)) {
                return ApplicationStore.GooglePlay;
            }
        } else if (android.os.Build.MANUFACTURER.equals("Amazon")) {
            return ApplicationStore.AmazonStore;
        }

        return ApplicationStore.GooglePlay;
    }
}