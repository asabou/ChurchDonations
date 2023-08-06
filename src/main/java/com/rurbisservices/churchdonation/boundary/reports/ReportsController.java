package com.rurbisservices.churchdonation.boundary.reports;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.*;
import com.rurbisservices.churchdonation.utils.DateUtils;
import com.rurbisservices.churchdonation.utils.converters.ChurchDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.DonationTopicDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.HouseDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.PersonDTOConverter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;
import static com.rurbisservices.churchdonation.utils.GUIUtils.showErrorAlert;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Slf4j
@Component("reports")
public class ReportsController extends AbstractController {
    @FXML
    private DatePicker toDatePicker;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private TableView totalsTableView;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        initChurchesComboBox();
        initDefaults();
        log.info("DonationsController initialized");
    }

    @FXML
    public void filterHouse() {
        String filterHouse = filterHouseTextField.getText().toLowerCase();
        if (!isStringNullOrEmpty(filterHouse)) {
            List<HouseDTO> filteredHouses = houseDTOS
                    .stream()
                    .filter(x -> !isObjectNull(x) && !isStringNullOrEmpty(x.getNumber()) && x.getNumber().toLowerCase().contains(filterHouse))
                    .collect(Collectors.toList());
            filteredHouses.add(0, null);
            setHouseDTOObservableList(filteredHouses);
        } else {
            setHouseDTOObservableList(houseDTOS);
        }
        initHouseComboBox();
        String filterPerson = filterPersonTextField.getText().toLowerCase();
        List<PersonDTO> filteredPersons = filterPersonsByFilterHouseAndFilterPerson(filterHouse, filterPerson);
        filteredPersons.add(0, null);
        setPersonDTOObservableList(filteredPersons);
        initPeopleComboBox();
        filterDonations();
        initDonationsTableView();
    }

    @FXML
    public void filterPerson() {
        String filterPerson = filterPersonTextField.getText().toLowerCase();
        List<PersonDTO> filteredPersons;
        if (!isObjectNull(selectedHouseDTO)) {
            filteredPersons = filterPersonsByHouseAndFilterPerson(selectedHouseDTO.displayFormat(), filterPerson);
        } else {
            filteredPersons = personDTOS
                    .stream().filter(x -> !isObjectNull(x) && (
                            (!isStringNullOrEmpty(x.getFirstName()) && x.getFirstName().toLowerCase().contains(filterPerson)) ||
                                    (!isStringNullOrEmpty(x.getLastName()) && x.getLastName().toLowerCase().contains(filterPerson))))
                    .collect(Collectors.toList());
        }
        filteredPersons.add(0, null);
        setPersonDTOObservableList(filteredPersons);
        initPeopleComboBox();
        filterDonations();
        initTotalsTableView();
    }

    @FXML
    public void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            filterDonations();
        }
    }

    @FXML
    public void filterDonationTopics() {
        log.info("Filtering donation Topics ...");
        String filter = filterDonationTopicTextField.getText().toLowerCase();
        List<DonationTopicDTO> filteredDonationTopics;
        if (isStringNullOrEmpty(filter)) {
            filteredDonationTopics = donationTopicDTOS;
        } else {
            filteredDonationTopics = donationTopicDTOS
                    .stream()
                    .filter(x -> !isObjectNull(x) && x.getTopic().toLowerCase().contains(filter))
                    .collect(Collectors.toList());
            filteredDonationTopics.add(0, null);
        }
        donationTopicDTOObservableList = FXCollections.observableArrayList(filteredDonationTopics);
        initDonationTopicComboBox();
        filterDonations();
    }

    @FXML
    public void filterDonations() {
        String filterDonation = filterDonationTextField.getText().toLowerCase();
        String filterHouse = filterHouseTextField.getText().toLowerCase();
        String filterPerson = filterPersonTextField.getText().toLowerCase();
        String filterDonationTopic = isObjectNull(selectedDonationTopicDTO) ? filterDonationTopicTextField.getText().toLowerCase() :
                selectedDonationTopicDTO.getTopic().toLowerCase();
        Timestamp dateFrom = DateUtils.getFromTimestampFromLocalDate(fromDatePicker.getValue());
        Timestamp dateTo = DateUtils.getToTimestampFromLocalDate(toDatePicker.getValue());
        List<DonationDTO> filteredDonations;
        if (isObjectNull(selectedHouseDTO)) {
            if (isObjectNull(selectedPersonDTO)) {
                filteredDonations =
                        filterDonationsByDateFromAndDateToAndFilterHouseAndFilterPersonAndFilterDonationTopicAndFilterDonations(dateFrom,
                                dateTo, filterHouse, filterPerson, filterDonationTopic,
                                filterDonation);
            } else {
                filteredDonations = filterDonationsByDateFromAndDateToAndFilterHouseAndPersonAndFilterDonationTopicAndFilterDonations(dateFrom,
                        dateTo, filterHouse, selectedPersonDTO.displayFormat(), filterDonationTopic, filterDonation);
            }
        } else {
            if (isObjectNull(selectedPersonDTO)) {
                filteredDonations = filterDonationsByDateFromAndDateToAndHouseAndFilterPersonAndFilterDonationTopicAndFilterDonations(dateFrom,
                        dateTo, selectedHouseDTO.displayFormat(), filterPerson, filterDonationTopic, filterDonation);
            } else {
                filteredDonations = filterDonationsByDateFromAndDateToAndHouseAndPersonAndFilterDonationTopicAndFilterDonations(dateFrom, dateTo,
                        selectedHouseDTO.displayFormat(), selectedPersonDTO.displayFormat(), filterDonationTopic, filterDonation);
            }
        }
        setDonationDTOObservableList(prepareRealSumeIfDonationTopicWasSelected(filteredDonations));
        initDonationsTableView();
        initTotalsTableView();
    }


    @FXML
    public void exportTotals() {
        try {
            exportTableView(totalsTableView, properties.getReportsTotalsFileXLS(), properties.getReportsTotalsFileCSV());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }

    @FXML
    public void exportDonations() {
        try {
            exportTableView(donationTable, properties.getReportsFileXLS(), properties.getReportsFileCSV());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }

    @Override
    protected void initHousesLists() {
        super.initHousesLists();
        houseDTOS.add(0, null);
        setHouseDTOObservableList(houseDTOS);
    }

    private void initDonationTopicComboBox() {
        donationTopicsComboBox.setItems(donationTopicDTOObservableList);
        donationTopicsComboBox.setConverter(new DonationTopicDTOConverter());
        donationTopicsComboBox.setOnAction((e) -> {
            DonationTopicDTO selected = (DonationTopicDTO) donationTopicsComboBox.getSelectionModel().getSelectedItem();
            setDonationTopicDTO(selected);
            filterDonations();
        });
        log.info("Init donationTopicComboBox finalized");
    }


    private void initChurchesComboBox() {
        churchesComboBox.setItems(churchDTOObservableList);
        churchesComboBox.setConverter(new ChurchDTOConverter());
        churchesComboBox.setOnAction((e) -> {
            ChurchDTO selected = (ChurchDTO) churchesComboBox.getSelectionModel().getSelectedItem();
            setChurchDTO(selected);
            setHouseDTO(null);
            setPersonDTO(null);
            resetFields();
            initHousesLists();
            initHouseComboBox();
            initPeopleLists();
            initPeopleComboBox();
            initDonationsLists();
            initDonationsTableView();
            initDonationTopicsLists();
            initDonationTopicsComboBox();
        });
    }

    private void initHouseComboBox() {
        housesComboBox.setItems(houseDTOObservableList);
        housesComboBox.setConverter(new HouseDTOConverter());
        housesComboBox.setOnAction((e) -> {
            HouseDTO selected = (HouseDTO) housesComboBox.getSelectionModel().getSelectedItem();
            setHouseDTO(selected);
            log.info("House selected {}", selected);
            String filterPerson = filterPersonTextField.getText().toLowerCase();
            List<PersonDTO> filteredPersons;
            if (isObjectNull(selected)) {
                filteredPersons = personDTOS;
            } else {
                filteredPersons = filterPersonsByHouseAndFilterPerson(selected.displayFormat(), filterPerson);
                filteredPersons.add(0, null);
            }
            setPersonDTOObservableList(filteredPersons);
            initPeopleComboBox();
            filterDonations();
        });
        log.info("Init houseComboBox finalized");
    }

    private void initPeopleComboBox() {
        peopleComboBox.setItems(personDTOObservableList);
        peopleComboBox.setConverter(new PersonDTOConverter());
        peopleComboBox.setOnAction((e) -> {
            PersonDTO selected = (PersonDTO) peopleComboBox.getSelectionModel().getSelectedItem();
            setPersonDTO(selected);
            log.info("Person selected {}", selected);
            filterDonations();
        });
        log.info("Init personComboBox finalized");
    }


    private void resetFields() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        filterHouseTextField.setText(EMPTY_STRING);
        filterPersonTextField.setText(EMPTY_STRING);
        filterDonationTextField.setText(EMPTY_STRING);
    }

    private void initDonationTopicsComboBox() {
        donationTopicsComboBox.setItems(donationTopicDTOObservableList);
        donationTopicsComboBox.setConverter(new DonationTopicDTOConverter());
        donationTopicsComboBox.setOnAction((e) -> {
            DonationTopicDTO selected = (DonationTopicDTO) donationTopicsComboBox.getSelectionModel().getSelectedItem();
            setDonationTopicDTO(selected);
            log.info("Selected donation topic {}", selected);
            filterDonations();
        });
        log.info("Init donationTopicComboBox finalized");
    }

    private void initTotalsTableView() {
        List<TableColumn> tableColumnList = totalsTableView.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
            if (col.getId().equals("sume") || col.getId().equals("donations") || col.getId().equals("average")) {
                col.setComparator((Comparator<String>) (o1, o2) -> {
                    double a = Double.parseDouble(o1);
                    double b = Double.parseDouble(o2);
                    return Double.compare(a, b);
                });
            }
        });
        List<TotalsDTO> totals = getTotalsToDisplay();
        totalsTableView.setItems(FXCollections.observableArrayList(totals));
    }

    private void initDisabled(boolean mode) {
        fromDatePicker.setDisable(mode);
        toDatePicker.setDisable(mode);
        filterHouseTextField.setDisable(mode);
        filterPersonTextField.setDisable(mode);
        filterDonationTextField.setDisable(mode);
        housesComboBox.setDisable(mode);
        peopleComboBox.setDisable(mode);
    }

    private void initDefaults() {
        if (churchDTOObservableList.size() > 0) {
            churchesComboBox.getSelectionModel().selectFirst();
            setChurchDTO(churchDTOObservableList.get(0));
            initHousesLists();
            initHouseComboBox();
            initPeopleLists();
            initPeopleComboBox();
            initDonationsLists();
            initDonationsTableView();
            initDonationTopicsLists();
            initDonationTopicsComboBox();
            initTotalsTableView();
            setFromDatePicker();
            filterDonations();
            initDisabled(false);
        } else {
            initDisabled(true);
        }
    }

    private Double getTotalSum() {
        Double sume = 0.0;
        for (DonationDTO x : donationDTOObservableList) {
            sume = sume + Double.parseDouble(x.getSume());
        }
        return sume;
    }

    private List<TotalsDTO> getTotalsToDisplay() {
        List<TotalsDTO> totals = new ArrayList<>();
        Double totalSum = getTotalSum();
        totals.add(new TotalsDTO(EMPTY_STRING, String.format("%.0f", totalSum), donationDTOObservableList.size() + EMPTY_STRING,
                donationDTOObservableList.size() > 0 ? String.format("%.2f", totalSum / donationDTOObservableList.size()) : "0"));
        List<String> houses = getHousesFromDonations();
        for (String house : houses) {
            Double sume = getSumeForHouse(house);
            Integer donations = getDonationsForHouse(house);
            TotalsDTO totalsDTO = new TotalsDTO(house, String.format("%.0f", sume), donations + EMPTY_STRING, donations > 0 ?
                    String.format("%.2f", sume / donations) : "0");
            totals.add(totalsDTO);
        }
        return totals;
    }

    private List<String> getHousesFromDonations() {
        return donationDTOObservableList.stream().map(DonationDTO::getHouse).distinct().collect(Collectors.toList());
    }

    private Double getSumeForHouse(String house) {
        Double sume = 0.0;
        for (DonationDTO x : donationDTOObservableList) {
            if (x.getHouse().equalsIgnoreCase(house)) {
                sume = sume + Double.parseDouble(x.getSume());
            }
        }
        return sume;
    }

    private Integer getDonationsForHouse(String house) {
        Integer donations = 0;
        for (DonationDTO x : donationDTOObservableList) {
            if (x.getHouse().equalsIgnoreCase(house)) {
                donations++;
            }
        }
        return donations;
    }

    private List<DonationDTO> filterDonationsByDateFromAndDateToAndFilterHouseAndFilterPersonAndFilterDonationTopicAndFilterDonations(Timestamp dateFrom,
                                                                                                                                      Timestamp dateTo,
                                                                                                                                      String filterHouse,
                                                                                                                                      String filterPerson,
                                                                                                                                      String filterDonationTopic,
                                                                                                                                      String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterHouse)) {
            if (isStringNullOrEmpty(filterPerson)) {
                if (isStringNullOrEmpty(filterDonationTopic)) {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS;
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                } else {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                                )
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                }
            } else {
                if (isStringNullOrEmpty(filterDonationTopic)) {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)))
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                } else {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                                )
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                }
            }
        } else {
            if (isStringNullOrEmpty(filterPerson)) {
                if (isStringNullOrEmpty(filterDonationTopic)) {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)))
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                } else {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                                )
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                }
            } else {
                if (isStringNullOrEmpty(filterDonationTopic)) {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)))
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                } else {
                    if (isStringNullOrEmpty(filterDonations)) {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                                )
                                .collect(Collectors.toList());
                    } else {
                        filteredDonations = donationDTOS
                                .stream()
                                .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                        (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                        (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                        ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                                )
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return filteredDonations;
    }

    private List<DonationDTO> filterDonationsByDateFromAndDateToAndFilterHouseAndPersonAndFilterDonationTopicAndFilterDonations(Timestamp dateFrom,
                                                                                                                                Timestamp dateTo,
                                                                                                                                String filterHouse,
                                                                                                                                String person,
                                                                                                                                String filterDonationTopic,
                                                                                                                                String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterHouse)) {
            if (isStringNullOrEmpty(filterDonationTopic)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            }
        } else {
            if (isStringNullOrEmpty(filterDonationTopic)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            }
        }
        return filteredDonations;
    }

    private List<DonationDTO> filterDonationsByDateFromAndDateToAndHouseAndFilterPersonAndFilterDonationTopicAndFilterDonations(Timestamp dateFrom,
                                                                                                                                Timestamp dateTo,
                                                                                                                                String house,
                                                                                                                                String filterPerson,
                                                                                                                                String filterDonationTopic,
                                                                                                                                String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterPerson)) {
            if (isStringNullOrEmpty(filterDonationTopic)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            }
        } else {
            if (isStringNullOrEmpty(filterDonationTopic)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson))
                            )
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                    (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                    (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) &&
                                    ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                            )
                            .collect(Collectors.toList());
                }
            }
        }
        return filteredDonations;
    }

    private List<DonationDTO> filterDonationsByDateFromAndDateToAndHouseAndPersonAndFilterDonationTopicAndFilterDonations(Timestamp dateFrom,
                                                                                                                          Timestamp dateTo,
                                                                                                                          String house,
                                                                                                                          String person,
                                                                                                                          String filterDonationTopic,
                                                                                                                          String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterDonationTopic)) {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person))
                        )
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                        )
                        .collect(Collectors.toList());
            }
        } else {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic))
                        )
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> x.getUpdateDate().after(dateFrom) && x.getUpdateDate().before(dateTo) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                (!isStringNullOrEmpty(x.getDonationTopics()) && x.getDonationTopics().toLowerCase().contains(filterDonationTopic)) &&
                                ((!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                        )
                        .collect(Collectors.toList());
            }
        }
        return filteredDonations;
    }

    private void setFromDatePicker() {
        int year = Year.now().getValue();
        Timestamp januaryCurrentYear = Timestamp.valueOf(year + "-01-01 00:00:00");
        fromDatePicker.setValue(DateUtils.convertTimestampToLocalDate(januaryCurrentYear));
    }

    private List<DonationDTO> prepareRealSumeIfDonationTopicWasSelected(List<DonationDTO> donationDTOS) {
        if (!isObjectNull(selectedDonationTopicDTO)) {
            log.info("Trying to prepare real sume when donation topic was selected ...");
            List<DonationDTO> donationDTOList = new ArrayList<>();
            String topic = selectedDonationTopicDTO.getTopic();
            donationDTOS.forEach(d -> {
                DonationDTO copy = d.copy();
                Double sume = Double.parseDouble(d.getSume());
                String donationTopics = d.getDonationTopics();
                if (donationTopics.contains(topic)) {
                    int totalValidTopics = donationTopicDTOS
                            .stream()
                            .filter(donationTopicDTO -> !isObjectNull(donationTopicDTO) && donationTopics.contains(donationTopicDTO.getTopic()))
                            .collect(Collectors.toList()).size();
                    if (totalValidTopics > 0) {
                        copy.setSume(String.format("%.0f", Math.floor(sume / totalValidTopics)));
                    }
                }
                donationDTOList.add(copy);
            });
            return donationDTOList;
        }
        return donationDTOS;
    }

}
