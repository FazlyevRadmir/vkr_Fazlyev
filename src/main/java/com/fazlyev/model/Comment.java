package com.fazlyev.model;

import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private String author;
    private String text;
    private Date date;
}