package org.senegas.tacticeditor.view;

import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.senegas.tacticeditor.model.PitchZone;
import org.senegas.tacticeditor.model.TacticModel;

public class ZoneController extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final TacticModel model;
	EnumMap<ButtonModel, JToggleButton> buttons = new EnumMap<> (ButtonModel.class);

	private enum ButtonModel {
		AREA1("1", PitchZone.AREA1, false),
		AREA2("2", PitchZone.AREA2, false),
		AREA3("3", PitchZone.AREA3, false),
		AREA4("4", PitchZone.AREA4, false),
		AREA5("5", PitchZone.AREA5, false),
		AREA6("6", PitchZone.AREA6, false),
		AREA7("7", PitchZone.AREA7, false),
		AREA8("8", PitchZone.AREA8, false),
		AREA9("9", PitchZone.AREA9, false),
		AREA10("10", PitchZone.AREA10, false),
		AREA11("11", PitchZone.AREA11, false),
		AREA12("12", PitchZone.AREA12, false),
		KICKOFF_OWN("Kickoff own", PitchZone.KICKOFF_OWN, true),
		KICKOFF_DEF("Kickoff def", PitchZone.KICKOFF_DEF, true),
		GOALKICK_OWN("Goalkick own", PitchZone.GOALKICK_OWN, true),
		GOALKICK_DEF("Goalkick def", PitchZone.GOALKICK_DEF, true),
		CORNER1("Corner1", PitchZone.CORNER1, true),
		CORNER2("Corner2", PitchZone.CORNER2, true),
		CORNER3("Corner3", PitchZone.CORNER3, true),
		CORNER4("Corner4", PitchZone.CORNER4, true);

		private final String text;
		private final PitchZone pitchZone;
		private final boolean isResumeGame;

		private ButtonModel(String text, PitchZone pitchZone, boolean b) {
			this.text = text;
			this.pitchZone = pitchZone;
			this.isResumeGame = b;
		}

		public String getText() {
		    return this.text;
		}

		public Integer getZone() {
		    return this.pitchZone.getIndex();
		}

		public boolean isResumeGame() {
		    return this.isResumeGame;
		}

		public static Stream<ButtonModel> stream() {
	        return Stream.of(ButtonModel.values());
	    }
	}

	public ZoneController(TacticModel model) {
		setLayout(new GridLayout(3,4));

		this.model = model;

		final ButtonGroup group = new ButtonGroup();
		ButtonModel.stream()
			.forEach(bm -> {
				final JToggleButton tb = new JToggleButton(bm.getText());
	        	this.buttons.put(bm, tb);
	        	group.add(tb);
	        	tb.addActionListener(e -> {
	            	this.model.setSelectedZone(bm.getZone());
	            	if (bm.isResumeGame())
	            		this.model.resetPreviousZoneSelection();
	            });
			});

        add(buildZoneSelectorPanel());
        add(buildResumeGamePanel());

        enableDisableButtons();
    }

	/**
	 * @return
	 */
	private JPanel buildResumeGamePanel() {
		final JPanel grid = new JPanel(new GridLayout(4,2));
		ButtonModel.stream()
			.filter(ButtonModel::isResumeGame)
			.forEach(bm -> grid.add(this.buttons.get(bm)));
		return grid;
	}

	/**
	 * @return
	 */
	private JPanel buildZoneSelectorPanel() {
		final JPanel grid = new JPanel(new GridLayout(3,4));
		ButtonModel.stream()
			.filter(Predicate.not(ButtonModel::isResumeGame))
			.forEach(bm -> grid.add(this.buttons.get(bm)));
		return grid;
	}

	public void enableDisableButtons() {
		final boolean b = this.model.getTatic() != null;
		this.buttons.values().stream()
			.forEach(tb -> tb.setEnabled(b));
	}

	public void toggleGoalkickOwn() {
		this.buttons.get(ButtonModel.KICKOFF_OWN).doClick();
	}
}
