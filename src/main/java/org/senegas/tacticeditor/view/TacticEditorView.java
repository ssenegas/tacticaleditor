package org.senegas.tacticeditor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TacticModel;
import org.senegas.tacticeditor.utils.PitchConstants;
import org.senegas.tacticeditor.utils.TacticUtil;

public class TacticEditorView extends JPanel implements PropertyChangeListener {
  public static final int PITCH_WIDTH_IN_PIXEL = 465;
  public static final int PITCH_HEIGHT_IN_PIXEL = 305;
  
  private static final Logger LOGGER = Logger.getLogger(TacticEditorView.class.getName());

  private static final Font dialog = new Font("Dialog", Font.BOLD, 14);
  private static final long serialVersionUID = 1L;
  private transient BufferedImage tacticPitchBackground; // not serialized
  private final TacticModel model;
  private boolean showRayTrace = false;

  public TacticEditorView(TacticModel model) {
	this.model = model;

	loadBackgroundImage();
  }

  private void loadBackgroundImage() {
	try (InputStream resourceAsStream = TacticEditorView.class.getClassLoader()
	    .getResourceAsStream("pitch_tactic_editor.png")) {
	  this.tacticPitchBackground = ImageIO.read(resourceAsStream);
	} catch (final IOException e) {
	  e.printStackTrace();
	}
  }

  @Override
  public Dimension getPreferredSize() {
	return this.tacticPitchBackground == null ? new Dimension(200, 200)
	    : new Dimension(this.tacticPitchBackground.getWidth(), this.tacticPitchBackground.getHeight());
  }

  public boolean isShowRayTrace() {
	return this.showRayTrace;
  }

  public void setShowRayTrace(boolean showRayTrace) {
	this.showRayTrace = showRayTrace;
  }

  public void toggleRayTrace() {
	this.showRayTrace = ! this.showRayTrace;
  }

  /**
   * Register the tactic controller as the listener to the TaticView Panel.
   * 
   * @param listener
   */
  public void registerListener(TacticEditorController listener) {
	addMouseListener(listener);
	addMouseMotionListener(listener);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
	repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	drawPitch(g);
	drawTactic(g);
  }

  private void drawPitch(Graphics g) {
	if (this.tacticPitchBackground == null) {
	  return;
	}

	g.drawImage(this.tacticPitchBackground, 0, 0, null);
  }

  private void drawTactic(Graphics g) {
	if (this.model == null) {
	  return;
	}
	final Graphics2D g2 = (Graphics2D) g;
	final AffineTransform saveXform = g2.getTransform();

	if (isDebugMode()) {
	  drawReferencePoints(g2);
	}
	drawPlayers(g2);

	g2.dispose();
	g2.setTransform(saveXform);
  }

  boolean isDebugMode() {
	return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
	    .contains("-agentlib:jdwp");
  }

  private void drawReferencePoints(Graphics2D g2) {
	final Point p1 = new Point(0, 0);
	final Point p2 = new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, PitchConstants.PITCH_HEIGHT_IN_PIXEL);
	final Point p3 = new Point(0, PitchConstants.PITCH_HEIGHT_IN_PIXEL);
	final Point p4 = new Point(PitchConstants.PITCH_WIDTH_IN_PIXEL, 0);

	final List<Point> points = List.of(p1, p2, p3, p4);
	final int radius = 8;
	final int diameter = radius * 2;

	g2.setColor(Color.RED);

	points.stream().forEach(p -> {
	  final Point projectedPoint = TacticUtil.project(p);
	  g2.drawOval(projectedPoint.x - radius, projectedPoint.y - radius, diameter, diameter);
	});
  }

  private void drawPlayers(Graphics2D g2) {
	if (this.model.getSelectedZone() == -1) {
	  return;
	}

	final Map<Integer, Point> positions = getTeamPositionsForZone(this.model.getSelectedZone());
	if (positions.isEmpty()) {
	  return;
	}

	drawRayTrace(g2, positions);

	Tactic.SQUAD_NUMBERS.stream().forEach(squadNumber -> {
	  final Point position = TacticUtil.project(positions.get(squadNumber));
	  drawPlayerShirt(g2, squadNumber, position.x, position.y, 8);
	});
  }

  private void drawRayTrace(Graphics2D g2, Map<Integer, Point> positions) {
	if (! this.showRayTrace || (this.model.getPreviousSelectedZone() == -1)) {
	  return;
	}

	final Map<Integer, Point> previousPositions = getTeamPositionsForZone(this.model.getPreviousSelectedZone());
	if (previousPositions.isEmpty()) {
	  return;
	}

	final Stroke oldStroke = g2.getStroke();

	g2.setStroke(new BasicStroke(1.5f));
	g2.setColor(Color.YELLOW);
	Tactic.SQUAD_NUMBERS.stream().forEach(squadNumber -> {
	  final Point from = TacticUtil.project(previousPositions.get(squadNumber));
	  final Point to = TacticUtil.project(positions.get(squadNumber));
	  LOGGER.log(Level.INFO, "Player {0}: from {1} to {2}", new Object[] { squadNumber, from, to });
	  g2.draw(new Line2D.Float(from.x, from.y, to.x, to.y));
	});

	g2.setStroke(oldStroke);
  }

  private void drawPlayerShirt(Graphics2D g2, int shirt, int x, int y, int radius) {
	final String shirtLabel = String.valueOf(shirt);
	final FontMetrics fm = g2.getFontMetrics(dialog);

	if (isDebugMode()) {
	  drawOval(g2, x, y, radius);
	  drawCross(g2, x, y, radius);
	  drawTextBoxFontMetrics(g2, x, y, shirtLabel);
	}
	g2.setColor(Color.YELLOW);
	g2.setFont(dialog);
	final Point textPoint = new Point(x - fm.stringWidth(shirtLabel) / 2,
	    (y - fm.getDescent() / 2) + fm.getAscent() / 2);
	g2.drawString(shirtLabel, textPoint.x, textPoint.y);
  }

  private static void drawCross(Graphics2D g2, int x, int y, int radius) {
	final Color oldColor = g2.getColor();
	g2.setColor(Color.RED);
	g2.drawLine(x, y - radius, x, y + radius);
	g2.drawLine(x - radius, y, x + radius, y);
	g2.setColor(oldColor);
  }

  private void drawOval(Graphics2D g2, int x, int y, int radius) {
	final Color oldColor = g2.getColor();
	g2.setColor(Color.RED);
	final int diameter = radius * 2;
	// shift x and y by the radius of the circle in order to correctly center it
	g2.drawOval(x - radius, y - radius, diameter, diameter);
	g2.setColor(oldColor);
  }

  private void drawTextBoxFontMetrics(Graphics2D g2, int x, int y, String text) {
	final FontMetrics fm = g2.getFontMetrics(dialog);
	final Dimension textSize = new Dimension(fm.stringWidth(text), fm.getAscent());
	final Color oldColor = g2.getColor();
	g2.setColor(Color.BLUE);
	g2.drawRect(x - textSize.width / 2, y - textSize.height / 2, textSize.width, textSize.height);
	g2.setColor(oldColor);
  }

  private Map<Integer, Point> getTeamPositionsForZone(int region) {
	Map<Integer, Point> result = new HashMap<>();
	if (this.model.getTatic() != null) {
	  result = this.model.getTatic().getPositions(PitchZone.of(region));
	}
	return result;
  }
}
