package client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class NewOutputStreamWriter extends OutputStreamWriter {

	public NewOutputStreamWriter(OutputStream arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void write(String s) throws IOException {
		super.write(s + '\n');
		super.flush();
	}
}
