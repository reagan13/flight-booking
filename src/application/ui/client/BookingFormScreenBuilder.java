package application.ui.client;

import application.model.Flight;
import application.model.User;
import application.service.BookingService;
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
    private CheckBox termsCheckbox, privacyCheckbox;
    
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
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #f5f5f5;");
        container.setFillWidth(true);
        container.setMaxWidth(500); // Mobile width constraint
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Main content section
        VBox contentSection = createContentSection(currentFlight);
        
        // Bottom action section
        VBox actionSection = createActionSection();
        
        container.getChildren().addAll(headerSection, contentSection, actionSection);
        return container;
    }
    
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #808080; -fx-border-width: 0 0 1 0;");
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-padding: 10 15;");
        
        Label titleLabel = new Label("📋 Booking Form");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        headerBox.getChildren().add(titleLabel);
        headerSection.getChildren().add(headerBox);
        
        return headerSection;
    }
    
    private VBox createContentSection(Flight currentFlight) {
        VBox contentSection = new VBox(10);
        contentSection.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        VBox.setVgrow(contentSection, Priority.ALWAYS);
        
        // Flight summary card
        VBox flightCard = createFlightSummaryCard(currentFlight);
        
        // Passenger form card
        VBox passengerCard = createPassengerFormCard();
        
        // Contact form card
        VBox contactCard = createContactFormCard();
        
        // Terms card
        VBox termsCard = createTermsCard();
        
        contentSection.getChildren().addAll(flightCard, passengerCard, contactCard, termsCard);
        return contentSection;
    }
    
    private VBox createFlightSummaryCard(Flight currentFlight) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Header
        Label titleLabel = new Label("✈️ Flight Summary");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Flight info
        VBox flightInfo = new VBox(6);
        
        Label airlineLabel = new Label(currentFlight.getAirlineName() + " - " + currentFlight.getFlightNo());
        airlineLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Route row
        HBox routeRow = new HBox();
        routeRow.setAlignment(Pos.CENTER);
        routeRow.setSpacing(10);
        routeRow.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        VBox originBox = createCompactAirportBox(currentFlight.getOrigin(), "From");
        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        VBox destBox = createCompactAirportBox(currentFlight.getDestination(), "To");
        
        routeRow.getChildren().addAll(originBox, arrowLabel, destBox);
        
        // Date and price row
        HBox detailsRow = new HBox();
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        detailsRow.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label dateLabel = new Label("📅 " + currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd")));
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label priceLabel = new Label("💰 " + currencyFormat.format(currentFlight.getPrice()));
        priceLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        detailsRow.getChildren().addAll(dateLabel, spacer, priceLabel);
        
        flightInfo.getChildren().addAll(airlineLabel, routeRow, detailsRow);
        card.getChildren().addAll(titleLabel, flightInfo);
        
        return card;
    }
    
    private VBox createCompactAirportBox(String code, String label) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        Label typeLabel = new Label(label);
        typeLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        box.getChildren().addAll(codeLabel, typeLabel);
        return box;
    }
    
    
    private VBox createPassengerFormCard() {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
    
        // Header
        Label titleLabel = new Label("👤 Passenger Information");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
    
        // Form fields
        VBox formBox = new VBox(8);
    
        // First Name
        VBox firstNameBox = createFormField("First Name *", firstNameField = new TextField(), "Enter your first name");
    
        // Last Name
        VBox lastNameBox = createFormField("Last Name *", lastNameField = new TextField(), "Enter your last name");
    
        // Age
        VBox ageBox = createFormField("Age *", ageField = new TextField(), "Enter your age");
    
        // Address
        VBox addressBox = createFormField("Address *", addressField = new TextField(), "Enter your address");
    
        // Pre-fill data if user is logged in
        if (UserSession.getInstance().isLoggedIn()) {
            User user = UserSession.getInstance().getCurrentUser();
            
            // DEBUG: Print what we're getting
            System.out.println("🔍 DEBUG - First name: '" + user.getFirstName() + "'");
            System.out.println("🔍 DEBUG - Last name: '" + user.getLastName() + "'");
            
            try {
                System.out.println("🔍 DEBUG - Age: " + user.getAge());
            } catch (Exception e) {
                System.out.println("❌ getAge() method error: " + e.getMessage());
            }
            
            try {
                System.out.println("🔍 DEBUG - Address: '" + user.getAddress() + "'");
            } catch (Exception e) {
                System.out.println("❌ getAddress() method error: " + e.getMessage());
            }
    
            // Pre-fill first name
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                firstNameField.setText(user.getFirstName());
                System.out.println("✅ First name pre-filled");
            } else {
                System.out.println("⚠️ First name is null or empty");
            }
    
            // Pre-fill last name
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                lastNameField.setText(user.getLastName());
                System.out.println("✅ Last name pre-filled");
            } else {
                System.out.println("⚠️ Last name is null or empty");
            }
    
            // Pre-fill age
            try {
                if (user.getAge() > 0) {
                    ageField.setText(String.valueOf(user.getAge()));
                    System.out.println("✅ Age pre-filled: " + user.getAge());
                } else {
                    System.out.println("⚠️ Age is 0 or negative: " + user.getAge());
                }
            } catch (Exception e) {
                System.out.println("❌ Cannot access age: " + e.getMessage());
            }
    
            // Pre-fill address
            try {
                if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                    addressField.setText(user.getAddress());
                    System.out.println("✅ Address pre-filled: " + user.getAddress());
                } else {
                    System.out.println("⚠️ Address is null or empty");
                }
            } catch (Exception e) {
                System.out.println("❌ Cannot access address: " + e.getMessage());
            }
        }
    
        formBox.getChildren().addAll(firstNameBox, lastNameBox, ageBox, addressBox);
        card.getChildren().addAll(titleLabel, formBox);
        return card;
    }
    
    private VBox createContactFormCard() {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
                        "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);");

        // Header
        Label titleLabel = new Label("📧 Contact Information");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");

        // Form fields
        VBox formBox = new VBox(8);

        // Email
        VBox emailBox = createFormField("Email Address *", emailField = new TextField(), "Enter your email");

        // Phone
        VBox phoneBox = createFormField("Phone Number *", phoneField = new TextField(), "Enter your phone number");

        // Pre-fill contact info if user is logged in
        if (UserSession.getInstance().isLoggedIn()) {
            User user = UserSession.getInstance().getCurrentUser();

            // Email is already working, so let's focus on phone
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailField.setText(user.getEmail());
            }

            // DEBUG: Check phone number method
            try {
                String phone = user.getPhoneNumber();
                System.out.println("🔍 DEBUG - Phone number: '" + phone + "'");

                if (phone != null && !phone.isEmpty()) {
                    phoneField.setText(phone);
                    System.out.println("✅ Phone number pre-filled: " + phone);
                } else {
                    System.out.println("⚠️ Phone number is null or empty");
                }
            } catch (Exception e) {
                System.out.println("❌ getPhoneNumber() method error: " + e.getMessage());
                System.out.println("❌ Your User model probably doesn't have getPhoneNumber() method!");
            }
        }

        formBox.getChildren().addAll(emailBox, phoneBox);
        card.getChildren().addAll(titleLabel, formBox);
        return card;
    }
    
    private VBox createActionSection() {
        VBox actionSection = new VBox();
        actionSection.setStyle(
                "-fx-background-color: #f0f0f0; -fx-border-color: #808080; " +
                        "-fx-border-width: 1 0 0 0; -fx-padding: 15;");

        Button continueButton = new Button("Continue to Payment");
        continueButton.setStyle(
                "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
                        "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                        "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
                        "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setOnAction(e -> {
            if (validateAndSubmitForm()) {
                // Form validation passed, continue to payment
            }
        });

        // Hover effects
        continueButton.setOnMouseEntered(e -> continueButton.setStyle(
                "-fx-background-color: linear-gradient(#45a049, #3d8b40); " +
                        "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                        "-fx-border-color: #2E7D32; -fx-border-width: 1; -fx-background-radius: 6; " +
                        "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));

        continueButton.setOnMouseExited(e -> continueButton.setStyle(
                "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
                        "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                        "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
                        "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));

        actionSection.getChildren().add(continueButton);
        return actionSection;
    }
    

    private VBox createTermsCard() {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Header
        Label titleLabel = new Label("📋 Terms & Conditions");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Checkboxes
        VBox checkboxBox = new VBox(8);
        
        termsCheckbox = new CheckBox("I agree to the Terms and Conditions");
        termsCheckbox.setStyle("-fx-font-size: 10px; -fx-text-fill: #333;");
        
        privacyCheckbox = new CheckBox("I agree to the Privacy Policy");
        privacyCheckbox.setStyle("-fx-font-size: 10px; -fx-text-fill: #333;");
        
        CheckBox marketingCheckbox = new CheckBox("Receive promotional updates (Optional)");
        marketingCheckbox.setStyle("-fx-font-size: 10px; -fx-text-fill: #333;");
        marketingCheckbox.setSelected(true);
        
        checkboxBox.getChildren().addAll(termsCheckbox, privacyCheckbox, marketingCheckbox);
        card.getChildren().addAll(titleLabel, checkboxBox);
        
        return card;
    }
    
    private VBox createFormField(String labelText, TextField field, String promptText) {
        VBox fieldBox = new VBox(3);
        
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        field.setPromptText(promptText);
        field.setStyle(
            "-fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; " +
            "-fx-border-color: #ddd; -fx-border-radius: 6; -fx-border-width: 1; " +
            "-fx-background-color: white;"
        );
        
        // Focus styles
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; " +
                    "-fx-border-color: #2196F3; -fx-border-radius: 6; -fx-border-width: 2; " +
                    "-fx-background-color: white;"
                );
            } else {
                field.setStyle(
                    "-fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; " +
                    "-fx-border-color: #ddd; -fx-border-radius: 6; -fx-border-width: 1; " +
                    "-fx-background-color: white;"
                );
            }
        });
        
        fieldBox.getChildren().addAll(label, field);
        return fieldBox;
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

        if (!termsCheckbox.isSelected()) {
            eventHandler.onShowAlert("Terms Required", "Please accept the Terms and Conditions.");
            return false;
        }

        if (!privacyCheckbox.isSelected()) {
            eventHandler.onShowAlert("Privacy Required", "Please accept the Privacy Policy.");
            return false;
        }

        if (UserSession.getInstance().isLoggedIn()) {
            User currentUser = UserSession.getInstance().getCurrentUser();

            // Check if ANY data has changed
            boolean dataChanged = false;

            if (!firstName.equals(currentUser.getFirstName() != null ? currentUser.getFirstName() : "")) {
                dataChanged = true;
            }
            if (!lastName.equals(currentUser.getLastName() != null ? currentUser.getLastName() : "")) {
                dataChanged = true;
            }
            if (!String.valueOf(Integer.parseInt(age)).equals(String.valueOf(currentUser.getAge()))) {
                dataChanged = true;
            }
            if (!address.equals(currentUser.getAddress() != null ? currentUser.getAddress() : "")) {
                dataChanged = true;
            }
            if (!email.equals(currentUser.getEmail() != null ? currentUser.getEmail() : "")) {
                dataChanged = true;
            }
            if (!phone.equals(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "")) {
                dataChanged = true;
            }

            // Only update database if something changed
            if (dataChanged) {
                boolean updateSuccess = BookingService.updateUserInformation(
                        firstName, lastName, Integer.parseInt(age), address, email, phone);

                if (!updateSuccess) {
                    eventHandler.onShowAlert("Update Failed", "Failed to update your information. Please try again.");
                    return false;
                }

                System.out.println("✅ User information updated in database");
            } else {
                System.out.println("ℹ️ No changes detected, skipping database update");
            }
        }
    

        // Create form data and notify handler
        BookingFormData formData = new BookingFormData(
            firstName, lastName, Integer.parseInt(age), address, email, phone
        );
        eventHandler.onFormValidated(formData);
        return true;
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