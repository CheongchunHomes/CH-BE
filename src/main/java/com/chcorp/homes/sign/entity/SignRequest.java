package com.chcorp.homes.sign.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;


@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SignRequest extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property propertyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignStatus status;

    public void providerSign() {
        this.status = SignStatus.PROVIDER_SIGNED;
    }

    public void customerSign() {
        this.status = SignStatus.COMPLETED;
    }

    public void cancel() {
        this.status = SignStatus.CANCELED;
    }
}
