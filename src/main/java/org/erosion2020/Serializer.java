package org.erosion2020;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class Serializer implements Callable<byte[]> {
	private final Object object;
	public Serializer(Object object) {
		this.object = object;
	}

	public byte[] call() throws Exception {
		return serialize(object);
	}

	public static byte[] serialize(final Object obj) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(obj, out);
		return out.toByteArray();
	}

	public static void serialize(final Object obj, final OutputStream out) throws IOException {
		if(obj instanceof String) {
			out.write(((String) obj).getBytes(StandardCharsets.UTF_8));
		}else if(obj instanceof byte[]) {
			out.write((byte[])obj);
		}else {
			final ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
		}
	}

}