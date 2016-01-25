package com.horizon.flake.util;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/15 17:39
 * @see
 * @since : 1.0.0
 */
public class FstSerializer{

	private FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	public byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		FSTObjectOutput out = conf.getObjectOutput(stream);
		out.writeObject(object);
		out.flush();
		stream.close();
		return stream.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes) throws IOException {
		T result = null;
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			FSTObjectInput in = conf.getObjectInput(stream);
			result = (T) in.readObject();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
