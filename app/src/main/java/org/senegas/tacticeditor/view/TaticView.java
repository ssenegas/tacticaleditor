package org.senegas.tacticeditor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TacticModel;
import org.senegas.tacticeditor.utils.TacticUtil;

public class TaticView extends JPanel implements PropertyChangeListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final int TACTIC_PITCH_WIDTH_IN_PX = 465;
	public static final int TACTIC_PITCH_HEIGHT_IN_PX = 305;
	private static final Font dialog = new Font("Dialog", Font.BOLD, 14);

	private BufferedImage tacticPitch;
	private final TacticModel model;
	private boolean showRayTrace = false;

	public TaticView(TacticModel model) {
		this.model = model;

		try (InputStream resourceAsStream = TaticView.class.getClassLoader().getResourceAsStream("pitch_tactic_editor.png")) {
			this.tacticPitch = ImageIO.read(resourceAsStream);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
//		addMouseListener(new MouseAdapter() {
//            private Color background;
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                background = getBackground();
//                setBackground(Color.RED);
//                repaint();
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                setBackground(background);
//            }
//        });
	}

	@Override
    public Dimension getPreferredSize() {
		return this.tacticPitch == null ? new Dimension(200, 200)
				                   : new Dimension(this.tacticPitch.getWidth(), this.tacticPitch.getHeight());
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
	 * @param listener
	 */
	public void registerListener(TacticController listener) {
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawPitch(g);
		drawTactic(g);
	}

	private void drawPitch(Graphics g) {
		if (this.tacticPitch == null)
			return;

		g.drawImage(this.tacticPitch, 0, 0, null);
	}

	private void drawTactic(Graphics g) {
		if (this.model == null)
			return;

		final Graphics2D g2 = (Graphics2D) g;
		final AffineTransform saveXform = g2.getTransform();

		drawReferencePoints(g2);
		drawPlayers(g2);

		g2.dispose();
		g2.setTransform(saveXform);
	}

	private void drawReferencePoints(Graphics2D g2) {
		final Point p1 = new Point(0, 0);
		final Point p2 = new Point(TacticUtil.PITCH_WIDTH_IN_PX, TacticUtil.PITCH_HEIGHT_IN_PX);
		final Point p3 = new Point(0, TacticUtil.PITCH_HEIGHT_IN_PX);
		final Point p4 = new Point(TacticUtil.PITCH_WIDTH_IN_PX, 0);

		final List<Point> points = List.of(p1, p2, p3, p4);
		final int radius = 8;
		final int diameter = radius * 2;

		g2.setColor(Color.RED);

		points.stream()
		.forEach(p -> {
			final Point projectedPoint = TacticUtil.project(p);
			g2.drawOval(projectedPoint.x - radius, projectedPoint.y - radius, diameter, diameter);
		});
	}

	private void drawPlayers(Graphics2D g2) {

		if (this.model.getSelectedZone() == -1)
			return;

		final Map<Integer, Point> positions = getTeamPositionsForZone(this.model.getSelectedZone());
		if (positions.isEmpty())
			return;

		drawRayTrace(g2, positions);
		
		IntStream.range(0, Tactic.NUMBER_OF_PLAYERS) // shirt numbers
		.boxed()
		.forEach(index -> {
			final Point position = TacticUtil.project(positions.get(index));
			drawPlayerShirt(g2, index + 2, position.x, position.y, 8);
		});
	}

	private void drawRayTrace(Graphics2D g2, Map<Integer, Point> positions) {

		if (! this.showRayTrace)
			return;

		if (this.model.getPreviousZoneSelection() == -1)
			return;

		final Map<Integer, Point> previousPositions = getTeamPositionsForZone(this.model.getPreviousZoneSelection());
		if (previousPositions.isEmpty())
			return;

		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(1.5f));
		g2.setColor(Color.YELLOW);

		IntStream.range(0, Tactic.NUMBER_OF_PLAYERS) // shirt numbers
			.boxed()
			.forEach(index -> {
				final Point from = TacticUtil.project(previousPositions.get(index));
				final Point to = TacticUtil.project(positions.get(index));
	
				System.out.println("Player" + index + ": " + to);
	
				g2.draw(new Line2D.Float(from.x, from.y, to.x, to.y));
			});
		g2.setStroke(oldStroke);
	}

	private static void drawPlayerShirt(Graphics2D g2, int shirt, int x, int y, int radius) {
		final String shirtLabel = String.valueOf(shirt);
		final FontMetrics fm = g2.getFontMetrics(dialog);
		
		// debug
//		drawOval(g2, x, y, radius);
//		drawCross(g2, x, y, radius);
//		drawTextBoxFontMetrics(g2, x, y, shirtLabel);
		
		g2.setColor(Color.YELLOW);
		g2.setFont(dialog);
		Point textPoint = new Point(x - fm.stringWidth(shirtLabel) / 2, (y - fm.getDescent() / 2) + fm.getAscent() / 2);
		g2.drawString(shirtLabel, textPoint.x, textPoint.y);
	}
	
	private static void drawCross(Graphics2D g2, int x, int y, int radius) {
		Color oldColor = g2.getColor();
		g2.setColor(Color.RED);
		g2.drawLine(x, y - radius, x, y + radius);
		g2.drawLine(x - radius, y, x + radius, y);
		g2.setColor(oldColor);
	}
	
	private static void drawOval(Graphics2D g2, int x, int y, int radius) {
		Color oldColor = g2.getColor();
		g2.setColor(Color.RED);
		final int diameter = radius * 2;
		//shift x and y by the radius of the circle in order to correctly center it
		g2.drawOval(x - radius, y - radius, diameter, diameter);
		g2.setColor(oldColor);
	}
	
	private static void drawTextBoxFontMetrics(Graphics2D g2, int x, int y, String text) {
		final FontMetrics fm = g2.getFontMetrics(dialog);
		Dimension textSize = new Dimension(fm.stringWidth(text), fm.getAscent());
		Color oldColor = g2.getColor();
		g2.setColor(Color.BLUE);
		g2.drawRect(x - textSize.width / 2, y - textSize.height / 2, textSize.width, textSize.height);
		g2.setColor(oldColor);
	}

	private Map<Integer, Point> getTeamPositionsForZone(int region) {
		Map<Integer, Point> result = new HashMap<>();
		if (this.model.getTatic() != null) {
			result = this.model.getTatic().getPositionsFor(PitchZone.getPitchZoneByIndex(region));
		}
		return result;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
}
