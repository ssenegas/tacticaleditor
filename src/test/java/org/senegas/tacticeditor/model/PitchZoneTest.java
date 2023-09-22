package org.senegas.tacticeditor.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class PitchZoneTest {

	@Test
	void whenOfWithKnownIndexGivenThenReturnAssociatedPitchZone() {
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
	
	@Test
    void givenUnknownIndexGivenThenExceptionThrown() {
		Exception exception = assertThrows(NoSuchElementException.class, () ->
			PitchZone.of(100));
		
		String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
	}
}
