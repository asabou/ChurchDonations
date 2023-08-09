package com.rurbisservices.churchdonation.boundary;

import com.rurbisservices.churchdonation.service.impl.*;
import com.rurbisservices.churchdonation.service.model.*;
import com.rurbisservices.churchdonation.utils.ApplicationProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Slf4j
@Component
public abstract class AbstractController {
    @Autowired
    protected ChurchServiceImpl churchService;

    @Autowired
    protected HouseServiceImpl houseService;

    @Autowired
    protected PersonServiceImpl personService;

    @Autowired
    protected DonationServiceImpl donationService;

    @Autowired
    protected DonationTopicServiceImpl donationTopicService;

    @Autowired
    protected ApplicationProperties properties;

    protected ObservableList<ChurchDTO> churchDTOObservableList = FXCollections.emptyObservableList();

    protected List<HouseDTO> houseDTOS = new ArrayList<>();

    protected ObservableList<HouseDTO> houseDTOObservableList = FXCollections.emptyObservableList();

    protected List<PersonDTO> personDTOS = new ArrayList<>();

    protected ObservableList<PersonDTO> personDTOObservableList = FXCollections.emptyObservableList();

    protected List<DonationTopicDTO> donationTopicDTOS = new ArrayList<>();

    protected ObservableList<DonationTopicDTO> donationTopicDTOObservableList = FXCollections.emptyObservableList();

    protected List<DonationDTO> donationDTOS = new ArrayList<>();

    protected ObservableList<DonationDTO> donationDTOObservableList = FXCollections.emptyObservableList();

    protected ChurchDTO selectedChurchDTO;

    protected HouseDTO selectedHouseDTO;

    protected PersonDTO selectedPersonDTO;

    protected DonationTopicDTO selectedDonationTopicDTO;

    protected boolean isAdd = true;

    protected Long id;

    protected Long churchId;

    protected Integer index;

    @FXML
    public ComboBox churchesComboBox;

    @FXML
    public ComboBox housesComboBox;

    @FXML
    public ComboBox donationTopicsComboBox;

    @FXML
    public ComboBox peopleComboBox;

    @FXML
    public TableView churchTable;

    @FXML
    public TableView houseTable;

    @FXML
    public TableView personTable;

    @FXML
    public TableView donationTable;

    @FXML
    public TableView donationTopicsTable;

    @FXML
    public Button saveButton;

    @FXML
    public Button cancelButton;

    @FXML
    public Button exportButton;

    @FXML
    public TextArea detailsTextArea;

    @FXML
    public TextField filterPersonTextField;

    @FXML
    public TextField filterHouseTextField;

    @FXML
    public TextField filterDonationTopicTextField;

    @FXML
    public TextField filterDonationTextField;

    protected void updateHouseInLists(HouseDTO house) {
        int realIndex = IntStream.range(0, houseDTOS.size())
                .filter(i -> !isObjectNull(houseDTOS.get(i)) && Objects.equals(houseDTOS.get(i).getId(), id))
                .findFirst()
                .orElse(-1);
        if (realIndex >= 0) {
            log.info("HouseDTOS real index {}", realIndex);
            houseDTOS.set(realIndex, house);
        }
        log.info("Index {}", index);
        houseDTOObservableList.set(index, house);
    }

    protected void updatePersonInLists(PersonDTO person) {
        int realIndex = IntStream.range(0, personDTOS.size())
                .filter(i -> !isObjectNull(houseDTOS.get(i)) && Objects.equals(personDTOS.get(i).getId(), id))
                .findFirst()
                .orElse(-1);
        if (realIndex >= 0) {
            personDTOS.set(realIndex, person);
            log.info("PersonDTO real index {}", realIndex);
        }
        log.info("Index {}", index);
        personDTOObservableList.set(index, person);
    }

    protected void updateDonationTopicInLists(DonationTopicDTO donationTopic) {
        int realIndex = IntStream.range(0, donationTopicDTOS.size())
                .filter(i -> !isObjectNull(donationTopicDTOS.get(i)) && Objects.equals(donationTopicDTOS.get(i).getId(), id))
                .findFirst()
                .orElse(-1);
        if (realIndex >= 0) {
            donationTopicDTOS.set(realIndex, donationTopic);
            log.info("Donation topic real index {}", realIndex);
        }
        log.info("Index {}", index);
        donationTopicDTOObservableList.set(index, donationTopic);
    }

    protected void updateDonationInLists(DonationDTO donation) {
        int realIndex = IntStream.range(0, donationDTOS.size())
                .filter(i -> !isObjectNull(donationDTOS.get(i)) && Objects.equals(donationDTOS.get(i).getId(), id))
                .findFirst()
                .orElse(-1);
        if (realIndex >= 0) {
            log.info("DonationDTOS real index {}", realIndex);
            donationDTOS.set(realIndex, donation);
        }
        log.info("Index {}", index);
        donationDTOObservableList.set(index, donation);
    }

    protected void initChurchesObservableList() {
        churchDTOObservableList = FXCollections.observableArrayList(churchService.findAll());
    }

    protected void setHouseDTO(HouseDTO houseDTO) {
        this.selectedHouseDTO = houseDTO;
    }

    protected void setChurchDTO(ChurchDTO churchDTO) {
        this.selectedChurchDTO = churchDTO;
    }

    protected void setPersonDTO(PersonDTO personDTO) {
        this.selectedPersonDTO = personDTO;
    }

    protected void setDonationTopicDTO(DonationTopicDTO donationTopicDTO) {
        this.selectedDonationTopicDTO = donationTopicDTO;
    }

    protected void setPersonDTOObservableList(List<PersonDTO> personDTOS) {
        this.personDTOObservableList = FXCollections.observableArrayList(personDTOS);
    }

    protected void setHouseDTOObservableList(List<HouseDTO> houseDTOS) {
        this.houseDTOObservableList = FXCollections.observableArrayList(houseDTOS);
    }

    protected void setDonationDTOObservableList(List<DonationDTO> donationDTOS) {
        donationDTOObservableList = FXCollections.observableArrayList(donationDTOS);
    }

    protected void setDonationTopicDTOObservableList(List<DonationTopicDTO> donationTopicDTOS) {
        donationTopicDTOObservableList = FXCollections.observableArrayList(donationTopicDTOS);
    }

    protected void initHousesLists() {
        houseDTOS = houseService.findAllByChurchId(selectedChurchDTO.getId());
        setHouseDTOObservableList(houseDTOS);
    }

    protected void initPeopleLists() {
        personDTOS = personService.findAllByChurchId(selectedChurchDTO.getId());
        setPersonDTOObservableList(personDTOS);
    }

    protected void initDonationsLists() {
        donationDTOS = donationService.findAllByChurchId(selectedChurchDTO.getId());
        setDonationDTOObservableList(donationDTOS);
    }

    protected void initDonationTopicsLists() {
        donationTopicDTOS = donationTopicService.findAllByChurchId(selectedChurchDTO.getId());
        donationTopicDTOS.add(0, null);
        setDonationTopicDTOObservableList(donationTopicDTOS);
    }

    protected void initChurchesTableView() {
        List<TableColumn> tableColumnList = churchTable.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
        });
        churchTable.setItems(churchDTOObservableList);
        log.info("Table churches init");
    }

    protected void initHouseTableView() {
        List<TableColumn> tableColumnList = houseTable.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
        });
        houseTable.setItems(houseDTOObservableList);
    }

    protected void initPersonTableView() {
        List<TableColumn> tableColumnList = personTable.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
        });
        personTable.setItems(personDTOObservableList);
        log.info("Init person table view");
    }

    protected void initDonationsTableView() {
        List<TableColumn> tableColumnList = donationTable.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
            if (col.getId().equals("sume") || col.getId().equals("receipt")) {
                col.setComparator((Comparator<String>) (o1, o2) -> {
                    double a = Double.parseDouble(o1);
                    double b = Double.parseDouble(o2);
                    return Double.compare(a, b);
                });
            }
            if (col.getId().toLowerCase().contains("date")) {
                col.setComparator((Comparator<String>) (String o1, String o2) -> {
                    LocalDate d1 = LocalDate.parse(o1, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    LocalDate d2 = LocalDate.parse(o2, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    return d1.compareTo(d2);
                });
            }
        });
        donationTable.setItems(donationDTOObservableList);
        log.info("Init donation table view ...");
    }

    protected void initDonationTopicTableView() {
        List<TableColumn> tableColumnList = donationTopicsTable.getColumns();
        tableColumnList.forEach(col -> {
            if (!col.getId().startsWith("col")) {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
            }
        });
        donationTopicsTable.setItems(donationTopicDTOObservableList);
    }

    protected List<PersonDTO> filterPersonsByFilterHouseAndFilterPerson(String filterHouse, String filterPerson) {
        return personDTOS
                .stream()
                .filter(x -> !isObjectNull(x) && ((!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().contains(filterHouse)) &&
                        ((!isStringNullOrEmpty(x.getFirstName()) && x.getFirstName().toLowerCase().contains(filterPerson)) ||
                                (!isStringNullOrEmpty(x.getLastName()) && x.getLastName().toLowerCase().contains(filterPerson)))
                ))
                .collect(Collectors.toList());
    }

    protected List<PersonDTO> filterPersonsByHouseAndFilterPerson(String house, String filterPerson) {
        return personDTOS
                .stream()
                .filter(x -> !isObjectNull(x) && (
                        (!isStringNullOrEmpty(x.getHouse()) && x.getHouse().toLowerCase().equals(house)) &&
                                ((!isStringNullOrEmpty(x.getFirstName()) && x.getFirstName().toLowerCase().contains(filterPerson)) ||
                                        (!isStringNullOrEmpty(x.getLastName()) && x.getLastName().toLowerCase().contains(filterPerson)))))
                .collect(Collectors.toList());
    }
}
