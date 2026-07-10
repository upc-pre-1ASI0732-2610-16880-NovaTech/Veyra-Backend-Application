/**
 * Aggregate root representing a medication assigned to a resident,
 * including its stock levels, presentation, dosage, and expiration date.
 *
 * <p>This aggregate encapsulates business rules related to stock management,
 * such as decreasing stock, validating availability, and emitting domain
 * events when the stock reaches a low threshold. It inherits auditing
 * capabilities from {@link AuditableAbstractAggregateRoot}.</p>
 *
 * @summary Represents a medication entity and handles stock-related domain logic and events.
 */
package com.novaperutech.veyra.platform.nursing.domain.model.aggregates;

import com.novaperutech.veyra.platform.nursing.domain.model.events.MedicationStockLowEvent;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.DrugPresentation;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.ExpirationDate;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.Stock;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Medication extends AuditableAbstractAggregateRoot<Medication> {

    private final int LOW_STOCK_THRESHOLD = 15;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Stock stock;

    @Embedded
    private ExpirationDate expirationDate;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String lot;

    @ManyToOne
    @JoinColumn(name ="nursing_home_id")
    private NursingHome nursingHome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DrugPresentation drugPresentation;

    public Medication(){}

    public Medication(String name, String description, Stock stock, ExpirationDate expirationDate,
                      DrugPresentation drugPresentation, String dosage, String lot, NursingHome nursingHome) {
        this.description = description;
        this.name = name;
        this.stock = stock;
        this.expirationDate = expirationDate;
        this.nursingHome = nursingHome;
        this.dosage = dosage;
        this.lot = lot;
        this.drugPresentation = drugPresentation;
    }

    public Medication decreaseStock(int quantity) {
        if (!this.stock.hasEnough(quantity)) {
            throw new IllegalArgumentException(String.format(
                    "Insufficient stock for medication '%s'. Available: %d, Requested: %d",
                    name, stock.amount(), quantity));
        }
        this.stock = stock.decrease(quantity);

        if (stock.isLow(LOW_STOCK_THRESHOLD)) {
            this.registerEvent(new MedicationStockLowEvent(
                    this, this.getId(), this.name, this.getNursingHome().getId()
            ));
        }
        return this;
    }

    public boolean hasEnoughStock(int quantity) {
        return stock.hasEnough(quantity);
    }

    public boolean isLowStock() {
        return stock.isLow(LOW_STOCK_THRESHOLD);
    }

    public boolean isExpiringSoon(int days) {
        return expirationDate.isExpiringSoon(days);
    }
}
