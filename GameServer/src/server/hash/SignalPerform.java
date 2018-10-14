/**
 * 
 */
package server.hash;

import hash.key.SignalKey;
import server.thread.ClientSocketThread;

/**
 * @author Park
 *
 */
public interface SignalPerform {
	public abstract void performAction(ClientSocketThread clientSocketThread, SignalKey key);
}
