package com.zzvc.mmps.remoting.file.client.task;

public class FileRepositoryClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileRepositoryClientException() {		super();
	}

	public FileRepositoryClientException(String message) {
		super(message);
	}

	public FileRepositoryClientException(Throwable cause) {
		super(cause);
	}

    public FileRepositoryClientException(String message, Throwable cause) {
    	super(message, cause);
    }

}
