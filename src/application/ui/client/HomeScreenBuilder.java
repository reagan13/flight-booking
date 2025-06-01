package application.ui.client;

import application.model.Flight;
import application.service.FlightService;
import application.util.FlightListCell;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeScreenBuilder {
    
    private final HomeEventHandler eventHandler;
    private final FlightService flightService;
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter dateFormatter;
    
    // Banner carousel
    private int currentBannerIndex = 0;
    private final String[] bannerTitles = {
        "Dream. Discover. GO", 
        "Explore New Places", 
        "Fly with Confidence"
    };
    
    public HomeScreenBuilder(HomeEventHandler eventHandler, FlightService flightService) {
        this.eventHandler = eventHandler;
        this.flightService = flightService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    }
    
    /**
     * Setup the flight list view
     */
    public void setupFlightListView(ListView<Flight> flightListView) {
        if (flightListView == null) {
            System.err.println("flightListView is null, skipping home setup");
            return;
        }
        
        // Flight list setup
        flightListView.setPlaceholder(new Label("No flights available"));
        
        // Store controller reference in the ListView for FlightListCell access
        flightListView.getProperties().put("controller", eventHandler);
        
        // Custom cell factory for card-like display
        flightListView.setCellFactory(listView -> new FlightListCell());
        
        // Configure ListView for vertical scrolling only
        flightListView.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                "-fx-focus-color: transparent; -fx-faint-focus-color: transparent; " +
                "-fx-selection-bar: transparent; -fx-selection-bar-non-focused: transparent;" +
                "-fx-cell-size: 200; -fx-vertical-cell-spacing: 100;");
    }
    
    /**
     * Setup search functionality
     */
    public void setupSearchField(TextField searchField) {
        if (searchField != null) {
            searchField.setOnAction(this::handleSearch);
        }
    }
    
    /**
     * Create mobile search section
     */
    public VBox createMobileSearchSection() {
        VBox searchSection = new VBox(15);
        searchSection.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                              "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Header
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 20px;");
        
        Label titleLabel = new Label("Find Your Flight");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        headerBox.getChildren().addAll(searchIcon, titleLabel);
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search flights, destinations, airlines...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 25; " +
                            "-fx-border-color: #ddd; -fx-border-radius: 25; -fx-border-width: 1;");
        searchField.setOnAction(this::handleSearch);
        
        // Quick filter buttons
        HBox filtersBox = new HBox(10);
        filtersBox.setAlignment(Pos.CENTER);
        
        Button todayBtn = createFilterButton("Today", "today");
        Button weekBtn = createFilterButton("This Week", "week");
        Button monthBtn = createFilterButton("This Month", "month");
        
        filtersBox.getChildren().addAll(todayBtn, weekBtn, monthBtn);
        
        searchSection.getChildren().addAll(headerBox, searchField, filtersBox);
        return searchSection;
    }
    
    /**
     * Create banner carousel section
     */
    public VBox createBannerCarousel() {
        VBox bannerSection = new VBox(15);
        bannerSection.setStyle("-fx-background-color: linear-gradient(to right, #2196F3, #21CBF3); " +
                              "-fx-background-radius: 15; -fx-padding: 25; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 3);");
        
        // Banner content
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        
        Label bannerTitle = new Label(bannerTitles[currentBannerIndex]);
        bannerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; " +
                            "-fx-text-alignment: center;");
        bannerTitle.setWrapText(true);
        
        Label bannerSubtitle = new Label("Book your next adventure with JetSetGO");
        bannerSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #E8F5FF; " +
                               "-fx-text-alignment: center;");
        bannerSubtitle.setWrapText(true);
        
        Button exploreBtn = new Button("Explore Destinations");
        exploreBtn.setStyle("-fx-background-color: white; -fx-text-fill: #2196F3; " +
                           "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; " +
                           "-fx-background-radius: 20; -fx-cursor: hand;");
        exploreBtn.setOnAction(e -> eventHandler.onExploreDestinations());
        
        contentBox.getChildren().addAll(bannerTitle, bannerSubtitle, exploreBtn);
        
        // Carousel indicators
        HBox indicators = new HBox(8);
        indicators.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < bannerTitles.length; i++) {
            Label dot = new Label("●");
            dot.setStyle("-fx-font-size: 8px; -fx-text-fill: " + 
                        (i == currentBannerIndex ? "white" : "rgba(255,255,255,0.5)"));
            indicators.getChildren().add(dot);
        }
        
        bannerSection.getChildren().addAll(contentBox, indicators);
        
        // Auto-rotate banner (optional)
        rotateBanner(bannerSection);
        
        return bannerSection;
    }
    
    /**
     * Create flight stats section
     */
    public VBox createFlightStatsSection() {
        VBox statsSection = new VBox(15);
        statsSection.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                             "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        // Header
        Label titleLabel = new Label("✈️ Flight Overview");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Stats grid
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);
        
        VBox availableBox = createStatBox("🛫", "Available", "124 Flights");
        VBox destinationsBox = createStatBox("🌍", "Destinations", "45 Cities");
        VBox airlinesBox = createStatBox("✈️", "Airlines", "12 Partners");
        
        statsRow.getChildren().addAll(availableBox, destinationsBox, airlinesBox);
        
        statsSection.getChildren().addAll(titleLabel, statsRow);
        return statsSection;
    }
    
    /**
     * Create popular destinations section
     */
    public VBox createPopularDestinationsSection() {
        VBox destSection = new VBox(15);
        destSection.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("🏆 Popular Destinations");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewAllBtn = new Button("View All");
        viewAllBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; " +
                           "-fx-font-size: 12px; -fx-cursor: hand;");
        viewAllBtn.setOnAction(e -> eventHandler.onViewAllDestinations());
        
        headerBox.getChildren().addAll(titleLabel, spacer, viewAllBtn);
        
        // Destinations grid
        VBox destGrid = new VBox(10);
        
        String[][] destinations = {
            {"🇯🇵", "Tokyo", "From ₱25,000"},
            {"🇰🇷", "Seoul", "From ₱18,000"},
            {"🇸🇬", "Singapore", "From ₱12,000"},
            {"🇹🇭", "Bangkok", "From ₱8,000"}
        };
        
        for (String[] dest : destinations) {
            HBox destRow = createDestinationRow(dest[0], dest[1], dest[2]);
            destGrid.getChildren().add(destRow);
        }
        
        destSection.getChildren().addAll(headerBox, destGrid);
        return destSection;
    }
    
    /**
     * Load available flights asynchronously
     */
    public void loadAvailableFlights(ListView<Flight> flightListView, Label sectionLabel, 
                                   ProgressIndicator loadingIndicator) {
        eventHandler.onShowStatus("Loading flights...", true);
        
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        
        if (sectionLabel != null) {
            sectionLabel.setText("Loading...");
        }
        
        flightService.getAvailableFlights()
            .thenAccept(flights -> {
                Platform.runLater(() -> {
                    try {
                        flightListView.setItems(flights);
                        
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                        
                        if (sectionLabel != null) {
                            sectionLabel.setText("Available Flights (" + flights.size() + ")");
                        }
                        
                        eventHandler.onHideStatus();
                    } catch (Exception e) {
                        System.err.println("Error updating UI with flights: " + e.getMessage());
                        e.printStackTrace();
                        eventHandler.onHideStatus();
                    }
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    if (sectionLabel != null) {
                        sectionLabel.setText("Error loading flights");
                    }
                    
                    eventHandler.onHideStatus();
                });
                return null;
            });
    }
    
    // Helper methods
    
    private Button createFilterButton(String text, String period) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666; " +
                    "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15; " +
                    "-fx-cursor: hand;");
        btn.setOnAction(e -> filterFlightsByDate(period));
        
        // Hover effect
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15; " +
                        "-fx-cursor: hand;");
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666; " +
                        "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15; " +
                        "-fx-cursor: hand;");
        });
        
        return btn;
    }
    
    private VBox createStatBox(String icon, String label, String value) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        statBox.getChildren().addAll(iconLabel, valueLabel, labelText);
        return statBox;
    }
    
    private HBox createDestinationRow(String flag, String city, String price) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 8; " +
                    "-fx-cursor: hand;");
        
        Label flagLabel = new Label(flag);
        flagLabel.setStyle("-fx-font-size: 20px;");
        
        VBox cityBox = new VBox(2);
        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label countryLabel = new Label("Popular destination");
        countryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        cityBox.getChildren().addAll(cityLabel, countryLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        row.getChildren().addAll(flagLabel, cityBox, spacer, priceLabel);
        
        // Click handler
        row.setOnMouseClicked(e -> eventHandler.onSearchDestination(city));
        
        return row;
    }
    
    private void rotateBanner(VBox bannerSection) {
        // Auto-rotate banner every 5 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(5), e -> {
                currentBannerIndex = (currentBannerIndex + 1) % bannerTitles.length;
                updateBannerContent(bannerSection);
            })
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void updateBannerContent(VBox bannerSection) {
        // Update banner title and indicators
        VBox contentBox = (VBox) bannerSection.getChildren().get(0);
        Label titleLabel = (Label) contentBox.getChildren().get(0);
        titleLabel.setText(bannerTitles[currentBannerIndex]);
        
        // Update indicators
        HBox indicators = (HBox) bannerSection.getChildren().get(1);
        for (int i = 0; i < indicators.getChildren().size(); i++) {
            Label dot = (Label) indicators.getChildren().get(i);
            dot.setStyle("-fx-font-size: 8px; -fx-text-fill: " + 
                        (i == currentBannerIndex ? "white" : "rgba(255,255,255,0.5)"));
        }
    }
    
    private void handleSearch(ActionEvent event) {
        TextField searchField = (TextField) event.getSource();
        String query = searchField.getText().trim();
        
        if (query.isEmpty()) {
            eventHandler.onLoadAvailableFlights();
            return;
        }
        
        eventHandler.onSearchFlights(query);
    }
    
    private void filterFlightsByDate(String period) {
        eventHandler.onShowStatus("Filtering flights by " + period + "...", true);
        
        // Simulate filtering (replace with actual implementation)
        Platform.runLater(() -> {
            eventHandler.onHideStatus();
            System.out.println("Filtered flights by: " + period);
        });
    }
    
    /**
     * Interface for handling home screen events
     */
    public interface HomeEventHandler {
        void onShowFlightDetails(Flight flight);
        void onShowStatus(String message, boolean showProgress);
        void onHideStatus();
        void onLoadAvailableFlights();
        void onSearchFlights(String query);
        void onExploreDestinations();
        void onViewAllDestinations();
        void onSearchDestination(String destination);
    }
}