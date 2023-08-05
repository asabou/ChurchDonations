package com.rurbisservices.churchdonation.boundary.admin;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.utils.converters.ChurchDTOConverter;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.rurbisservices.churchdonation.utils.GUIUtils.showErrorAlert;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Slf4j
@Component
public class HousesController extends AbstractController {
    private String number;

    @FXML
    private TextField numberNew;

    @FXML
    private Label selectedHouseLabel;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        initChurchesComboBox();
        initDefaults();
        createActionsTableColumns();
        log.info("HousesController initialized");
    }

    @FXML
    public void filter() {
        String filter = filterHouseTextField.getText().toLowerCase();
        if (!isStringNullOrEmpty(filter)) {
            List<HouseDTO> filteredHouses = houseDTOS.stream().filter(x ->
                            (!isStringNullOrEmpty(x.getNumber()) && x.getNumber().toLowerCase().contains(filter) ||
                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filter))))
                    .collect(Collectors.toList());
            houseDTOObservableList = FXCollections.observableArrayList(filteredHouses);
        } else {
            houseDTOObservableList = FXCollections.observableArrayList(houseDTOS);
        }
        initHouseTableView();
        log.info("Houses filtered");
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
            try {
                if (isAdd) {
                    HouseDTO house = new HouseDTO(numberNew.getText(), detailsTextArea.getText(), selectedChurchDTO.getId());
                    house = houseService.create(house);
                    houseDTOS.add(house);
                    houseDTOObservableList.add(house);
                } else {
                    HouseDTO house = new HouseDTO(id, number, numberNew.getText(), detailsTextArea.getText(), churchId);
                    house = houseService.update(house);
                    updateHouseInLists(house);
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

    @FXML
    public void cancel() {
        initForm(new HouseDTO(), -1);
        isAdd = true;
    }

    @FXML
    public void export() {
        try {
            exportTableView(houseTable, properties.getHousesFileXLS(), properties.getHousesFileXLS());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }


    private void initChurchesComboBox() {
        log.info("Init Churches combo box");
        churchesComboBox.setItems(churchDTOObservableList);
        churchesComboBox.setConverter(new ChurchDTOConverter());
        churchesComboBox.setOnAction((e) -> {
            ChurchDTO selected = (ChurchDTO) churchesComboBox.getSelectionModel().getSelectedItem();
            setChurchDTO(selected);
            filter();
        });
    }

    private void initDefaults() {
        if (churchDTOObservableList.size() > 0) {
            churchesComboBox.getSelectionModel().selectFirst();
            setChurchDTO(churchDTOObservableList.get(0));
            initHousesLists();
            initHouseTableView();
            initDisabled(false);
        } else {
            initDisabled(true);
        }
    }

    private void createActionsTableColumns() {
        houseTable.getColumns().addAll(createDeleteActionTableColumn(),
                createEditActionTableColumn(),
                createShowPeopleActionTableColumn());
    }

    private void initForm(HouseDTO houseDTO, Integer index) {
        this.isAdd = false;
        this.index = index;
        this.number = houseDTO.getNumber();
        this.numberNew.setText(houseDTO.getNumberNew());
        this.detailsTextArea.setText(houseDTO.getDetails());
        this.churchId = houseDTO.getChurchId();
        this.id = houseDTO.getId();
    }

    private void deleteHouse(HouseDTO houseDTO) {
        try {
            Long id = houseDTO.getId();
            houseService.delete(id);
            houseDTOObservableList.remove(houseDTO);
        } catch (NotFoundException e) {
            showErrorAlert(e.getMessage());
        } catch (Throwable e) {
            showErrorAlert(100);
        }
    }

    private TableColumn<HouseDTO, Void> createEditActionTableColumn() {
        TableColumn<HouseDTO, Void> colEdit = new TableColumn<>("Editează");
        colEdit.setId("colEdit");
        Callback<TableColumn<HouseDTO, Void>, TableCell<HouseDTO, Void>> cellFactoryEdit = new Callback<TableColumn<HouseDTO, Void>,
                TableCell<HouseDTO, Void>>() {
            @Override
            public TableCell<HouseDTO, Void> call(TableColumn<HouseDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<HouseDTO, Void> cell = new TableCell<HouseDTO, Void>() {
                    Button btn = new Button("Editează");

                    {
                        btn.getStyleClass().add("button-color-orange");
                        btn.setOnAction((ActionEvent event) -> {
                            HouseDTO houseDTO = getTableView().getItems().get(getIndex());
                            initForm(houseDTO, getIndex());
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

    private TableColumn<HouseDTO, Void> createDeleteActionTableColumn() {
        TableColumn<HouseDTO, Void> colDelete = new TableColumn<>("Șterge");
        colDelete.setId("colDelete");
        Callback<TableColumn<HouseDTO, Void>, TableCell<HouseDTO, Void>> cellFactoryDelete = new Callback<TableColumn<HouseDTO, Void>,
                TableCell<HouseDTO, Void>>() {
            @Override
            public TableCell<HouseDTO, Void> call(TableColumn<HouseDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<HouseDTO, Void> cell = new TableCell<HouseDTO, Void>() {
                    Button btn = new Button("Șterge");

                    {
                        btn.getStyleClass().add("button-color-red");
                        btn.setOnAction((ActionEvent event) -> {
                            HouseDTO houseDTO = getTableView().getItems().get(getIndex());
                            deleteHouse(houseDTO);
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

    private TableColumn<HouseDTO, Void> createShowPeopleActionTableColumn() {
        TableColumn<HouseDTO, Void> colDelete = new TableColumn<>("Locuitori");
        colDelete.setId("colPeople");
        Callback<TableColumn<HouseDTO, Void>, TableCell<HouseDTO, Void>> cellFactoryDelete = new Callback<TableColumn<HouseDTO, Void>,
                TableCell<HouseDTO, Void>>() {
            @Override
            public TableCell<HouseDTO, Void> call(TableColumn<HouseDTO, Void> houseDTOVoidTableColumn) {
                final TableCell<HouseDTO, Void> cell = new TableCell<HouseDTO, Void>() {
                    Button btn = new Button("Locuitori");

                    {
                        btn.getStyleClass().add("button-color-blue");
                        btn.setOnAction((ActionEvent event) -> {
                            HouseDTO houseDTO = getTableView().getItems().get(getIndex());
                            showPeopleFromHouse(houseDTO);
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

    private void showPeopleFromHouse(HouseDTO houseDTO) {
        selectedHouseLabel.setText(houseDTO.getNumber());
        Long houseId = houseDTO.getId();
        personDTOObservableList = FXCollections.observableArrayList(personService.findAllByHouseId(houseId));
        initPersonTableView();
    }

    private void initDisabled(boolean mode) {
        filterHouseTextField.setDisable(mode);
        numberNew.setDisable(mode);
        detailsTextArea.setDisable(mode);
        saveButton.setDisable(mode);
        exportButton.setDisable(mode);
    }
}
