package com.authservice.repository;

import com.authservice.model.UserToken;
import com.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    
    List<UserToken> findByUser(User user);
    
    Optional<UserToken> findByToken(String token);
    
    Optional<UserToken> findByRefreshToken(String refreshToken);
    
    List<UserToken> findByUserAndRevokedFalseAndExpiresAtAfter(User user, LocalDateTime now);
    
    @Query("SELECT t FROM UserToken t WHERE t.user.id = :userId AND t.revoked = false AND t.expiresAt > :now")
    List<UserToken> findValidTokensByUserId(UUID userId, LocalDateTime now);
    
    void deleteByExpiresAtBefore(LocalDateTime now);
}
