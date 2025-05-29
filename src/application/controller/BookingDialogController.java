package application.controller;

import application.model.Flight;
import application.model.User;
import application.service.FlightService;
import application.service.UserSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BookingDialogController {
    
    private FlightService flightService;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormatter;
    
    public BookingDialogController(FlightService flightService) {
        this.flightService = flightService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    }
    
    
    
    public void showBookingForm(Flight flight) {
        try {
            System.out.println("=== DEBUG: Starting showBookingForm ===");
            System.out.println("Flight ID: " + flight.getId());
            System.out.println("Flight Number: " + flight.getFlightNo());

            // Create dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Book Flight - " + flight.getFlightNo());
            dialog.setHeaderText("Complete Your Booking");

            // Create main content container
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: white;"); // Ensure background

            try {
                // Build form sections
                addFlightDetailsSection(mainContent, flight);
                addPassengerDetailsSection(mainContent, flight, dialog);
                addBookingSummarySection(mainContent, flight);
                addTravelClassSection(mainContent);
                addSeatPreferenceSection(mainContent);
                addBaggageSection(mainContent);
                addImportantNotesSection(mainContent);

                System.out.println("=== DEBUG: All sections added successfully ===");

            } catch (Exception e) {
                System.err.println("=== ERROR: Failed to build form sections ===");
                e.printStackTrace();

                // Show error dialog
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Failed to Load Booking Form");
                errorAlert.setContentText("An error occurred while loading the booking form: " + e.getMessage());
                errorAlert.showAndWait();
                return;
            }

            // FIXED ScrollPane - This WILL work
            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(false); // CRITICAL for scrolling
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: white;");

            // Set fixed dialog size to force scrolling
            dialog.getDialogPane().setPrefSize(650, 500);
            dialog.getDialogPane().setMaxSize(650, 500);
            dialog.getDialogPane().setMinSize(650, 500);

            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Style buttons
            try {
                Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("Continue to Payment");
                okButton.setStyle(
                        "-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

                Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                cancelButton.setStyle("-fx-padding: 10 20;");
            } catch (Exception e) {
                System.err.println("Warning: Could not style buttons: " + e.getMessage());
            }

            System.out.println("=== DEBUG: About to show dialog ===");
            dialog.showAndWait();
            System.out.println("=== DEBUG: Dialog closed ===");

        } catch (Exception e) {
            System.err.println("=== CRITICAL ERROR: Failed to show booking form ===");
            e.printStackTrace();

            Alert criticalAlert = new Alert(Alert.AlertType.ERROR);
            criticalAlert.setTitle("Critical Error");
            criticalAlert.setHeaderText("Booking System Error");
            criticalAlert.setContentText("A critical error occurred: " + e.getMessage());
            criticalAlert.showAndWait();
        }
    }

    private void addFlightDetailsSection(VBox mainContent, Flight flight) {
        Label bookingDetailsLabel = new Label("Flight Booking Details");
        bookingDetailsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        bookingDetailsLabel.setStyle("-fx-text-fill: #1a237e;");

        GridPane flightDetailsGrid = new GridPane();
        flightDetailsGrid.setHgap(15);
        flightDetailsGrid.setVgap(8);
        flightDetailsGrid.setPadding(new Insets(15));
        flightDetailsGrid.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        int row = 0;
        flightDetailsGrid.add(new Label("Flight:"), 0, row);
        Label flightLabel = new Label(flight.getAirlineName() + " " + flight.getFlightNo());
        flightLabel.setStyle("-fx-font-weight: bold;");
        flightDetailsGrid.add(flightLabel, 1, row++);

        flightDetailsGrid.add(new Label("Route:"), 0, row);
        Label routeLabel = new Label(flight.getOrigin() + " → " + flight.getDestination());
        routeLabel.setStyle("-fx-font-weight: bold;");
        flightDetailsGrid.add(routeLabel, 1, row++);

        flightDetailsGrid.add(new Label("Departure:"), 0, row);
        flightDetailsGrid.add(new Label(flight.getDeparture().format(dateFormatter)), 1, row++);

        flightDetailsGrid.add(new Label("Arrival:"), 0, row);
        flightDetailsGrid.add(new Label(flight.getArrival().format(dateFormatter)), 1, row++);

        flightDetailsGrid.add(new Label("Aircraft:"), 0, row);
        flightDetailsGrid.add(new Label(flight.getAircraft()), 1, row++);

        flightDetailsGrid.add(new Label("Available Seats:"), 0, row);
        flightDetailsGrid.add(new Label(String.valueOf(flight.getSeats())), 1, row++);

        flightDetailsGrid.add(new Label("Fare:"), 0, row);
        Label fareLabel = new Label(currencyFormat.format(flight.getPrice()));
        fareLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-size: 14px;");
        flightDetailsGrid.add(fareLabel, 1, row++);

        mainContent.getChildren().addAll(bookingDetailsLabel, flightDetailsGrid, new Separator());
    }
    
    private void addPassengerDetailsSection(VBox mainContent, Flight flight, Dialog<ButtonType> dialog) {
        Label passengerDetailsLabel = new Label("Passenger Information");
        passengerDetailsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        passengerDetailsLabel.setStyle("-fx-text-fill: #1a237e;");

        GridPane passengerGrid = new GridPane();
        passengerGrid.setHgap(15);
        passengerGrid.setVgap(12);
        passengerGrid.setPadding(new Insets(15));
        passengerGrid.setStyle("-fx-background-color: #fff3e0; -fx-background-radius: 5;");

        // Create form fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        firstNameField.setStyle("-fx-padding: 8; -fx-pref-width: 250;");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        lastNameField.setStyle("-fx-padding: 8; -fx-pref-width: 250;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");
        emailField.setStyle("-fx-padding: 8; -fx-pref-width: 250;");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number (e.g., 09123456789)");
        phoneField.setStyle("-fx-padding: 8; -fx-pref-width: 250;");

        // Try to pre-fill form with user data
        int row = 0;
        try {
            fetchCurrentUserData(firstNameField, lastNameField, emailField);
            if (UserSession.getInstance().isLoggedIn()) {
                Label prefillNote = new Label("✓ Information pre-filled from your account");
                prefillNote.setStyle("-fx-text-fill: #4caf50; -fx-font-style: italic; -fx-font-size: 12px;");
                passengerGrid.add(prefillNote, 0, 0, 2, 1);
                row = 1;
            }
        } catch (Exception e) {
            System.err.println("Error fetching user data: " + e.getMessage());
        }

        // Add form fields
        Label firstNameLabel = new Label("First Name: *");
        firstNameLabel.setStyle("-fx-font-weight: bold;");
        passengerGrid.add(firstNameLabel, 0, row);
        passengerGrid.add(firstNameField, 1, row++);

        Label lastNameLabel = new Label("Last Name: *");
        lastNameLabel.setStyle("-fx-font-weight: bold;");
        passengerGrid.add(lastNameLabel, 0, row);
        passengerGrid.add(lastNameField, 1, row++);

        Label emailLabel = new Label("Email Address:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        passengerGrid.add(emailLabel, 0, row);
        passengerGrid.add(emailField, 1, row++);

        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-weight: bold;");
        passengerGrid.add(phoneLabel, 0, row);
        passengerGrid.add(phoneField, 1, row++);

        // Set up validation and submission
        setupFormValidation(dialog, flight, firstNameField, lastNameField, emailField, phoneField);

        mainContent.getChildren().addAll(passengerDetailsLabel, passengerGrid, new Separator());
    }
    
    private void addBookingSummarySection(VBox mainContent, Flight flight) {
        Label summaryLabel = new Label("Booking Summary");
        summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        summaryLabel.setStyle("-fx-text-fill: #1a237e;");

        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(8);
        summaryGrid.setPadding(new Insets(15));
        summaryGrid.setStyle("-fx-background-color: #e8f5e8; -fx-background-radius: 5;");

        int row = 0;
        summaryGrid.add(new Label("Passengers:"), 0, row);
        Label passengerCountLabel = new Label("1 Adult");
        passengerCountLabel.setStyle("-fx-font-weight: bold;");
        summaryGrid.add(passengerCountLabel, 1, row++);

        summaryGrid.add(new Label("Base Fare:"), 0, row);
        summaryGrid.add(new Label(currencyFormat.format(flight.getPrice())), 1, row++);

        summaryGrid.add(new Label("Taxes & Fees:"), 0, row);
        double taxesAndFees = flight.getPrice() * 0.12;
        summaryGrid.add(new Label(currencyFormat.format(taxesAndFees)), 1, row++);

        Separator separator = new Separator();
        summaryGrid.add(separator, 0, row++, 2, 1);

        summaryGrid.add(new Label("Total Amount:"), 0, row);
        double totalAmount = flight.getPrice() + taxesAndFees;
        Label totalLabel = new Label(currencyFormat.format(totalAmount));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1a237e;");
        summaryGrid.add(totalLabel, 1, row++);

        mainContent.getChildren().addAll(summaryLabel, summaryGrid, new Separator());
    }
    
    private void addTravelClassSection(VBox mainContent) {
        Label classLabel = new Label("Travel Class");
        classLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        classLabel.setStyle("-fx-text-fill: #1a237e;");

        VBox classBox = new VBox(10);
        classBox.setPadding(new Insets(15));
        classBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        ToggleGroup classGroup = new ToggleGroup();
        RadioButton economyRadio = new RadioButton("Economy Class - Included");
        economyRadio.setToggleGroup(classGroup);
        economyRadio.setSelected(true);

        RadioButton businessRadio = new RadioButton("Business Class - +₱5,000");
        businessRadio.setToggleGroup(classGroup);

        RadioButton firstRadio = new RadioButton("First Class - +₱12,000");
        firstRadio.setToggleGroup(classGroup);

        classBox.getChildren().addAll(economyRadio, businessRadio, firstRadio);
        mainContent.getChildren().addAll(classLabel, classBox, new Separator());
    }
    
    private void addSeatPreferenceSection(VBox mainContent) {
        Label seatLabel = new Label("Seat Preference");
        seatLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        seatLabel.setStyle("-fx-text-fill: #1a237e;");

        VBox seatBox = new VBox(10);
        seatBox.setPadding(new Insets(15));
        seatBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5;");

        ToggleGroup seatGroup = new ToggleGroup();
        RadioButton autoSeatRadio = new RadioButton("Auto-assign seat (Free)");
        autoSeatRadio.setToggleGroup(seatGroup);
        autoSeatRadio.setSelected(true);

        RadioButton windowRadio = new RadioButton("Window seat preference (+₱500)");
        windowRadio.setToggleGroup(seatGroup);

        RadioButton aisleRadio = new RadioButton("Aisle seat preference (+₱500)");
        aisleRadio.setToggleGroup(seatGroup);

        seatBox.getChildren().addAll(autoSeatRadio, windowRadio, aisleRadio);
        mainContent.getChildren().addAll(seatLabel, seatBox, new Separator());
    }
    
    private void addBaggageSection(VBox mainContent) {
        Label baggageLabel = new Label("Baggage Allowance");
        baggageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        baggageLabel.setStyle("-fx-text-fill: #1a237e;");

        GridPane baggageGrid = new GridPane();
        baggageGrid.setHgap(15);
        baggageGrid.setVgap(8);
        baggageGrid.setPadding(new Insets(15));
        baggageGrid.setStyle("-fx-background-color: #fff8e1; -fx-background-radius: 5;");

        int row = 0;
        baggageGrid.add(new Label("Carry-on:"), 0, row);
        baggageGrid.add(new Label("7kg included"), 1, row++);

        baggageGrid.add(new Label("Checked Baggage:"), 0, row);
        ComboBox<String> baggageCombo = new ComboBox<>();
        baggageCombo.getItems().addAll(
            "No checked baggage",
            "15kg (+₱1,200)",
            "20kg (+₱1,800)",
            "30kg (+₱2,500)"
        );
        baggageCombo.setValue("No checked baggage");
        baggageGrid.add(baggageCombo, 1, row++);

        mainContent.getChildren().addAll(baggageLabel, baggageGrid, new Separator());
    }
    
    private void addImportantNotesSection(VBox mainContent) {
        Label notesLabel = new Label("Important Information");
        notesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        notesLabel.setStyle("-fx-text-fill: #ff5722;");

        VBox notesBox = new VBox(8);
        notesBox.setPadding(new Insets(15));
        notesBox.setStyle("-fx-background-color: #ffebee; -fx-background-radius: 5;");

        Label note1 = new Label("• Please ensure passenger name matches government-issued ID exactly");
        note1.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        note1.setWrapText(true);

        Label note2 = new Label("• Arrive at airport at least 2 hours before domestic flights, 3 hours for international");
        note2.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        note2.setWrapText(true);

        Label note3 = new Label("• Booking confirmation will be sent to your email address");
        note3.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        note3.setWrapText(true);

        Label note4 = new Label("• Free cancellation within 24 hours of booking");
        note4.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        note4.setWrapText(true);

        notesBox.getChildren().addAll(note1, note2, note3, note4);
        mainContent.getChildren().addAll(notesLabel, notesBox);
    }
    
    private void setupFormValidation(Dialog<ButtonType> dialog, Flight flight, 
                                   TextField firstNameField, TextField lastNameField, 
                                   TextField emailField, TextField phoneField) {
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();

                if (firstName.isEmpty() || lastName.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Form Error");
                    alert.setHeaderText("Please fill in required fields");
                    alert.setContentText("First Name and Last Name are required.");
                    alert.showAndWait();
                    return null;
                }

                if (email.isEmpty()) {
                    emailField.setText("guest@jetsetgo.com");
                }
                if (phone.isEmpty()) {
                    phoneField.setText("09000000000");
                }

                // Process booking
                processBooking(flight, firstName, lastName, emailField.getText(), phoneField.getText());
            }
            return dialogButton;
        });
    }
    
    private void processBooking(Flight flight, String firstName, String lastName, String email, String phone) {
        double totalPrice = flight.getPrice() * 1.12; // Include 12% tax
        String bookingRef = "JET-" + System.currentTimeMillis() % 10000;
        
        PaymentDialogController paymentController = new PaymentDialogController(flightService, currencyFormat);
        paymentController.showPaymentDialog(flight, firstName, lastName, email, phone, totalPrice, bookingRef);
    }
    
    private void fetchCurrentUserData(TextField firstNameField, TextField lastNameField, TextField emailField) {
        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null) {
                firstNameField.setText(currentUser.getFirstName());
                lastNameField.setText(currentUser.getLastName());
                emailField.setText(currentUser.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error fetching user data: " + e.getMessage());
        }
    }
}