package org.senegas.tacticeditor.utils;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.senegas.tacticeditor.model.PlayerBean;

public class PlayersUtilTest {

	@Test
	public void readPointsShouldReturnTwoHundredPoints() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		List<Point> points = PlayersUtil.readPoints(fileName);
		
		// then
		assertEquals(200, points.size());
	}
	
	@Test
	public void toPlayersShouldReturnTenPlayers() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, PlayerBean> players = PlayersUtil.toPlayers(PlayersUtil.readPoints(fileName));
		
		// then
		assertEquals(10, players.size());
	}
	
	@Test
	public void playersPositionsShouldReturnTwentyPositions() throws IOException {
		// given
		String fileName = "src/test/resources/5-3-2.txt";
		
		// when
		Map<String, PlayerBean> players = PlayersUtil.toPlayers(PlayersUtil.readPoints(fileName));
		
		// then
		players.forEach((key, value) -> assertEquals(20, value.getPositions().size()));
	}

}
