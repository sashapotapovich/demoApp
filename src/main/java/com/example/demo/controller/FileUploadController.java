package com.example.demo.controller;

import com.example.demo.service.ImageService;
import com.example.demo.storage.StorageException;
import com.example.demo.storage.StorageFileNotFoundException;
import com.example.demo.storage.StorageService;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class FileUploadController {

    private final StorageService storageService;
    private final ImageService imageService;

    @Autowired
    public FileUploadController(StorageService storageService, ImageService imageService) {
        this.storageService = storageService;
        this.imageService = imageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                                               "serveFile", path.getFileName().toString()).build().toString())
                                                  .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                                          "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            storageService.store(file);
            log.info("File name - {}", file.getOriginalFilename());
            String path = storageService.load(file.getOriginalFilename()).toAbsolutePath().toString();
            log.info("Loaded file path - {}", path);
            imageService.loadImage(path);
            redirectAttributes.addFlashAttribute("message",
                                                 "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (IOException ex) {
            storageService.deleteFile(storageService.load(file.getOriginalFilename()).toAbsolutePath());
        }
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(StorageException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Some error occured - " + exc.getLocalizedMessage());
        return "redirect:/";
    }

}
