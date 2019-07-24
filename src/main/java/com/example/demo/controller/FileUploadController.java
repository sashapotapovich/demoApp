package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.exception.StorageException;
import com.example.demo.service.ImageService;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.storage.StorageService;
import java.io.InputStream;
import java.nio.file.Path;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/images")
public class FileUploadController {

    private final StorageService storageService;
    private final ImageService imageService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public FileUploadController(StorageService storageService, ImageService imageService, UserDetailsServiceImpl userDetailsService) {
        this.storageService = storageService;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().stream().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                                               "serveFile", path.getFileName().toString()).build().toString())
                                                  .collect(Collectors.toList()));

        return "index";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                                          "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            User user = userDetailsService.getUser();
            String emailHash = String.valueOf(user.getEmail().hashCode());
            storageService.store(file);
            log.info("File name - {}", file.getOriginalFilename());
            Path loadedFile = storageService.load(file.getOriginalFilename());
            try {
                imageService.saveImageInfo(loadedFile, emailHash);
            } catch (Exception ex) {
                log.error("Some error happened - ", ex);
                boolean check = storageService.deleteFile(loadedFile);
                log.info("Image was deleted from the storage due to exception - {}", check);
            }
        } catch (StorageException ex) {
            log.error(ex.getLocalizedMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/test")
    public boolean handleFileUpload(@RequestParam("file") InputStream inputStream, String fileName) {
        boolean testFlag = false;
        try {
            User user = userDetailsService.getUser();
            String emailHash = String.valueOf(user.getEmail().hashCode());
            Path store = storageService.store(inputStream, fileName);
            log.info("File name - {}", fileName);
            try {
                imageService.saveImageInfo(store, emailHash);
                testFlag = true;
            } catch (Exception ex) {
                log.error("Some error happened - ", ex);
                boolean check = storageService.deleteFile(store);
                log.info("Image was deleted from the storage due to exception - {}", check);
            }
        } catch (StorageException ex) {
            log.error(ex.getLocalizedMessage());
        }
        return testFlag;
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(StorageException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Some error occured - " + exc.getLocalizedMessage());
        return "redirect:/";
    }

}
