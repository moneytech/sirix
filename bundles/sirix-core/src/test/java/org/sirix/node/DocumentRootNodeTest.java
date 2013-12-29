/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sirix.node;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sirix.Holder;
import org.sirix.TestHelper;
import org.sirix.api.PageReadTrx;
import org.sirix.exception.SirixException;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.delegates.StructNodeDelegate;
import org.sirix.settings.Fixed;

import com.google.common.base.Optional;

/**
 * Document root node test.
 */
public class DocumentRootNodeTest {

	/** {@link Holder} instance. */
	private Holder mHolder;

	/** Sirix {@link PageReadTrx} instance. */
	private PageReadTrx mPageReadTrx;

	@Before
	public void setUp() throws SirixException {
		TestHelper.closeEverything();
		TestHelper.deleteEverything();
		mHolder = Holder.generateDeweyIDSession();
		mPageReadTrx = mHolder.getSession().beginPageReadTrx();
	}

	@After
	public void tearDown() throws SirixException {
		mPageReadTrx.close();
		mHolder.close();
	}

	@Test
	public void testDocumentRootNode() throws IOException {

		// Create empty node.
		final NodeDelegate nodeDel = new NodeDelegate(
				Fixed.DOCUMENT_NODE_KEY.getStandardProperty(),
				Fixed.NULL_NODE_KEY.getStandardProperty(),
				Fixed.NULL_NODE_KEY.getStandardProperty(), 0,
				Optional.of(SirixDeweyID.newRootID()));
		final StructNodeDelegate strucDel = new StructNodeDelegate(nodeDel,
				Fixed.NULL_NODE_KEY.getStandardProperty(),
				Fixed.NULL_NODE_KEY.getStandardProperty(),
				Fixed.NULL_NODE_KEY.getStandardProperty(), 0, 0);
		final DocumentRootNode node = new DocumentRootNode(nodeDel, strucDel);
		check(node);

		// Serialize and deserialize node.
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		node.getKind().serialize(new DataOutputStream(out), node, mPageReadTrx);
		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final DocumentRootNode node2 = (DocumentRootNode) Kind.DOCUMENT.deserialize(new DataInputStream(in), node.getNodeKey(),
				mPageReadTrx);
		check(node2);
	}

	private final static void check(final DocumentRootNode node) {
		// Now compare.
		assertEquals(Fixed.DOCUMENT_NODE_KEY.getStandardProperty(),
				node.getNodeKey());
		assertEquals(Fixed.NULL_NODE_KEY.getStandardProperty(), node.getParentKey());
		assertEquals(Fixed.NULL_NODE_KEY.getStandardProperty(),
				node.getFirstChildKey());
		assertEquals(Fixed.NULL_NODE_KEY.getStandardProperty(),
				node.getLeftSiblingKey());
		assertEquals(Fixed.NULL_NODE_KEY.getStandardProperty(),
				node.getRightSiblingKey());
		assertEquals(0L, node.getChildCount());
		assertEquals(Kind.DOCUMENT, node.getKind());
	}

}
