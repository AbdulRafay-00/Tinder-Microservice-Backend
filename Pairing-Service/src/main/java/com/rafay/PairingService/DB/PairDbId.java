package com.rafay.PairingService.DB;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PairDbId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "swiper_id", nullable = false)
    private String swiperId;

    @Column(name = "swiped_id", nullable = false)
    private String swipedId;
}