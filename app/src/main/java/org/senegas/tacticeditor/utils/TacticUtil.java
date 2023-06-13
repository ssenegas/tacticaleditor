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

import org.senegas.tacticeditor.model.PitchConstants;
import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.view.TaticView;

public class TacticUtil {

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

			for (PitchZone pz : PitchZone.values()) {
				System.out.println("PitchZone: " + pz.getName());
				IntStream.range(0, Tactic.NUMBER_OF_PLAYERS)
				.boxed()
				.forEach(index -> {
					//System.out.println("Player" + index + ": " + pitchZonePositions.get(index));

					List<Point> pos = pitchZonePositions.get(index);

					Map<Integer, Point> map = t.get(pz);
					if (map == null) {
						map = new HashMap<>();
						t.put(pz, map);
					}
					
					Integer key = mapToShirt(index);
					map.put(key, pos.get(pz.getIndex()));
					
					System.out.println("Player " + key + ": " + map.get(key));
				});
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return new Tactic(t);
	}
	
	/**
	 * Map an index to the given shirt in tactic
	 * 
	 * @param index from 0 to 10
	 * @return shirt number for tactics
	 */
	static private Integer mapToShirt(int index) {
		return Tactic.SHIRTS.get(index);
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
	 * projecting point from world-coordinates to screen-coordinates
	 * 
	 * world-coordinates            screen-coordinates
	 * 
	 *    x            0            0                y
	 *    ◄────────────┐0          0┌────────────────►
	 *    912          │            │              465
	 *                 │            │
	 *                 │     ──►    │
	 *                 │            │
	 *                 │            │
	 *                 │           x▼ 305
	 *                 │
	 *                 │
	 *                 │
	 *                 │
	 *            1392 ▼y
	 * 
	 * @param worldPosition
	 * @return screenPosition
	 */
	public static Point project(Point worldPosition) {
		final Dimension d = new Dimension(TaticView.PITCH_WIDTH_IN_PIXEL, TaticView.PITCH_HEIGHT_IN_PIXEL);
		
		final AffineTransform flipOverY = AffineTransform.getScaleInstance(-1, 1);
		Point2D transform = flipOverY.transform(worldPosition, null);
		
		final AffineTransform rotate = AffineTransform.getQuadrantRotateInstance(-1);
		transform = rotate.transform(transform, null);
		
		final double sx = d.getWidth() / PitchConstants.PITCH_HEIGHT_IN_PIXEL;
		final double sy = d.getHeight() / PitchConstants.PITCH_WIDTH_IN_PIXEL;
		final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);
		
		transform = scale.transform(transform, null);
		
		// +10 pixels for the offset behind the goal box in the tactic pitch image
		final AffineTransform translate = AffineTransform.getTranslateInstance(10, 0);
		transform = translate.transform(transform, null);
		
		final Point retValue = new Point();
		retValue.setLocation(transform);
		return retValue;
	}
	
	/**
	 * projecting point from screen-coordinates to world-coordinates
	 * 
	 *   screen-coordinates             world-coordinates
	 *  
	 *   0              0 y                x            0
	 *  0┌────────────────►                ◄────────────┐0
	 *   │              465                912          │
	 *   │                                              │
	 *   │                                              │
	 *   │                        ──►                   │
	 *   │                                              │
	 *   │                                              │
	 *  x▼ 305                                          │
	 *                                                  │
	 *                                                  │
	 *                                                  │
	 *                                                  │
	 *                                             1392 ▼y
	 * 
	 * @param screenPosition
	 * @return worldPosition
	 */
	public static Point unproject(Point screenPosition) {
		final Dimension d = new Dimension(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL);
		
		// -10 pixels for the offset behind the goal box in the tactic pitch image
		final AffineTransform translate = AffineTransform.getTranslateInstance(-10, 0);
		Point2D transform = translate.transform(screenPosition, null);
		
		final double sx = d.getHeight() / TaticView.PITCH_WIDTH_IN_PIXEL;
		final double sy = d.getWidth() / TaticView.PITCH_HEIGHT_IN_PIXEL;
		final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);
		
		transform = scale.transform(transform, null);
		
		final AffineTransform rotate = AffineTransform.getQuadrantRotateInstance(1);
		transform = rotate.transform(transform, null);
		
		final AffineTransform flipOverY = AffineTransform.getScaleInstance(-1, 1);
		transform = flipOverY.transform(transform, null);
		
		final Point retValue = new Point();
		retValue.setLocation(transform);
		return retValue;
	}
}
