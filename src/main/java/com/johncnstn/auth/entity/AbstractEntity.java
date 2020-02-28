package com.johncnstn.auth.entity;

import static java.time.Instant.now;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;

    @Column(name = "deleted_at")
    protected Instant deletedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected Instant updatedAt;

    @PrePersist
    public void onPrePersist() {
        setCreatedAt(now());
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(now());
    }
}
