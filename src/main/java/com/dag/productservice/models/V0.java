package com.dag.productservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class V0 {

    @Id
    @GeneratedValue(generator = "uuid",strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    protected UUID Id;
    @Column
    protected String modifiedBy;
    @Column
    protected LocalDateTime modifiedOn;
    @Column
    protected String createdBy;
    @Column
    protected LocalDateTime createdOn;
    @Column
    protected Boolean isDeleted;
}
