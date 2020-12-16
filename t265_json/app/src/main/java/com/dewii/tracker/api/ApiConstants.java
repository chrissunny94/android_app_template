package com.dewii.tracker.api;

public final class ApiConstants {
    private ApiConstants() {

    }

    public static final class RequestKeys {
        private RequestKeys() {

        }

        public static final String CALLING_NUMBER = "calling_number";
        public static final String CALL_DURATION = "call_duration";
        public static final String MAP_NAME = "map_name";
        public static final String MAP_CREATED_BY = "who_is_creating_the_map";
        public static final String LAST_LOCATION = "last_location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String TIME_BASE_TRIGGER = "time_based_trigger";
        public static final String DISTANCE_BASED_TRIGGER = "distance_based_trigger";
        public static final String IP_ADR_RPI = "ip_address_rpi";
    }
}
