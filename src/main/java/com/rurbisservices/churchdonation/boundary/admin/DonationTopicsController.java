package com.rurbisservices.churchdonation.boundary.admin;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;
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
public class DonationTopicsController extends AbstractController {
    @FXML
    private TextField topicTextField;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        initChurchesComboBox();
        initDefaults();
        createActionsTableColumns();
        log.info("DonationTopicController initialized");
    }

    @FXML
    public void filter() {
        log.info("Filter donation topics ...");
        String filter = filterDonationTopicTextField.getText().toLowerCase();
        if (isStringNullOrEmpty(filter)) {
            donationTopicDTOObservableList = FXCollections.observableArrayList(donationTopicDTOS);
        } else {
            List<DonationTopicDTO> filteredDonationTopics = donationTopicDTOS.stream().filter(x ->
                            (!isStringNullOrEmpty(x.getTopic()) && x.getTopic().toLowerCase().contains(filter)) ||
                            (!isStringNullOrEmpty(x.getDetails()) && x.getDetails().toLowerCase().contains(filter)))
                    .collect(Collectors.toList());
            donationTopicDTOObservableList = FXCollections.observableArrayList(filteredDonationTopics);
        }
        initDonationTopicTableView();
    }

    @FXML
    public void save() {
        if (!isObjectNull(selectedChurchDTO)) {
            try {
                if (isAdd) {
                    DonationTopicDTO donationTopic = new DonationTopicDTO(topicTextField.getText(), detailsTextArea.getText(),
                            selectedChurchDTO.getId());
                    donationTopic = donationTopicService.create(donationTopic);
                    donationTopicDTOS.add(donationTopic);
                    donationTopicDTOObservableList.add(donationTopic);
                } else {
                    DonationTopicDTO donationTopic = new DonationTopicDTO(id, topicTextField.getText(), detailsTextArea.getText(),
                            churchId);
                    donationTopic = donationTopicService.update(donationTopic);
                    updateDonationTopicInLists(donationTopic);
                }
                cancel();
            } catch (BadRequestException | InternalServerErrorException | NotFoundException e) {
                showErrorAlert(e.getMessage());
            } catch (Throwable e) {
                log.error(e.getMessage());
                showErrorAlert(100);
            }
        }
    }

    @FXML
    public void cancel() {
        initForm(new DonationTopicDTO(), -1);
        isAdd = true;
    }

    @FXML
    public void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            log.info("On Enter event triggered");
            save();
        }
    }

    @FXML
    public void export() {
        try {
            exportTableView(donationTopicsTable, properties.getDonationTopicsFileXLS(), properties.getDonationTopicsFileCSV());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }

    private void initDonationTopicList() {
        if (churchDTOObservableList.size() > 0) {
            donationTopicDTOS = donationTopicService.findAllByChurchId(selectedChurchDTO.getId());
            donationTopicDTOObservableList = FXCollections.observableArrayList(donationTopicDTOS);
            initDisabled(false);
        } else {
            initDisabled(true);
        }
    }

    private void createActionsTableColumns() {
        donationTopicsTable.getColumns().addAll(createDeleteActionTableColumn(), createEditActionTableColumn());
        log.info("Actions table created");
    }

    private TableColumn<DonationTopicDTO, Void> createEditActionTableColumn() {
        TableColumn<DonationTopicDTO, Void> colEdit = new TableColumn("Editează");
        colEdit.setId("colEdit");
        Callback<TableColumn<DonationTopicDTO, Void>, TableCell<DonationTopicDTO, Void>> cellFactory = new Callback<TableColumn<DonationTopicDTO, Void>, TableCell<DonationTopicDTO, Void>>() {
            @Override
            public TableCell<DonationTopicDTO, Void> call(final TableColumn<DonationTopicDTO, Void> DonationTopicDTOVoidTableColumn) {
                final TableCell<DonationTopicDTO, Void> cell = new TableCell<DonationTopicDTO, Void>() {
                    private final Button btn = new Button("Editează");
                    {
                        btn.getStyleClass().add("button-color-orange");
                        btn.setOnAction((ActionEvent event) -> {
                            DonationTopicDTO donationTopicDTO = getTableView().getItems().get(getIndex());
                            log.info("Edit button pressed for donation topic {}", donationTopicDTO);
                            initForm(donationTopicDTO, getIndex());
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
        colEdit.setCellFactory(cellFactory);
        return colEdit;
    }

    private TableColumn<DonationTopicDTO, Void> createDeleteActionTableColumn() {
        TableColumn<DonationTopicDTO, Void> colDelete = new TableColumn("Șterge");
        colDelete.setId("colDelete");
        Callback<TableColumn<DonationTopicDTO, Void>, TableCell<DonationTopicDTO, Void>> cellFactoryDelete = new Callback<TableColumn<DonationTopicDTO, Void>,
                TableCell<DonationTopicDTO, Void>>() {
            @Override
            public TableCell<DonationTopicDTO, Void> call(final TableColumn<DonationTopicDTO, Void> DonationTopicDTOVoidTableColumn) {
                final TableCell<DonationTopicDTO, Void> cell = new TableCell<DonationTopicDTO, Void>() {
                    private final Button btn = new Button("Șterge");
                    {
                        btn.getStyleClass().add("button-color-red");
                        btn.setOnAction((ActionEvent event) -> {
                            DonationTopicDTO donationTopicDTO = getTableView().getItems().get(getIndex());
                            log.info("Delete button pressed for donation topic {}", donationTopicDTO);
                            deleteDonationTopic(donationTopicDTO);
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

    private void deleteDonationTopic(DonationTopicDTO donationTopic) {
        Long id = donationTopic.getId();
        try {
            donationTopicService.delete(id);
            donationTopicDTOS.remove(donationTopic);
            donationTopicDTOObservableList.remove(donationTopic);
        } catch (NotFoundException e) {
            showErrorAlert(e.getMessage());
        } catch (Throwable e) {
            showErrorAlert(100);
        }
    }

    private void initForm(DonationTopicDTO donationTopicDTO, Integer index) {
        this.isAdd = false;
        this.index = index;
        this.id = donationTopicDTO.getId();
        this.topicTextField.setText(donationTopicDTO.getTopic());
        this.detailsTextArea.setText(donationTopicDTO.getDetails());
        this.churchId = donationTopicDTO.getChurchId();
    }

    private void initDisabled(boolean mode) {
        filterDonationTopicTextField.setDisable(mode);
        topicTextField.setDisable(mode);
        detailsTextArea.setDisable(mode);
        saveButton.setDisable(mode);
    }

    private void initChurchesComboBox() {
        log.info("Init churches combo box ...");
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
            initDonationTopicList();
            initDonationTopicTableView();
            initDisabled(false);
        } else {
            initDisabled(true);
        }
    }


}
