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
import org.senegas.tacticeditor.model.TestPlayer;

public class TestPlayerUtil {

  private TestPlayerUtil() {
	throw new IllegalStateException("Utility class");
  }

  public static Map<String, TestPlayer> toTestPlayers(List<Point> points) {
	final int numberOfPitchZone = PitchZone.values().length;
	final List<TestPlayer> playerPositions = IntStream
	    .range(0, (points.size() + numberOfPitchZone - 1) / numberOfPitchZone)
	    .mapToObj(i -> new TestPlayer("player" + String.valueOf(i + 2),
	        points.subList(i * numberOfPitchZone, Math.min(numberOfPitchZone * (i + 1), points.size()))))
	    .collect(Collectors.toList());

	return playerPositions.stream().collect(Collectors.toMap(TestPlayer::getName, Function.identity()));
  }

  public static List<Point> readPoints(String fileName) throws IOException {
	final Pattern p = Pattern.compile("\\[x=(\\d*),y=(\\d*)\\]");

	final Predicate<String> isPlayerName = line -> line.startsWith("player");

	final Function<String, Point> toPoint = line -> {
	  final Matcher m = p.matcher(line);
	  if (m.find()) {
		final int x = Integer.valueOf(m.group(1));
		final int y = Integer.valueOf(m.group(2));
		return new Point(x, y);
	  }
	  return new Point();
	};

	final List<Point> points = Files.lines(Path.of(fileName)).filter(isPlayerName.negate()).map(toPoint)
	    .collect(Collectors.toList());

	return points;
  }

}
