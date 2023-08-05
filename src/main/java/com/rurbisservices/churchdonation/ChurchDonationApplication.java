package com.rurbisservices.churchdonation;

import com.rurbisservices.churchdonation.utils.MessagesUtils;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChurchDonationApplication {
	public static void main(String[] args) {
		MessagesUtils.loadMessages();

		Application.launch(DesktopApplication.class, args);
	}
}
