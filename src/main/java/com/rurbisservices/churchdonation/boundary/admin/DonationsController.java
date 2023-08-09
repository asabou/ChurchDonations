package com.rurbisservices.churchdonation.boundary.admin;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.*;
import com.rurbisservices.churchdonation.utils.DateUtils;
import com.rurbisservices.churchdonation.utils.converters.ChurchDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.DonationTopicDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.HouseDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.PersonDTOConverter;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import com.rurbisservices.churchdonation.utils.exceptions.InternalServerErrorException;
import com.rurbisservices.churchdonation.utils.exceptions.NotFoundException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;
import static com.rurbisservices.churchdonation.utils.GUIUtils.showErrorAlert;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Slf4j
@Component("donations")
public class DonationsController extends AbstractController {
    private String receipt;

    private LocalDate updateDate;

    private List<SumeDonationTopicDTO> sumeDonationTopicDTOS = new ArrayList<>();

    @FXML
    private TextField receiptNewTextField;

    @FXML
    private TextField sumeTextField;

    @FXML
    private TextField houseTextField;

    @FXML
    private TextField personTextField;

    @FXML
    private DatePicker updateDateDatePicker;

    @FXML
    private TextField topic1;
    @FXML
    private TextField topic2;
    @FXML
    private TextField topic3;
    @FXML
    private TextField topic4;
    @FXML
    private TextField topic5;

    @FXML
    private TextField sume1;

    @FXML
    private TextField sume2;

    @FXML
    private TextField sume3;

    @FXML
    private TextField sume4;

    @FXML
    private TextField sume5;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        initChurchesComboBox();
        initDefaults();
        createActionsTableColumns();
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
            filteredPersons = filterPersonsByHouseAndFilterPerson(selectedHouseDTO.displayFormat().toLowerCase(), filterPerson);
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
        initDonationsTableView();
    }

    @FXML
    public void filterDonations() {
        String filterDonations = filterDonationTextField.getText().toLowerCase();
        String filterHouse = filterHouseTextField.getText().toLowerCase();
        String filterPerson = filterPersonTextField.getText().toLowerCase();
        List<DonationDTO> filteredDonations;
        if (isObjectNull(selectedHouseDTO)) {
            if (isObjectNull(selectedPersonDTO)) {
                filteredDonations = filterDonationsByFilterHouseAndFilterPersonAndFilterDonations(filterHouse, filterPerson, filterDonations);
            } else {
                filteredDonations = filterDonationsByFilterHouseAndPersonAndFilterDonations(filterHouse, selectedPersonDTO.displayFormat().toLowerCase(),
                        filterDonations);
            }
        } else {
            if (isObjectNull(selectedPersonDTO)) {
                filteredDonations = filterDonationsByHouseAndFilterPersonAndFilterDonations(selectedHouseDTO.displayFormat().toLowerCase(), filterPerson,
                        filterDonations);
            } else {
                filteredDonations = filterDonationsByHouseAndPersonAndFilterDonations(selectedHouseDTO.displayFormat().toLowerCase(),
                        selectedPersonDTO.displayFormat().toLowerCase(),
                        filterDonations);
            }
        }
        setDonationDTOObservableList(filteredDonations);
        initDonationsTableView();
    }

    private void cancel() {
        initForm(new DonationDTO(), -1);
        setNextReceiptNumber();
        filterDonationTopicTextField.setText(EMPTY_STRING);
        setDonationTopicDTOObservableList(donationTopicDTOS);
        initDonationTopicComboBox();
        donationTopicsComboBox.getSelectionModel().selectFirst();
        initFilterDisabled(false);
        isAdd = true;
    }

    @FXML
    public void cancelFromButton() {
        cancel();
        setHouseDTO(null);
        setPersonDTO(null);
        filterHouseTextField.setText(EMPTY_STRING);
        filterPersonTextField.setText(EMPTY_STRING);
        filterHouse();
        housesComboBox.getSelectionModel().selectFirst();
        peopleComboBox.getSelectionModel().selectFirst();
        houseTextField.setText(EMPTY_STRING);
        personTextField.setText(EMPTY_STRING);
        resetTopics();
    }

    @FXML
    public void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            save();
        }
    }

    @FXML
    public void save() {
        if (!isObjectNull(selectedChurchDTO)) {
            if (isObjectNull(selectedHouseDTO)) {
                showErrorAlert(402);
            } else {
                Long selectedPersonId = !isObjectNull(selectedPersonDTO) ? selectedPersonDTO.getId() : null;
                try {
                    if (isAdd) {
                        DonationDTO donationDTO = new DonationDTO(receiptNewTextField.getText(), sumeTextField.getText(),
                                getSumeDonationTopics(), detailsTextArea.getText(), updateDateDatePicker.getValue(),
                                selectedChurchDTO.getId(),
                                selectedHouseDTO.getId(), selectedPersonId);
                        donationDTO = donationService.create(donationDTO);
                        donationDTOS.add(donationDTO);
                        donationDTOObservableList.add(donationDTO);
                    } else {
                        DonationDTO donation = new DonationDTO(id, receipt, receiptNewTextField.getText(), sumeTextField.getText(),
                                getSumeDonationTopics(), detailsTextArea.getText(), updateDate,
                                updateDateDatePicker.getValue(),
                                selectedChurchDTO.getId(), selectedHouseDTO.getId(), selectedPersonId);
                        donation = donationService.update(donation);
                        updateDonationInLists(donation);
                    }
                    cancelFromButton();
                } catch (BadRequestException | InternalServerErrorException | NotFoundException e) {
                    showErrorAlert(e.getMessage());
                } catch (Throwable e) {
                    log.error(e.getMessage(), e.getCause(), e.getLocalizedMessage());
                    showErrorAlert(100);
                }
            }
        }
    }

    @FXML
    public void filterDonationTopics() {
        log.info("Filtering donation Topics ...");
        String filter = filterDonationTopicTextField.getText().toLowerCase();
        if (isStringNullOrEmpty(filter)) {
            donationTopicDTOS = donationTopicService.findAllByChurchId(selectedChurchDTO.getId());
        } else {
            donationTopicDTOS = donationTopicDTOS.stream().filter(x -> !isObjectNull(x) && (x.getTopic().toLowerCase().contains(filter) ||
                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filter))))
                    .collect(Collectors.toList());
        }
        donationTopicDTOObservableList = FXCollections.observableArrayList(donationTopicDTOS);
        initDonationTopicComboBox();
    }

    @FXML
    public void export() {
        try {
            exportTableView(donationTable, properties.getDonationsFileXLS(), properties.getDonationsFileCSV());
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

    @Override
    protected void initPeopleLists() {
        super.initPeopleLists();
        personDTOS.add(0, null);
        setPersonDTOObservableList(personDTOS);
    }

    private List<SumeDonationTopicDTO> getSumeDonationTopics() {
        List<SumeDonationTopicDTO> sumeDonationTopics = new ArrayList<>();
        if (!isStringNullOrEmpty(topic1.getText())) sumeDonationTopics.add(new SumeDonationTopicDTO(topic1.getText(), sume1.getText()));
        if (!isStringNullOrEmpty(topic2.getText())) sumeDonationTopics.add(new SumeDonationTopicDTO(topic2.getText(), sume2.getText()));
        if (!isStringNullOrEmpty(topic3.getText())) sumeDonationTopics.add(new SumeDonationTopicDTO(topic3.getText(), sume3.getText()));
        if (!isStringNullOrEmpty(topic4.getText())) sumeDonationTopics.add(new SumeDonationTopicDTO(topic4.getText(), sume4.getText()));
        if (!isStringNullOrEmpty(topic5.getText())) sumeDonationTopics.add(new SumeDonationTopicDTO(topic5.getText(), sume5.getText()));
        return sumeDonationTopics;
    }

    private void initDonationTopicComboBox() {
        donationTopicsComboBox.setItems(donationTopicDTOObservableList);
        donationTopicsComboBox.setConverter(new DonationTopicDTOConverter());
        donationTopicsComboBox.setOnAction((e) -> {
            DonationTopicDTO selected = (DonationTopicDTO) donationTopicsComboBox.getSelectionModel().getSelectedItem();
            if (!isObjectNull(selected)) {
                if (!topic1.getText().toLowerCase().contains(selected.getTopic().toLowerCase()) && !topic2.getText().toLowerCase().contains(selected.getTopic().toLowerCase()) &&
                !topic3.getText().toLowerCase().contains(selected.getTopic().toLowerCase()) && !topic4.getText().toLowerCase().contains(selected.getTopic().toLowerCase()) &&
                !topic5.getText().toLowerCase().contains(selected.getTopic().toLowerCase())) {
                    if (isStringNullOrEmpty(topic1.getText())) {
                        topic1.setText(selected.getTopic());
                    } else {
                        if (isStringNullOrEmpty(topic2.getText())) {
                            topic2.setText(selected.getTopic());
                        } else {
                            if (isStringNullOrEmpty(topic3.getText())) {
                                topic3.setText(selected.getTopic());
                            } else {
                                if (isStringNullOrEmpty(topic4.getText())) {
                                    topic4.setText(selected.getTopic());
                                } else {
                                    if (isStringNullOrEmpty(topic5.getText())) {
                                        topic5.setText(selected.getTopic());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        log.info("Init donationTopicComboBox finalized");
    }

    private void initForm(DonationDTO donationDTO, Integer index) {
        this.isAdd = false;
        this.index = index;
        this.id = donationDTO.getId();
        this.receipt = donationDTO.getReceipt();
        this.updateDate = DateUtils.convertTimestampToLocalDate(!isObjectNull(donationDTO.getUpdateDate()) ?
                donationDTO.getUpdateDate() : DateUtils.getCurrentTimestamp());
        this.receiptNewTextField.setText(donationDTO.getReceipt());
        this.sumeTextField.setText(donationDTO.getSume());
        this.detailsTextArea.setText(donationDTO.getDetails());
        this.updateDateDatePicker.setValue(!isObjectNull(donationDTO.getUpdateDate()) ?
                DateUtils.convertTimestampToLocalDate(donationDTO.getUpdateDate()) :
                DateUtils.convertTimestampToLocalDate(DateUtils.getCurrentTimestamp()));
        this.sumeDonationTopicDTOS = donationDTO.getSumeDonationTopics();
        initSumeDonationTopicDTOS();
    }

    private void initSumeDonationTopicDTOS() {
        resetTopics();
        int size = !isListNullOrEmpty(sumeDonationTopicDTOS)? sumeDonationTopicDTOS.size() : 0;
        if (size >= 1) {
            topic1.setText(sumeDonationTopicDTOS.get(0).getTopic());
            sume1.setText(sumeDonationTopicDTOS.get(0).getSume());
        }
        if (size >= 2) {
            topic2.setText(sumeDonationTopicDTOS.get(1).getTopic());
            sume2.setText(sumeDonationTopicDTOS.get(1).getSume());
        }
        if (size >= 3) {
            topic3.setText(sumeDonationTopicDTOS.get(2).getTopic());
            sume3.setText(sumeDonationTopicDTOS.get(2).getSume());
        }
        if (size >= 4) {
            topic4.setText(sumeDonationTopicDTOS.get(3).getTopic());
            sume4.setText(sumeDonationTopicDTOS.get(3).getSume());
        }
        if (size >= 5) {
            topic5.setText(sumeDonationTopicDTOS.get(4).getTopic());
            sume5.setText(sumeDonationTopicDTOS.get(4).getSume());
        }
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
            setNextReceiptNumber();
            initDonationTopicsLists();
            initDonationTopicComboBox();
        });
        log.info("Init Churches ComboBox finalized");
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
            setNextReceiptNumber();
            initDonationTopicsLists();
            initDonationTopicComboBox();
            initDisabled(false);
            updateDateDatePicker.setValue(DateUtils.convertTimestampToLocalDate(DateUtils.getCurrentTimestamp()));
        } else {
            initDisabled(true);
        }
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
                filteredPersons = filterPersonsByHouseAndFilterPerson(selected.displayFormat().toLowerCase(), filterPerson);
                filteredPersons.add(0, null);
            }
            setPersonDTOObservableList(filteredPersons);
            initPeopleComboBox();
            filterDonations();
            houseTextField.setText(!isObjectNull(selected) ? selected.displayFormat() : EMPTY_STRING);
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
            personTextField.setText(!isObjectNull(selected) ? selected.displayFormat() : EMPTY_STRING);
        });
        log.info("Init personComboBox finalized");
    }


    private void createActionsTableColumns() {
        donationTable.getColumns().addAll(createDeleteActionTableColumn(),
                createEditActionTableColumn());
    }

    private TableColumn<DonationDTO, Void> createDeleteActionTableColumn() {
        TableColumn<DonationDTO, Void> colDelete = new TableColumn<>("Șterge");
        colDelete.setId("colDelete");
        Callback<TableColumn<DonationDTO, Void>, TableCell<DonationDTO, Void>> cellFactoryDelete = new Callback<TableColumn<DonationDTO, Void>,
                TableCell<DonationDTO, Void>>() {
            @Override
            public TableCell<DonationDTO, Void> call(TableColumn<DonationDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<DonationDTO, Void> cell = new TableCell<DonationDTO, Void>() {
                    Button btn = new Button("Șterge");

                    {
                        btn.getStyleClass().add("button-color-red");
                        btn.setOnAction((ActionEvent event) -> {
                            DonationDTO donationDTO = getTableView().getItems().get(getIndex());
                            log.info("Clicked delete for donation {}", donationDTO);
                            deleteDonation(donationDTO);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                cell.setAlignment(Pos.BASELINE_CENTER);
                return cell;
            }
        };
        colDelete.setCellFactory(cellFactoryDelete);
        return colDelete;
    }

    private TableColumn<DonationDTO, Void> createEditActionTableColumn() {
        TableColumn<DonationDTO, Void> colEdit = new TableColumn<>("Editează");
        colEdit.setId("colEdit");
        Callback<TableColumn<DonationDTO, Void>, TableCell<DonationDTO, Void>> cellFactoryEdit = new Callback<TableColumn<DonationDTO, Void>,
                TableCell<DonationDTO, Void>>() {
            @Override
            public TableCell<DonationDTO, Void> call(TableColumn<DonationDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<DonationDTO, Void> cell = new TableCell<DonationDTO, Void>() {
                    Button btn = new Button("Editează");

                    {
                        btn.getStyleClass().add("button-color-orange");
                        btn.setOnAction((ActionEvent event) -> {
                            DonationDTO donationDTO = getTableView().getItems().get(getIndex());
                            log.info("Clicked Edit button for donation {}", donationDTO);
                            initForm(donationDTO, getIndex());
                            Long houseId = donationDTO.getHouseId();
                            selectedHouseDTO = houseService.findById(houseId);
                            houseTextField.setText(selectedHouseDTO.displayFormat());
                            Long personId = donationDTO.getPersonId();
                            if (!isObjectNull(personId)) {
                                selectedPersonDTO = personService.findById(personId);
                                personTextField.setText(selectedPersonDTO.displayFormat());
                                if (!isObjectNull(selectedPersonDTO.getHouseId())) {
                                    selectedHouseDTO = houseService.findById(selectedPersonDTO.getHouseId());
                                    houseTextField.setText(selectedHouseDTO.displayFormat());
                                }
                            }
                            initFilterDisabled(true);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                cell.setAlignment(Pos.BASELINE_CENTER);
                return cell;
            }
        };
        colEdit.setCellFactory(cellFactoryEdit);
        return colEdit;
    }

    private void deleteDonation(DonationDTO donation) {
        try {
            Long id = donation.getId();
            donationService.delete(id);
            donationDTOS.remove(donation);
            donationDTOObservableList.remove(donation);
            setNextReceiptNumber();
        } catch (NotFoundException e) {
            showErrorAlert(e.getMessage());
        } catch (Throwable e) {
            showErrorAlert(100);
        }
    }

    private void initDisabled(boolean mode) {
        filterHouseTextField.setDisable(mode);
        filterPersonTextField.setDisable(mode);
        filterDonationTextField.setDisable(mode);
        receiptNewTextField.setDisable(mode);
        sumeTextField.setDisable(mode);
        detailsTextArea.setDisable(mode);
        saveButton.setDisable(mode);
        housesComboBox.setDisable(mode);
        peopleComboBox.setDisable(mode);
        cancelButton.setDisable(mode);
        filterDonationTopicTextField.setDisable(mode);
        donationTopicsComboBox.setDisable(mode);
        updateDateDatePicker.setDisable(mode);
        topic1.setDisable(mode);
        sume1.setDisable(mode);
        topic2.setDisable(mode);
        sume2.setDisable(mode);
        topic3.setDisable(mode);
        sume3.setDisable(mode);
        topic4.setDisable(mode);
        sume4.setDisable(mode);
        topic5.setDisable(mode);
        sume5.setDisable(mode);
        houseTextField.setDisable(true);
        personTextField.setDisable(true);
    }

    private void resetTopics() {
        topic1.setText(EMPTY_STRING);
        sume1.setText(EMPTY_STRING);
        topic2.setText(EMPTY_STRING);
        sume2.setText(EMPTY_STRING);
        topic3.setText(EMPTY_STRING);
        sume3.setText(EMPTY_STRING);
        topic4.setText(EMPTY_STRING);
        sume4.setText(EMPTY_STRING);
        topic5.setText(EMPTY_STRING);
        sume5.setText(EMPTY_STRING);
    }

    private void resetFields() {
        filterHouseTextField.setText(EMPTY_STRING);
        filterPersonTextField.setText(EMPTY_STRING);
        filterDonationTextField.setText(EMPTY_STRING);
        cancel();
    }

    private void setNextReceiptNumber() {
        receiptNewTextField.setText(donationService.getNextReceiptByChurchId(selectedChurchDTO.getId()).toString());
    }

    protected List<DonationDTO> filterDonationsByFilterHouseAndFilterPersonAndFilterDonations(String filterHouse,
                                                                                              String filterPerson,
                                                                                              String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterHouse)) {
            if (isStringNullOrEmpty(filterPerson)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS;
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> !isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson))
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> !isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson) &&
                                    (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                            )
                            .collect(Collectors.toList());
                }
            }
        } else {
            if (isStringNullOrEmpty(filterPerson)) {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)))
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                    (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                            )
                            .collect(Collectors.toList());
                }
            } else {
                if (isStringNullOrEmpty(filterDonations)) {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> !isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)))
                            .collect(Collectors.toList());
                } else {
                    filteredDonations = donationDTOS
                            .stream()
                            .filter(x -> !isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson) &&
                                    (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                    (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                            )
                            .collect(Collectors.toList());
                }
            }
        }
        return filteredDonations;
    }

    protected List<DonationDTO> filterDonationsByFilterHouseAndPersonAndFilterDonations(String filterHouse,
                                                                                        String person,
                                                                                        String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterHouse)) {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)))
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                        )
                        .collect(Collectors.toList());
            }
        } else {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)))
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) &&
                                (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                                (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                        )
                        .collect(Collectors.toList());
            }
        }
        return filteredDonations;
    }

    private List<DonationDTO> filterDonationsByHouseAndFilterPersonAndFilterDonations(String house, String filterPerson, String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterPerson)) {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)))
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) && (
                                (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                        ))
                        .collect(Collectors.toList());
            }
        } else {
            if (isStringNullOrEmpty(filterDonations)) {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)))
                        .collect(Collectors.toList());
            } else {
                filteredDonations = donationDTOS
                        .stream()
                        .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().contains(filterPerson)) && (
                                (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                        (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations))
                        ))
                        .collect(Collectors.toList());
            }
        }
        return filteredDonations;
    }

    private List<DonationDTO> filterDonationsByHouseAndPersonAndFilterDonations(String house, String person, String filterDonations) {
        List<DonationDTO> filteredDonations;
        if (isStringNullOrEmpty(filterDonations)) {
            filteredDonations = donationDTOS
                    .stream()
                    .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                            (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)))
                    .collect(Collectors.toList());
        } else {
            filteredDonations = donationDTOS
                    .stream()
                    .filter(x -> (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                            (!isStringNullOrEmpty(x.getPerson()) && x.getPerson().toLowerCase().equals(person)) && (
                            (!isStringNullOrEmpty(x.getReceipt()) && x.getReceipt().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getSume()) && x.getSume().toLowerCase().contains(filterDonations)) ||
                                    (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterDonations)))
                    )
                    .collect(Collectors.toList());
        }
        return filteredDonations;
    }

    private void initFilterDisabled(boolean mode) {
        filterHouseTextField.setDisable(mode);
        housesComboBox.setDisable(mode);
        filterPersonTextField.setDisable(mode);
        peopleComboBox.setDisable(mode);
        filterDonationTextField.setDisable(mode);
    }

}
