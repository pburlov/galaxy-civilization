/*
 * $Id$
 * Created on Jul 16, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.commons.lang.math.RandomUtils;
import mou.net2.PeerHandle;
import junit.framework.TestCase;


public class JUnitTest extends TestCase
{
	public void testPeerHandleSerialize() throws IOException, ClassNotFoundException
	{
		PeerHandle handle = new PeerHandle(RandomUtils.nextLong(), new InetSocketAddress(InetAddress.getLocalHost(),1234));
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		handle.writeExternal(out);
		out.close();
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectInputStream in = new ObjectInputStream(bin);
		PeerHandle handle2 = new PeerHandle();
		handle2.readExternal(in);
		assertEquals(handle, handle2);
	}
}
