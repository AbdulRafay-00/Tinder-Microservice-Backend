package com.rafay.match_service.db_entries.Swiptable;
import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data                    // generates all getters, setters, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class SwipeIdEmbedd implements Serializable {
    private String swiperId;
    private String swipedId;
}
