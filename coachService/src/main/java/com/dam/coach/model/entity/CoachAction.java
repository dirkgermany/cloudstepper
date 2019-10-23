package com.dam.coach.model.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;


@Entity
@Component
@Table(name = "CoachAction", uniqueConstraints= {@UniqueConstraint(columnNames = {"actionId"})},
		indexes = {@Index(name = "idx_coachaction_reference", columnList = "actionReference")})

public class CoachAction {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long actionId;
	
	@Column(nullable = false)
	private String actionReference;
	
	@Column(nullable = true)
	private String btnText;
	
	@Column(nullable = true)
	@Lob
	private String message;
	
	@Column(nullable = true)
	private String options;
	
	@Column(nullable = true)
	@Lob
	private String text;
	
	@Transient
	private List<String> optionList;
	
	private void splitOptions() {
		optionList = new ArrayList<>();
		if (null != options) {
			if (options.contains(",")) {
				String option[] = options.split(",");
				Iterator<String> it = Arrays.asList(option).iterator();
				while (it.hasNext()) {
					optionList.add(it.next().trim());
				}
			}
			else {
				optionList.add(options);
			}
		}
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getActionReference() {
		return actionReference;
	}

	public void setActionReference(String actionReference) {
		this.actionReference = actionReference;
	}

	public String getBtnText() {
		return btnText;
	}

	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Transient
	public List<String> getOptionList() {
		splitOptions();
		return optionList;
	}

}
