package com.touhed.app.user.service;

import com.touhed.app.document.DocumentDTO;
import com.touhed.app.document.DocumentType;
import com.touhed.app.document.model.Document;
import com.touhed.app.document.response.DocumentResponse;
import com.touhed.app.document.service.DocumentService;
import com.touhed.app.user.BloodGroup;
import com.touhed.app.user.Gender;
import com.touhed.app.user.Role;
import com.touhed.app.user.model.User;
import com.touhed.app.user.repository.UserRepository;
import com.touhed.app.user.request.AddUserRequest;
import com.touhed.app.user.request.GetUserRequest;
import com.touhed.app.user.response.FilterOptionResponse;
import com.touhed.app.user.response.UserResponse;
import com.touhed.app.util.dto.EntityDocumentDTO;
import com.touhed.app.util.enums.EntityName;
import com.touhed.app.util.exceptions.NotFoundException;
import com.touhed.app.util.responses.EnumResponse;
import com.touhed.app.util.responses.PaginatedApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.touhed.app.user.repository.UserSpecification.*;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocumentService documentService;

    @PersistenceContext
    private final EntityManager entityManager;
    private final LoggedInUserService loggedInUserService;

    @CacheEvict( value = "userCache", key = "#email" )
    public void evictUserCache( String email ) {}

    @CacheEvict( value = "userIdCache", key= "#id" )
    public void evictUserIdCache( Long id ) {}

    public UserResponse addUser(AddUserRequest addUserRequest ) {
        User user = new User( addUserRequest );
        user.setPassword( passwordEncoder.encode( addUserRequest.getPassword() ) );

        user = userRepository.save( user );

        if( addUserRequest.getProfilePic() != null ) {
            Document profilePicDocument = getProfilePicDocument( addUserRequest.getProfilePic(), user );
            user.setProfilePic( profilePicDocument );
            user = userRepository.save( user );
        }

        return getUserResponse( user );
    }

    public UserResponse getUser( Long id ) {
        User user = Objects.requireNonNull( userRepository.findById( id )
                .orElseThrow( () -> new NotFoundException( "user", id ) ) );

        return getUserResponse( user );
    }

    public PaginatedApiResponse<List<UserResponse>> getUsers( GetUserRequest request, Pageable pageable ) {

        Specification<User> userSpecification = getUserSpecification( request );
        Page<User> userPage = userRepository.findAll( userSpecification, pageable );

        List<Long> userIds = userPage.getContent().stream().map( User::getId ).toList();

        Map<Long, Document> profilePicMap = userRepository.getUserProfilePicByIdIn( userIds ).stream()
                .filter( Objects::nonNull )
                .filter( entityDocumentDTO -> entityDocumentDTO.getId() != null && entityDocumentDTO.getImage() != null )
                .collect( Collectors.toMap( EntityDocumentDTO::getId, EntityDocumentDTO::getImage) );

        List<UserResponse> userResponseList = userPage.stream()
                .map( user -> {
                    UserResponse userResponse = getUserResponse( user );
                    DocumentResponse profilePic = getProfilePic( profilePicMap.getOrDefault( user.getId(), null ) );
                    userResponse.setProfilePic( profilePic );
                    return userResponse;
                } )
                .toList();

        return new PaginatedApiResponse<>(
                userResponseList,
                pageable.getPageNumber(),
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );
    }

    public UserResponse getUserResponse( User user ) {

        User loggedInUser = loggedInUserService.getLoginUser();

        UserResponse userResponse = new UserResponse();
        userResponse.setId( user.getId() );
        userResponse.setName( user.getName() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setPhoneNumber( user.getPhoneNumber() );

        if( Objects.equals( loggedInUser.getRole(), Role.ADMIN ) ){
            userResponse.setRole( new EnumResponse(user.getRole().getValue(), user.getRole().toString() ) );
        }

        DocumentResponse profilePic = getProfilePic( user.getProfilePic() );
        userResponse.setProfilePic( profilePic );

        return userResponse;
    }

    public DocumentResponse getProfilePic(Document document) {
        DocumentResponse profilePic = new DocumentResponse();
        if( document != null ){
            profilePic.setId( document.getId() );
            profilePic.setFileName( document.getFileName() );
            profilePic.setFileURL( documentService.getDocumentUrl( document ) );
            profilePic.setDocumentType( document.getDocumentType() );
        }

        return profilePic;
    }

    private Specification<User> getUserSpecification( GetUserRequest request ) {

        return filterByIsActive( request.getIsActive() )
                .and( filterByIsDeletedNull().or( filterByIsDeletedFalse() ) )
                .and( filterByRoleIn( request.getRole() == null ? null : List.of( request.getRole() ) ) );
    }

    @Transactional
    public UserResponse updateUser( Long id, AddUserRequest request ) {

        User user = entityManager.getReference( User.class, id );
        evictUserCache( user.getEmail() );
        evictUserIdCache( id );

        if ( request.getName() != null )
            user.setName( request.getName() );

        if ( request.getEmail() != null )
            user.setEmail( request.getEmail() );

        if ( request.getPhoneNumber() != null )
            user.setPhoneNumber( request.getPhoneNumber() );

        if ( request.getProfilePic() != null ) {
            Document profilePicDocument = getProfilePicDocument( request.getProfilePic(), user );
            user.setProfilePic( profilePicDocument );
        }

        User updatedUser = userRepository.save( user );
        return new UserResponse( updatedUser );
    }

    private Document getProfilePicDocument(DocumentDTO profilePic, User user ) {
        profilePic.setDocumentType( DocumentType.PROFILE_PHOTO );
        return documentService.uploadDocument( profilePic, user.getId(), EntityName.USER );
    }

    @Transactional
    public void deleteUser( Long id ) {

        User user = userRepository.findById( id )
                .orElseThrow( () -> new NotFoundException( "user", id ) );
        user.setIsDeleted( true );
        user.setIsActive( false );

        userRepository.save( user );
    }

    public FilterOptionResponse getFilterOptions() {

        List<EnumResponse> roles = Arrays.stream( Role.values() )
                .map( role -> new EnumResponse( role.getValue(), role.name() ) )
                .collect( Collectors.toList() );

        List<EnumResponse>bloodGroups = Arrays.stream( BloodGroup.values() )
                .map( bloodGroup -> new EnumResponse( bloodGroup.getValue(), bloodGroup.name() ) )
                .collect( Collectors.toList() );

        List<EnumResponse>genders = Arrays.stream( Gender.values() )
                .map( gender -> new EnumResponse( gender.getValue(), gender.name() ) )
                .collect( Collectors.toList() );

        return new FilterOptionResponse( roles, bloodGroups, genders );
    }
}
