package com.esprit.planning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A comment on a progress update")
public class ProgressComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_update_id", nullable = false)
    @JsonIgnore
    private ProgressUpdate progressUpdate;

    @Column(nullable = false)
    @Schema(description = "User ID who wrote the comment", example = "5")
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Comment text", example = "Great progress!")
    private String message;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getProgressUpdateId() {
        return progressUpdate != null ? progressUpdate.getId() : null;
    }
}
