package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Day;
import org.example.utils.PropertiesLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.utils.ApplicationConstants.*;

public class MainApplication {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);
        ObjectMapper mapper = new ObjectMapper();
        Properties properties = PropertiesLoader.loadProperties();
        String accessKey = properties.getProperty(ACCESS_KEY);
        println("Введите широту (возможное значение: число с плавающей точкой от -90 до 90)");
        String lat = readCoordinate(scanner, LAT);
        println("Введите долготу (возможное значение: число с плавающей точкой от -180 до 180)");
        String lon = readCoordinate(scanner, LON);

        // Формирование запроса.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s", lat, lon)))
                .GET()
                .setHeader(YANDEX_HEADER, accessKey)
                .build();

        // Получение ответа.
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode responseBody = mapper.readTree(response.body());
        Map<LocalDate, Day> dayMap = new HashMap<>();
        responseBody.get("forecasts").forEach(dayNode -> {
            Day day = new Day();
            day.setDate(createDate(dayNode.get("date").asText()));
            day.setTemp(dayNode.get("parts").get("day").get("temp_avg").asText());
            dayMap.put(day.getDate(), day);
        });

        // Вывод базовых данных.
        List<String> dateList = dayMap.keySet().stream().sorted().map(LocalDate::toString).collect(Collectors.toList());
        String factTemp = responseBody.get("fact").get("temp").toString();
        String today = responseBody.get("now_dt").asText().substring(0, 10);
        println("Сегодня: " + today + " . Фактическая температура: " + factTemp);
        println("Вычисление средней температуры за указанный период");
        println("Период, за который известна температура : с " + dateList.getFirst()
                + " по " + dateList.getLast());

        // Вычисление температуры за указанный период.
        println("Введите период, для вычисления средней температуры");
        println("Первый день: ");
        int firstIndex = calculateIndex(dateList, 0, scanner);
        if (dateList.size() - 1 == firstIndex) {
            println("Вы ввели последний день из известного периода.");
            println("Средняя температура в этот день: "
                    + dayMap.get(createDate(dateList.getLast())).getTemp());
        }
        println("Второй день: ");
        int secondIndex = calculateIndex(dateList, firstIndex, scanner);

        OptionalDouble averageTemp = dateList.subList(firstIndex, secondIndex + 1).stream()
                .mapToDouble(date -> Double.parseDouble(dayMap.get(createDate(date)).getTemp()))
                .average();

        averageTemp.ifPresent(it -> println("Средняя температура за указанный период : " + it));

        println("Желаете ли вы получить полную информацию о погоде(yes/no)?");
        String answer = scanner.next();
        if (POSITIVE_ANSWER.contains(answer.toLowerCase())) println(responseBody.toPrettyString());
    }

    private static void println(String s) {
        System.out.println(s);
    }

    /**
     * Принимаем дату и проводим валидацию на ее наличие и порядок.
     *
     * @param dateList      Список дат.
     * @param previousIndex Index предыдущей даты.
     * @param scanner       Сканнер.
     * @return Index даты
     */
    private static int calculateIndex(List<String> dateList, int previousIndex, Scanner scanner) {
        String day = scanner.next();
        List<String> availableDays = dateList.subList(previousIndex, dateList.size());
        if (!dateList.contains(day)) {
            println("Такой день недоступен.");
            println("Период, за который известна температура : с " + availableDays.getFirst()
                    + " по " + availableDays.getLast());
            println("Введите день: ");
            return calculateIndex(dateList, previousIndex, scanner);
        }
        int calculatedIndex = dateList.indexOf(day);
        if (previousIndex > calculatedIndex) {
            println("Второй день должен быть не раньше первого дня.");
            println("Период, за который известна температура : с " + availableDays.getFirst()
                    + " по " + availableDays.getLast());
            println("Введите день: ");
            return calculateIndex(dateList, previousIndex, scanner);
        }
        return calculatedIndex;
    }

    /**
     * Парсер даты.
     *
     * @param date Строчное представление даты.
     * @return результат в LocalDate формате.
     */
    private static LocalDate createDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Чтение и валидация координат для поиска температуры
     *
     * @param scanner Сканнер.
     * @param type Тип координаты.
     * @return координата широты или долготы.
     */
    private static String readCoordinate(Scanner scanner, String type) {
        String coordinate = scanner.next();
        if (type.equals("lat")) {
            if (Pattern.matches(LATITUDE_PATTERN, coordinate)) {
                return coordinate;
            } else {
                println("Некорректное значение");
                println("Введите широту (возможное значение: число с плавающей точкой от -90 до 90)");
                return readCoordinate(scanner, type);
            }
        } else if (type.equals("lon")) {
            if (Pattern.matches(LONGITUDE_PATTERN, coordinate)) {
                return coordinate;
            } else {
                println("Некорректное значение");
                println("Введите долготу (возможное значение: число с плавающей точкой от -180 до 180)");
                return readCoordinate(scanner, type);
            }
        }
        println("Неизвестный тип данных");
        throw new IllegalArgumentException("Неизвестный тип данных");
    }
}
