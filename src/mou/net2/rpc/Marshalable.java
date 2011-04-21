/*
 * $Id$
 * Created on Jun 4, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public interface Marshalable
{
	public void marshal(DataOutput out) throws IOException;
	public void unmarshal(DataInput in)throws IOException;
}
