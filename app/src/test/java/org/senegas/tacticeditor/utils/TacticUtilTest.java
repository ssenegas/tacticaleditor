package org.senegas.tacticeditor.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Test;
import org.senegas.tacticeditor.model.PitchConstants;
import org.senegas.tacticeditor.model.PitchZone;
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

		assertEquals(490, readBinaryTacticFile.length);
	}

	@Test
	public void shouldReturnProjectedLowerRightScreenPositionWhenLowerLeftWorldPositionIsGiven() {
		// given
		Point lowerLeftWorldPosition = new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL);
		Point expected = new Point(TaticView.PITCH_WIDTH_IN_PIXEL + 10, // +10 pixels for the offset behind the goal
				TaticView.PITCH_HEIGHT_IN_PIXEL);
		
		// when
		final Point result = TacticUtil.project(lowerLeftWorldPosition);

		// then
		assertThat(result, is(expected)); 
	}

	@Test
	public void shouldReturnProjectedUpperRightScreenPositionWhenOriginUpperRightWorldPositionIsGiven() {
		// given
		Point originUpperRightWorldPosition = new Point(0, 0);
		Point expected = new Point(0 + 10, // +10 pixels for the offset behind the goal
				0);
		
		// when
		final Point result = TacticUtil.project(originUpperRightWorldPosition);

		// then
		assertThat(result, is(expected));
	}
	
	@Test
	public void shouldReturnLowerLeftWorldPositionWhenLowerLeftScreenPositionIsGiven() {
		// given
		Point lowerLeftScreenPosition = new Point(10, 305);
		
		// when
		final Point result = TacticUtil.unproject(lowerLeftScreenPosition);
		
		// then
		assertThat(result, is(new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, 0)));
	}
	
	@Test
	public void shouldReturnUpperRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
		Point upperRightLeftScreenPosition = new Point(TaticView.PITCH_WIDTH_IN_PIXEL + 10, 0);
		
		// when
		final Point result = TacticUtil.unproject(upperRightLeftScreenPosition);
		
		// then
		assertThat(result, is(new Point(0, PitchConstants.PITCH_HEIGHT_IN_PIXEL)));
	}
	
	@Test
	public void shouldReturnLowerRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
		Point upperRightLeftScreenPosition = new Point(TaticView.PITCH_WIDTH_IN_PIXEL + 10, TaticView.PITCH_HEIGHT_IN_PIXEL);
		
		// when
		final Point result = TacticUtil.unproject(upperRightLeftScreenPosition);
		
		// then
		assertThat(result, is(new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL)));
	}
	
	@Test
	public void shouldReturnCorrectPositionForKickOffOwnPitchZone() {
		// given
		final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");
		final Integer shirt = 2;
		final Tactic t = TacticUtil.read(path);
		final Point expected = new Point(666, 345);

		// when
		Map<Integer, Point> positionsForKickoffOwn = t.getPositionsFor(PitchZone.KICKOFF_OWN);
		Point result = positionsForKickoffOwn.get(shirt);
		
		// then
		assertThat(result, is(expected));
	}
}
