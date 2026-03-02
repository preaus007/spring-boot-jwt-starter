package com.touhed.app.document;

import lombok.Data;

@Data
public class DocumentDTO {
    private Long id;
    private byte[] fileData;
    private String fileName;
    private String contentType;
    private DocumentType documentType;
}
