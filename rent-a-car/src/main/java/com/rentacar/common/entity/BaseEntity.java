package com.rentacar.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass                                  // ← tablo DEĞİL, sadece kalıp
@EntityListeners(AuditingEntityListener.class)     // ← tarih/kullanıcıyı otomatik doldurur
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @LastModifiedBy
    private Long updatedBy;

    private OffsetDateTime deletedAt;      // soft delete

    @Version
    private Long version;                  // optimistic lock

    public boolean isDeleted() {
        return deletedAt != null;
    }
}

