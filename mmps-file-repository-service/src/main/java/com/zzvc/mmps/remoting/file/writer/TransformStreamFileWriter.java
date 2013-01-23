package com.zzvc.mmps.remoting.file.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class TransformStreamFileWriter implements StreamFileWriter {

	@Override
	public void saveFile(InputStream is, File file) throws IOException {
    	ReadableByteChannel rbc = Channels.newChannel(is);
    	FileOutputStream fos = new FileOutputStream(file);
    	fos.getChannel().transferFrom(rbc, 0, 1 << 24);
    	fos.close();
	}

}
