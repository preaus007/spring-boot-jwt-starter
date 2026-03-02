package com.touhed.app.user.controller;

import com.touhed.app.user.request.AddUserRequest;
import com.touhed.app.user.request.GetUserRequest;
import com.touhed.app.user.response.FilterOptionResponse;
import com.touhed.app.user.response.UserResponse;
import com.touhed.app.user.service.UserService;
import com.touhed.app.util.responses.ApiResponse;
import com.touhed.app.util.responses.PaginatedApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController()
@RequestMapping( "/users" )
@RequiredArgsConstructor( onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @PreAuthorize( "hasAuthority( 'Admin' )" )
    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody AddUserRequest request ) {
        UserResponse response = userService.addUser( request );
        return ResponseEntity.ok( response );
    }

    @GetMapping
    public ResponseEntity<?> getUsers(GetUserRequest request, Pageable pageable ) {
        PaginatedApiResponse<List<UserResponse>> userResponses = userService.getUsers( request, pageable);
        return ResponseEntity.ok( userResponses );
    }

    @GetMapping( "/{id}" )
    public ResponseEntity<UserResponse> getUser( @PathVariable Long id ) {
        System.out.println( id );
        UserResponse user = userService.getUser( id );
        return ResponseEntity.ok( user );
    }

    @PutMapping( "/{id}" )
    public ResponseEntity<UserResponse> updateUser( @PathVariable Long id, @RequestBody AddUserRequest request ) {
        UserResponse updatedUser = userService.updateUser( id, request );
        return ResponseEntity.ok( updatedUser );
    }

    @PreAuthorize( "hasAuthority( 'Admin' )" )
    @DeleteMapping( "/{id}" )
    public ApiResponse<?> deleteUser(@PathVariable Long id ) {
        userService.deleteUser( id );
        return new ApiResponse<>( "User deleted successfully", 200 );
    }

    @GetMapping( "/filter-options" )
    public ResponseEntity<FilterOptionResponse> getFilterOptions() {
        FilterOptionResponse options = userService.getFilterOptions();
        return ResponseEntity.ok( options );
    }
}