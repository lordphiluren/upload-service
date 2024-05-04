package ru.sushchenko.objectstorage.service;

import java.util.List;
import java.util.Set;

public interface ObjectStorageService {
    Set<String> uploadAttachments(List<byte[]> attachments);
}
