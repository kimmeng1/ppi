package org.sar.ppi;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import org.junit.Test;
import org.sar.ppi.dispatch.MessageHandler;
import org.sar.ppi.events.Message;
import org.sar.ppi.peersim.PeerSimRunner;
import peersim.config.Configuration;

/**
 * ExampleNodeProces
 */
public class BroadcastOrderTest extends RedirectedTest {

	public static class ExampleMessage extends Message {
		private static final long serialVersionUID = 1L;
		private String s;

		public ExampleMessage(int src, int dest, String s) {
			super(src, dest);
			this.s = s;
		}

		public String getS() {
			return s;
		}
	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		System.out.printf(
			"%d Received '%s' from %d\n",
			message.getIddest(),
			message.getS(),
			message.getIdsrc()
		);
		infra.exit();
	}

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			for (int i = 1; i < infra.size(); i++) {
				infra.send(new ExampleMessage(infra.getId(), i, "OrderTest"));
			}
			infra.exit();
		}
	}

	private static final Integer NETWORKSIZE = 10;
	private static final String PAR_SIZE = "network.size";

	String outputPeersim;

	@Test
	public void peersim() {
		Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], NETWORKSIZE);
		int networkSize = Configuration.getInt(PAR_SIZE);
		int i = 0;

		outputPeersim = outContent.toString();
		System.out.println(outputPeersim);
		Scanner scanner = new Scanner(outputPeersim);
		String[] expected = new String[networkSize];

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()) { //Skipping empty output lines
				continue;
			} else {
				String[] results = line.split(" ");
				System.out.println("dd" + line);
				if (i < networkSize - 1) { // Initialisation des output expected pour chaque ligne lors du premier experiment
					expected[i] = results[0] + "," + results[2] + "," + results[4];
				} else { // Comparaison des outputs pour chaque experiment au-delà du premier
					assertEquals(
						expected[i % (networkSize - 1)],
						results[0] + "," + results[2] + "," + results[4]
					);
				}
			}
			i++;
		}
		scanner.close();
		//originalErr.println(outputPeersim);
	}
}
