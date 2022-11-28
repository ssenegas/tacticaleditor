package org.senegas.tacticeditor.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.Test;
import org.senegas.tacticeditor.model.Tactic;

public class TacticUtilTest {

	private static final int PITCH_WIDTH_IN_PX = 914;
	private static final int PITCH_HEIGHT_IN_PX = 1392;
	private static final int TACTIC_PITCH_WIDTH_IN_PX = 465;
	private static final int TACTIC_PITCH_HEIGHT_IN_PX = 305;


	@Test
	public void shouldReturnCorrectTacticWhenReadingFromPath() {
		final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");

		final Tactic t = TacticUtil.read(path);

		assertEquals(10, t.getPositions().entrySet().size());
	}

	@Test
	public void shouldReturnCorrectTacticWhenReadingInputStream() {
		final Tactic t = TacticUtil.read(this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac"));

		assertEquals(10, t.getPositions().entrySet().size());
	}

	@Test
	public void shouldReadByteArrayWhenReadingTacticFile() throws IOException {
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac");

		final short[] readBinaryTacticFile = TacticUtil.readBinaryTacticFile(is);

		assertEquals(1, 1);
	}

	@Test
	public void shouldReturnUpperRightCoordinate() {
		final Dimension d = new Dimension(TACTIC_PITCH_WIDTH_IN_PX, TACTIC_PITCH_HEIGHT_IN_PX);

		final Point result = TacticUtil.transformTo(d, new Point(PITCH_WIDTH_IN_PX, PITCH_HEIGHT_IN_PX));

		assertThat(result, is(new Point(TACTIC_PITCH_WIDTH_IN_PX + 10, 0))); // +10 for the offset
	}

	@Test
	public void shouldReturnCorrectOrigin() {
		final Dimension d = new Dimension(TACTIC_PITCH_WIDTH_IN_PX, TACTIC_PITCH_HEIGHT_IN_PX);

		final Point result = TacticUtil.transformTo(d, new Point(0, 0));

		assertThat(result, is(new Point(0 + 10, TACTIC_PITCH_HEIGHT_IN_PX)));  // +10 for the offset
	}
}
