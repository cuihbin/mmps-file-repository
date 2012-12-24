package com.zzvc.mmps.remoting.file.server.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.zzvc.mmps.remoting.file.dao.FileRepositoryDao;
import com.zzvc.mmps.remoting.file.model.FileRepository;
import com.zzvc.mmps.remoting.file.server.service.FileRepositoryServerException;
import com.zzvc.mmps.remoting.file.server.service.FileRepositoryUploadManager;
import com.zzvc.mmps.remoting.file.server.service.HashEncoder;
import com.zzvc.mmps.remoting.file.server.service.StreamFileWriter;

public class FileRepositoryUploadManagerImpl implements FileRepositoryUploadManager, InitializingBean {
	private Logger logger = Logger.getLogger(FileRepositoryUploadManagerImpl.class);
	
	@Resource
	private FileRepositoryDao fileRepositoryDao;
	
	@Autowired(required=false)
	private StreamFileWriter streamFileSaver;
	
	@Autowired(required=false)
	private HashEncoder hashUtil;
	
	private String fileRepositoryLocalPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (streamFileSaver == null) {
			streamFileSaver = new TransformStreamFileWriter();
		}
		
		if (hashUtil == null) {
			hashUtil = new Md5HashEncoder();
		}
		
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("file-repository");
			File localPath = new File(bundle.getString("file.repository.path.local"));
			if (localPath.exists() && localPath.isDirectory() 
					|| !localPath.exists() && localPath.mkdirs()) {
				fileRepositoryLocalPath = localPath.getAbsolutePath().replaceAll("[\\\\/]", "/") + "/";
			} else {
				logger.error("Cannot create file repository directory");
			}
		} catch (Exception e) {
			logger.error("Cannot create file repository directory", e);
		}
	}
	
	@Override
	public FileRepository uploadFile(String fileUrl) {
		Date time = new Date();
		String name = getFilename(fileUrl);
		String path = hashUtil.encode(name, time);
		File localFile = getLocalFile(path, name);
		
		InputStream is = null;
		try {
			FileObject fo = VFS.getManager().resolveFile(fileUrl);
			is = fo.getContent().getInputStream();
		} catch (FileSystemException e) {
			logger.error("Cannot read file from '" + fileUrl + "'", e);
			throw new FileRepositoryServerException("Cannot read file from '" + fileUrl + "'");
		}
		
		try {
			File folder = localFile.getParentFile();
			if (!folder.exists() && !folder.mkdirs()) {
				throw new FileRepositoryServerException("Cannot create local file path '" + folder.getAbsolutePath() + "'");
			}
			streamFileSaver.saveFile(is, localFile);
		} catch (IOException e) {
			logger.error("Cannot read file from '" + fileUrl + "'", e);
			throw new FileRepositoryServerException("Cannot save file from '" + fileUrl + "'");
		}
		
		FileRepository fileRepository = new FileRepository();
		fileRepository.setTime(time);
		fileRepository.setName(name);
		fileRepository.setPath(path);
		fileRepository = fileRepositoryDao.save(fileRepository);
		
		return fileRepository;
	}
	
	@Override
	public void removeFile(String path) {
		FileRepository fileRepository = fileRepositoryDao.findByPath(path);
		
		File localFile = getLocalFile(path, fileRepository.getName());
		localFile.delete();
		localFile.getParentFile().delete();
		
		fileRepositoryDao.remove(fileRepository.getId());
	}
	
	private String separator = "[\\/]";
	private String getFilename(String path) {
		String[] pathList = path.split(separator);
		return pathList[pathList.length - 1];
	}
	
	private File getLocalFile(String path, String name) {
		if (fileRepositoryLocalPath == null) {
			throw new FileRepositoryServerException("Cannot locate file repository local path");
		}
		return new File(fileRepositoryLocalPath + path + "/" + name);
	}

}
