/**
 * @file
 * Contains an implementation of the countOnes function.
 */

unsigned countOnes(unsigned input) {
	// TODO: write your code here
	unsigned left_counter = 0;
	unsigned right_counter = 0;

	left_counter = input & 0xaaaaaaaa;
	right_counter = input & 0x55555555;
  unsigned step1 = (left_counter >> 1) + right_counter;

	unsigned left_mask = 0xcccccccc;
	unsigned right_mask = 0x33333333;
	unsigned step2 = ((step1 & left_mask) >> 2) + (step1 & right_mask);

	left_mask = 0xf0f0f0f0;
	right_mask = 0x0f0f0f0f;
	unsigned step3 = ((step2 & left_mask) >> 4) + (step2 & right_mask);

	left_mask = 0xff00ff00;
	right_mask = 0x00ff00ff;
	unsigned step4 = ((step3 & left_mask) >> 8) + (step3 & right_mask);

	left_mask = 0xffff0000;
	right_mask = 0x0000ffff;
	unsigned step5 = ((step4 & left_mask) >> 16) + (step4 & right_mask);

	input = step5;

	return input;
}
