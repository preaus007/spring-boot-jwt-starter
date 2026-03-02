package com.touhed.app.document;

import java.util.HashMap;
import java.util.Map;

public enum DocumentType {

    PROFILE_PHOTO( 0 ),
    CV( 1 ),
    BANNER(2)
    ;

    private final Integer value;
    private static Map<DocumentType, String> documentTypeStringMap = new HashMap<>();

    static {

        documentTypeStringMap.put( DocumentType.PROFILE_PHOTO, "profile_pic/" );
        documentTypeStringMap.put( DocumentType.CV, "cv/" );
        documentTypeStringMap.put( DocumentType.BANNER, "banner/" );
    }

    DocumentType( Integer val ){
        this.value = val;
    }

    public String getDirectory(){
        return DocumentType.documentTypeStringMap.getOrDefault( this, "" );
    }

    public Integer getValue(){
        return value;
    }
}
