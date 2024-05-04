package ru.sushchenko.objectstorage.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sushchenko.objectstorage.service.ObjectStorageService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class ObjectStorageController {
    private final ObjectStorageService objectStorageService;
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Set<String> upload(@RequestBody List<byte[]> photos) {
        return objectStorageService.uploadAttachments(photos);
    }
}
