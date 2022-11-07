package com.sanath.springBootMongoDB.service;

import com.sanath.springBootMongoDB.collection.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    String addPhoto(String fileName, MultipartFile image) throws IOException;

    Photo getPhoto(String id);
}
