package com.example.demo.ui;

import com.example.demo.controller.FileUploadController;
import com.example.demo.entity.CurrentUser;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UIScope
@SpringComponent
@Route(value = "images", layout = MenuView.class)
public class ImagesView extends VerticalLayout implements RouterLayout {

    public static final String ID = "images";
    private final FileUploadController controller;
    private VerticalLayout verticalLayout = new VerticalLayout();

    @Autowired
    public ImagesView(FileUploadController controller) {
        this.controller = controller;
    }

    @PostConstruct
    public void init() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            controller.handleFileUpload(buffer.getInputStream(event.getFileName()), event.getFileName());
        });
        verticalLayout.add(upload);
        add(verticalLayout);
    }

}
