package com.dam.depot.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import com.dam.depot.types.ActionType;
import com.dam.depot.types.Currency;
import com.dam.depot.types.ReferenceType;

/**
 * The depot entity mirrors the depot at the depot bank.
 * Investments always are transferred via this account to the depot bank.
 * @author dirk
 *
 */

@Entity
@Component
@Table(name = "DepotTransaction", 
			uniqueConstraints= {@UniqueConstraint(columnNames = {"userId", "action", "amount", "actionDate"})},
			indexes = {@Index(name = "idx_depot_user_action", columnList = "userId, action"),
					   @Index(name = "idx_depot_user_date", columnList = "userId, actionDate"), 
					   @Index(name = "idx_depot_action_date", columnList = "action, actionDate"), 
					   @Index(name = "idx_depot_action", columnList = "action"), 
					   @Index(name = "idx_depot_user", columnList = "userId"), 
					   @Index(name = "idx_depot_user_amount", columnList = "userId, amount")}
)
public class DepotTransaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long depotTransactionId;
	
	@Column (nullable=false)
	private Long userId;
	
	@Column(nullable=true)
	private Long requestorUserId;
	
	@Column (nullable=false)
	private Date actionDate;
	
	@Column
	@Enumerated(EnumType.STRING)
	private ActionType action;
	
	@Column
	private Long portfolioId;
	
	@Column
	private String eventText;
	
	@Column (nullable=false)
	private Float amount;

	@Column (nullable=false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;

	@Column(nullable = true)
	private Long referenceId;
	
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private ReferenceType referenceType;
	

	public DepotTransaction updateEntity (DepotTransaction container) {
		this.action = container.getAction();
		this.actionDate = container.getActionDate();
		this.amount = container.getAmount();
		this.eventText = container.getEventText();
		this.userId = container.getUserId();
		this.requestorUserId  = container.getRequestorUserId();
		this.portfolioId = container.getPortfolioId();
		this.currency = container.getCurrency();
		this.referenceId = container.getReferenceId();
		this.referenceType = container.getReferenceType();
		return this;
	}

	public Long getDepotTransactionId() {
		return depotTransactionId;
	}

	public void setDepotTransactionId(Long depotTransactionId) {
		this.depotTransactionId = depotTransactionId;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}


	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Long getRequestorUserId() {
		return requestorUserId;
	}

	public void setRequestorUserId(Long requestorUserId) {
		this.requestorUserId = requestorUserId;
	}

	public String getEventText() {
		return eventText;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}
}
