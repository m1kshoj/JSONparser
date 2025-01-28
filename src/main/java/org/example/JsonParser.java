package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonParser {

    public static class TicketsData {
        public List<Ticket> tickets;
    }

    public static class Ticket {
        @JsonProperty("origin")
        public String origin;

        @JsonProperty("origin_name")
        public String originName;

        @JsonProperty("destination")
        public String destination;

        @JsonProperty("destination_name")
        public String destinationName;

        @JsonProperty("departure_date")
        public String departureDate;

        @JsonProperty("departure_time")
        public String departureTime;

        @JsonProperty("arrival_date")
        public String arrivalDate;

        @JsonProperty("arrival_time")
        public String arrivalTime;

        @JsonProperty("carrier")
        public String carrier;

        @JsonProperty("stops")
        public int stops;

        @JsonProperty("price")
        public int price;

        public long getFlightTime() {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
                Date departure = format.parse(departureDate + " " + departureTime);
                Date arrival = format.parse(arrivalDate + " " + arrivalTime);
                return (arrival.getTime() - departure.getTime()) / (60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    public static void main(String[] args) {
        try {
            String filePath;
            ObjectMapper mapper = new ObjectMapper();

            if (isWindows()) {
                System.out.println("Путь к tickets.json:");
                Scanner scanner = new Scanner(System.in, "UTF-8");
                filePath = scanner.nextLine().trim();
                scanner.close();
            } else {
                if (args.length == 0) {
                    System.out.println("linux: java -jar program.jar /путь/к/tickets.json");
                    return;
                }
                filePath = args[0];
            }

            File inputFile = new File(filePath.replace("\\", "/"));

            if (!inputFile.exists()) {
                System.out.println("Файл не найден: " + inputFile.getAbsolutePath());
                return;
            }

            TicketsData data = mapper.readValue(inputFile, TicketsData.class);

            if (!inputFile.exists()) {
                System.out.println("Файл не найден: " + inputFile.getAbsolutePath());
                return;
            }

            List<Ticket> filtered = new ArrayList<>();
            for (Ticket t : data.tickets) {
                if (t.originName.equals("Владивосток") && t.destinationName.equals("Тель-Авив")) {
                    filtered.add(t);
                }
            }

            HashMap<String, Long> minTimes = new HashMap<>();
            for (Ticket t : filtered) {
                long time = t.getFlightTime();
                if (!minTimes.containsKey(t.carrier) || time < minTimes.get(t.carrier)) {
                    minTimes.put(t.carrier, time);
                }
            }

            System.out.println("Минимальное время полета в минутах:");
            for (Map.Entry<String, Long> entry : minTimes.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }

            List<Integer> prices = new ArrayList<>();
            for (Ticket t : filtered) {
                prices.add(t.price);
            }
            Collections.sort(prices);

            if (prices.size() == 0) {
                System.out.println("\nНет данных.");
                return;
            }

            double median;
            int middle = prices.size() / 2;
            if (prices.size() % 2 == 0) {
                median = (prices.get(middle-1) + prices.get(middle)) / 2.0;
            } else {
                median = prices.get(middle);
            }

            double sum = 0;
            for (int price : prices) {
                sum += price;
            }
            double average = sum / prices.size();

            System.out.println("\nСредняя цена - " + average);
            System.out.println("Медиана - " + median);
            System.out.println("Разница - " + (average - median));

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка:");
            e.printStackTrace();
        }
    }
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}