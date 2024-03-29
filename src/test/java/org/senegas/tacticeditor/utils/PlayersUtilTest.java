package org.senegas.tacticeditor.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TestPlayer;

class PlayersUtilTest {

	@Test
	void readPointsShouldReturnTwoHundredPoints() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		List<Point> points = TestPlayerUtil.readPoints(fileName);
		
		// then
		assertEquals(Tactic.NUMBER_OF_PLAYERS * PitchZone.values().length, points.size());
	}
	
	@Test
	void toPlayersShouldReturnTenPlayers() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, TestPlayer> players = TestPlayerUtil.toTestPlayers(TestPlayerUtil.readPoints(fileName));
		
		// then
		assertEquals(Tactic.NUMBER_OF_PLAYERS, players.size());
	}
	
	@Test
	void playersPositionsShouldReturnTwentyPositions() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, TestPlayer> players = TestPlayerUtil.toTestPlayers(TestPlayerUtil.readPoints(fileName));
		
		// then
		players.forEach((key, value) -> assertEquals(PitchZone.values().length, value.getPositions().size()));
	}
}
