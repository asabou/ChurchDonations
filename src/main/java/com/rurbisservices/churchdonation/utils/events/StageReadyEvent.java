package com.rurbisservices.churchdonation.utils.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent extends ApplicationEvent {
    public Stage getStage() {
        return Stage.class.cast(getSource());
    }
    public StageReadyEvent(Stage source) {
        super(source);
    }
}