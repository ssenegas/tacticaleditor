package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileSystemView;

import org.senegas.tacticeditor.model.Tactic;
import org.senegas.tacticeditor.model.TacticModel;
import org.senegas.tacticeditor.utils.TacticUtil;

public class TacticController extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final TacticModel model;
	private final TaticView view;
	private final ZoneController sectorController;
	private final Preferences prefs;
	private static final String LAST_USED_FOLDER = "LAST_USED_FOLDER";

	public TacticController(TacticModel model, TaticView view) {
		super(new BorderLayout());

		this.model = model;
        this.view = view;
        this.sectorController = new ZoneController(this.model);

        this.prefs = Preferences.userRoot().node(getClass().getName());
        this.prefs.put(LAST_USED_FOLDER, FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());

        final JButton load = new JButton("Load");
        load.addActionListener(e -> {
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
//        	        action.setText("erreur");
        	    	return;
        	}
        	final Tactic t = TacticUtil.read(path);
        	this.model.setTatic(t);
        	this.sectorController.enableDisableButtons();
        	this.sectorController.toggleGoalkickOwn();
        });

        final JButton save = new JButton("Save");
//        save.addActionListener(e -> {
//
//        });

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
        this.add(this.sectorController, BorderLayout.CENTER);
	}
}
