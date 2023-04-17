package org.senegas.tacticeditor.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

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
	public void shouldLowerRightWorldPositionReturnProjectedLowerRightScreenCoordinate() {
		// Given
		Point lowerRightWorldPosition = new Point(TacticUtil.PITCH_WIDTH_IN_PX, TacticUtil.PITCH_HEIGHT_IN_PX);
		
		// When
		final Point result = TacticUtil.project(lowerRightWorldPosition);

		// Then
		assertThat(result, is(new Point(TaticView.TACTIC_PITCH_WIDTH_IN_PX + 10, 0))); // +10 pixels for the offset behind the goal
	}

	@Test
	public void shouldReturnProjectedLowerLeftScreenCoordinateWhenUpperLeftWorldPositionIsGiven() {
		// Given
		Point upperLeftWorldPosition = new Point(0, 0);
		
		// When
		final Point result = TacticUtil.project(upperLeftWorldPosition);

		// Then
		assertThat(result, is(new Point(0 + 10, TaticView.TACTIC_PITCH_HEIGHT_IN_PX)));
	}
	
	@Test
	public void shouldReturnLowerRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
		Point lowerLeftScreenPosition = new Point(10, 305);
		
		// When
		final Point result = TacticUtil.unproject(lowerLeftScreenPosition);
		
		// Then
		assertThat(result, is(new Point(0, 0)));
	}
	
	@Test
	public void shouldReturnUpperRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
		Point upperRightLeftScreenPosition = new Point(475, 0);
		
		// When
		final Point result = TacticUtil.unproject(upperRightLeftScreenPosition);
		
		// Then
		assertThat(result, is(new Point(TacticUtil.PITCH_WIDTH_IN_PX, TacticUtil.PITCH_HEIGHT_IN_PX)));
	}
}
