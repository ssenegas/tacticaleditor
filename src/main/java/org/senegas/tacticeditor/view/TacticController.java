package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
  private static final long serialVersionUID = 1L;

  private TacticModel model;
  private TacticView view;
  private ZoneController zoneController;
  private static final String LAST_USED_FOLDER = "LAST_USED_FOLDER";

  /** the point that is currently being dragged */
  private Point draggedPoint = null;

  /** the offset between the mouse click and the top-left corner of the dragged */
  private final Point offset = new Point();

  public TacticController(TacticModel model, TacticView view) {
	super(new BorderLayout());

	initializeMVC(model, view);
	initGui();
  }

  private void initializeMVC(TacticModel model, TacticView view) {
	this.model = model;
	this.view = view;
	this.zoneController = new ZoneController(this.model);
  }

  private void setLastUsedFolder(String path) {
	final Preferences prefs = Preferences.userRoot().node(getClass().getName());
	prefs.put(LAST_USED_FOLDER, path);
  }

  private String getLastUsedFolder() {
	final Preferences prefs = Preferences.userRoot().node(getClass().getName());
	return prefs.get(LAST_USED_FOLDER, FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
  }

  private void initGui() {
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

  private void handleLoadAction(ActionEvent event) {
	Path path = null;
	final JFileChooser chooser = new JFileChooser(getLastUsedFolder());
	final int state = chooser.showDialog(TacticController.this, "Load");
	switch (state) {
	case JFileChooser.APPROVE_OPTION:
	  // action.setText("OK");
	  try {
		path = Path.of(chooser.getSelectedFile().getCanonicalPath());
		setLastUsedFolder(chooser.getSelectedFile().getParent());
	  } catch (final IOException e) {
		e.printStackTrace();
	  }
	  break;

	case JFileChooser.CANCEL_OPTION:
	  // action.setText("Cancel");break;

	default:
	  // action.setText("Error");
	  return;
	}
	try {
	  final Tactic t = Tactic.create(path);
	  this.model.setTatic(t);
	  this.zoneController.enableDisableButtons();
	  this.zoneController.toggleGoalkickOwn();
	} catch (final IOException e) {
	  e.printStackTrace();
	}
  }

  private void handleSaveAction(ActionEvent e) {
	// Not yet implemented
  }

  @Override
  public void mousePressed(MouseEvent event) {
	if (this.model.getSelectedZone() == -1) {
	  return;
	}
	final Point worldPosition = TacticUtil.unproject(event.getPoint());
	System.out.println("clicked point: " + event.getPoint() + " world point: " + worldPosition);

	final Map<Integer, Point> positions = this.model.getTatic()
	    .getPositions(PitchZone.of(this.model.getSelectedZone()));
	for (final Point point : positions.values()) {
	  System.out.println("checked point: " + point);
	  if (worldPosition.getX() >= point.x - 8 && worldPosition.getX() <= point.x + 8
	      && worldPosition.getY() >= point.y - 8 && worldPosition.getY() <= point.y + 8) {
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
	  final Point worldPosition = TacticUtil.unproject(event.getPoint());
	  this.draggedPoint.setLocation(worldPosition.getX() - this.offset.x, worldPosition.getY() - this.offset.y);
	  this.view.repaint();
	}
  }

  @Override
  public void mouseClicked(MouseEvent event) {
  }

  @Override
  public void mouseMoved(MouseEvent event) {
  }

  @Override
  public void mouseEntered(MouseEvent event) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }
}
