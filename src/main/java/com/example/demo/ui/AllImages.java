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
    public static final String ID = "allimages";
    private final StorageService storageService;
    private static final  String SIZE = "200px";
    private ImageDetails imageDetails;

    public AllImages(StorageService storageService, ImageDetails imageDetails) {
        this.storageService = storageService;
        this.imageDetails = imageDetails;
    }

    @PostConstruct
    public void init() {
        List<Path> pathStream = storageService.loadAll();
        if (!pathStream.isEmpty()) {
            List<Pair<Path, InputStream>> collect = pathStream.stream().map(Path::toAbsolutePath).map(path -> {
                InputStream stream = null;
                try {
                    stream = Files.newInputStream(path);
                } catch (IOException e) {
                    log.error("Unable to find file, cause - {}", e.toString());
                }
                return Pair.of(path, stream);
            }).collect(Collectors.toList());
            for (Pair<Path, InputStream> pair : collect) {
                Image image = new Image(new StreamResource(pair.getFirst().toFile().getName(), pair::getSecond),
                                        pair.getFirst().toFile().getName());
                image.setWidth(SIZE);
                image.setHeight(SIZE);
                image.addClickListener((event) -> imageDetails.open(image, pair.getFirst().toString()));
                HorizontalLayout horizontalLayout = new HorizontalLayout(image);
                add(horizontalLayout);
            }
        } else {
            H2 h2 = new H2("You have no images yet, please upload them first");
            add(h2);
            setAlignSelf(Alignment.CENTER, h2);
        }
    }
}
