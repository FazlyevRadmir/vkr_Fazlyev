package com.fazlyev.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@Setter
@Data
public class ProjectForm {
    private String title;
    private String description;
    private String category;
    private List<MultipartFile> screenshots;
    private MultipartFile mainFile;
    private List<MultipartFile> additionalFiles;
}