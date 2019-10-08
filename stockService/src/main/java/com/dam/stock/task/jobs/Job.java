package com.dam.stock.task.jobs;

import com.dam.exception.DamServiceException;

public abstract class Job {

	public abstract void executeJob () throws DamServiceException;
	public abstract void login (String className) throws DamServiceException;
	public abstract void logout (String className) throws DamServiceException;

}
