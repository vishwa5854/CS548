package edu.stevens.cs548.clinic.rest.client.stub;

import java.io.IOException;
import java.io.OutputStream;

public interface IStreamingOutput {
	
	public void write(OutputStream out) throws IOException;

}
