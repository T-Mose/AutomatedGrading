package main

import (
	"testing"
)

// test that ConcurrentSum sums an even-length array correctly
func TestSumConcurrentCorrectlySumsEvenArray(t *testing.T) {
	arr := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
	expected := 55

	actual := ConcurrentSum(arr)

	if actual != expected {
		t.Errorf("expected %d, was %d", expected, actual)
	}
}
func TestSumConcurrentCorrectlySumsOddArray(t *testing.T) {
	arr := []int{2, 3, 4, 5, 6, 7, 8, 9, 10}
	expected := 54

	actual := ConcurrentSum(arr)

	if actual != expected {
		t.Errorf("expected %d, was %d", expected, actual)
	}
}
func TestEmptyArray(t *testing.T) {
	arr := []int{}
	expected := 0
	actual := ConcurrentSum(arr)
	if actual != expected {
		t.Errorf("expected %d, was %d", expected, actual)
	}
}

// TODO add at least two more test cases!