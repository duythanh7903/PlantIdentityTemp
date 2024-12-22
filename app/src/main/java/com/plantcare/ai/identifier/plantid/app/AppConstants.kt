package com.plantcare.ai.identifier.plantid.app

object AppConstants {
    var isShowAds = false
    internal const val KEY_CONFIRM_CONSENT = "key_confirm_consent"
    internal const val KEY_IS_USER_GLOBAL = "key_is_user_global"

    const val KEY_SET_SHOW_DIALOG_RATE: String = "key_set_show_dialod_rate"
    const val DEFAULT_TIME_SPLASH: Long = 6500          //TODO /* Dont change this value*/
    const val DEFAULT_LIMIT_TIME_SPLASH: Long = 5000   //TODO /* Dont change this value*/

    internal const val KEY_TRACKING_SCREEN_FROM = "key_tracking_screen_from"
    internal const val LINK_APP = ""
    internal const val LINK_PRIVACY_POLICY = ""
    internal const val LINK_PLAY_STORE = ""

    internal const val KEY_LANGUAGE = "KEY_LANGUAGE"
    internal const val KEY_FIRST_INSTALL_APP = "KEY_FIRST_INSTALL_APP"
    internal const val KEY_SETTING = "KEY_SETTING"
    internal const val KEY_RESULT_SEARCH = "KEY_RESULT_SEARCH"
    internal const val KEY_COUNT_NOTI_PERMISSION_DENIED = "KEY_COUNT_NOTI_PERMISSION_DENIED"
    internal const val KEY_COUNT_CAMERA_PERMISSION_DENIED = "KEY_COUNT_CAMERA_PERMISSION_DENIED"
    internal const val KEY_COUNT_LOCATION_PERMISSION_DENIED = "KEY_COUNT_LOCATION_PERMISSION_DENIED"
    internal const val KEY_COUNT_STORAGE_PERMISSION_DENIED = "KEY_COUNT_STORAGE_PERMISSION_DENIED"
    internal const val KEY_SECRET_KEY = "KEY_SECRET_KEY"

    internal const val MESSAGE_RES_PLANT_ERROR = "PLANT_NOT_FOUND"
    internal const val MESSAGE_RES_PLANT_SUCCESS = "SUCCESS"

    internal const val NAMED_PLANT = "PLANT_DOMAIN"
    internal const val NAMED_LOCATION = "LOCATION_DOMAIN"
    internal const val NAMED_WEATHER = "WEATHER_DOMAIN"

    internal const val KEY_PLANT_DISEASE: String = "KEY_PLANT_DISEASE"
    internal const val KEY_PLANT: String = "KEY_PLANT"
    internal const val KEY_HISTORY: String = "KEY_HISTORY"
    internal const val KEY_DATA_TO_RING_SCREEN = "KEY_DATA_TO_RING_SCREEN"

    internal const val SOUND_DEFAULT = 0
    internal const val SOUND_SILENT = -1

    internal const val LOCATION_CITY_NAME = "LOCATION_CITY_NAME"
    internal const val LOCATION_WEATHER_INFO = "LOCATION_WEATHER_INFO"

    internal const val ID_SUB = "sub_yearly_plantid_25.99"
    internal const val ID_SUB_30_TIMES = "plantid_2.99_30scans"

    internal const val KEY_SCAN_PLANT = "KEY_SCAN_PLANT"
    internal const val KEY_TYPE_SUB = "KEY_TYPE_SUB"
    internal const val TYPE_SUB_YEARLY = 0
    internal const val TYPE_SUB_30_SCANS = 1
    internal const val DEFAULT_VALUE_BY_DOCUMENT = 3
    internal const val KEY_OBJ_IAP = "KEY_OBJ_IAP"
}