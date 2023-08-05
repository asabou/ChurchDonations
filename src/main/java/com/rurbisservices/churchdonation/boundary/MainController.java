package com.rurbisservices.churchdonation.boundary;

import com.rurbisservices.churchdonation.utils.ApplicationProperties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.GUIUtils.showErrorAlert;

@Slf4j
@Component
public class MainController {
    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ApplicationProperties properties;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize() {
        onDonationClick();
        log.info("Main View initialized");
    }

    @FXML
    public void onChurchClick() {
        log.info("Churches clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getChurchesView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onHouseClick() {
        log.info("Houses clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getHousesView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onDonationClick() {
        log.info("Donations clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getDonationsView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onDonationTopicClick() {
        log.info("DonationTopics clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getDonationTopicsView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onPeopleClick() {
        log.info("People clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getPeopleView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onReportsClick() {
        log.info("Reports clicked ...");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getReportsView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onAboutClick() {
        log.info("About clicked ..");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getAboutView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onManualClick() {
        log.info("Manual clicked ..");
        try {
            anchorPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getManualView()));
            loader.setControllerFactory(springContext::getBean);
            anchorPane.getChildren().setAll((AnchorPane) loader.load());
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            showErrorAlert(100);
        }
    }
}
