package com.kiddoware.kidsplace.sdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for 3rd party apps to integrate with Kids Place
 *
 * @author kiddoware
 */

public class KPUtility {

    /**
     * Flag to enable debug mode. Default set to false
     */
    public static boolean DEBUG_MODE = false;
    // APP MARKETS Constants


    /**
     * Constant to indicate Goolgle Play Store as the marketplace to download Kids Place.
     * Default marketplace is set to this value.
     */
    public static final int GOOGLE_MARKET = 1;
    /**
     * Constant to indicate Amazon App Store as the marketplace to download Kids Place
     */
    public static final int AMAZON_MARKET = 2;

    /**
     * Constant to indicate Samsung App Store as the marketplace to download Kids Place
     */
    public static final int SAMSUNG_MARKET = 3;

    /**
     * Constant to indicate NOOK App Store as the marketplace to download Kids Place
     */
    public static final int NOOK_MARKET = 4;

    /**
     * Constant to indicate SOC.IO App Store as the marketplace to download Kids Place
     */
    public static final int SOCIO_MARKET = 5;
    /**
     * Constant to indicate VODAFONE App Store as the marketplace to download Kids Place
     */
    public static final int VODAFONE_MARKET = 6;

    /**
     * Constant to indicate SOC.IO App Store as the marketplace to download Kids Place
     */
    public static final int VERIZON_MARKET = 7;

    private static String KIDSPLACE_PKG_NAME = "com.kiddoware.kidsplace";
    private static String KIDSPLACE_SERVICE_NAME = "com.kiddoware.kidsplace.KidsPlaceService";
    private static String KIDSPLACE_MAIN_CLASS = "com.kiddoware.kidsplace.LaunchActivity";
    private static final String BUNDLE_PKG_KEY = "package_name";

    // market switch logic
    private static int APP_MARKET = GOOGLE_MARKET;// comment this for
    // Amazon market
    // protected static String APP_MARKET = AMAZON_MARKET;//comment this for
    // Android market

    private static final String TAG = "Utility";
    private static boolean LOGGING_ERR = true;

    private static final String AUTHORITY = "com.kiddoware.kidsplace.providers.AuthenticationProvider";
    private static final String CONTENT_AUTHORITY = "com.kiddoware.kidsplace.providers.AppDataProvider";
    private static final String USER_CONTENT_AUTHORITY = "com.kiddoware.kidsplace.providers.UserDataProvider";
    private static final String CATEGORY_CONTENT_AUTHORITY = "com.kiddoware.kidsplace.providers.CategoryDataProvider";
    private static final String PREFERENCE_CONTENT_AUTHORITY = "com.kiddoware.kidsplace.providers.PreferenceDataProvider";


    private static String CONTENT_VALIDATE_PIN_URL = "content://" + AUTHORITY
            + "/validatePin/";
    private static final String CONTENT_PIN_HINT_URL = "content://" + AUTHORITY
            + "/getPinHint";

    private static final String CONTENT_ADD_PKG_TO_WHITELIST_URL = "content://" + AUTHORITY
            + "/addToWhiteList";
    private static final String CONTENT_REMOVE_PKG_FROM_WHITELIST_URL = "content://" + AUTHORITY
            + "/removeFromWhiteList";

    private static final String GET_SELECTED_APPS_URL = "content://" + CONTENT_AUTHORITY
            + "/getSelectedApps";

    private static final String IS_KIDSPLACE_SERVICE_RUNNING_URL = "content://" + CONTENT_AUTHORITY
            + "/isServiceRunning";

    private static final String GET_APP_CATEGORY_URL = "content://" + CONTENT_AUTHORITY
            + "/getAppCategory/";
    private static final String GET_ALL_USERS_URL = "content://" + USER_CONTENT_AUTHORITY
            + "/getAllUsers";

    private static final String GET_CURRENT_USER_URL = "content://" + USER_CONTENT_AUTHORITY
            + "/getCurrentUser";

    private static final String GET_ALL_USERS_APP_URL = "content://" + USER_CONTENT_AUTHORITY
            + "/getAllUsersApps";

    private static final String ADD_USER_URL = "content://" + USER_CONTENT_AUTHORITY
            + "/addUser";

    private static final String CONTENT_ADD_PKG_TO_KIDSPLACE = "content://" + CONTENT_AUTHORITY
            + "/addApp";
    private static final String CONTENT_REMOVE_PKG_FROM_KIDSPALCE = "content://" + CONTENT_AUTHORITY
            + "/removeApp";

    private static final String GET_ALL_CATEGORIES_URL = "content://" + CATEGORY_CONTENT_AUTHORITY
            + "/getAllCategories";

    private static final String ADD_CATEGORY_URL = "content://" + CATEGORY_CONTENT_AUTHORITY
            + "/addCategory";


    private static final String GET_ALL_PREFERENCES_URL = "content://" + PREFERENCE_CONTENT_AUTHORITY
            + "/getAllPreferences";

    private static final String SAVE_PREFERENCE_URL = "content://" + PREFERENCE_CONTENT_AUTHORITY
            + "/savePreference";

    private static final String GET_CURRENT_RUNNING_APP_URL = "content://" + CONTENT_AUTHORITY
            + "/getCurrentRunningApp";
    private static final String PIN_HINT = "pinhint";
    private static final String PIN_RESULT = "pin_result";
    private static final String PKG_NAME_KEY = "package_name";
    private static final String USER_ID_KEY = "user_id";
    private static final String CATEGORY_ID_KEY = "category_id";
    private static final String CATEGORY_NAME_KEY = "name";
    public static final String CURRENT_RUNNING_APP = "current_running_app";

    public static final String SETTING_KEY = "key";
    public static final String SETTING_VALUE = "value";
    public static final String SETTING_TYPE = "type";
    public static final String SETTING_LABEL = "label";


    private static final int ADD_ACTION = 1;
    private static final int REMOVE_ACTION = 2;
    //Default messages displayed on install/upgrade dialog prompt.
    //Developers can customize and set these from their resource files

    public static final String ID = "_id";
    //Cursor columns contsants for apps
    public static final String PACKAGE_NAME = "package_name";
    public static final String CATEGORY_NAME = "category_name";
    public static final String APP_NAME = "name";    //app title
    public static final String CLASS_NAME = "class_name";
    public static final String APP_CATEGORY_ID = "category_id";
    public static final String WIFI_ENABLED = "wifi_enabled";
    public static final String INSTALLED_FROM_KP_STORE = "from_kpstore";


    public static final String KP_SERVICE_STATE = "kp_service_state";

    public static final String USER_NAME = "name";
    public static final String USER_IMAGE = "image";
    public static final String KP_USER_CHANGE_NOTIFICATION_INTENT = "com.kiddoware.kidsplace.user.changed";
    public static final String KP_USER_CHANGE_NOTIFICATION_USER_ID = "id";
    private static final int LATEST_KP_VERSION_CODE = 645;

    //Cursor columns contsants for Category
    public static final String CAT_NAME = "name";
    public static final String CAT_ID = "_id";

    //Cursor columns contsants for User Application Table
    public static final String USERS_APPLICATIONS_APP_ID = "app_id";
    public static final String USERS_APPLICATIONS_USER_ID = "user_id";

    /**
     * Title on Alert Dialog displayed if user does not have Kids Place app installed on their device.
     * Developers can set this value from their Resource file.
     * Default Value: "Child Lock feature requires Kids Place App"
     */
    public static String KIDSPLACE_INSTALL_TITLE = "Child Lock feature requires Kids Place App";

    /**
     * Message on Alert Dialog displayed if user does not have Kids Place app installed on their device.
     * Developers can set this value from their Resource file.
     * Default Value: "Install Kids Place - With Child Lock from App Store?\nIt is a free Parental Control app that lets parents choose apps their kids can access then locks them out of the rest of their device."
     */
    public static String KIDSPLACE_INSTALL_MSG = "Install Kids Place - With Child Lock from App Store?\nIt is a free Parental Control app that lets parents choose apps their kids can access then locks them out of the rest of their device.";

    /**
     * Title on Alert Dialog displayed if user Kids Place app version is below the required version for API.
     * Developers can set this value from their Resource file.
     * Default Value: "Update Kids Place?"
     */
    public static String KIDSPLACE_UPGRADE_TITLE = "Update Kids Place?";
    /**
     * Message on Alert Dialog displayed if user Kids Place app version is below the required version for API.
     * Developers can set this value from their Resource file.
     * Default Value: "Child Lock feature requires Kids Place v 1.7.4 or higher. Get the latest version from App Store?"
     */
    public static String KIDSPLACE_UPGRADE_MSG = "Child Lock feature requires Kids Place v 2.7.5 or higher. Get the latest version from App Store?";

    /**
     * Negative Button text on Alert Dialog displayed on Install/Upgrade prompt for Kids Place.
     * Developers can set this value from their Resource file.
     * Default Value: "No"
     */
    public static String KIDSPLACE_NO = "No";
    /**
     * Positive Button text on Alert Dialog displayed on Install/Upgrade prompt for Kids Place
     * Developers can set this value from their Resource file.
     * Default Value: "OK"
     */
    public static String KIDSPLACE_OK = "OK";


    /**
     * Title on Alert Dialog displayed when app request to allow an app (like marketplace app) to white list
     * Developers can set this value from their Resource file.
     * Default Value: "Allow access to <app name>"
     */
    public static String KIDSPLACE_WHITELIST_TITLE = "Allow access to  <app name>";

    /**
     * Message on Alert Dialog displayed when app request to allow an app (like marketplace app) to white list
     * Developers can set this value from their Resource file.
     * Default Value: "Temporary allow access to <app name>?\nA 3rd party app is requesting Kids Place to allow access to app <app name>."
     */
    public static String KIDSPLACE_WHITELIST_MSG = "Temporary allow access to <app name>?\nA 3rd party app is requesting Kids Place to allow access to app <app name>.";

    /**
     * Call this method from the activity where you would like to start Kids Place from and add
     * caller app as approved app in Kids Place.
     *
     * @param activity        : Android activity from where Kids Place integration call need to be made
     * @param appMarketValue: Use KPUtility.GOOGLE_MARKET or KPUtility.AMAZON_MARKET to indicate where user should be directed
     *                        to upgrade/installed Kids Place if they do not have the correct KP version installed. Currently only Google and Amazon
     *                        app stores are supported.
     * @return: True if Kids Place integration call is handled successfully and false if not.
     */
    public static boolean handleKPIntegration(Activity activity, int appMarketValue) {
        try {
            return handleKPIntegration(activity, appMarketValue, true);

        } catch (Exception ex) {
            logErrorMsg("handleKPIntegration", TAG, ex);
        }
        return false;

    }

    /**
     * Call this method from the activity where you would like to start Kids Place from.
     *
     * @param activity        : Android activity from where Kids Place integration call need to be made
     * @param appMarketValue: Use KPUtility.GOOGLE_MARKET or KPUtility.AMAZON_MARKET to indicate where user should be directed
     *                        to upgrade/installed Kids Place if they do not have the correct KP version installed. Currently only Google and Amazon
     *                        app stores are supported.
     * @param startKidsPlace: true to Starts kids palce and add caller app as approved app.
     *                        false to just check for Kids Place install/upgrade without starting Kids Place.
     * @return: True if Kids Place integration call is handled successfully and false if not.
     */
    public static boolean handleKPIntegration(Activity activity, int appMarketValue, boolean startKidsPlace) {
        try {
            KPUtility.setMarketPlace(appMarketValue);
            //check for Kids Place
            int kpVersionCode = KPUtility.isKidsPlaceInstalled(activity.getApplicationContext());
            if (kpVersionCode == -1) {
                KPUtility.showKPInstallDialog(activity);
                return true;
            } else if (kpVersionCode < LATEST_KP_VERSION_CODE) {//TODO: change it latest KP version
                //show KP upgrade dialog
                KPUtility.showKPUpgradeDialog(activity);
                return true;
            } else {
                //start Kids Place
                if (startKidsPlace)
                    KPUtility.startKPService(activity);
                return true;
            }

        } catch (Exception ex) {
            logErrorMsg("handleKPIntegration", TAG, ex);
        }
        return false;

    }

    /**
     * Validates the PIN entered by user against the PIN set up in Kids Place
     *
     * @param context: Android context object
     * @param pin:     Pin entered by user
     * @return: True if supplied Pin is correct (validated against pin set up in Kids Place app) otherwise false.
     */
    public static boolean validatePin(Context context, String pin) {
        boolean value = false;
        Cursor cursor = null;
        if (pin != null) {
            try {
                ContentResolver r = context.getContentResolver();
                cursor = r.query(
                        Uri.parse(CONTENT_VALIDATE_PIN_URL + pin), null, null,
                        null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    logMsg("cursor.getCount()=" + cursor.getCount(), TAG);

                    // Get value from content provider
                    int pinResultIndex = cursor
                            .getColumnIndexOrThrow(PIN_RESULT);

                    cursor.moveToFirst();
                    int validationResult = cursor.getInt(pinResultIndex);
                    if (validationResult == 1) {
                        value = true;
                    }
                }

            } catch (Exception ex) {
                KPUtility.logErrorMsg("getPinHint:", TAG, ex);

            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return value;
    }

    /**
     * Returns the PIN hint set in Kids Place. This information can be displayed in prompt for PIN from apps.
     *
     * @param context: Android context object
     * @return: returns Pin hint that user has entered in Kids Place app.
     */
    public static String getPinHint(Context context) {
        String value = "";
        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(CONTENT_PIN_HINT_URL), null,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                // Let activity manage the cursor
                logMsg("cursor.getCount()=" + cursor.getCount(), TAG);

                // Get value from content provider
                int pinHintIndex = cursor.getColumnIndexOrThrow(PIN_HINT);

                cursor.moveToFirst();
                value = cursor.getString(pinHintIndex);
            }

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getPinHint:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return value;
    }

    /**
     * Checks if Kids Place is installed on users device or not.
     *
     * @param context:Android context object
     * @return: -1 if not installed otherwise returns Kids Place app's version number
     */
    public static int isKidsPlaceInstalled(Context context) {
        int value = -1;
        // TODO Auto-generated method stub
        PackageInfo packageInfo = getPackageInfo(KIDSPLACE_PKG_NAME, context);
        if (packageInfo != null) {
            value = packageInfo.versionCode;
        }

        return value;
    }

    /**
     * Call to check if Kids Place app is currently running on users device
     *
     * @param ctxt: Android activity from where check needs to be made
     * @return: true if Kids Place is already running otherwise false
     */
    public static boolean isKidsPlaceRunning(Context ctxt) {
        boolean value = false;
        ActivityManager manager = (ActivityManager) ctxt
                .getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (KIDSPLACE_SERVICE_NAME.equals(service.service.getClassName())) {
                value = true;
            }
        }
        return value;
    }

    /**
     * Call to check if Kids Place app is currently running on users device
     *
     * @param activity: Android activity from where check needs to be made
     * @return: true if Kids Place is already running otherwise false
     */
    public static boolean isKidsPlaceRunning(Activity activity) {
        boolean value = false;
        ActivityManager manager = (ActivityManager) activity
                .getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (KIDSPLACE_SERVICE_NAME.equals(service.service.getClassName())) {
                value = true;
            }
        }
        return value;
    }

    /**
     * Call to check if Kids Place service is active. This can be used if kids place service
     * is suspended if screen is powered off or for some oteh reason. Useful for timer plugin.
     */
    public static boolean isKidsPlaceActive(Context ctxt) {
        boolean value = true;
        Cursor cursor = null;

        try {
            ContentResolver r = ctxt.getContentResolver();
            cursor = r.query(Uri.parse(IS_KIDSPLACE_SERVICE_RUNNING_URL), null,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {

                // Get value from content provider
                int colIndex = cursor.getColumnIndexOrThrow(KP_SERVICE_STATE);

                cursor.moveToFirst();
                int kpState = cursor.getInt(colIndex);
                if (kpState != 1) {
                    value = false;
                }
            }

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getSelectedApps:", TAG, ex);
            value = false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return value;
    }

    /**
     * Call this method from the activity from where you would like to start Kids Place if you have already identified
     * that Kids Place is installed on user's device. Its recommended to use handleKPIntegration method instead of this.
     *
     * @param activity: Android activity from where Kids Place need to be started
     */
    public static void startKPService(final Activity activity) {
        // only start if its not running already.
        if (!isKidsPlaceRunning(activity)) {
            // start Kids Place service
            ComponentName component = new ComponentName(KIDSPLACE_PKG_NAME,
                    KIDSPLACE_MAIN_CLASS);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(component);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Bundle b = new Bundle();
            // notify home activity to start package specified in bundle
            b.putString(BUNDLE_PKG_KEY, activity.getPackageName());
            intent.putExtras(b);
            activity.startActivity(intent);
        }
    }

    /**
     * Call this method from the activity from where you would like to start Kids Place if you have already identified
     * that Kids Place is installed on user's device. Its recommended to use handleKPIntegration method instead of this.
     *
     * @param activity:       Android activity from where Kids Place need to be started
     * @param startCallerApp: Flag to indicate if Kids Place should prompt to add and start the called app
     */
    public static void startKPService(final Activity activity, boolean startCallerApp) {
        // only start if its not running already.
        if (!isKidsPlaceRunning(activity)) {
            // start Kids Place service
            ComponentName component = new ComponentName(KIDSPLACE_PKG_NAME,
                    KIDSPLACE_MAIN_CLASS);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(component);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (startCallerApp) {
                Bundle b = new Bundle();
                // notify home activity to start package specified in bundle
                b.putString(BUNDLE_PKG_KEY, activity.getPackageName());
                intent.putExtras(b);
            }
            activity.startActivity(intent);
        }
    }

    /**
     * Call to prompt user to install Kids Place
     *
     * @param activity: Android activity from where prompt needs to be displayed
     */
    public static void showKPInstallDialog(final Activity activity) {
        showKPInstallDialog(activity, false);
    }

    /**
     * Call to prompt user to upgrade Kids Place
     *
     * @param activity: Android activity from where prompt needs to be displayed
     */
    public static void showKPUpgradeDialog(final Activity activity) {
        showKPInstallDialog(activity, true);
    }

    /**
     * Call to set marketplace to download Kids Place app from.
     *
     * @param markeplaceValue:: Use KPUtility.GOOGLE_MARKET or KPUtility.AMAZON_MARKET to indicate where user should be directed
     *                         to upgrade/installed Kids Place if they do not have the correct KP version installed. Currently only Google and Amazon
     *                         app stores are supported.
     * @throws Exception
     */
    public static void setMarketPlace(int markeplaceValue) throws Exception {
        if (markeplaceValue == AMAZON_MARKET || markeplaceValue == GOOGLE_MARKET ||
                markeplaceValue == SAMSUNG_MARKET || markeplaceValue == NOOK_MARKET ||
                markeplaceValue == VERIZON_MARKET || markeplaceValue == SOCIO_MARKET) {
            APP_MARKET = markeplaceValue;
        } else {
            throw new Exception("Unsupported marketplace passed");
        }

    }

    /**
     * Add a package temporarily to white list and remove from blacklist in KP Service only
     *
     * @param activity
     * @param packageName
     */
    public static void addAppToWhiteList(final Activity activity, String packageName) {
        addToWhiteList(activity, packageName);
    }

    /**
     * Remove a package temporarily from white list and add to blacklist in KP Service only
     *
     * @param activity
     * @param packageName
     */
    public static void removeAppFromWhiteList(final Activity activity, String packageName) {
        removeFromWhiteList(activity, packageName);
    }

    /**
     * Remove a package temporarily from white list and add to blacklist in KP Service only
     *
     * @param activity
     * @param packageName
     */
    public static void addAppToBlackList(final Activity activity, String packageName) {
        addToBlackList(activity, packageName);
    }

    /**
     * Add a package temporarily to white list and remove from blacklist in KP Service only
     *
     * @param activity
     * @param packageName
     */
    public static void removeAppFromBlackList(final Activity activity, String packageName) {
        removeFromBlackList(activity, packageName);
    }

    /**
     * @param context
     * @return
     */
    public static Cursor getSelectedApps(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_SELECTED_APPS_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getSelectedApps:", TAG, ex);

        }

        return cursor;

    }

    /**
     * @param context
     * @return
     */
    public static long getAppCategoryId(String packageName, Context context) {

        Cursor cursor = null;
        long categoryId = 0;
        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_APP_CATEGORY_URL + packageName), null,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                logMsg("cursor.getCount()=" + cursor.getCount(), TAG);

                // Get value from content provider
                int categoryIdIndex = cursor
                        .getColumnIndexOrThrow(CATEGORY_ID_KEY);

                cursor.moveToFirst();
                categoryId = cursor.getLong(categoryIdIndex);

            }
        } catch (Exception ex) {
            KPUtility.logErrorMsg("getAppCategoryId:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return categoryId;

    }

    /**
     * @param context
     * @return
     */
    public static Cursor getUsersSelectedApps(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_ALL_USERS_APP_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getUsersSelectedApps:", TAG, ex);

        }

        return cursor;

    }

    /**
     * @param context
     * @return
     */
    public static Cursor getAllUsers(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_ALL_USERS_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getAllUsers:", TAG, ex);

        }

        return cursor;

    }

    /**
     * @param context
     * @return
     */
    public static Cursor getCurrentUser(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_CURRENT_USER_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getCurrentUser:", TAG, ex);

        }

        return cursor;

    }

    /**
     * @param context
     * @return
     */
    public static long getCurrentUserId(Context context) {
        long userId = 0;
        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_CURRENT_USER_URL), null,
                    null, null, null);
            // Get value from content provider
            int idIndex = cursor.getColumnIndexOrThrow(KPUtility.ID);
            while (cursor.moveToNext()) {
                userId = cursor.getInt(idIndex);

            }

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getCurrentUser:", TAG, ex);

        }

        return userId;

    }

    /**
     * @param context
     * @return
     */
    public static int addUser(Context context, String userName, String userId) {
        int id = 0;
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            ContentValues cValues = new ContentValues();
            cValues.put(USER_NAME, userName);
            cValues.put(ID, userId);
            Uri uri = r.insert(Uri.parse(ADD_USER_URL), cValues);
            id = Integer.parseInt(uri.toString());

        } catch (Exception ex) {
            KPUtility.logErrorMsg("addUser:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return id;

    }

    /**
     * FOR Backward API compatibility
     *
     * @param context
     * @param packageName
     * @param userId
     * @param categoryId
     */
    public static void addAppToKidsPlace(Context context, String packageName, Long userId, Long categoryId) {
        addAppToKidsPlace(context, packageName, userId, categoryId, false, false);
    }

    /**
     * @param context
     * @param packageName
     * @param userId
     * @param categoryId
     */
    public static void addAppToKidsPlace(Context context, String packageName, Long userId, Long categoryId, boolean wifiEnabled, boolean fromKPStore) {
        Cursor cursor = null;
        try {

            if (!packageName.equalsIgnoreCase(KIDSPLACE_PKG_NAME)) {
                ContentResolver r = context.getContentResolver();
                ContentValues cValues = new ContentValues();
                cValues.put(PKG_NAME_KEY, packageName);
                cValues.put(USER_ID_KEY, userId);
                cValues.put(CATEGORY_ID_KEY, categoryId);
                cValues.put(WIFI_ENABLED, wifiEnabled);
                cValues.put(INSTALLED_FROM_KP_STORE, fromKPStore);
                r.insert(Uri.parse(CONTENT_ADD_PKG_TO_KIDSPLACE), cValues);
            }
        } catch (Exception ex) {
            KPUtility.logErrorMsg("addAppToKidsPlace:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * @param context
     * @param packageName
     * @param userIds
     */
    public static void removeAppFromKidsPlace(Context context, String packageName, String[] userIds) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            r.delete(Uri.parse(CONTENT_REMOVE_PKG_FROM_KIDSPALCE), packageName, userIds);
        } catch (Exception ex) {
            KPUtility.logErrorMsg("removeAppFromKidsPlace:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * @param context
     * @return
     */
    public static Cursor getAllCategories(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_ALL_CATEGORIES_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getAllCategories:", TAG, ex);

        }

        return cursor;

    }

    /**
     * @param context
     * @return
     */
    public static int addCategory(Context context, String categoryName) {
        int catId = 0;
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            ContentValues cValues = new ContentValues();
            cValues.put(CATEGORY_NAME_KEY, categoryName);
            Uri uri = r.insert(Uri.parse(ADD_CATEGORY_URL), cValues);
            catId = Integer.parseInt(uri.toString());

        } catch (Exception ex) {
            KPUtility.logErrorMsg("addAppToKidsPlace:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return catId;

    }

    /**
     * @param context
     * @return
     */
    public static Cursor getAllPrefernces(Context context) {

        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_ALL_PREFERENCES_URL), null,
                    null, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getAllPrefernces:", TAG, ex);

        }

        return cursor;

    }


    /**
     * @param context
     * @return
     */
    public static void savePrefernce(Context context, String key, String value, String type) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            ContentValues cValues = new ContentValues();
            cValues.put(SETTING_KEY, key);
            cValues.put(SETTING_VALUE, value);
            cValues.put(SETTING_TYPE, type);
            r.update(Uri.parse(SAVE_PREFERENCE_URL), cValues, null, null);

        } catch (Exception ex) {
            KPUtility.logErrorMsg("addAppToKidsPlace:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }


    }


    private static void logMsg(String messgae, String tag) {
        if (DEBUG_MODE)
            Log.v(tag, messgae);
    }

    private static void logErrorMsg(String messgae, String tag) {
        if (LOGGING_ERR) {
            Log.e(tag, messgae);
        }
    }

    private static void logErrorMsg(String messgae, String tag,
                                    Throwable throwable) {
        if (LOGGING_ERR) {
            logErrorMsg(messgae, TAG);
            StackTraceElement[] arrayOfStackTraceElement = throwable
                    .getStackTrace();
            int stackLength = arrayOfStackTraceElement.length;
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("\nException Message:"
                    + throwable.getMessage() + "\n");

            for (int i = 0; i < stackLength; i++) {
                stringBuilder.append("Stack Trace Metadata:" + "\n");
                stringBuilder.append(arrayOfStackTraceElement[i].getClassName()
                        + "::");
                stringBuilder.append(arrayOfStackTraceElement[i]
                        .getMethodName() + "::");
                stringBuilder.append(arrayOfStackTraceElement[i]
                        .getLineNumber() + "::");
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            stringBuilder.append("\nRaw Stack Trace:" + sw.toString() + "\n*******END OF ERROR****\n");
            logErrorMsg(stringBuilder.toString(), TAG);
        }
    }


    private static String getMarketURL(boolean forExternalUse) {
        String appGoogleURI = "market://details?id=com.kiddoware.kidsplace"
                + "&referrer=utm_source%3Dkp_sdk%26utm_medium%3Dandroid_app%26utm_term%3Dkp_sdk%26utm_campaign%3Dkp_sdk";//Android Market;
        if (forExternalUse) {
            // used for sending link outside of android phone - for social
            // sharing
            appGoogleURI = "https://market.android.com/details?id=com.kiddoware.kidsplace";// Android
            // market
        }
        String appAmazonURI = "http://www.amazon.com/gp/mas/dl/android?p=com.kiddoware.kidsplace";// amazon market
        String appSamsungURI = "samsungapps://ProductDetail/com.kiddoware.kidsplace";//amazon market
        String appNookURI = "samsungapps://ProductDetail/com.kiddoware.kidsplace";//amazon market
        String appSocioURI = appGoogleURI;//Soc.io market
        String vodafoneURI = "samsungapps://ProductDetail/com.kiddoware.kidsplace";//amazon market
        String verizonURI = "http://mall.soc.io/MyApps/1003281811";//Soc.io market

        String appURI = appGoogleURI; // default to android
        if (KPUtility.APP_MARKET == KPUtility.AMAZON_MARKET) {
            appURI = appAmazonURI;
        } else if (KPUtility.APP_MARKET == KPUtility.SAMSUNG_MARKET) {
            appURI = appSamsungURI;
        } else if (KPUtility.APP_MARKET == KPUtility.NOOK_MARKET) {
            appURI = appNookURI;
        } else if (KPUtility.APP_MARKET == KPUtility.SOCIO_MARKET) {
            appURI = appSocioURI;
        } else if (KPUtility.APP_MARKET == KPUtility.VODAFONE_MARKET) {
            appURI = vodafoneURI;
        } else if (KPUtility.APP_MARKET == KPUtility.VERIZON_MARKET) {
            appURI = verizonURI;
        }
        return appURI;
    }

    // check if specified package exists or not and returns package info if
    // found otherwise nu,,
    private static PackageInfo getPackageInfo(String targetPackage,
                                              final Context mContext) {
        PackageInfo packageInfo = null;
        try {
            // check if package exists
            packageInfo = mContext.getPackageManager().getPackageInfo(
                    targetPackage, PackageManager.GET_META_DATA);
            KPUtility.logMsg(targetPackage + " exists", TAG);

        } catch (NameNotFoundException nameNotFoundEx) {
            KPUtility.logMsg(targetPackage + "does not exists", TAG);
            // package does not exists
            packageInfo = null;
        } catch (Exception ex) {
            // DO Nothing
        }
        return packageInfo;
    }

    private static void showKPInstallDialog(final Activity activity, boolean upgrade) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.kpsdk_install_dialog, null, false);

        TextView title = (TextView) view.findViewById(android.R.id.text1);
        TextView description = (TextView) view.findViewById(android.R.id.text2);

        if (upgrade) {
            title.setText(getString(KIDSPLACE_UPGRADE_TITLE));
            description.setText(getString(KIDSPLACE_UPGRADE_MSG));
        } else {
            title.setText(getString(KIDSPLACE_INSTALL_TITLE));
            description.setText(getString(KIDSPLACE_INSTALL_MSG));
        }

        builder.setView(view);

        builder.setCancelable(true);
        builder.setPositiveButton(getString(KIDSPLACE_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        accept(activity);

                    }
                });
        builder.setNegativeButton(getString(KIDSPLACE_NO),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();

    }

    private static void allowRestrictAppDialog(final Activity activity, final String packageName,
                                               final int action, String reasonForRequest) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(KIDSPLACE_WHITELIST_TITLE));
        builder.setMessage(getString(KIDSPLACE_WHITELIST_MSG));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(KIDSPLACE_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        accept(activity);
                        if (action == ADD_ACTION) {
                            addToWhiteList(activity, packageName);
                        } else if (action == REMOVE_ACTION) {
                            removeFromWhiteList(activity, packageName);
                        }

                    }
                });
        builder.setNegativeButton(getString(KIDSPLACE_NO),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();

    }

    public static void addToWhiteList(Context context, String packageName) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            ContentValues cValues = new ContentValues();
            cValues.put(PKG_NAME_KEY, packageName);
            r.insert(Uri.parse(CONTENT_ADD_PKG_TO_WHITELIST_URL), cValues);
        } catch (Exception ex) {
            KPUtility.logErrorMsg("getPinHint:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public static void removeFromWhiteList(Context context, String packageName) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            r.delete(Uri.parse(CONTENT_REMOVE_PKG_FROM_WHITELIST_URL), packageName, null);
        } catch (Exception ex) {
            KPUtility.logErrorMsg("getPinHint:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public static void addToBlackList(Context context, String packageName) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            r.delete(Uri.parse(CONTENT_REMOVE_PKG_FROM_WHITELIST_URL), packageName, null);
        } catch (Exception ex) {
            KPUtility.logErrorMsg("getPinHint:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public static void removeFromBlackList(Context context, String packageName) {
        Cursor cursor = null;
        try {
            ContentResolver r = context.getContentResolver();
            ContentValues cValues = new ContentValues();
            cValues.put(PKG_NAME_KEY, packageName);
            r.insert(Uri.parse(CONTENT_ADD_PKG_TO_WHITELIST_URL), cValues);
        } catch (Exception ex) {
            KPUtility.logErrorMsg("getPinHint:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * Returns the package name of current running app from Kids Place.
     *
     * @param context: Android context object
     * @return: Returns the package name of current running app from Kids Place. .
     */
    public static String getCurrentRunningApp(Context context) {
        String value = null;
        Cursor cursor = null;

        try {
            ContentResolver r = context.getContentResolver();
            cursor = r.query(Uri.parse(GET_CURRENT_RUNNING_APP_URL), null,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                // Let activity manage the cursor
                logMsg("cursor.getCount()=" + cursor.getCount(), TAG);

                // Get value from content provider
                int currentRunningAppIndex = cursor.getColumnIndexOrThrow(CURRENT_RUNNING_APP);

                cursor.moveToFirst();
                value = cursor.getString(currentRunningAppIndex);
            }

        } catch (Exception ex) {
            KPUtility.logErrorMsg("getCurrentRunningApp:", TAG, ex);

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return value;
    }

    private static void accept(Activity activity) {
        try {
            if (APP_MARKET == NOOK_MARKET) {
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction("com.bn.sdk.shop.details");
                i.putExtra("product_details_ean", "2940043908025");//EAN for Nook
                activity.startActivity(i);

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(KPUtility.getMarketURL(false)));
                activity.startActivity(intent);
            }

        } catch (Exception ex) {

        }
    }

    private static String getString(String defMsg) {
        if (defMsg == null) {
            defMsg = "";
        }
        return defMsg;
    }

    public static void verifyPin(FragmentManager manager, PinDialogFragment.ValidationListener listener) {
        PinDialogFragment dialogFragment = PinDialogFragment.newInstance(listener);
        dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.KPSDKFullScreenDialog);
        dialogFragment.show(manager, null);
    }

    public static class PinDialogFragment extends DialogFragment implements View.OnClickListener {
        private static final String DELETE_TAG = "D";
        private static final String ENTER_TAG = "E";

        private TextView mDescriptionTextView;
        private String pin;

        private ValidationListener listener;

        public static PinDialogFragment newInstance(ValidationListener listener) {
            PinDialogFragment fragment = new PinDialogFragment();

            if (listener == null) {
                throw new IllegalStateException("validation listener null");
            }

            fragment.listener = listener;
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.kpsdk_pin_dialog, null, false);
            View pinGroup = view.findViewById(R.id.kpsdk_pin_group);

            mDescriptionTextView = (TextView) view.findViewById(R.id.kpsdk_pin_txt);
            mDescriptionTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            setClickListeners(pinGroup);
            builder.setView(view);
            return builder.create();
        }

        private void setClickListeners(View view) {
            if (view instanceof Button) {
                view.setOnClickListener(this);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setClickListeners(viewGroup.getChildAt(i));
                }
            }

        }

        private boolean validatePin() {
            return KPUtility.validatePin(getActivity(), pin);
        }

        private void unlock() {
            if (!KPUtility.isKidsPlaceRunning(getActivity())) {
                KPUtility.handleKPIntegration(getActivity(), KPUtility.GOOGLE_MARKET);
                return;
            }
            if (validatePin()) {
                listener.onValidate(this, true);
            } else {
                mDescriptionTextView.setText(R.string.kpsdk_e_invalid_pin);
                listener.onValidate(this, false);
            }
        }

        @Override
        public void onClick(View view) {

            if (DELETE_TAG.equals(view.getTag())) {
                if (pin != null && pin.length() > 0) {
                    pin = pin.substring(0, pin.length() - 1);
                    mDescriptionTextView.setText(pin);
                }
            } else if (ENTER_TAG.equals(view.getTag())) {
                unlock();
            } else {
                Button button = (Button) view;
                try {
                    int number = Integer.parseInt(button.getText().toString());

                    mDescriptionTextView.append("" + number);

                    pin = mDescriptionTextView.getText().toString();


                } catch (NumberFormatException e) {
                    if (DELETE_TAG.equals(view.getTag())) {
                        if (pin != null && pin.length() > 0) {
                            pin = pin.substring(0, pin.length() - 1);
                            mDescriptionTextView.setText(pin);
                        }
                    } else if (ENTER_TAG.equals(view.getTag())) {
                        unlock();
                    }
                }
            }
        }

        public static interface ValidationListener {
            public void onValidate(PinDialogFragment fragment, boolean success);
        }
    }

}
