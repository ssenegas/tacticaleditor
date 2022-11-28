package org.senegas.tacticeditor.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.senegas.tacticeditor.model.Tactic;

public class TacticUtil {

	public static final int PITCH_WIDTH_IN_PX = 914;
	public static final int PITCH_HEIGHT_IN_PX = 1392;
	private static final int NUM_PLAYERS = 10;
	private static final int NUM_SECTORS = 20;

	private TacticUtil() {
	    throw new IllegalStateException("Utility class");
	  }

	public static Tactic read(Path path) {
		try (InputStream inputStream = Files.newInputStream(path)) {
			return read(inputStream);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new Tactic();
	}

	public static Tactic read(InputStream is) {

		final Tactic result = new Tactic();

		try (BufferedInputStream bis = new BufferedInputStream(is);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			final byte[] bytes = new byte[10 * 20 * 2 * 2]; // 10 players x 20 areas x position x and y (2 bytes per value, so 4)
			int count = bis.read(bytes);
			while (count != -1) {
				baos.write(bytes, 0, count);
				count = bis.read(bytes);
			}

			baos.flush();

			final byte[] readBytes = baos.toByteArray();
			final short[] shortArray = new short[readBytes.length / 2];
			ByteBuffer.wrap(readBytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shortArray);

			final List<Point> rawPositions = IntStream.range(0, shortArray.length - 1)
					.filter(n -> n % 2 == 0)
					.mapToObj(i -> new Point(shortArray[i], shortArray[i + 1]))
					.collect(Collectors.toList());

			final Map<Integer, List<Point>> positions = new HashMap<>();
			int index = 0;
			for (int i = 0; i < NUM_PLAYERS; i++) {
				final List<Point> points = new ArrayList<>();
				for (int j = 0; j < NUM_SECTORS; j++) {
					points.add(rawPositions.get(index));
					index++;
				}
				positions.put( i + 2, points);
			}

//			Map<Integer, List<Point>> positions = IntStream.range(2, NUMBER_OF_PLAYERS + 2) // shirt number start @ 2 to 11
//						.boxed()
//						.collect(Collectors.toMap(Function.identity(),
//							i -> rawPositions.subList(i * NUMBER_OF_REGIONS,
//									Math.min(NUMBER_OF_REGIONS * (i + 1),rawPositions.size()))));

			for (final Map.Entry<Integer, List<Point>> entry : positions.entrySet()) {
				System.out.println("Player" + entry.getKey() + " :" + entry.getValue().toString());
			}

			result.setPositions(positions);

		} catch (final IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static short[] readBinaryTacticFile(InputStream stream) throws IOException {
		final byte[] byteArray = stream.readAllBytes();

 		// to turn bytes to shorts as either big endian or little endian.
		final short[] shortArray = new short[byteArray.length / 2];
		ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shortArray);

		return shortArray;
	}

	public static Point transformTo(Dimension d, Point source) {
		final AffineTransform rotateTranslate = AffineTransform.getQuadrantRotateInstance(-1);
		rotateTranslate.translate(-PITCH_WIDTH_IN_PX, 0);

		final double sx = d.getWidth() / PITCH_HEIGHT_IN_PX;
		final double sy = d.getHeight() / PITCH_WIDTH_IN_PX;
		final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);

		final AffineTransform translate = AffineTransform.getTranslateInstance(10, 0);

		final Point retValue = new Point();
		retValue.setLocation(translate.transform(scale.transform(rotateTranslate.transform(source, null), null), null));

		return retValue;
	}
}
