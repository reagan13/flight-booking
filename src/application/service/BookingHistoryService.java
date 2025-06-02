package application.service;

import application.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryService {
    
    public static class BookingHistory {
        private int bookingId;
        private String bookingReference;
        private String flightNo;
        private String airlineName;
        private String origin;
        private String destination;
        private LocalDateTime departure;
        private LocalDateTime arrival;
        private String seatNumber;
        private String bookingStatus;
        private LocalDateTime bookingDate;
        private double amount;
        private String paymentStatus;
        private String paymentMethod;
        
        // Constructor
        public BookingHistory(int bookingId, String bookingReference, String flightNo, 
                            String airlineName, String origin, String destination,
                            LocalDateTime departure, LocalDateTime arrival, String seatNumber,
                            String bookingStatus, LocalDateTime bookingDate, double amount,
                            String paymentStatus, String paymentMethod) {
            this.bookingId = bookingId;
            this.bookingReference = bookingReference;
            this.flightNo = flightNo;
            this.airlineName = airlineName;
            this.origin = origin;
            this.destination = destination;
            this.departure = departure;
            this.arrival = arrival;
            this.seatNumber = seatNumber;
            this.bookingStatus = bookingStatus;
            this.bookingDate = bookingDate;
            this.amount = amount;
            this.paymentStatus = paymentStatus;
            this.paymentMethod = paymentMethod;
        }
        
        // Getters
        public int getBookingId() { return bookingId; }
        public String getBookingReference() { return bookingReference; }
        public String getFlightNo() { return flightNo; }
        public String getAirlineName() { return airlineName; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public LocalDateTime getDeparture() { return departure; }
        public LocalDateTime getArrival() { return arrival; }
        public String getSeatNumber() { return seatNumber; }
        public String getBookingStatus() { return bookingStatus; }
        public LocalDateTime getBookingDate() { return bookingDate; }
        public double getAmount() { return amount; }
        public String getPaymentStatus() { return paymentStatus; }
        public String getPaymentMethod() { return paymentMethod; }
    }
    
    public static List<BookingHistory> getUserBookings() {
        List<BookingHistory> bookings = new ArrayList<>();

        if (!UserSession.getInstance().isLoggedIn()) {
            return bookings; // Return empty list for guest users
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            // Corrected SQL with actual column names from your DatabaseConnection
            String sql = "SELECT b.id, b.booking_reference, b.seat_number, b.status, b.booking_date, " +
                    "f.flight_number, f.airline_name, f.origin, f.destination, f.departure_time, f.arrival_time, " +
                    "f.base_price, " +
                    "t.total_amount, t.payment_status, t.payment_method " +
                    "FROM bookings b " +
                    "LEFT JOIN flights f ON b.flight_id = f.id " +
                    "LEFT JOIN transactions t ON b.id = t.booking_id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.booking_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                BookingHistory booking = new BookingHistory(
                        rs.getInt("id"),
                        rs.getString("booking_reference"),
                        rs.getString("flight_number"),
                        rs.getString("airline_name"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getTimestamp("departure_time").toLocalDateTime(), 
                        rs.getTimestamp("arrival_time").toLocalDateTime(), 
                        rs.getString("seat_number"),
                        rs.getString("status"),
                        rs.getTimestamp("booking_date").toLocalDateTime(),
                        rs.getDouble("total_amount") != 0 ? rs.getDouble("total_amount") : rs.getDouble("base_price"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user bookings: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }
    
}