package com.dam.depot.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.dam.depot.types.Currency;

@Entity
@Component
@Table(name = "Balance")
public class Balance {
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long userId;
		
		@Column (nullable=false)
		private Float amount;
		
		@Column (nullable=false)
		@Enumerated(EnumType.STRING)
		private Currency currency;
		

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Float getAmount() {
			return amount;
		}

		public void setAmount(Float amount) {
			this.amount = amount;
		}

		public Currency getCurrency() {
			return currency;
		}

		public void setCurrency(Currency currency) {
			this.currency = currency;
		}
}

