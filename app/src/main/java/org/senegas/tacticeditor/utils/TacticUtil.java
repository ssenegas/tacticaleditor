package org.senegas.tacticeditor.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.view.TaticView;

public class TacticUtil {

	public static final int PITCH_WIDTH_IN_PX = 914;
	public static final int PITCH_HEIGHT_IN_PX = 1392;

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
		Map<PitchZone, Map<Integer, Point>> t = new EnumMap<>(PitchZone.class);

		try (BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			final byte[] bytes = new byte[10 * 20 * 2 * 2]; // 10 players x 20 areas x position x and y (2 bytes per value, so 4)
			int count = bis.read(bytes);
			while (count != -1) {
				baos.write(bytes, 0, count);
				count = bis.read(bytes);
			}

			baos.flush();

			// from short values create a list of points
			final List<Point> points = extractPoints(baos.toByteArray());

			List<List<Point>> pitchZonePositions = chunk(points, PitchZone.values().length).stream()
					.collect(Collectors.toList());

			for (PitchZone z : PitchZone.values()) {
				IntStream.range(0, Tactic.NUMBER_OF_PLAYERS)
				.boxed()
				.forEach(index -> {
					System.out.println("Player" + index + ": " + pitchZonePositions.get(index));

					List<Point> pos = pitchZonePositions.get(index);

					Map<Integer, Point> map = t.get(z);
					if (map == null) {
						map = new HashMap<>();
						t.put(z, map);
					}
					map.put(index, pos.get(z.getIndex()));
				});
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return new Tactic(t);
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

	public static short[] readBinaryTacticFile(InputStream stream) throws IOException {
		final byte[] byteArray = stream.readAllBytes();

		// to turn bytes to shorts as either big endian or little endian.
		final short[] shortArray = new short[byteArray.length / 2];
		ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shortArray);

		return shortArray;
	}

	/**
	 * moving from world-coordinates to screen-coordinates
	 * @param worldPosition
	 * @return screenPosition
	 */
	public static Point project(Point worldPosition) {
		final Dimension d = new Dimension(TaticView.TACTIC_PITCH_WIDTH_IN_PX, TaticView.TACTIC_PITCH_HEIGHT_IN_PX);
		
		final AffineTransform rotateTranslate = AffineTransform.getQuadrantRotateInstance(-1);
		rotateTranslate.translate(-PITCH_WIDTH_IN_PX, 0);

		final double sx = d.getWidth() / PITCH_HEIGHT_IN_PX;
		final double sy = d.getHeight() / PITCH_WIDTH_IN_PX;
		final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);

		// +10 pixels for the offset behind the goal in the tactic pitch image
		final AffineTransform translate = AffineTransform.getTranslateInstance(10, 0);

		final Point retValue = new Point();
		
		Point2D transform1 = rotateTranslate.transform(worldPosition, null);
		Point2D transform2 = scale.transform(transform1, null);
		Point2D transform3 = translate.transform(transform2, null);
		
		retValue.setLocation(translate.transform(scale.transform(rotateTranslate.transform(worldPosition, null), null), null));

		return retValue;
	}
	
	/**
	 * moving from screen-coordinates to world-coordinates
	 * @param screenPosition
	 * @return worldPosition
	 */
	public static Point unproject(Point screenPosition) {
		final Dimension d = new Dimension(PITCH_WIDTH_IN_PX, PITCH_HEIGHT_IN_PX);
		
		final AffineTransform rotateTranslate = AffineTransform.getQuadrantRotateInstance(1);
		rotateTranslate.translate(0, -PITCH_WIDTH_IN_PX);

		final double sx = d.getHeight() / TaticView.TACTIC_PITCH_WIDTH_IN_PX;
		final double sy = d.getWidth() / TaticView.TACTIC_PITCH_HEIGHT_IN_PX;
		
		final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);

		// -10 pixels for the offset behind the goal in the tactic pitch image
		final AffineTransform translate = AffineTransform.getTranslateInstance(-10, 0);

		final Point retValue = new Point();
		
		Point2D transform1 = translate.transform(screenPosition, null);
		Point2D transform2 = scale.transform(transform1, null);
		Point2D transform3 = rotateTranslate.transform(transform2, null);
		
		retValue.setLocation(rotateTranslate.transform(scale.transform(translate.transform(screenPosition, null), null), null));

		return retValue;
	}
}
