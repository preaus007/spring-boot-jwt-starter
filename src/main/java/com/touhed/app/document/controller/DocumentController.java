package com.touhed.app.document.controller;

import com.touhed.app.document.service.DocumentService;
import com.touhed.app.util.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/documents" )
@RequiredArgsConstructor( onConstructor = @__( @Autowired) )
public class DocumentController {

    private final DocumentService documentService;

    @DeleteMapping( "/{id}" )
    public ApiResponse<?> deleteDocument(@PathVariable Long id ) {
        documentService.deleteDocument( id );
        return new ApiResponse<>( "Document deleted successfully", HttpStatus.NO_CONTENT.value() );
    }
}
