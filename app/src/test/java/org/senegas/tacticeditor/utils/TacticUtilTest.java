package org.senegas.tacticeditor.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.senegas.tacticeditor.model.PitchConstants;
import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.PlayerBean;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.view.TaticView;

public class TacticUtilTest {
	
	private static Map<String, PlayerBean> playersPositionIn532;
	
	@BeforeClass
    public static void setup() throws IOException {
		playersPositionIn532 =
				PlayersUtil.toPlayers(PlayersUtil.readPoints("src/test/resources/5-3-2.txt"));
    }

	@Test
	public void shouldReturnCorrectTacticWhenReadingFromPath() throws IOException {
		// given
		final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");
		
		// when
		final Tactic t = Tactic.create(path);

		// then
		assertEquals(20, t.getAllPositions().entrySet().size());
		IntStream.range(2, 12)
			.forEach(i -> IntStream.range(0, 20)
							.forEach(j -> assertTrue(playersPositionIn532.get("player" + i).getPositions().get(j)
									.equals(t.getPositions(PitchZone.of(j)).get(i)))));
	}

	@Test
	public void shouldReturnCorrectTacticWhenReadingInputStream() throws IOException {
		try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac")) {
			final Tactic t = Tactic.createFromStream(is);
			// TODO refactor assertion
			assertEquals(20, t.getAllPositions().entrySet().size());
		}
	}

	@Test
	public void shouldReadByteArrayWhenReadingTacticFile() throws IOException {
		try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac")) {
			final short[] readBinaryTacticFile = Tactic.readBinaryTacticFile(is);
			assertEquals(490, readBinaryTacticFile.length);
		}
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
	public void shouldReturnCorrectPositionForKickOffOwnPitchZone() throws IOException {
		// given
		final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");
		final Integer shirt = 2;
		final Tactic t = Tactic.create(path);
		final Point expected = new Point(666, 345);

		// when
		Map<Integer, Point> positionsForKickoffOwn = t.getPositions(PitchZone.KICKOFF_OWN);
		Point result = positionsForKickoffOwn.get(shirt);
		
		// then
		assertThat(result, is(expected));
	}
}
