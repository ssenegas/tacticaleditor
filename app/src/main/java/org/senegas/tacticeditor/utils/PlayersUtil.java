package org.senegas.tacticeditor.utils;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.PlayerBean;

public class PlayersUtil {

	private PlayersUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Map<String, PlayerBean> toPlayers(List<Point> points) {
		int numberOfPitchZone = PitchZone.values().length;
		List<PlayerBean> playerPositions = IntStream.range(0,
				(points.size() + numberOfPitchZone - 1) / numberOfPitchZone)
				.mapToObj(i -> new PlayerBean("player" + String.valueOf(i + 2),
						points.subList(i * numberOfPitchZone,
								Math.min(numberOfPitchZone * (i + 1), points.size()))))
				.collect(Collectors.toList());

		return playerPositions.stream()
				.collect(Collectors.toMap(PlayerBean::getName, Function.identity()));
	}

	public static List<Point> readPoints(String fileName) throws IOException {
		Path pathToPlayers = Path.of(fileName);

		final Pattern p = Pattern.compile("\\[x=(\\d*),y=(\\d*)\\]");

		Predicate<String> isPlayerName = line -> line.startsWith("player");
		Function<String, Point> toPoint = line -> {
			Matcher m = p.matcher(line);
			if (m.find()) {
				int x = Integer.valueOf(m.group(1));
				int y = Integer.valueOf(m.group(2));
				return new Point(x, y);
			}
			return new Point();
		};

		List<Point> points =
				Files.lines(pathToPlayers)
				.filter(isPlayerName.negate())
				.map(toPoint)
				.collect(Collectors.toList());
		return points;
	}

}
