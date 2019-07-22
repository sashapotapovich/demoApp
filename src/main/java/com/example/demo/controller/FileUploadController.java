package com.example.demo.controller;

import com.example.demo.exception.StorageException;
import com.example.demo.service.ImageService;
import com.example.demo.storage.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails){
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                log.error(username);
            }
            storageService.store(file);
            log.info("File name - {}", file.getOriginalFilename());
            Path loadedFile = storageService.load(file.getOriginalFilename());
            try {
                imageService.saveImageInfo(loadedFile);
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
    public String handleFileUpload(@RequestParam("file") InputStream inputStream, String fileName) {
        try {
            Path store = storageService.store(inputStream, fileName);
            log.info("File name - {}", fileName);
            try {
                imageService.saveImageInfo(store);
            } catch (Exception ex) {
                log.error("Some error happened - ", ex);
                boolean check = storageService.deleteFile(store);
                log.info("Image was deleted from the storage due to exception - {}", check);
            }
        } catch (StorageException ex) {
            log.error(ex.getLocalizedMessage());
        }
        return "redirect:/";
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(StorageException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Some error occured - " + exc.getLocalizedMessage());
        return "redirect:/";
    }

}
