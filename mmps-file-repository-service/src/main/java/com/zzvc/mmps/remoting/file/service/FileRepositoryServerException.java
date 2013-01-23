package com.zzvc.mmps.remoting.file.service;

public class FileRepositoryServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileRepositoryServerException() {
	}

	public FileRepositoryServerException(String message) {
		super(message);
	}

	public FileRepositoryServerException(Throwable cause) {
		super(cause);
	}

    public FileRepositoryServerException(String message, Throwable cause) {
    	super(message, cause);
    }

}