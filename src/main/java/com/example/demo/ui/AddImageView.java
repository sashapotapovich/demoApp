package com.example.demo.ui;

import com.example.demo.controller.FileUploadController;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.io.ByteArrayInputStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UIScope
@SpringComponent
@Route(value = "addimage", layout = MenuView.class)
public class AddImageView extends VerticalLayout implements RouterLayout {

    public static final String ID = "addimage";
    private final FileUploadController controller;
    private String fileName;
    private String fileType;
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload = new Upload(buffer);
    private VerticalLayout verticalLayout = new VerticalLayout();
    private VerticalLayout previewLayout = new VerticalLayout();
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private Button button = new Button();
    private ByteArrayInputStream imageBytes;
    private StreamResource resource;
    private Dialog dialog = new Dialog();
    private Text message = new Text("");
    private Button closeDialog = new Button("Close", (event) -> dialog.close());
    private Image preview = new Image();

    @Autowired
    public AddImageView(FileUploadController controller) {
        this.controller = controller;
    }

    @PostConstruct
    public void init() {
        dialog.add(new VerticalLayout(message, closeDialog));
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/gif");
        button.setText("Submit");
        button.setEnabled(false);
        button.addClickListener(listener -> {
            boolean test = controller.handleFileUpload(buffer.getInputStream(), fileName);
            toggleButton();
            if (test){
                message.setText("Image was Successfully saved");
            } else {
                message.setText("Some error occurred, please try later!");
            }
            dialog.open();
        });

        upload.addSucceededListener(event -> {
            fileType = event.getMIMEType().replace("image/", "");
            fileName = event.getFileName();
            imageBytes = (ByteArrayInputStream) buffer.getInputStream();
            resource = new StreamResource("YourImage." + fileType, () -> imageBytes);
            preview.setSrc(resource);
            preview.setAlt("alternative image text");
            preview.setVisible(true);
            toggleButton();
        });

        preview.setHeight("500px");
        preview.setWidth("500px");
        preview.setVisible(false);
        horizontalLayout.add(verticalLayout, previewLayout);
        verticalLayout.add(upload, button);
        upload.setSizeFull();
        horizontalLayout.setAlignSelf(Alignment.START, verticalLayout);
        horizontalLayout.setAlignSelf(Alignment.CENTER, previewLayout);
        previewLayout.add(preview);
        previewLayout.setAlignItems(Alignment.CENTER);
        add(horizontalLayout);
    }

    private void toggleButton() {
        button.setEnabled(!button.isEnabled());
    }

}
