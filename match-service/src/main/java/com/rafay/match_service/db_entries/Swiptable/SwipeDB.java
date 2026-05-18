package com.rafay.match_service.db_entries.Swiptable;

import java.time.LocalDateTime;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "swipes")
public class SwipeDB {

    @EmbeddedId
    private SwipeIdEmbedd id;  // composite key

    @Enumerated(EnumType.STRING)
    private SwipeDirectionEnum direction;

    private LocalDateTime swipedAt;
}