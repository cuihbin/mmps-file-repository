package com.zzvc.mmps.remoting.file.client.task;

import com.zzvc.mmps.remoting.client.task.RemotingClientModule;

public interface FileRepositoryClientModule extends RemotingClientModule {
	void setFileRepositoryUrl(String fileRepositoryUrl);
}
