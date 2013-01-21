package com.zzvc.mmps.remoting.file.client.task.manager;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.zzvc.mmps.remoting.file.client.task.FileRepositoryClientModule;
import com.zzvc.mmps.task.TaskException;
import com.zzvc.mmps.task.TaskSupport;

public class FileRepositoryClientManager extends TaskSupport {
	private static Logger logger = Logger.getLogger(FileRepositoryClientManager.class);
	
	@Autowired(required=false)
	private List<FileRepositoryClientModule> tasks;

	public FileRepositoryClientManager() {
		super();
		pushBundle("FileRepositoryClientResources");
	}

	@Override
	public void init() {
		String fileRepositoryUrl = loadFileRepositoryUrl();

		for (FileRepositoryClientModule task : tasks) {
			task.setFileRepositoryUrl(fileRepositoryUrl);
		}
	}
	
	private String loadFileRepositoryUrl() {
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		try {
			return bundle.getString("file.repository.url");
		} catch (MissingResourceException e) {
			errorMessage("file.repository.client.error.init.config.loadfailed");
			logger.error("Error loading file repository url", e);
			throw new TaskException("Error loading file repository url", e);
		}
		
	}

}
