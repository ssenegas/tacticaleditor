package org.senegas.tacticeditor.utils;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Player;
import org.senegas.tacticeditor.model.Tactic;

public class PlayersUtilTest {

	@Test
	public void readPointsShouldReturnTwoHundredPoints() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		List<Point> points = PlayersUtil.readPoints(fileName);
		
		// then
		assertEquals(Tactic.NUMBER_OF_PLAYERS * PitchZone.values().length, points.size());
	}
	
	@Test
	public void toPlayersShouldReturnTenPlayers() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, Player> players = PlayersUtil.toPlayers(PlayersUtil.readPoints(fileName));
		
		// then
		assertEquals(Tactic.NUMBER_OF_PLAYERS, players.size());
	}
	
	@Test
	public void playersPositionsShouldReturnTwentyPositions() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, Player> players = PlayersUtil.toPlayers(PlayersUtil.readPoints(fileName));
		
		// then
		players.forEach((key, value) -> assertEquals(PitchZone.values().length, value.getPositions().size()));
	}
}
