package com.rurbisservices.churchdonation.utils.listeners;

import com.rurbisservices.churchdonation.utils.ApplicationProperties;
import com.rurbisservices.churchdonation.utils.events.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MainStageListener implements ApplicationListener<StageReadyEvent> {
    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(properties.getMainView()));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            int width = (int) Screen.getPrimary().getBounds().getWidth() - 100;
            int height = (int) Screen.getPrimary().getBounds().getHeight() - 100;
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource(properties.getStyleSheet()).toExternalForm());
            stage.setScene(scene);
            stage.setTitle(properties.getApplicationTitle());
//            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
