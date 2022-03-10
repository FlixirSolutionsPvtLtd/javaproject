package com.alinote.api.domains;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@NoArgsConstructor

public class Auditable {

	@JsonIgnore
	@Field("created_by")
	private String createdBy;

	@JsonIgnore
	@Field("modified_by")
	private String modifiedBy;

	@Field("created_ts")
	private Long createdTs;

	@Field("modified_ts")
	private Long modifiedTs;

	@JsonIgnore
	@Field("is_active")
	private Integer activeStatus;

	public Auditable(String createdBy, String modifiedBy, Long createdTs, Long modifiedTs) {
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.createdTs = createdTs;
		this.modifiedTs = modifiedTs;
	}

	public Auditable(String createdBy, String modifiedBy, Long createdTs, Long modifiedTs, Integer activeStatus) {
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.createdTs = createdTs;
		this.modifiedTs = modifiedTs;
		this.activeStatus = activeStatus;
	}

	public Auditable(String createdBy, Long createdTs) {
		this.createdBy = createdBy;
		this.createdTs = createdTs;
	}

	public void updateAuditableFields(
			boolean isCreate, 
			String userId,
			Integer activeStatus) {
    	
    	Long currentTimeInMillis = System.currentTimeMillis();
    	if (isCreate) {
    		this.createdBy = userId;
    		this.createdTs = currentTimeInMillis;
    	}
    	
    	this.modifiedBy = userId;
    	this.modifiedTs = currentTimeInMillis;
    	this.activeStatus = activeStatus;
    }
}
