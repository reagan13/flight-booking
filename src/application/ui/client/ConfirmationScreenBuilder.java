package application.ui.client;

import application.model.Flight;
import application.service.BookingService;
import application.ui.client.BookingFormScreenBuilder.BookingFormData;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConfirmationScreenBuilder {
    
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    public static VBox createConfirmationScreen(BookingService.BookingResult result, 
                                              Flight flight, 
            BookingFormData bookingData, ConfirmationEventHandler confirmationEventHandler) {

        

        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 20;");
        container.setAlignment(Pos.CENTER);

        // Success header
        VBox successHeader = createSuccessHeader(result.getBookingReference());

        // Booking details card
        VBox bookingDetails = createBookingDetailsCard(result, flight, bookingData);

        // Important information
        VBox importantInfo = createImportantInfoCard(result.getBookingReference(), bookingData.getEmail());

    
        container.getChildren().addAll(successHeader, bookingDetails, importantInfo);
        return container;
    }
    public interface ConfirmationEventHandler {
        void onNavigateToHome();
    }
    
    private static VBox createSuccessHeader(String bookingReference) {
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        header.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
            "-fx-border-color: #4CAF50; -fx-border-radius: 15; -fx-border-width: 2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);"
        );
        
        Label successIcon = new Label("🎉");
        successIcon.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Booking Confirmed!");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        Label subtitleLabel = new Label("Your flight has been successfully booked");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-text-alignment: center;");
        subtitleLabel.setWrapText(true);
        
        Label referenceLabel = new Label("Booking Reference: " + bookingReference);
        referenceLabel.setStyle(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3; " +
            "-fx-background-color: #E3F2FD; -fx-padding: 10 20; -fx-background-radius: 20;"
        );
        
        header.getChildren().addAll(successIcon, titleLabel, subtitleLabel, referenceLabel);
        return header;
    }
    
    private static VBox createBookingDetailsCard(BookingService.BookingResult result, 
                                               Flight flight, 
                                               BookingFormData bookingData) {
        VBox card = new VBox(20);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 25; " +
            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        
        // Card header
        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label headerIcon = new Label("✈️");
        headerIcon.setStyle("-fx-font-size: 20px;");
        
        Label headerTitle = new Label("Flight Details");
        headerTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        cardHeader.getChildren().addAll(headerIcon, headerTitle);
        
        // Flight route section
        VBox routeSection = createRouteSection(flight);
        
        // Details grid
        VBox detailsGrid = new VBox(12);
        detailsGrid.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        detailsGrid.getChildren().addAll(
            createDetailRow("Booking Reference", result.getBookingReference()),
            createDetailRow("Flight Number", flight.getFlightNo()),
            createDetailRow("Airline", flight.getAirlineName()),
            createDetailRow("Passenger", bookingData.getFirstName() + " " + bookingData.getLastName()),
            createDetailRow("Departure Date", flight.getDeparture().format(dateFormatter)),
            createDetailRow("Seat Assignment", "Will be assigned at check-in"),
            createDetailRow("Total Amount", currencyFormat.format(flight.getPrice()))
        );
        
        card.getChildren().addAll(cardHeader, routeSection, detailsGrid);
        return card;
    }
    
    private static VBox createRouteSection(Flight flight) {
        VBox routeSection = new VBox(10);
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 15; -fx-background-radius: 10;");
        
        HBox routeRow = new HBox();
        routeRow.setAlignment(Pos.CENTER);
        routeRow.setSpacing(20);
        
        // Origin
        VBox originBox = new VBox(3);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(flight.getOrigin());
        originCode.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label originTime = new Label(flight.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
        originTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        originBox.getChildren().addAll(originCode, originTime);
        
        // Arrow and duration
        VBox pathBox = new VBox(3);
        pathBox.setAlignment(Pos.CENTER);
        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50;");
        Label durationLabel = new Label(flight.getDuration());
        durationLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        pathBox.getChildren().addAll(arrowLabel, durationLabel);
        
        // Destination
        VBox destBox = new VBox(3);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(flight.getDestination());
        destCode.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label destTime = new Label(flight.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
        destTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        destBox.getChildren().addAll(destCode, destTime);
        
        routeRow.getChildren().addAll(originBox, pathBox, destBox);
        routeSection.getChildren().add(routeRow);
        
        return routeSection;
    }
    
    private static VBox createImportantInfoCard(String bookingReference, String email) {
        VBox infoCard = new VBox(15);
        infoCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
            "-fx-border-color: #FF9800; -fx-border-radius: 15; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label warningIcon = new Label("⚠️");
        warningIcon.setStyle("-fx-font-size: 20px;");
        
        Label headerTitle = new Label("Important Information");
        headerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");
        
        headerBox.getChildren().addAll(warningIcon, headerTitle);
        
        VBox infoList = new VBox(8);
        String[] infoItems = {
            "• Save your booking reference: " + bookingReference,
            "• Arrive at airport 2 hours before departure for domestic flights",
            "• Bring valid government-issued ID and travel documents", 
            "• Online check-in opens 24 hours before departure",
            "• Confirmation email sent to: " + email,
            "• Contact customer service for any changes or cancellations"
        };
        
        for (String item : infoItems) {
            Label infoItem = new Label(item);
            infoItem.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true;");
            infoItem.setMaxWidth(Double.MAX_VALUE);
            infoList.getChildren().add(infoItem);
        }
        
        infoCard.getChildren().addAll(headerBox, infoList);
        return infoCard;
    }
    

    private static HBox createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setPrefWidth(140);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-font-weight: bold;");
        valueText.setWrapText(true);
        valueText.setMaxWidth(200);
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
}