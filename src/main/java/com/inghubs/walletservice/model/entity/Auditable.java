package com.inghubs.walletservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.createdBy = getCurrentUser(); // Replace with actual user retrieval logic
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
        this.updatedBy = getCurrentUser(); // Replace with actual user retrieval logic
    }

    private String getCurrentUser() {
        // Implement logic to retrieve the current user (e.g., from security context)
        return "system"; // Placeholder value
    }
}