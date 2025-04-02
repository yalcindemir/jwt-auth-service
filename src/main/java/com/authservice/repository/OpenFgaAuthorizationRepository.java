package com.authservice.repository;

import com.authservice.model.OpenFgaAuthorization;
import com.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpenFgaAuthorizationRepository extends JpaRepository<OpenFgaAuthorization, UUID> {
    
    List<OpenFgaAuthorization> findByUser(User user);
    
    List<OpenFgaAuthorization> findByObjectTypeAndObjectId(String objectType, String objectId);
    
    Optional<OpenFgaAuthorization> findByObjectTypeAndObjectIdAndRelationAndUser(
            String objectType, String objectId, String relation, User user);
    
    boolean existsByObjectTypeAndObjectIdAndRelationAndUser(
            String objectType, String objectId, String relation, User user);
    
    @Query("SELECT o FROM OpenFgaAuthorization o WHERE o.objectType = :objectType AND o.relation = :relation AND o.user.id = :userId")
    List<OpenFgaAuthorization> findByObjectTypeAndRelationAndUserId(String objectType, String relation, UUID userId);
    
    void deleteByObjectTypeAndObjectId(String objectType, String objectId);
}
