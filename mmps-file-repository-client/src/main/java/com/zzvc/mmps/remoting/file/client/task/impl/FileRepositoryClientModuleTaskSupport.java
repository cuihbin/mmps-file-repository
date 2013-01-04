package com.zzvc.mmps.remoting.file.client.task.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

import com.zzvc.mmps.remoting.client.task.impl.RemotingClientModuleTaskSupport;
import com.zzvc.mmps.remoting.file.client.task.FileRepositoryClientException;
import com.zzvc.mmps.remoting.model.RemotingData;

abstract public class FileRepositoryClientModuleTaskSupport extends RemotingClientModuleTaskSupport {
	
	private String fileRepositoryHome;
	
	@Override
	public void init() {
		super.init();
		
		fileRepositoryHome = keyModelUtil.getValueScalar("fileRepositoryBaseUrl");
	}

	protected String downloadFile(RemotingData data) {
		String fileName = data.getPropertyValueScalar("FileName");
		String filePath = data.getPropertyValueScalar("FilePath");
		
		String fileRelativePath = filePath + "/" + fileName;
		
		String fileRepositoryUrl = fileRepositoryHome + fileRelativePath;
		String localFilePath = clientHome + fileRelativePath;
		downloadFile(fileRepositoryUrl, localFilePath);
		
		return localFilePath;
	}
	
	protected boolean removeFile(String filePath) {
		File file = new File(filePath);
		return file.delete() && file.getParentFile().delete();
	}
	
	private void downloadFile(String remoteUrl, String localPath) {
		InputStream is = null;
		long lastModifiedTime = 0;
		try {
			FileObject fo = VFS.getManager().resolveFile(remoteUrl);
			is = fo.getContent().getInputStream();
			lastModifiedTime = fo.getContent().getLastModifiedTime();
		} catch (FileSystemException e) {
			throw new FileRepositoryClientException("Cannot retrieve file from '" + remoteUrl + "'", e);
		}
		
		File localFile = new File(localPath);
		try {
			saveStreamToFile(is, localFile);
			localFile.setLastModified(lastModifiedTime);
		} catch (IOException e) {
			throw new FileRepositoryClientException("Cannot save file to '" + localPath + "'", e);
		}
	}
	
	private void saveStreamToFile(InputStream is, File file) throws IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
    	ReadableByteChannel rbc = Channels.newChannel(is);
    	FileOutputStream fos = new FileOutputStream(file);
    	fos.getChannel().transferFrom(rbc, 0, 1 << 24);
    	fos.close();
	}
	
}
