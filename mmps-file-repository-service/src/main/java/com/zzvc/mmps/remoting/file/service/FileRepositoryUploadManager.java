package com.zzvc.mmps.remoting.file.service;

import com.zzvc.mmps.remoting.file.model.FileRepository;

public interface FileRepositoryUploadManager {
	FileRepository uploadFile(String fileUrl);
	void removeFile(String path);
}
