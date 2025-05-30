package application.service;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONObject;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeAPIService {
    
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    
    // Time zones data with TimeAPI.io area identifiers
    public static final String[][] TIME_ZONES = {
        {"🇯🇵", "Japan", "Asia/Tokyo", "Tokyo"},
        {"🇨🇳", "China", "Asia/Shanghai", "Beijing"},
        {"🇫🇷", "France", "Europe/Paris", "Paris"},
        {"🇺🇸", "USA", "America/New_York", "New York"},
        {"🇮🇳", "India", "Asia/Kolkata", "Mumbai"}
    };
    
    /**
     * Fetch time data from TimeAPI.io asynchronously
     */
    public static CompletableFuture<TimeData> fetchTimeAsync(String timeZone) {
        return CompletableFuture.supplyAsync(() -> fetchTimeFromAPI(timeZone), EXECUTOR);
    }
    
    /**
     * Fetch time from TimeAPI.io synchronously
     */
    private static TimeData fetchTimeFromAPI(String timeZone) {
        try {
            String apiUrl = "https://timeapi.io/api/Time/current/zone?timeZone=" + timeZone;
            
            URL url = new URL(apiUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "JetSetGO-App/1.0");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                // Parse JSON response using org.json instead of Gson
                JSONObject jsonResponse = new JSONObject(response.toString());
                
                return parseTimeAPIResponse(jsonResponse, timeZone);
                
            }
        } catch (IOException e) {
            System.err.println("Error fetching time from API for " + timeZone + ": " + e.getMessage());
            return createFallbackTimeData(timeZone);
        } catch (Exception e) {
            System.err.println("Error parsing API response for " + timeZone + ": " + e.getMessage());
            return createFallbackTimeData(timeZone);
        }
    }
    
    /**
     * Parse TimeAPI.io JSON response
     */
    private static TimeData parseTimeAPIResponse(JSONObject jsonResponse, String timeZone) {
        try {
            String dateTime = jsonResponse.getString("dateTime");
            String date = jsonResponse.getString("date");
            String time = jsonResponse.getString("time");
            
            // Get timezone offset if available
            String timeZoneName = jsonResponse.has("timeZone") ? 
                jsonResponse.getString("timeZone") : timeZone;
            
            // Get day of week if available
            String dayOfWeek = jsonResponse.has("dayOfWeek") ? 
                jsonResponse.getString("dayOfWeek") : "";
            
            return new TimeData(dateTime, date, time, timeZoneName, dayOfWeek, true);
            
        } catch (Exception e) {
            System.err.println("Error parsing time data: " + e.getMessage());
            return createFallbackTimeData(timeZone);
        }
    }
    
    /**
     * Create fallback time data using system time
     */
    private static TimeData createFallbackTimeData(String timeZone) {
        try {
            ZonedDateTime zonedTime = ZonedDateTime.now(ZoneId.of(timeZone));
            
            String dateTime = zonedTime.toString();
            String date = zonedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = zonedTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String dayOfWeek = zonedTime.format(DateTimeFormatter.ofPattern("EEEE"));
            
            return new TimeData(dateTime, date, time, timeZone, dayOfWeek, false);
            
        } catch (Exception e) {
            System.err.println("Error creating fallback time data: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Data class to hold time information
     */
    public static class TimeData {
        private final String dateTime;
        private final String date;
        private final String time;
        private final String timeZone;
        private final String dayOfWeek;
        private final boolean fromAPI;
        
        public TimeData(String dateTime, String date, String time, String timeZone, String dayOfWeek, boolean fromAPI) {
            this.dateTime = dateTime;
            this.date = date;
            this.time = time;
            this.timeZone = timeZone;
            this.dayOfWeek = dayOfWeek;
            this.fromAPI = fromAPI;
        }
        
        public String getDateTime() { return dateTime; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getTimeZone() { return timeZone; }
        public String getDayOfWeek() { return dayOfWeek; }
        public boolean isFromAPI() { return fromAPI; }
    }
}