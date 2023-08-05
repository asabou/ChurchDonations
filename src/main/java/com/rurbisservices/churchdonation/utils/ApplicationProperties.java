package com.rurbisservices.churchdonation.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {
    @Value("${donations.app-title}")
    private String applicationTitle;

    @Value("${donations.paths.fxml.main}")
    private String mainView;

    @Value("${donations.paths.fxml.admin.churches}")
    private String churchesView;

    @Value("${donations.paths.fxml.admin.donations}")
    private String donationsView;

    @Value("${donations.paths.fxml.admin.houses}")
    private String housesView;

    @Value("${donations.paths.fxml.admin.people}")
    private String peopleView;

    @Value("${donations.paths.fxml.admin.donation-topics}")
    private String donationTopicsView;

    @Value("${donations.paths.fxml.reports}")
    private String reportsView;

    @Value("${donations.paths.fxml.help.about}")
    private String aboutView;

    @Value("${donations.paths.fxml.help.manual}")
    private String manualView;

    @Value("${donations.paths.fxml.style}")
    private String styleSheet;

    @Value("${donations.paths.export.churches.csv-file}")
    private String churchesFileCSV;

    @Value("${donations.paths.export.churches.xls-file}")
    private String churchesFileXLS;

    @Value("${donations.paths.export.houses.csv-file}")
    private String housesFileCSV;

    @Value("${donations.paths.export.houses.xls-file}")
    private String housesFileXLS;

    @Value("${donations.paths.export.people.csv-file}")
    private String peopleFileCSV;

    @Value("${donations.paths.export.people.xls-file}")
    private String peopleFileXLS;

    @Value("${donations.paths.export.donation-topics.csv-file}")
    private String donationTopicsFileCSV;

    @Value("${donations.paths.export.donation-topics.xls-file}")
    private String donationTopicsFileXLS;

    @Value("${donations.paths.export.donations.csv-file}")
    private String donationsFileCSV;

    @Value("${donations.paths.export.donations.xls-file}")
    private String donationsFileXLS;

    @Value("${donations.paths.export.reports.csv-file}")
    private String reportsFileCSV;

    @Value("${donations.paths.export.reports.xls-file}")
    private String reportsFileXLS;

    @Value("${donations.paths.export.reports.totals.csv-file}")
    private String reportsTotalsFileCSV;

    @Value("${donations.paths.export.reports.totals.xls-file}")
    private String reportsTotalsFileXLS;

    @Value("${donations.paths.initial-db.file}")
    private String initialDBCSV;

    @Value("${donations.paths.initial-db.duplicated}")
    private String initialDBCSVDuplicatedRecords;

    public String getChurchesFileXLS() {
        return churchesFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getChurchesFileCSV() {
        return churchesFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getHousesFileXLS() {
        return housesFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getHousesFileCSV() {
        return housesFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getPeopleFileXLS() {
        return peopleFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getPeopleFileCSV() { return peopleFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp())); }

    public String getDonationTopicsFileXLS() { return donationTopicsFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));}

    public String getDonationTopicsFileCSV() { return donationTopicsFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));}

    public String getDonationsFileXLS() {
        return donationsFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getDonationsFileCSV() { return donationsFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));}
    public String getReportsTotalsFileXLS() {
        return reportsTotalsFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getReportsTotalsFileCSV() {
        return reportsTotalsFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getReportsFileXLS() {
        return reportsFileXLS.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }

    public String getReportsFileCSV() {
        return reportsFileCSV.replace("{date}", DateUtils.convertToddMMyyyy(DateUtils.getCurrentTimestamp()));
    }
}
