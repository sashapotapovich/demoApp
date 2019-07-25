package com.example.demo.ui;

import com.example.demo.entity.ImageInfo;
import com.example.demo.repository.ImageRepository;
import com.example.demo.storage.StorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SpringComponent
public class ImageDetails extends Dialog {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private final transient ImageRepository repository;
    private final transient StorageService storageService;
    private transient ImageInfo imageInfo;
    private TextField filename;
    private String initialFilename;
    private Label size;
    private Label width;
    private Label height;
    private Label uploadTime;
    private Label lastUpdateTime;
    private Button update;
    private Button close;
    private Image image;
    private SplitLayout splitLayout = new SplitLayout();

    public ImageDetails(ImageRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    @PostConstruct
    public void init() {
        filename = new TextField();
        size = new Label();
        width = new Label();
        height = new Label();
        uploadTime = new Label();
        lastUpdateTime = new Label();
        close = new Button("Close", VaadinIcon.CLOSE.create(), event -> close());
        update = new Button("Update Name");
        update.addClickListener(event -> {
            String newName = filename.getValue();
            if (!initialFilename.equals(newName)) {
                Path load = storageService.load(newName);
                if (load.toFile().exists()) {
                    imageInfo.setFileName(newName);
                    imageInfo.setUpdateTimeStamp(LocalDateTime.now());
                    Path path = storageService.updateFileName(imageInfo.getPathToFile(), newName);
                    imageInfo.setPathToFile(path.toString());
                    imageInfo = repository.saveAndFlush(imageInfo);
                    populateFields();
                } else {
                    filename.setValue(initialFilename);
                    filename.setErrorMessage("File with such name already exist");
                }
            }
        });
    }

    @SneakyThrows
    public void open(String imageSrc, String path) {
        filename.setValueChangeMode(ValueChangeMode.ON_CHANGE);
/*        filename.addValueChangeListener(event -> {
           if (!filename.getErrorMessage().isEmpty()){
               filename.setErrorMessage("");
           }
        });*/
        splitLayout.removeAll();
        imageInfo = repository.findByPathToFileIs(path).orElseThrow(() -> new NoSuchFileException("Unable to find file"));
        image = new Image(imageSrc, "placeholder");
        image.setHeight("80%");
        image.setWidth("80%");
        splitLayout.addToPrimary(image);
        populateFields();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(filename, update);
        VerticalLayout verticalLayout = new VerticalLayout(
                horizontalLayout, size, width, height, uploadTime, lastUpdateTime, close);
        verticalLayout.setSizeFull();
        splitLayout.addToSecondary(verticalLayout);
        add(splitLayout);
        open();
    }

    private void populateFields() {
        initialFilename = imageInfo.getFileName();
        filename.setValue(imageInfo.getFileName());
        size.setText(imageInfo.getSize() / 1024 + "KB");
        width.setText(String.valueOf(imageInfo.getWidth()));
        height.setText(String.valueOf(imageInfo.getHeight()));
        uploadTime.setText(imageInfo.getUploadTimeStamp().format(dateTimeFormatter));
        lastUpdateTime.setText(imageInfo.getUpdateTimeStamp().format(dateTimeFormatter));
    }

}
