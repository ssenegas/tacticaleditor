package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileSystemView;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TacticModel;
import org.senegas.tacticeditor.utils.TacticUtil;

public class TacticController extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final TacticModel model;
	private final TaticView view;
	private final ZoneController zoneController;
	private final Preferences prefs;
	private static final String LAST_USED_FOLDER = "LAST_USED_FOLDER";
	
    private Point draggedPoint = null; // the point that is currently being dragged
    private Point offset = new Point(); // the offset between the mouse click and the top-left corner of the dragged point


	public TacticController(TacticModel model, TaticView view) {
		super(new BorderLayout());

		this.model = model;
        this.view = view;
        this.zoneController = new ZoneController(this.model);

        this.prefs = Preferences.userRoot().node(getClass().getName());
        this.prefs.put(LAST_USED_FOLDER, FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());

        final JButton load = new JButton("Load");
        load.addActionListener(this::handleLoadAction);

        final JButton save = new JButton("Save");
        save.addActionListener(this::handleSaveAction);

        final JToggleButton flip = new JToggleButton("Flip");
//        flip.addActionListener(e -> {
//
//        });

        final JToggleButton raytrace = new JToggleButton("Ray");
        raytrace.addActionListener(e -> {
        	this.view.toggleRayTrace();
        	this.view.repaint();
        });

        final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(load);
        actionPanel.add(save);
        actionPanel.add(flip);
        actionPanel.add(raytrace);

        this.add(actionPanel, BorderLayout.NORTH);
        this.add(this.zoneController, BorderLayout.CENTER);
	}
	
	/**
	 * Handle the Load action
	 */
	private void handleLoadAction(ActionEvent e) {
		Path path = null;
		final JFileChooser chooser = new JFileChooser(this.prefs.get(LAST_USED_FOLDER,
				new File(".").getAbsolutePath()));
		final int state = chooser.showDialog(TacticController.this, "Load");
		switch (state) {
			case JFileChooser.APPROVE_OPTION:
				//        	        action.setText("OK");
				try {
					path = Path.of(chooser.getSelectedFile().getCanonicalPath());
					this.prefs.put(LAST_USED_FOLDER, chooser.getSelectedFile().getParent());
				} catch (final IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case JFileChooser.CANCEL_OPTION:
				//        	        action.setText("Cancel");break;
				
			default:
				//        	        action.setText("Error");
				return;
		}
		final Tactic t = TacticUtil.read(path);
		this.model.setTatic(t);
		this.zoneController.enableDisableButtons();
		this.zoneController.toggleGoalkickOwn();
	}
	
	/**
	 * Handle the Save action
	 */
	private void handleSaveAction(ActionEvent e) {
		// To be implemented
	}


	@Override
	public void mousePressed(MouseEvent event) {
		if (model.getSelectedZone() == -1) {
			return;
		}
		
		Map<Integer, Point> positions = model.getTatic().getPositionsFor(PitchZone.of(model.getSelectedZone()));
		
		Point worldPosition = TacticUtil.unproject(event.getPoint());
		System.out.println("clicked point: " + event.getPoint() + " world point: " + worldPosition);
		
		for (Point point : positions.values()) {
			System.out.println("checked point: " + point);
            if (worldPosition.getX() >= point.x - 8 && worldPosition.getX() <= point.x + 8 &&
            		worldPosition.getY() >= point.y - 8 && worldPosition.getY() <= point.y + 8) {
    			System.out.println("found!");
    			this.draggedPoint = point;
    			this.offset.setLocation(worldPosition.getX() - point.x, worldPosition.getY() - point.y);
                break;
            }
        }
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		// stop dragging the point
		System.out.println("stop dragging the point!");
		System.out.println("dragged point: " + this.draggedPoint);
        this.draggedPoint = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {
		// move the dragged point
        if (this.draggedPoint != null) {
        	Point worldPosition = TacticUtil.unproject(event.getPoint());
            this.draggedPoint.setLocation(worldPosition.getX() - this.offset.x, worldPosition.getY() - this.offset.y);
            this.view.repaint();
        }
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {}
	
	@Override
	public void mouseMoved(MouseEvent event) {}
	
	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
