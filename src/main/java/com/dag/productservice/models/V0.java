package com.dag.productservice.models;

import java.time.LocalDateTime;

public abstract class V0 {
    protected Integer Id;
    protected String modifiedBy;
    protected LocalDateTime modifiedOn;
    protected String createdBy;
    protected LocalDateTime createdOn;
    protected Boolean isDeleted;
}
