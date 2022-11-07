package com.sanath.springBootMongoDB.service;

import com.sanath.springBootMongoDB.collection.Photo;
import com.sanath.springBootMongoDB.repository.PhotoRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public String addPhoto(String fileName, MultipartFile image) throws IOException {
        Photo photo = new Photo();
        photo.setTitle(fileName);
        photo.setImage(new Binary(BsonBinarySubType.BINARY, image.getBytes()));
        return photoRepository.save(photo).getId();
    }

    @Override
    public Photo getPhoto(String id) {
        return photoRepository.findById(id).get();
    }
}
