package org.senegas.tacticeditor.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.senegas.tacticeditor.model.PitchConstants;
import org.senegas.tacticeditor.view.TaticView;

public class TacticUtil {

	private TacticUtil() {
		throw new IllegalStateException("Utility class");
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
