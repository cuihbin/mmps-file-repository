package com.zzvc.mmps.remoting.file.writer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface StreamFileWriter {
	void saveFile(InputStream is, File file) throws IOException;
}
