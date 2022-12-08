package edu.stevens.cs548.clinic.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;

import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;

@Provider
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
public class GsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
	
	/*
	 * Adapted from https://github.com/codehaus/jackson/blob/master/src/jaxrs/java/org/codehaus/jackson/jaxrs/JacksonJaxbJsonProvider.java
	 */

	private static final Logger logger = Logger.getLogger(GsonProvider.class.getCanonicalName());

	private static final String UTF_8 = "UTF-8";

	private static final Gson gson = GsonFactory.createGson();

	/**
	 * Looks like we need to worry about accidental data binding for types we
	 * shouldn't be handling. This is probably not a very good way to do it, but
	 * let's start by blacklisting things we are not to handle.
	 * <p>
	 * (why ClassKey? since plain old Class has no hashCode() defined, lookups are
	 * painfully slow)
	 */
	public final static HashSet<ClassKey> _untouchables = new HashSet<ClassKey>();
	static {
		// First, I/O things (direct matches)
		_untouchables.add(new ClassKey(java.io.InputStream.class));
		_untouchables.add(new ClassKey(java.io.Reader.class));
		_untouchables.add(new ClassKey(java.io.OutputStream.class));
		_untouchables.add(new ClassKey(java.io.Writer.class));

		// then some primitive types
		_untouchables.add(new ClassKey(byte[].class));
		_untouchables.add(new ClassKey(char[].class));
		// 24-Apr-2009, tatu: String is an edge case... let's leave it out
		_untouchables.add(new ClassKey(String.class));

		// Then core JAX-RS things
		_untouchables.add(new ClassKey(StreamingOutput.class));
		_untouchables.add(new ClassKey(Response.class));
	}

	/**
	 * These are classes that we never use for reading (never try to deserialize
	 * instances of these types).
	 */
	public final static Class<?>[] _unreadableClasses = new Class<?>[] { InputStream.class, Reader.class };

	/**
	 * These are classes that we never use for writing (never try to serialize
	 * instances of these types).
	 */
	public final static Class<?>[] _unwritableClasses = new Class<?>[] { OutputStream.class, Writer.class,
			StreamingOutput.class, Response.class };


	/*
	 * /********************************************************** /* Construction
	 * /**********************************************************
	 */

	/**
	 * Default constructor, usually used when provider is automatically configured
	 * to be used with JAX-RS implementation.
	 */
	public GsonProvider() {
	}

	/*
	 * /********************************************************** /*
	 * MessageBodyReader impl
	 * /**********************************************************
	 */

	/**
	 * Method that JAX-RS container calls to try to check whether values of given
	 * type (and media type) can be deserialized by this provider. Implementation
	 * will first check that expected media type is a JSON type (via call to
	 * {@link #isJsonType}; then verify that type is not one of "untouchable" types
	 * (types we will never automatically handle), and finally that there is a
	 * deserializer for type (iff {@link #checkCanDeserialize} has been called with
	 * true argument -- otherwise assumption is there will be a handler)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//		if (!isJsonType(mediaType)) {
//			return false;
//		}
//
//		/*
//		 * Ok: looks like we must weed out some core types here; ones that make no sense
//		 * to try to bind from JSON:
//		 */
//		if (_untouchables.contains(new ClassKey(type))) {
//			return false;
//		}
//		// and there are some other abstract/interface types to exclude too:
//		for (Class<?> cls : _unreadableClasses) {
//			if (cls.isAssignableFrom(type)) {
//				return false;
//			}
//		}

		return true;
	}

	/**
	 * Method that JAX-RS container calls to deserialize given value.
	 */
	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
		InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8);
		try {
			return gson.fromJson(streamReader, genericType);
		} catch (com.google.gson.JsonSyntaxException e) {
			logger.log(Level.SEVERE, "Syntax error reading JSON!", e);
		} finally {
			streamReader.close();
		}
		return null;
	}

	/*
	 * /********************************************************** /*
	 * MessageBodyWriter impl
	 * /**********************************************************
	 */

	/**
	 * Method that JAX-RS container calls to try to figure out serialized length of
	 * given value. Since computation of this length is about as expensive as
	 * serialization itself, implementation will return -1 to denote "not known", so
	 * that container will determine length from actual serialized output (if
	 * needed).
	 */
	@Override
	public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		/*
		 * In general figuring output size requires actual writing; usually not worth it
		 * to write everything twice.
		 */
		return -1;
	}

	/**
	 * Method that JAX-RS container calls to try to check whether given value (of
	 * specified type) can be serialized by this provider. Implementation will first
	 * check that expected media type is a JSON type (via call to
	 * {@link #isJsonType}; then verify that type is not one of "untouchable" types
	 * (types we will never automatically handle), and finally that there is a
	 * serializer for type (iff {@link #checkCanSerialize} has been called with true
	 * argument -- otherwise assumption is there will be a handler)
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//		if (!isJsonType(mediaType)) {
//			return false;
//		}
//
//		/*
//		 * Ok: looks like we must weed out some core types here; ones that make no sense
//		 * to try to bind from JSON:
//		 */
//		if (_untouchables.contains(new ClassKey(type))) {
//			return false;
//		}
//		// but some are interface/abstract classes, so
//		for (Class<?> cls : _unwritableClasses) {
//			if (cls.isAssignableFrom(type)) {
//				return false;
//			}
//		}

		return true;
	}

	/**
	 * Method that JAX-RS container calls to serialize given value.
	 */
	@Override
	public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		try {
			gson.toJson(object, genericType, writer);
		} finally {
			writer.close();
		}
	}

	/*
	 * Public helper methods
	 */

	/**
	 * Helper method used to check whether given media type is JSON type or sub
	 * type. Current implementation essentially checks to see whether
	 * {@link MediaType#getSubtype} returns "json" or something ending with "+json".
	 */
	protected boolean isJsonType(MediaType mediaType) {
		/*
		 * As suggested by Stephen D, there are 2 ways to check: either being as
		 * inclusive as possible (if subtype is "json"), or exclusive (major type
		 * "application", minor type "json"). Let's start with inclusive one, hard to
		 * know which major types we should cover aside from "application".
		 */
		if (mediaType != null) {
			// Ok: there are also "xxx+json" subtypes, which count as well
			String subtype = mediaType.getSubtype();
			return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json");
		}
		/*
		 * Not sure if this can happen; but it seems reasonable that we can at least
		 * produce json without media type?
		 */
		return true;
	}

}