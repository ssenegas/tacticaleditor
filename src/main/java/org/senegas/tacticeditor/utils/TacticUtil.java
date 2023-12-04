package org.senegas.tacticeditor.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.senegas.tacticeditor.view.TacticEditorView;

public class TacticUtil {

  private TacticUtil() {
	throw new IllegalStateException("Utility class");
  }

  /**
  * Projection of a point from world-coordinates to screen-coordinates
  * @param fromWorld
  * @return Point to screen
  */
  public static Point project(Point fromWorld) {
	final Dimension d = new Dimension(TacticEditorView.PITCH_WIDTH_IN_PIXEL, TacticEditorView.PITCH_HEIGHT_IN_PIXEL);

	final AffineTransform flipOverY = AffineTransform.getScaleInstance(-1, 1);
	Point2D transform = flipOverY.transform(fromWorld, null);

	final AffineTransform rotate = AffineTransform.getQuadrantRotateInstance(-1);
	transform = rotate.transform(transform, null);

	final double sx = d.getWidth() / PitchConstants.PITCH_HEIGHT_IN_PIXEL;
	final double sy = d.getHeight() / PitchConstants.PITCH_WIDTH_IN_PIXEL;
	final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);

	transform = scale.transform(transform, null);

	// +10 pixels for the offset behind the goal box in the tactic pitch image
	final AffineTransform translate = AffineTransform.getTranslateInstance(10, 0);
	transform = translate.transform(transform, null);

	final Point toScreen = new Point();
	toScreen.setLocation(transform);
	return toScreen;
  }

  /**
  * Projection of a point from screen-coordinates to world-coordinates
  * @param fromScreen
  * @return Point to world
  */
  public static Point unproject(Point fromScreen) {
	final Dimension d = new Dimension(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL);

	// -10 pixels for the offset behind the goal box in the tactic pitch image
	final AffineTransform translate = AffineTransform.getTranslateInstance(-10, 0);
	Point2D transform = translate.transform(fromScreen, null);

	final double sx = d.getHeight() / TacticEditorView.PITCH_WIDTH_IN_PIXEL;
	final double sy = d.getWidth() / TacticEditorView.PITCH_HEIGHT_IN_PIXEL;
	final AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);

	transform = scale.transform(transform, null);

	final AffineTransform rotate = AffineTransform.getQuadrantRotateInstance(1);
	transform = rotate.transform(transform, null);

	final AffineTransform flipOverY = AffineTransform.getScaleInstance(-1, 1);
	transform = flipOverY.transform(transform, null);

	final Point toWorld = new Point();
	toWorld.setLocation(transform);
	return toWorld;
  }
}
