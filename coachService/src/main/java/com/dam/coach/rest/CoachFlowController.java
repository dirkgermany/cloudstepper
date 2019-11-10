package com.dam.coach.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.coach.flow.FlowGenerator;
import com.dam.exception.DamServiceException;

@CrossOrigin
@RestController
public class CoachFlowController {
	
	@Autowired
	FlowGenerator flowGenerator;
	
	@GetMapping("/getFlow")
	public String getFlow(@RequestParam String actionReference, @RequestParam String tokenId) throws DamServiceException {
			return flowGenerator.getHtml(actionReference, tokenId);
	}

}