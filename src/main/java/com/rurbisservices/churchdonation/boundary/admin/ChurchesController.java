package com.rurbisservices.churchdonation.boundary.admin;

import com.rurbisservices.churchdonation.boundary.AbstractController;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import com.rurbisservices.churchdonation.utils.MessagesUtils;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import com.rurbisservices.churchdonation.utils.exceptions.InternalServerErrorException;
import com.rurbisservices.churchdonation.utils.exceptions.NotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rurbisservices.churchdonation.utils.GUIUtils.*;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.exportTableView;

@Slf4j
@Component
public class ChurchesController extends AbstractController {
    private String name;

    @FXML
    private TextField nameNew;

    @FXML
    private TextField street;

    @FXML
    private TextField number;

    @FXML
    public void initialize() {
        initChurchesObservableList();
        createActionsTableColumns();
        initChurchesTableView();
        log.info("Churches controller initialized");
    }

    @FXML
    public void save() {
        try {
            if (isAdd) {
                ChurchDTO churchDTO = new ChurchDTO(nameNew.getText(), street.getText(), number.getText(), detailsTextArea.getText());
                churchDTO = churchService.create(churchDTO);
                churchDTOObservableList.add(churchDTO);
            } else {
                ChurchDTO churchDTO = new ChurchDTO(id, name, nameNew.getText(), street.getText(), number.getText(), detailsTextArea.getText());
                churchDTO = churchService.update(churchDTO);
                churchDTOObservableList.set(index, churchDTO);
            }
            cancel();
        } catch (BadRequestException | InternalServerErrorException | NotFoundException e) {
            showErrorAlert(e.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e.getCause(), e.getLocalizedMessage());
            showErrorAlert(100);
        }
    }

    @FXML
    public void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            save();
        }
    }

    @FXML
    public void cancel() {
        initForm(new ChurchDTO(), -1);
        isAdd = true;
    }

    @FXML
    public void export() {
        try {
            exportTableView(churchTable, properties.getChurchesFileXLS(), properties.getChurchesFileCSV());
        } catch (Exception e) {
            showErrorAlert(100);
        }
    }

    private void createActionsTableColumns() {
        churchTable.getColumns().addAll(createDeleteActionTableColumn(), createEditActionTableColumn());
        log.info("Actions table created");
    }

    private void initForm(ChurchDTO churchDTO, Integer index) {
        this.isAdd = false;
        this.index = index;
        this.id = churchDTO.getId();
        this.name = churchDTO.getName();
        this.nameNew.setText(churchDTO.getNameNew());
        this.street.setText(churchDTO.getStreet());
        this.number.setText(churchDTO.getNumber());
        this.detailsTextArea.setText(churchDTO.getDetails());
    }

    private TableColumn<ChurchDTO, Void> createEditActionTableColumn() {
        TableColumn<ChurchDTO, Void> colEdit = new TableColumn("Editează");
        colEdit.setId("colEdit");
        Callback<TableColumn<ChurchDTO, Void>, TableCell<ChurchDTO, Void>> cellFactory = new Callback<TableColumn<ChurchDTO, Void>, TableCell<ChurchDTO, Void>>() {
            @Override
            public TableCell<ChurchDTO, Void> call(final TableColumn<ChurchDTO, Void> churchDTOVoidTableColumn) {
                final TableCell<ChurchDTO, Void> cell = new TableCell<ChurchDTO, Void>() {
                    private final Button btn = new Button("Editează");

                    {
                        btn.getStyleClass().add("button-color-orange");
                        btn.setOnAction((ActionEvent event) -> {
                            ChurchDTO churchDTO = getTableView().getItems().get(getIndex());
                            log.info("Edit button pressed for church {}", churchDTO);
                            initForm(churchDTO, getIndex());
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

    private TableColumn<ChurchDTO, Void> createDeleteActionTableColumn() {
        TableColumn<ChurchDTO, Void> colDelete = new TableColumn("Șterge");
        colDelete.setId("colDelete");
        Callback<TableColumn<ChurchDTO, Void>, TableCell<ChurchDTO, Void>> cellFactoryDelete = new Callback<TableColumn<ChurchDTO, Void>,
                TableCell<ChurchDTO, Void>>() {
            @Override
            public TableCell<ChurchDTO, Void> call(final TableColumn<ChurchDTO, Void> churchDTOVoidTableColumn) {
                final TableCell<ChurchDTO, Void> cell = new TableCell<ChurchDTO, Void>() {
                    private final Button btn = new Button("Șterge");

                    {
                        btn.getStyleClass().add("button-color-red");
                        btn.setOnAction((ActionEvent event) -> {
                            ChurchDTO churchDTO = getTableView().getItems().get(getIndex());
                            log.info("Delete button pressed for church {}", churchDTO);
                            deleteChurch(churchDTO);
                            cancel();
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

    private void deleteChurch(ChurchDTO church) {
        Long id = church.getId();
        String name = church.getNameNew();
        Alert alert = createConfirmationAlert(String.format(MessagesUtils.getMessage(104), name), MessagesUtils.getMessage(105));
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == buttonTypeDelete) {
            try {
                churchService.delete(id);
                churchDTOObservableList.remove(church);
            } catch (NotFoundException e) {
                showErrorAlert(e.getMessage());
            } catch (Throwable e) {
                showErrorAlert(100);
            }
        }
    }
}
