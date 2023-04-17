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
import org.senegas.tacticeditor.view.TaticView;

public class TacticUtilTest {

	@Test
	public void shouldReturnCorrectTacticWhenReadingFromPath() {
		final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");

		final Tactic t = TacticUtil.read(path);

		assertEquals(20, t.getPositions().entrySet().size());
	}

	@Test
	public void shouldReturnCorrectTacticWhenReadingInputStream() {
		final Tactic t = TacticUtil.read(this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac"));

		assertEquals(20, t.getPositions().entrySet().size());
	}

	@Test
	public void shouldReadByteArrayWhenReadingTacticFile() throws IOException {
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac");

		final short[] readBinaryTacticFile = TacticUtil.readBinaryTacticFile(is);

		assertEquals(1, 1);
	}

	@Test
	public void shouldReturnUpperRightCoordinate() {
		final Dimension d = new Dimension(TaticView.TACTIC_PITCH_WIDTH_IN_PX, TaticView.TACTIC_PITCH_HEIGHT_IN_PX);

		final Point result = TacticUtil.transformTo(d, new Point(TacticUtil.PITCH_WIDTH_IN_PX, TacticUtil.PITCH_HEIGHT_IN_PX));

		assertThat(result, is(new Point(TaticView.TACTIC_PITCH_WIDTH_IN_PX + 10, 0))); // +10 for the offset
	}

	@Test
	public void shouldReturnCorrectOrigin() {
		final Dimension d = new Dimension(TaticView.TACTIC_PITCH_WIDTH_IN_PX, TaticView.TACTIC_PITCH_HEIGHT_IN_PX);

		final Point result = TacticUtil.transformTo(d, new Point(0, 0));

		assertThat(result, is(new Point(0 + 10, TaticView.TACTIC_PITCH_HEIGHT_IN_PX)));  // +10 for the offset
	}
}
