package com.example.demo.ui;

import com.example.demo.storage.StorageService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@UIScope
@SpringComponent
@Route(value = "allimages", layout = MenuView.class)
public class AllImages extends VerticalLayout implements RouterLayout {
    private static final String SIZE = "300px";
    public static final String ID = "allimages";
    private final transient StorageService storageService;
    private ImageDetails imageDetails;

    public AllImages(StorageService storageService, ImageDetails imageDetails) {
        this.storageService = storageService;
        this.imageDetails = imageDetails;
    }

    @PostConstruct
    public void init() {
        List<Path> pathStream = storageService.loadAll();
        if (!pathStream.isEmpty()) {
            List<Pair<Path, InputStream>> pairInStrList = pathStream.stream().map(Path::toAbsolutePath).map(path -> {
                InputStream stream = null;
                try {
                    stream = Files.newInputStream(path);
                } catch (IOException e) {
                    log.error("Unable to find file, cause - {}", e.toString());
                }
                return Pair.of(path, stream);
            }).collect(Collectors.toList());
            for (int i = 0; i < pairInStrList.size(); i = i + 6) {
                if (i + 5 < pairInStrList.size()) {
                    List<Pair<Path, InputStream>> pairs = pairInStrList.subList(i, i + 5);
                    populateImages(pairs);
                } else {
                    List<Pair<Path, InputStream>> pairs = pairInStrList.subList(i, pairInStrList.size());
                    populateImages(pairs);
                    i = pairInStrList.size();
                }
            }
        } else {
            H2 h2 = new H2("You have no images yet, please upload them first");
            add(h2);
            setAlignSelf(Alignment.CENTER, h2);
        }
        setAlignItems(Alignment.CENTER);
    }

    private void populateImages(List<Pair<Path, InputStream>> pairs) {
        List<Image> images = new ArrayList<>();
        pairs.forEach(pathInputStreamPair -> {
            Image image = new Image(new StreamResource(
                    pathInputStreamPair.getFirst().toFile().getName(),
                    pathInputStreamPair::getSecond), pathInputStreamPair.getFirst().toFile().getName());
            image.setWidth(SIZE);
            image.setHeight(SIZE);
            image.addClickListener(event -> imageDetails.open(image.getSrc(), pathInputStreamPair.getFirst().toString()));
            images.add(image);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(images.toArray(new Image[0]));
        horizontalLayout.setAlignItems(Alignment.END);
        add(horizontalLayout);
    }
}
