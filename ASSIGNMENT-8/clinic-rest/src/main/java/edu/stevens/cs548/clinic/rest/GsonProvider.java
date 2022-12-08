package edu.stevens.cs548.clinic.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;

import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;

/*
 * A custom JSON provider for the Web service, using Gson instead of Moxy, the reference implementation
 * of JSON-B.  This may be unnecessary since the only essential use of Gson is in streaming, and there
 * we basically parse the data manually with a call out to Gson for DTO parsing.  But at least this
 * shows how you can configure your own serialization library.  The app server scans the classes and 
 * sees the @Provider annotation.
 * 
 * Outside of Payara, the Jersey implementation makes it easy to register a Jackson implementation.
 * 
 * https://eclipsesource.com/blogs/2012/11/02/integrating-gson-into-a-jax-rs-based-application/
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider implements MessageBodyWriter<Object>,
		MessageBodyReader<Object> {
	
	private static final Logger logger = Logger.getLogger(GsonProvider.class.getCanonicalName());

	private static final String UTF_8 = "UTF-8";
	
	private static final Gson gson = GsonFactory.createGson();

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		InputStreamReader streamReader = new InputStreamReader(entityStream,
				UTF_8);
		try {
			return gson.fromJson(streamReader, genericType);
		} catch (com.google.gson.JsonSyntaxException e) {
			logger.log(Level.SEVERE, "Syntax error reading JSON!", e);
		} finally {
			streamReader.close();
		}
		return null;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(Object object, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Object object, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		try {
			gson.toJson(object, genericType, writer);
		} finally {
			writer.close();
		}
	}
}
