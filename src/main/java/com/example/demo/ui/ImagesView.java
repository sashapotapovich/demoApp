package com.example.demo.ui;

import com.example.demo.controller.FileUploadController;
import com.example.demo.entity.CurrentUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Slf4j
@UIScope
@SpringComponent
@Route(value = "images", layout = MenuView.class)
public class ImagesView extends VerticalLayout implements RouterLayout {

    public static final String ID = "images";
    private final FileUploadController controller;
    @Autowired @Lazy
    private CurrentUser currentUser;
    private String fileName;
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload = new Upload(buffer);
    private VerticalLayout verticalLayout = new VerticalLayout();
    private Button button = new Button();

    @Autowired
    public ImagesView(FileUploadController controller) {
        this.controller = controller;
    }

    @PostConstruct
    public void init() {
        button.setText("Submit");
        button.setEnabled(false);
        button.addClickListener(listener -> {
            String userEmail = currentUser.getUser().getEmail();
            String path = String.valueOf(userEmail.hashCode());
            controller.handleFileUpload(buffer.getInputStream(), path, fileName);
            toggleButton();
        });

        upload.addSucceededListener(event -> {
            fileName = event.getFileName();
            toggleButton();
        });

        verticalLayout.add(upload, button);
        verticalLayout.setAlignItems(Alignment.BASELINE);
        add(verticalLayout);
    }

    private void toggleButton() {
        button.setEnabled(!button.isEnabled());
    }

}
