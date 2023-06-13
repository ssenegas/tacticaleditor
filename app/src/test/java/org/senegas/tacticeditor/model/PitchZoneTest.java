package org.senegas.tacticeditor.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.junit.Test;

public class PitchZoneTest {
	

	@Test
	public void whenOfWithKnownIndexGivenThenReturnAssociatedPitchZone() {
		// Given
		List<PitchZone> referenceList = Arrays.asList(PitchZone.values());
		
		// When
		List<PitchZone> builtList = new ArrayList<>();
		IntStream.range(0, 20)
			.forEach(i -> builtList.add(PitchZone.of(i)));
		
		// Then
		assertTrue(referenceList.size() == builtList.size() &&
				referenceList.containsAll(builtList) &&
				builtList.containsAll(referenceList));
	}

	@Test(expected = NoSuchElementException.class)
	public void whenUnknownIndexGivenThenExceptionThrown() {
		PitchZone.of(100);
	}
}
