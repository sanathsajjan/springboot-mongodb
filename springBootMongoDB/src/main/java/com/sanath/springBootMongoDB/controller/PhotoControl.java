package com.sanath.springBootMongoDB.controller;

import com.sanath.springBootMongoDB.collection.Photo;
import com.sanath.springBootMongoDB.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/photo")
public class PhotoControl {

    @Autowired
    private PhotoService photoService;

    //Use postman -> body -> formdata -> select file -> upload the image.
    // The key should be your requestParam name
    @PostMapping
    public String addPhoto(@RequestParam("image")MultipartFile image) throws IOException {
        return photoService.addPhoto(image.getOriginalFilename(), image);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadPhoto(@PathVariable String id){
        Photo photo = photoService.getPhoto(id);
        Resource resource = new ByteArrayResource(photo.getImage().getData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\""+photo.getTitle()+"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
