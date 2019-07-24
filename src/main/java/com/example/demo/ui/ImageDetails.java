package com.example.demo.ui;

import com.example.demo.entity.ImageInfo;
import com.example.demo.repository.ImageRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UIScope
@SpringComponent
public class ImageDetails extends Dialog {

    private final ImageRepository repository;
    private static final String SIZE_PX = "800px";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private ImageInfo imageInfo;
    private TextField filename;
    private String initialFilename;
    private Label size;
    private Label width;
    private Label height;
    private Label uploadTime;
    private Label lastUpdateTime;
    private Button update;

    public ImageDetails(ImageRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        filename = new TextField();
        size = new Label();
        width = new Label();
        height = new Label();
        uploadTime = new Label();
        lastUpdateTime = new Label();
        update = new Button("Update Name");
        update.addClickListener(event -> {
            String newName = filename.getValue();
            if (!initialFilename.equals(newName)) {
                imageInfo.setFileName(newName);
                imageInfo.setUpdateTimeStamp(LocalDateTime.now());
                repository.saveAndFlush(imageInfo);
            }
        });
    }

    @SneakyThrows
    public void open(Image image, String path) {
        imageInfo = repository.findByPathToFileIs(path).orElseThrow(() -> new NoSuchFileException("Unable to find file"));
        image.setHeight(SIZE_PX);
        image.setWidth(SIZE_PX);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.addToPrimary(image);
        populateFields();
        VerticalLayout verticalLayout = new VerticalLayout(
                new HorizontalLayout(filename, update), size, width, height, uploadTime, lastUpdateTime);
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
