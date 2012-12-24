package com.zzvc.mmps.remoting.file.server.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.zzvc.mmps.remoting.file.server.service.FileRepositoryServerException;
import com.zzvc.mmps.remoting.file.server.service.HashEncoder;

public class Md5HashEncoder implements HashEncoder {
	private MessageDigest md;
	
	public String encode(Object... args) {
		byte[] mixed = new byte[16];
		for (Object arg : args) {
			mixDigests(mixed, digest(getBytes(arg)));
		}
		return getHexString(mixed);
	}
	
	private MessageDigest getMessageDigest() {
		if (md == null) {
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new FileRepositoryServerException("Cannot create file repository directory");
			}
		}
		return md;
	}
	
	private byte[] mixDigests(byte[] to, byte[] from) {
		int length = to.length < from.length ? to.length : from.length;
		for (int i = 0; i < length; i++) {
			to[i] = (byte) (to[i] ^ from[i]);
		}
		return to;
	}
	
	private byte[] digest(byte[] ba) {
		return getMessageDigest().digest(ba);
	}
	
	private byte[] getBytes(Object o) {
		if (o instanceof String) {
			return getBytes((String) o);
		} else if (o instanceof Date) {
			return getBytes((Date) o);
		} else {
			return getBytes(o.toString());
		}
	}
	
	private byte[] getBytes(String s) {
		return s.getBytes();
	}
	
	private byte[] getBytes(Date d) {
		return getBytes(d.getTime());
	}
	
	private byte[] getBytes(long l) {
		byte[] ba = new byte[8];
		for (int i = 7; i >=0; i--) {
			ba[i] = (byte) l;
			l = l >> 8;
		}
		return ba;
	}
	
	private String getHexString(byte[] ba) {
		StringBuffer sb = new StringBuffer();
		for (int b : ba) {
			if (b < 0) {
				b += 256;
			}
			if (b < 16) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(b));
		}
		return sb.toString();
	}
}
