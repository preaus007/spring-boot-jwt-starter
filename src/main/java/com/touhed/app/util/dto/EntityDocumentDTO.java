package com.touhed.app.util.dto;

import com.touhed.app.document.model.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDocumentDTO {

    private Long id;
    private Document image;
}

