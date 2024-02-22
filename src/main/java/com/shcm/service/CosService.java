package com.shcm.service;

import org.springframework.web.multipart.MultipartFile;

public interface CosService {

    String uploadFile(MultipartFile file);
}
