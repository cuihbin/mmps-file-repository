package com.zzvc.mmps.remoting.file.dao;

import org.appfuse.dao.GenericDao;

import com.zzvc.mmps.remoting.file.model.FileRepository;

public interface FileRepositoryDao extends GenericDao<FileRepository, Long> {
	FileRepository findByPath(String path);
}
