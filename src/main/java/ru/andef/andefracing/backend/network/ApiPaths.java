package ru.andef.andefracing.backend.network;

public class ApiPaths {
    private ApiPaths() {
    }

    // auth
    public static final String AUTH_CLIENT = "/auth/client";
    public static final String AUTH_EMPLOYEE = "/auth/employee";

    // bookings
    public static final String BOOKINGS_CLIENT = "/bookings/client";
    public static final String BOOKINGS_EMPLOYEE = "/bookings/employee/{clubId}";

    // club management
    public static final String CLUB_MANAGEMENT = "/club-management/{clubId}";
    public static final String CLUB_MANAGEMENT_GAMES = "/club-management/games";
    public static final String CLUB_MANAGEMENT_HR = "/club-management/hr/{clubId}";
    public static final String CLUB_MANAGEMENT_PHOTOS = "/club-management/photos/{clubId}";
    public static final String CLUB_MANAGEMENT_PRICES = "/club-management/prices/{clubId}";
    public static final String CLUB_MANAGEMENT_WORK_SCHEDULE = "/club-management/work-schedule/{clubId}";

    // profile
    public static final String PROFILE_CLIENT = "/profile/client";
    public static final String PROFILE_EMPLOYEE = "/profile/employee";

    // reports
    public static final String REPORTS = "/reports/{clubId}";

    // search
    public static final String SEARCH = "/search";
}
