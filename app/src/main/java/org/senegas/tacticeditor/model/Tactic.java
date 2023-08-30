package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tactic implements Serializable {
	
	public static final int NUMBER_OF_PLAYERS = 10;
	public static final List<Integer> SQUAD_NUMBERS = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

	/**
	 *
	 */
	private static final long serialVersionUID = -6255623013679331171L;

	private static final Logger LOGGER = Logger.getLogger(Tactic.class.getName());
	private final Map<PitchZone, Map<Integer, Point>> positions;

	/**
	 * Returns a tactic
	 * 
	 * @param path
	 * @return a Tactic containing squad positions read from a file
	 * @throws IOException
	 */
	public static Tactic create(Path path) throws IOException {
		LOGGER.log(Level.INFO, "Creating Tactic instance at : {0}", LocalTime.now());
		try (InputStream inputStream = Files.newInputStream(path)) {
			return createFromStream(inputStream);
		}
	}
	
	/**
	 * Returns a tactic
	 * 
	 * @param path
	 * @return a Tactic containing squad positions read from an InputStream
	 * @throws IOException
	 */
	public static Tactic createFromStream(InputStream inputStream) throws IOException {
		return new Tactic(read(inputStream));
	}

	// Suppresses default constructor, ensuring non-instantiability
	private Tactic() {
		this(new EnumMap<>(PitchZone.class));
	}

	private Tactic(Map<PitchZone, Map<Integer, Point>> positions) {
		this.positions = positions;
	}

	public Map<PitchZone, Map<Integer, Point>> getAllPositions() {
		return this.positions;
	}
	
	public Map<Integer, Point> getPositions(PitchZone zone) {
		return Optional.ofNullable(this.positions.get(zone))
				.orElseThrow(() -> new NoSuchElementException("Zone not found"));
	}
	
//	public Point getPositionFor(PitchZone zone, Integer shirt) {
//		if (! SHIRTS.contains(shirt)) {
//			throw new InvalidParameterException();
//		}
//		return Optional.ofNullable(getPositions(zone).get(shirt))
//				.orElseThrow(() -> new NoSuchElementException("Position not found"));
//	}

	@Override
	public String toString() {
		return "Tactic [positions=" + this.positions + "]";
	}
	
	public static short[] readBinaryTacticFile(InputStream stream) throws IOException {
		final byte[] byteArray = stream.readAllBytes();
	
		// to turn bytes to shorts as either big endian or little endian.
		final short[] shortArray = new short[byteArray.length / 2];
		ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shortArray);
	
		return shortArray;
	}

	private static Map<PitchZone, Map<Integer, Point>> read(InputStream is) {
		Map<PitchZone, Map<Integer, Point>> positions = new EnumMap<>(PitchZone.class);

		try (BufferedInputStream bis = new BufferedInputStream(is);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			final byte[] bytes = new byte[Tactic.NUMBER_OF_PLAYERS * PitchZone.values().length * 2 * 2]; // 10 players x 20 areas x position x and y (2 bytes per value, so 4)
			int count = bis.read(bytes);
			while (count != -1) {
				baos.write(bytes, 0, count);
				count = bis.read(bytes);
			}
			baos.flush();

			// wrap byte array to a list of points, resulting in a list of two hundred points (20 pitch zones with 10 positions)
			final List<Point> points = extractPoints(baos.toByteArray());

			// form ten groups of twenty points. For each player, his position for each pitch zone.
			List<List<Point>> splittedPositions = chunk(points, PitchZone.values().length).stream()
					.collect(Collectors.toList());
			
			AtomicInteger counter = new AtomicInteger(1);
			splittedPositions.stream()
				.forEach(player -> {
					counter.getAndIncrement();
					System.out.println("player " + counter.get());
					player.stream()
						.forEach(System.out::println);
				});

			// populate the pitch zone players positions
			for (PitchZone pitchZone : PitchZone.values()) {
				LOGGER.log(Level.INFO, "Populate pitchZone : {0}", pitchZone.getName());
				IntStream.range(0, Tactic.NUMBER_OF_PLAYERS)
					.boxed()
					//.forEach(p -> populatePositions(pitchZone, p, splittedPositions, positions));
				.forEach(p -> {
					Map<Integer, Point> pitchZonePositions = positions.get(pitchZone);
					if (pitchZonePositions == null) { // if no value for the pitch zone, create an empty one and add it
						pitchZonePositions = new HashMap<>();
						positions.put(pitchZone, pitchZonePositions);
					}
					Map.Entry<Integer, Point> entry = extractPlayerPitchZonePosition(splittedPositions, pitchZone, p);
					pitchZonePositions.put(entry.getKey(), entry.getValue());
				});
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return positions;
	}
	
	private static Map.Entry<Integer, Point> extractPlayerPitchZonePosition(List<List<Point>> splittedPositions, PitchZone pitchZone,
			Integer index) {
		List<Point> pos = splittedPositions.get(index);
		Integer squadNumber = Tactic.SQUAD_NUMBERS.get(index);
		LOGGER.log(Level.INFO, "Player {0} at {1}", new Object[] { squadNumber, pos.get(pitchZone.getIndex())});
		return new AbstractMap.SimpleEntry<>(squadNumber, pos.get(pitchZone.getIndex()));
	}
	
	/**
	 * @param buffer
	 * @return
	 */
	private static List<Point> extractPoints(final byte[] buffer) {
		ShortBuffer shortBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
		return IntStream.iterate(0, i -> i + 2)
				.limit(Tactic.NUMBER_OF_PLAYERS * PitchZone.values().length)
				.mapToObj(index -> new Point(shortBuffer.get(index), shortBuffer.get(index + 1)))
				.collect(Collectors.toList());
	}

	private static <T> Collection<List<T>> chunk(List<T> src, int size) {
		return IntStream.range(0, src.size())
				.boxed()
				.collect(Collectors.groupingBy(x -> x / size,
						Collectors.mapping(src::get, Collectors.toList())))
				.values();
	}
}
