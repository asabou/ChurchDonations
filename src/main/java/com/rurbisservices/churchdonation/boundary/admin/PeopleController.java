package com.rurbisservices.churchdonation.boundary.admin;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.service.model.PersonDTO;
import com.rurbisservices.churchdonation.utils.GUIUtils;
import com.rurbisservices.churchdonation.utils.converters.ChurchDTOConverter;
import com.rurbisservices.churchdonation.utils.converters.HouseDTOConverter;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import com.rurbisservices.churchdonation.utils.exceptions.InternalServerErrorException;
import com.rurbisservices.churchdonation.utils.exceptions.NotFoundException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;
import static com.rurbisservices.churchdonation.utils.GUIUtils.showErrorAlert;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Slf4j
@Component
public class PeopleController extends AbstractController {
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        initChurchesComboBox();
        initDefaults();
        createActionsTableColumns();
        log.info("PeopleController initialized");
    }

    @FXML
    public void filterPeople() {
        String filterPeople = filterPersonTextField.getText().toLowerCase();
        List<PersonDTO> filteredPersons;
        if (isObjectNull(selectedHouseDTO)) {
            String filterHouse = filterHouseTextField.getText().toLowerCase();
            filteredPersons = filterPersonsByFilterHouseAndFilterPeople(filterHouse, filterPeople);
        } else {
            filteredPersons = filterPersonsByHouseAndFilterPeople(selectedHouseDTO.getNumberNew(), filterPeople);
        }
        setPersonDTOObservableList(filteredPersons);
        initPersonTableView();
    }

    @FXML
    public void filterHouse() {
        String filterHouse = filterHouseTextField.getText().toLowerCase();
        String filterPeople = filterPersonTextField.getText().toLowerCase();
        if (!isStringNullOrEmpty(filterHouse)) {
            List<HouseDTO> filteredHouses = houseDTOS
                    .stream()
                    .filter(x -> !isObjectNull(x) && (
                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterHouse)) ||
                                    (!isStringNullOrEmpty(x.getNumber()) && x.getNumber().toLowerCase().contains(filterHouse))))
                    .collect(Collectors.toList());
            filteredHouses.add(0, null);
            setHouseDTOObservableList(filteredHouses);
        }
        List<PersonDTO> filteredPersons = filterPersonsByFilterHouseAndFilterPeople(filterHouse, filterPeople);
        setPersonDTOObservableList(filteredPersons);
        initPersonTableView();
        initHouseComboBox();
    }


    @FXML
    public void cancel() {
        initForm(new PersonDTO(), -1);
        isAdd = true;
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
            log.info("Trying to save person ...");
            if (isObjectNull(selectedHouseDTO)) {
                showErrorAlert(303);
            } else {
                try {
                    if (isAdd) {
                        PersonDTO person = new PersonDTO(firstName.getText(), lastName.getText(), detailsTextArea.getText(), selectedHouseDTO.getId());
                        person = personService.create(person);
                        personDTOS.add(person);
                        personDTOObservableList.add(person);
                    } else {
                        PersonDTO person = new PersonDTO(id, firstName.getText(), lastName.getText(), detailsTextArea.getText(), selectedHouseDTO.getId());
                        person = personService.update(person);
                        updatePersonInLists(person);
                    }
                    cancel();
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
    public void export() {
        try {
            exportTableView(personTable, properties.getPeopleFileXLS(), properties.getPeopleFileCSV());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }

    private List<PersonDTO> filterPersonsByFilterHouseAndFilterPeople(String filterHouse, String filterPeople) {
        return personDTOS
                .stream()
                .filter(x -> !isObjectNull(x) && !isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse) && (
                        (!isStringNullOrEmpty(x.getFirstName()) && x.getFirstName().toLowerCase().contains(filterPeople)) ||
                                (!isStringNullOrEmpty(x.getLastName()) && x.getLastName().toLowerCase().contains(filterPeople)) ||
                                (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterPeople)))
                )
                .collect(Collectors.toList());
    }

    private List<PersonDTO> filterPersonsByHouseAndFilterPeople(String house, String filterPeople) {
        return personDTOS
                .stream()
                .filter(x -> !isObjectNull(x) && !isStringNullOrEmpty(x.getHouse()) && x.getHouse().equals(house) && (
                        (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filterPeople)) ||
                                (!isStringNullOrEmpty(x.getFirstName()) && x.getFirstName().toLowerCase().contains(filterPeople)) ||
                                (!isStringNullOrEmpty(x.getLastName()) && x.getLastName().toLowerCase().contains(filterPeople)))
                )
                .collect(Collectors.toList());
    }

    private void initForm(PersonDTO personDTO, Integer index) {
        this.isAdd = false;
        this.index = index;
        this.firstName.setText(personDTO.getFirstName());
        this.lastName.setText(personDTO.getLastName());
        this.detailsTextArea.setText(personDTO.getDetails());
        this.id = personDTO.getId();
    }

    @Override
    public void initHousesLists() {
        super.initHousesLists();
        houseDTOS.add(0, null);
        setHouseDTOObservableList(houseDTOS);
    }


    private void initChurchesComboBox() {
        churchesComboBox.setItems(churchDTOObservableList);
        churchesComboBox.setConverter(new ChurchDTOConverter());
        churchesComboBox.setOnAction((e) -> {
            ChurchDTO selected = (ChurchDTO) churchesComboBox.getSelectionModel().getSelectedItem();
            setChurchDTO(selected);
            filterHouseTextField.setText(EMPTY_STRING);
            filterPersonTextField.setText(EMPTY_STRING);
            log.info("Church selected {}", selected);
            initHousesLists();
            initHouseComboBox();
            filterPeople();
        });
    }

    private void initDefaults() {
        if (churchDTOObservableList.size() > 0) {
            churchesComboBox.getSelectionModel().selectFirst();
            setChurchDTO(churchDTOObservableList.get(0));
            setHouseDTO(null);
            initHousesLists();
            initHouseComboBox();
            initPeopleLists();
            initPersonTableView();
            initDisabled(false);
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
            filterPersonTextField.setText(EMPTY_STRING);
            filterPeople();
        });
    }

    private void createActionsTableColumns() {
        personTable.getColumns().addAll(createDeleteActionTableColumn(),
                createEditActionTableColumn(),
                createDonationsActionTableColumn());
    }

    private TableColumn<PersonDTO, Void> createDonationsActionTableColumn() {
        TableColumn<PersonDTO, Void> colDonations = new TableColumn<>("Donații");
        colDonations.setId("colDonations");
        Callback<TableColumn<PersonDTO, Void>, TableCell<PersonDTO, Void>> cellFactoryDelete = new Callback<TableColumn<PersonDTO, Void>,
                TableCell<PersonDTO, Void>>() {
            @Override
            public TableCell<PersonDTO, Void> call(TableColumn<PersonDTO, Void> peopleDTOVoidTableColumn) {
                final TableCell<PersonDTO, Void> cell = new TableCell<PersonDTO, Void>() {
                    Button btn = new Button("Donații");

                    {
                        btn.getStyleClass().add("button-color-blue");
                        btn.setOnAction((ActionEvent event) -> {
                            PersonDTO personDTO = getTableView().getItems().get(getIndex());
                            log.info("Clicked donations for person {}", personDTO);
                            getDonations(personDTO);
                            initDonationsTableView();
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
        colDonations.setCellFactory(cellFactoryDelete);
        return colDonations;
    }

    private TableColumn<PersonDTO, Void> createDeleteActionTableColumn() {
        TableColumn<PersonDTO, Void> colDelete = new TableColumn<>("Șterge");
        colDelete.setId("colDelete");
        Callback<TableColumn<PersonDTO, Void>, TableCell<PersonDTO, Void>> cellFactoryDelete = new Callback<TableColumn<PersonDTO, Void>,
                TableCell<PersonDTO, Void>>() {
            @Override
            public TableCell<PersonDTO, Void> call(TableColumn<PersonDTO, Void> peopleDTOVoidTableColumn) {
                final TableCell<PersonDTO, Void> cell = new TableCell<PersonDTO, Void>() {
                    Button btn = new Button("Șterge");

                    {
                        btn.getStyleClass().add("button-color-red");
                        btn.setOnAction((ActionEvent event) -> {
                            PersonDTO personDTO = getTableView().getItems().get(getIndex());
                            log.info("Clicked delete for person {}", personDTO);
                            deletePerson(personDTO);
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

    private TableColumn<PersonDTO, Void> createEditActionTableColumn() {
        TableColumn<PersonDTO, Void> colEdit = new TableColumn<>("Editează");
        colEdit.setId("colEdit");
        Callback<TableColumn<PersonDTO, Void>, TableCell<PersonDTO, Void>> cellFactoryEdit = new Callback<TableColumn<PersonDTO, Void>,
                TableCell<PersonDTO, Void>>() {
            @Override
            public TableCell<PersonDTO, Void> call(TableColumn<PersonDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<PersonDTO, Void> cell = new TableCell<PersonDTO, Void>() {
                    Button btn = new Button("Editează");

                    {
                        btn.getStyleClass().add("button-color-orange");
                        btn.setOnAction((ActionEvent event) -> {
                            PersonDTO personDTO = getTableView().getItems().get(getIndex());
                            log.info("Clicked edit for person {}", personDTO);
                            initForm(personDTO, getIndex());
                            Long houseId = personDTO.getHouseId();
                            selectedHouseDTO = houseService.findById(houseId);
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

    private void deletePerson(PersonDTO personDTO) {
        Long id = personDTO.getId();
        try {
            personService.delete(id);
            personDTOS.remove(personDTO);
            personDTOObservableList.remove(personDTO);
        } catch (NotFoundException e) {
            GUIUtils.showErrorAlert(e.getMessage());
        } catch (Throwable e) {
            showErrorAlert(100);
        }
    }

    private void getDonations(PersonDTO personDTO) {
        Long churchId = selectedChurchDTO.getId();
        Long houseId = personDTO.getHouseId();
        donationDTOObservableList = FXCollections.observableArrayList(donationService.findAllByChurchIdAndHouseId(churchId, houseId));
    }

    private void initDisabled(boolean mode) {
        filterHouseTextField.setDisable(mode);
        filterPersonTextField.setDisable(mode);
        firstName.setDisable(mode);
        lastName.setDisable(mode);
        detailsTextArea.setDisable(mode);
        saveButton.setDisable(mode);
        housesComboBox.setDisable(mode);
    }
}
