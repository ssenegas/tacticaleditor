package org.senegas.tacticeditor.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.senegas.tacticeditor.model.PitchConstants;
import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TestPlayer;
import org.senegas.tacticeditor.view.TacticView;

class TacticUtilTest {

  private static Map<String, TestPlayer> playersPositionIn532;

  @BeforeAll
  public static void setup() throws IOException {
	playersPositionIn532 = TestPlayerUtil.toTestPlayers(TestPlayerUtil.readPoints("src/test/resources/5-3-2.txt"));
  }

  @Test
   void shouldReturnCorrectTacticWhenReadingFromPath() throws IOException {
	// given
	final Path path = Path.of("src/test/resources/tactics/5-3-2.tac");

	// when
	final Tactic t = Tactic.create(path);

	// then
	assertEquals(PitchZone.values().length, t.getAllPositions().entrySet().size());
	IntStream.range(0 + 2, Tactic.NUMBER_OF_PLAYERS + 2) // +2, squad numbers go from 2 to 11
	    .forEach(squadNumber -> IntStream.range(0, PitchZone.values().length)
	        .forEach(pzIndex -> assertTrue(playersPositionIn532.get("player" + squadNumber).getPositions().get(pzIndex)
	            .equals(t.getPositions(PitchZone.of(pzIndex)).get(squadNumber)))));
  }

  @Test
  void shouldReturnCorrectTacticWhenReadingInputStream() throws IOException {
	// given
	try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac")) {

	  // when
	  final Tactic t = Tactic.createFromStream(is);

	  // then
	  assertEquals(PitchZone.values().length, t.getAllPositions().entrySet().size());
	  IntStream.range(0 + 2, Tactic.NUMBER_OF_PLAYERS + 2) // +2, squad numbers go from 2 to 11
	      .forEach(squadNumber -> IntStream.range(0, PitchZone.values().length)
	          .forEach(pzIndex -> assertTrue(playersPositionIn532.get("player" + squadNumber).getPositions()
	              .get(pzIndex).equals(t.getPositions(PitchZone.of(pzIndex)).get(squadNumber)))));
	}
  }

  @Test
  void shouldReadByteArrayWhenReadingTacticFile() throws IOException {
	// given
	try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("5-3-2.tac")) {

	  // when
	  final short[] readBinaryTacticFile = Tactic.readBinaryTacticFile(is);

	  // then
	  assertEquals(490, readBinaryTacticFile.length);
	}
  }

  @Test
  void shouldReturnProjectedLowerRightScreenPositionWhenLowerLeftWorldPositionIsGiven() {
	// given
	final Point lowerLeftWorldPosition = new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL,
	    PitchConstants.PITCH_HEIGHT_IN_PIXEL);
	final Point expected = new Point(TacticView.PITCH_WIDTH_IN_PIXEL + 10, // +10 pixels for the offset behind the goal
	    TacticView.PITCH_HEIGHT_IN_PIXEL);

	// when
	final Point actual = TacticUtil.project(lowerLeftWorldPosition);

	// then
	assertThat(actual, is(expected));
  }

  @Test
  void shouldReturnProjectedUpperRightScreenPositionWhenOriginUpperRightWorldPositionIsGiven() {
	// given
	final Point originUpperRightWorldPosition = new Point(0, 0);
	final Point expected = new Point(0 + 10, // +10 pixels for the offset behind the goal
	    0);

	// when
	final Point actual = TacticUtil.project(originUpperRightWorldPosition);

	// then
	assertThat(actual, is(expected));
  }

  @Test
  void shouldReturnLowerLeftWorldPositionWhenLowerLeftScreenPositionIsGiven() {
	// given
	final Point lowerLeftScreenPosition = new Point(10, 305);

	// when
	final Point actual = TacticUtil.unproject(lowerLeftScreenPosition);

	// then
	assertThat(actual, is(new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, 0)));
  }

  @Test
  void shouldReturnUpperRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
	// given
	final Point upperRightLeftScreenPosition = new Point(TacticView.PITCH_WIDTH_IN_PIXEL + 10, 0);

	// when
	final Point actual = TacticUtil.unproject(upperRightLeftScreenPosition);

	// then
	assertThat(actual, is(new Point(0, PitchConstants.PITCH_HEIGHT_IN_PIXEL)));
  }

  @Test
  void shouldReturnLowerRightWorldPositionWhenLowerLeftScreenPositionIsGiven() {
	// given
	final Point upperRightLeftScreenPosition = new Point(TacticView.PITCH_WIDTH_IN_PIXEL + 10,
	    TacticView.PITCH_HEIGHT_IN_PIXEL);

	// when
	final Point actual = TacticUtil.unproject(upperRightLeftScreenPosition);

	// then
	assertThat(actual, is(new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL)));
  }
}
