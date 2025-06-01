package application.ui.client;

import application.model.Flight;
import application.model.User;
import application.service.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class BookingFormScreenBuilder {
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    
    // Form fields
    private TextField firstNameField, lastNameField, emailField, phoneField;
    private TextField ageField, addressField;
    
    // Event handlers
    public interface BookingFormEventHandler {
        void onFormValidated(BookingFormData formData);
        void onShowAlert(String title, String message);
    }
    
    private final BookingFormEventHandler eventHandler;
    
    public BookingFormScreenBuilder(BookingFormEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public VBox createBookingForm(Flight currentFlight) {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 10;");
        
        // Flight summary card
        VBox flightSummary = createFlightSummary(currentFlight);
        container.getChildren().add(flightSummary);
        
        // Passenger information form
        VBox passengerForm = createPassengerForm();
        container.getChildren().add(passengerForm);
        
        // Contact information form
        VBox contactForm = createContactForm();
        container.getChildren().add(contactForm);
        
        // Terms and conditions
        VBox termsSection = createTermsSection();
        container.getChildren().add(termsSection);
        
        return container;
    }
    
    public boolean validateAndSubmitForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String age = ageField.getText().trim();
        String address = addressField.getText().trim();

        if (firstName.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your first name.");
            firstNameField.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your last name.");
            lastNameField.requestFocus();
            return false;
        }

        if (age.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your age.");
            ageField.requestFocus();
            return false;
        }

        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 1 || ageValue > 120) {
                eventHandler.onShowAlert("Invalid Age", "Please enter a valid age between 1 and 120.");
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            eventHandler.onShowAlert("Invalid Age", "Please enter a valid numeric age.");
            ageField.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your address.");
            addressField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your email address.");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(email)) {
            eventHandler.onShowAlert("Invalid Email", "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            eventHandler.onShowAlert("Required Field", "Please enter your phone number.");
            phoneField.requestFocus();
            return false;
        }

        if (!isValidPhone(phone)) {
            eventHandler.onShowAlert("Invalid Phone", "Please enter a valid phone number.");
            phoneField.requestFocus();
            return false;
        }

        // Create form data and notify handler
        BookingFormData formData = new BookingFormData(
            firstName, lastName, Integer.parseInt(age), address, email, phone
        );
        eventHandler.onFormValidated(formData);
        return true;
    }
    
    private VBox createFlightSummary(Flight currentFlight) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                      "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);
        
        Label flightIcon = new Label("✈️");
        flightIcon.setStyle("-fx-font-size: 20px;");
        
        Label titleLabel = new Label("Flight Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        headerBox.getChildren().addAll(flightIcon, titleLabel);
        
        // Flight details
        VBox detailsBox = new VBox(12);
        
        // Airline and flight number
        VBox flightInfo = new VBox(2);
        Label airlineLabel = new Label(currentFlight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label flightNoLabel = new Label("Flight " + currentFlight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        flightInfo.getChildren().addAll(airlineLabel, flightNoLabel);
        
        // Route
        HBox routeRow = new HBox();
        routeRow.setAlignment(Pos.CENTER);
        routeRow.setSpacing(15);
        routeRow.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 8;");
        
        VBox originBox = new VBox(3);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(currentFlight.getOrigin());
        originCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        Label depTime = new Label(currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
        depTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        originBox.getChildren().addAll(originCode, depTime);
        
        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        
        VBox destBox = new VBox(3);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(currentFlight.getDestination());
        destCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        Label arrTime = new Label(currentFlight.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        destBox.getChildren().addAll(destCode, arrTime);
        
        routeRow.getChildren().addAll(originBox, arrowLabel, destBox);
        
        // Date and duration
        VBox scheduleBox = new VBox(8);
        Label dateLabel = new Label("📅 " + currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        Label durationLabel = new Label("⏱️ " + currentFlight.getDuration() + " flight time");
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        scheduleBox.getChildren().addAll(dateLabel, durationLabel);
        
        // Price section
        VBox priceBox = new VBox(5);
        priceBox.setAlignment(Pos.CENTER);
        priceBox.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label priceTitle = new Label("Flight Price");
        priceTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        Label priceLabel = new Label(currencyFormat.format(currentFlight.getPrice()));
        priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        Label priceNote = new Label("per person • includes taxes & fees");
        priceNote.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        priceBox.getChildren().addAll(priceTitle, priceLabel, priceNote);
        
        detailsBox.getChildren().addAll(flightInfo, routeRow, scheduleBox, priceBox);
        card.getChildren().addAll(headerBox, detailsBox);
        
        return card;
    }
    
    private VBox createPassengerForm() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        Label passengerIcon = new Label("👤");
        passengerIcon.setStyle("-fx-font-size: 20px;");

        Label titleLabel = new Label("Passenger Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        headerBox.getChildren().addAll(passengerIcon, titleLabel);

        // Form fields
        VBox formBox = new VBox(15);

        // First Name
        VBox firstNameBox = new VBox(5);
        Label firstNameLabel = new Label("First Name *");
        firstNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        firstNameBox.getChildren().addAll(firstNameLabel, firstNameField);

        // Last Name
        VBox lastNameBox = new VBox(5);
        Label lastNameLabel = new Label("Last Name *");
        lastNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");
        lastNameField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        lastNameBox.getChildren().addAll(lastNameLabel, lastNameField);

        // Age
        VBox ageBox = new VBox(5);
        Label ageLabel = new Label("Age *");
        ageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        ageField = new TextField();
        ageField.setPromptText("Enter your age");
        ageField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        ageBox.getChildren().addAll(ageLabel, ageField);

        // Address
        VBox addressBox = new VBox(5);
        Label addressLabel = new Label("Address *");
        addressLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        addressField = new TextField();
        addressField.setPromptText("Enter your address");
        addressField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        addressBox.getChildren().addAll(addressLabel, addressField);

        // Pre-fill data if user is logged in
        if (UserSession.getInstance().isLoggedIn()) {
            User user = UserSession.getInstance().getCurrentUser();
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            if (user.getAge() > 0) {
                ageField.setText(String.valueOf(user.getAge()));
            }
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                addressField.setText(user.getAddress());
            }
        }

        formBox.getChildren().addAll(firstNameBox, lastNameBox, ageBox, addressBox);

        // Important note
        VBox noteBox = new VBox(5);
        noteBox.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 12; -fx-background-radius: 8;");

        Label noteIcon = new Label("ℹ️");
        noteIcon.setStyle("-fx-font-size: 14px;");

        Label noteText = new Label(
                "Important: Please ensure the name matches exactly as shown on your government-issued ID.");
        noteText.setStyle("-fx-font-size: 11px; -fx-text-fill: #E65100; -fx-wrap-text: true;");

        HBox noteRow = new HBox(8);
        noteRow.setAlignment(Pos.TOP_LEFT);
        noteRow.getChildren().addAll(noteIcon, noteText);
        noteBox.getChildren().add(noteRow);

        card.getChildren().addAll(headerBox, formBox, noteBox);
        return card;
    }
    
    private VBox createContactForm() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                      "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);
        
        Label contactIcon = new Label("📧");
        contactIcon.setStyle("-fx-font-size: 20px;");
        
        Label titleLabel = new Label("Contact Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        headerBox.getChildren().addAll(contactIcon, titleLabel);
        
        // Form fields
        VBox formBox = new VBox(15);
        
        // Email
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email Address *");
        emailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                           "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Pre-fill if user is logged in
        if (UserSession.getInstance().isLoggedIn()) {
            emailField.setText(UserSession.getInstance().getCurrentUser().getEmail());
        }
        
        emailBox.getChildren().addAll(emailLabel, emailField);
        
        // Phone
        VBox phoneBox = new VBox(5);
        Label phoneLabel = new Label("Phone Number *");
        phoneLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                           "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);
        
        formBox.getChildren().addAll(emailBox, phoneBox);
        
        // Contact note
        VBox noteBox = new VBox(5);
        noteBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 8;");
        
        Label noteText = new Label("📱 We'll send your booking confirmation and flight updates to this email and phone number.");
        noteText.setStyle("-fx-font-size: 11px; -fx-text-fill: #1976D2; -fx-wrap-text: true;");
        noteBox.getChildren().add(noteText);
        
        card.getChildren().addAll(headerBox, formBox, noteBox);
        return card;
    }
    
    private VBox createTermsSection() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        Label termsIcon = new Label("📋");
        termsIcon.setStyle("-fx-font-size: 20px;");

        Label titleLabel = new Label("Terms & Conditions");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        headerBox.getChildren().addAll(termsIcon, titleLabel);

        // Checkboxes
        VBox checkboxBox = new VBox(12);

        CheckBox termsCheckbox = new CheckBox("I agree to the Terms and Conditions");
        termsCheckbox.setStyle("-fx-font-size: 13px;");

        CheckBox privacyCheckbox = new CheckBox("I agree to the Privacy Policy");
        privacyCheckbox.setStyle("-fx-font-size: 13px;");

        CheckBox marketingCheckbox = new CheckBox("I want to receive promotional emails and updates (Optional)");
        marketingCheckbox.setStyle("-fx-font-size: 13px;");
        marketingCheckbox.setSelected(true);

        checkboxBox.getChildren().addAll(termsCheckbox, privacyCheckbox, marketingCheckbox);

        card.getChildren().addAll(headerBox, checkboxBox);
        return card;
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    private boolean isValidPhone(String phone) {
        return phone.replaceAll("[^0-9]", "").length() >= 10;
    }
    
    // Data transfer object
    public static class BookingFormData {
        private final String firstName;
        private final String lastName;
        private final int age;
        private final String address;
        private final String email;
        private final String phone;
        
        public BookingFormData(String firstName, String lastName, int age, String address, String email, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.address = address;
            this.email = email;
            this.phone = phone;
        }
        
        // Getters
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public int getAge() { return age; }
        public String getAddress() { return address; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }
}