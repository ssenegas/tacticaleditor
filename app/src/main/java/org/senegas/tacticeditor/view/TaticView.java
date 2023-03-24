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
	private static final Font dialog = new Font("Dialog", Font.BOLD, 16);

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
		
		addMouseListener(new MouseAdapter() {
            private Color background;

            @Override
            public void mousePressed(MouseEvent e) {
                background = getBackground();
                setBackground(Color.RED);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(background);
            }
        });
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
		final Point p2 = new Point(914, 1392);
		final Point p3 = new Point(0, 1392);
		final Point p4 = new Point(914, 0);

		final int radius = 9;
		final int diameter = radius * 2;

		g2.setColor(Color.RED);

		final List<Point> points = List.of(p1, p2, p3, p4);
		points.stream()
		.forEach(p -> {
			final Point s = positionToScreen(p);
			g2.drawOval(s.x - radius, s.y - radius, diameter, diameter);
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
			final Point position = positionToScreen(positions.get(index));
			drawPlayer(g2, index + 2, position.x, position.y, 9);
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

		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.YELLOW);

		IntStream.range(0, Tactic.NUMBER_OF_PLAYERS) // shirt numbers
			.boxed()
			.forEach(index -> {
				final Point from = positionToScreen(previousPositions.get(index));
				final Point to = positionToScreen(positions.get(index));
	
				System.out.println("Player" + index + ": " + to);
	
				g2.draw(new Line2D.Float(from.x, from.y, to.x, to.y));
			});
	}

	/**
	 * @param source
	 * @return
	 */
	private Point positionToScreen(Point source) {
		return TacticUtil.transformTo(new Dimension(TACTIC_PITCH_WIDTH_IN_PX, TACTIC_PITCH_HEIGHT_IN_PX), source);
	}

	private static void drawPlayer(Graphics2D g2, int shirt, int x, int y, int radius) {
		g2.setColor(Color.YELLOW);

		final int diameter = radius * 2;
		//shift x and y by the radius of the circle in order to correctly center it
		//g2.drawOval(x - radius, y - radius, diameter, diameter);

		final String text = String.valueOf(shirt);
		final FontMetrics metrics = g2.getFontMetrics(dialog);

		final Rectangle rect = new Rectangle(x - radius, y - radius, diameter, diameter);

		final int tx = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		final int ty = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

		g2.setFont(dialog);
		g2.drawString(text, tx, ty);
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
