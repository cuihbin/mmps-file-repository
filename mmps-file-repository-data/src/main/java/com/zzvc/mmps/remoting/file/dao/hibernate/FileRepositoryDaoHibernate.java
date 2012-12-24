package com.zzvc.mmps.remoting.file.dao.hibernate;

import java.util.List;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import com.zzvc.mmps.remoting.file.dao.FileRepositoryDao;
import com.zzvc.mmps.remoting.file.model.FileRepository;

@Repository("fileRepositoryDao")
public class FileRepositoryDaoHibernate extends GenericDaoHibernate<FileRepository, Long> implements FileRepositoryDao {

	public FileRepositoryDaoHibernate() {
		super(FileRepository.class);
	}

	@Override
	public FileRepository findByPath(String path) {
		List<FileRepository> list = getSession().createCriteria(FileRepository.class).add(Restrictions.eq("path", path)).list();
		if (list.isEmpty()) {
			throw new ObjectRetrievalFailureException("Cannot find entity", null);
		}
		if (list.size() > 1) {
			throw new ObjectRetrievalFailureException("Entity duplicated", null);
		}
		
		return list.get(0);
	}

}
