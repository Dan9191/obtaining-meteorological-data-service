package org.example.utils;

import java.util.List;

public class ApplicationConstants {

    /**
     * Обозначение ключа яндекс.
     */
    public static final String ACCESS_KEY = "access_key";

    /**
     * Заголовок для ключа доступа яндекс.
     */
    public static final String  YANDEX_HEADER = "X-Yandex-Weather-Key";

    /**
     * Обозначение широты.
     */
    public static final String LAT = "lat";

    /**
     * Обозначение долготы.
     */
    public static final String LON = "lon";

    /**
     * Варианты позитивного ответа.
     */
    public static final List<String> POSITIVE_ANSWER = List.of("lf", "l", "da", "d", "t", "1", "yes", "y");

    /**
     * Паттерн для проверки широты.
     */
    public static final String LATITUDE_PATTERN = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$";

    /**
     * Паттерн для проверки долготы.
     */
    public static final String LONGITUDE_PATTERN = "^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
}
