package com.rurbisservices.churchdonation.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class GUIUtils {
    public static void showErrorAlert(Integer code) {
        String message = MessagesUtils.getMessage(code);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public static Alert createConfirmationAlert(Integer codeQuestion, Integer codeMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().setHeaderText(MessagesUtils.getMessage(codeQuestion));
        alert.getDialogPane().setContentText(MessagesUtils.getMessage(codeMessage));
        return alert;
    }

    public static Alert createConfirmationAlert(String question, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, buttonTypeDelete, buttonTypeCancel);
        alert.getDialogPane().setHeaderText(question);
        alert.getDialogPane().setContentText(message);
        return alert;
    }

    public static ButtonType buttonTypeDelete = new ButtonType("Șterge", ButtonBar.ButtonData.OK_DONE);

    public static ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
}
