package application.ui;

import application.service.BookingHistoryService;
import application.service.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingsScreenBuilder {
    
    private final BookingsEventHandler eventHandler;
    private final NumberFormat currencyFormat;
    
    public BookingsScreenBuilder(BookingsEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    }
    
    /**
     * Setup the bookings screen content (refactored from setupBookingsScreen)
     */
    public void setupBookingsContent(VBox bookingsContent) {
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null");
            return;
        }

        bookingsContent.getChildren().clear();
        bookingsContent.setSpacing(15);

        if (!UserSession.getInstance().isLoggedIn()) {
            VBox loginPrompt = createLoginPrompt();
            bookingsContent.getChildren().add(loginPrompt);
            return;
        }

        List<BookingHistoryService.BookingHistory> bookings = BookingHistoryService.getUserBookings();

        if (bookings.isEmpty()) {
            VBox noBookingsMessage = createNoBookingsMessage();
            bookingsContent.getChildren().add(noBookingsMessage);
        } else {
            for (BookingHistoryService.BookingHistory booking : bookings) {
                VBox bookingCard = createBookingCard(booking);
                bookingsContent.getChildren().add(bookingCard);
            }
        }
    }
    
    /**
     * Create login prompt (moved from showLoginPrompt in HomeController)
     */
    private VBox createLoginPrompt() {
        VBox loginPrompt = new VBox(20);
        loginPrompt.setAlignment(Pos.CENTER);
        loginPrompt.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        Label icon = new Label("🔒");
        icon.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("Login Required");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label message = new Label("Please log in to view your booking history");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        Button loginButton = new Button("Go to Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 20;");
        loginButton.setOnAction(e -> eventHandler.onGoToLogin());
        
        loginPrompt.getChildren().addAll(icon, title, message, loginButton);
        return loginPrompt;
    }
    
    /**
     * Create no bookings message (moved from showNoBookingsMessage in HomeController)
     */
    private VBox createNoBookingsMessage() {
        VBox noBookingsBox = new VBox(20);
        noBookingsBox.setAlignment(Pos.CENTER);
        noBookingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                              "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        Label icon = new Label("✈️");
        icon.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("No Bookings Yet");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label message = new Label("Your travel bookings and reservations will appear here after you make a booking.");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        message.setMaxWidth(300);
        
        Button exploreButton = new Button("Explore Flights");
        exploreButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 20;");
        exploreButton.setOnAction(e -> eventHandler.onExploreFlights());
        
        noBookingsBox.getChildren().addAll(icon, title, message, exploreButton);
        return noBookingsBox;
    }
    
    /**
     * Create booking card (moved from createBookingCard in HomeController)
     */
    private VBox createBookingCard(BookingHistoryService.BookingHistory booking) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Header with booking reference and status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox refBox = new VBox(2);
        Label refLabel = new Label("Booking Reference");
        refLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label refValue = new Label(booking.getBookingReference());
        refValue.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        refBox.getChildren().addAll(refLabel, refValue);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusBadge = createStatusBadge(booking.getBookingStatus(), booking.getPaymentStatus());
        
        header.getChildren().addAll(refBox, spacer, statusBadge);
        
        // Flight info
        HBox flightInfo = new HBox();
        flightInfo.setAlignment(Pos.CENTER);
        flightInfo.setSpacing(15);
        flightInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        VBox originBox = new VBox(3);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(booking.getOrigin());
        originCode.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label depTime = new Label(booking.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
        depTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        originBox.getChildren().addAll(originCode, depTime);
        
        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        
        VBox destBox = new VBox(3);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(booking.getDestination());
        destCode.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label arrTime = new Label(booking.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        destBox.getChildren().addAll(destCode, arrTime);
        
        flightInfo.getChildren().addAll(originBox, arrow, destBox);
        
        // Details
        VBox details = new VBox(8);
        details.getChildren().addAll(
            createDetailRow("✈️ Flight", booking.getFlightNo() + " • " + booking.getAirlineName()),
            createDetailRow("📅 Date", booking.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
            createDetailRow("💺 Seat", booking.getSeatNumber()),
            createDetailRow("💳 Payment", booking.getPaymentMethod() + " • " + currencyFormat.format(booking.getAmount()))
        );
        
        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                           "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15;");
        viewButton.setOnAction(e -> eventHandler.onViewBookingDetails(booking));
        
        Button downloadButton = new Button("Download Ticket");
        downloadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                               "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15;");
        downloadButton.setOnAction(e -> eventHandler.onDownloadTicket(booking));
        
        actions.getChildren().addAll(viewButton, downloadButton);
        
        card.getChildren().addAll(header, flightInfo, details, actions);
        return card;
    }
    
    /**
     * Create status badge (moved from createStatusBadge in HomeController)
     */
    private Label createStatusBadge(String bookingStatus, String paymentStatus) {
        String text = "";
        String style = "";

        if ("confirmed".equals(bookingStatus) && "paid".equals(paymentStatus)) {
            text = "✅ Confirmed";
            style = "-fx-background-color: #E8F5E8; -fx-text-fill: #4CAF50;";
        } else if ("pending".equals(paymentStatus)) {
            text = "⏳ Pending";
            style = "-fx-background-color: #FFF3E0; -fx-text-fill: #FF9800;";
        } else if ("failed".equals(paymentStatus)) {
            text = "❌ Failed";
            style = "-fx-background-color: #FFEBEE; -fx-text-fill: #F44336;";
        } else {
            text = "📋 " + bookingStatus;
            style = "-fx-background-color: #E3F2FD; -fx-text-fill: #2196F3;";
        }

        Label badge = new Label(text);
        badge.setStyle(style + " -fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-padding: 5 10; -fx-background-radius: 12;");

        return badge;
    }
    
    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        labelText.setPrefWidth(80);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        
        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    /**
     * Interface for handling bookings screen events
     */
    public interface BookingsEventHandler {
        void onGoToLogin();
        void onExploreFlights();
        void onViewBookingDetails(BookingHistoryService.BookingHistory booking);
        void onDownloadTicket(BookingHistoryService.BookingHistory booking);
    }
}