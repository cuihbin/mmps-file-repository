package com.zzvc.mmps.remoting.file.client.task.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.log4j.Logger;

import com.zzvc.mmps.remoting.client.task.impl.RemotingClientModuleTaskSupport;
import com.zzvc.mmps.remoting.file.client.task.FileRepositoryClientModule;
import com.zzvc.mmps.remoting.file.client.task.FileRepositoryClientException;
import com.zzvc.mmps.remoting.model.RemotingData;

abstract public class FileRepositoryClientModuleSupport extends RemotingClientModuleTaskSupport implements FileRepositoryClientModule {
	private static Logger logger = Logger.getLogger(FileRepositoryClientModuleSupport.class);

	private String fileRepositoryUrl;
	
	@Override
	public boolean isWaitingPrequisiteInit() {
		return super.isWaitingPrequisiteInit() || fileRepositoryUrl == null;
	}
	
	public void setFileRepositoryUrl(String fileRepositoryUrl) {
		this.fileRepositoryUrl = fileRepositoryUrl;
	}

	public String downloadFile(RemotingData data) {
		String fileName = data.getPropertyValueScalar("FileName");
		String filePath = data.getPropertyValueScalar("FilePath");
		
		String relativePath = filePath + "/" + fileName;
		
		String repositoryFileUrl = fileRepositoryUrl + relativePath;
		String localFilePath = getClientResource("client.home") + relativePath;
		
		if (!downloadFileFromUrl(repositoryFileUrl, new File(localFilePath))) {
			throw new FileRepositoryClientException("Error download file from " + repositoryFileUrl + " to " + localFilePath);
		}
		
		return localFilePath;
	}
	
	public boolean removeFile(String filePath) {
		File file = new File(filePath);
		return file.delete() && file.getParentFile().delete();
	}
	
	
	private boolean downloadFileFromUrl(String url, java.io.File file) {
		FileContent content = null;
		java.io.File tempFile = null;
		OutputStream os = null;
		try {
			content = VFS.getManager().resolveFile(url).getContent();
			tempFile = java.io.File.createTempFile("temp-", ".tmp");
			os = new FileOutputStream(tempFile);
			IOUtils.copy(content.getInputStream(), os);
		} catch (FileNotFoundException e) {
			logger.error("Error accessing local file", e);
			return false;
		} catch (IOException e) {
			logger.error("Error downloading file from " + url, e);
			return false;
		} finally {
			IOUtils.closeQuietly(os);
		}
		
		if (file.exists() && !file.delete() || !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
			return false;
		}
		
		try {
			return tempFile.renameTo(file) && file.setLastModified(content.getLastModifiedTime());
		} catch (FileSystemException e) {
			logger.error("Error setting file timestamp", e);
			return false;
		}
	}
}
